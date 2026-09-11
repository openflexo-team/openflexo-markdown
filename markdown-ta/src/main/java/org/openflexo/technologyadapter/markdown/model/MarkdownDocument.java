/*
 * (c) Copyright 2013- Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.technologyadapter.markdown.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;

import org.openflexo.foundation.doc.FlexoDocElement;
import org.openflexo.foundation.doc.FlexoDocParagraph;
import org.openflexo.foundation.doc.FlexoDocTable;
import org.openflexo.foundation.doc.FlexoDocument;
import org.openflexo.foundation.doc.NamedDocStyle;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResource;

/**
 * Root model object for a Markdown document.
 * <p>
 * Wraps a flexmark-java {@link Document} AST and exposes its block-level elements
 * ({@link MarkdownHeading}, {@link MarkdownParagraph}, {@link MarkdownCodeBlock}) as
 * {@link FlexoDocElement} instances so they can be manipulated uniformly through the
 * {@link FlexoDocument} API.
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(MarkdownDocument.MarkdownDocumentImpl.class)
@XMLElement
@Imports({
		@Import(MarkdownParagraph.class),
		@Import(MarkdownHeading.class),
		@Import(MarkdownCodeBlock.class),
		@Import(MarkdownNamedStyle.class)
})
public interface MarkdownDocument extends FlexoDocument<MarkdownDocument, MarkdownTechnologyAdapter> {

	@PropertyIdentifier(type = Document.class)
	String MARKDOWN_DOCUMENT_KEY = "markdownDocument";

	/**
	 * The underlying flexmark AST root node. Stored in PAMELA with {@code ignoreType = true}
	 * so it is not serialised (serialisation is handled by {@link org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResourceImpl}).
	 */
	@Getter(value = MARKDOWN_DOCUMENT_KEY, ignoreType = true)
	Document getMarkdownDocument();

	@Setter(MARKDOWN_DOCUMENT_KEY)
	void setMarkdownDocument(Document doc);

	/**
	 * Synchronise the in-memory PAMELA model with the supplied flexmark AST.
	 * Called after loading or after an in-place re-parse.
	 */
	void updateFromMarkdownDocument(Document doc, MarkdownFactory factory);

	/**
	 * Serialise this document back to raw Markdown text.
	 * Used by the resource for saving and by the UI editor for display.
	 */
	String toMarkdown();

	@Override
	MarkdownFactory getFactory();

	// -----------------------------------------------------------------------
	// Implementation
	// -----------------------------------------------------------------------

	abstract class MarkdownDocumentImpl extends FlexoDocument.FlexoDocumentImpl<MarkdownDocument, MarkdownTechnologyAdapter>
			implements MarkdownDocument {

		private static final java.util.logging.Logger logger =
				java.util.logging.Logger.getLogger(MarkdownDocumentImpl.class.getPackage().getName());

		/** Fallback factory reference set by {@link MarkdownFactory#makeDocument()}, used before the resource is bound. */
		MarkdownFactory _factory;

		private MarkdownDocumentResource resource;

		@Override
		public MarkdownDocument getFlexoDocument() {
			return this;
		}

		@Override
		public MarkdownDocumentResource getResource() {
			return resource;
		}

		@Override
		public void setResource(FlexoResource<MarkdownDocument> resource) {
			if ((resource == null && this.resource != null) || (resource != null && !resource.equals(this.resource))) {
				FlexoResource<MarkdownDocument> oldValue = this.resource;
				this.resource = (MarkdownDocumentResource) resource;
				getPropertyChangeSupport().firePropertyChange("resource", oldValue, resource);
			}
		}

		// ------------------------------------------------------------------
		// Synchronisation with the flexmark AST
		// ------------------------------------------------------------------

		@Override
		public void setMarkdownDocument(Document doc) {
			if (doc != null && getResource() != null) {
				updateFromMarkdownDocument(doc, getResource().getFactory());
			}
		}

		@Override
		public void updateFromMarkdownDocument(Document doc, MarkdownFactory factory) {

			List<FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter>> elementsToRemove = new ArrayList<>(getElements());

			postponeRootElementChangedNotifications = true;

			// Register heading styles first so they are available when assigning namedStyle to headings
			registerHeadingStructuringStyles(factory);

			int index = 0;
			for (Node block : doc.getChildren()) {
				FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> element = null;

				if (block instanceof Heading) {
					element = getOrCreateHeading((Heading) block, factory, index);
				}
				else if (block instanceof Paragraph) {
					element = getOrCreateParagraph((Paragraph) block, factory, index);
				}
				else if (block instanceof FencedCodeBlock || block instanceof IndentedCodeBlock) {
					element = getOrCreateCodeBlock(block, factory, index);
				}
				// other block types (BlockQuote, BulletList, …) are currently ignored

				if (element != null) {
					elementsToRemove.remove(element);
					index++;
				}
			}

			for (FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> obsolete : elementsToRemove) {
				internallyRemoveFromElements(obsolete);
			}

			// Commit to PAMELA without triggering the setter loop
			performSuperSetter(MARKDOWN_DOCUMENT_KEY, doc);

			postponeRootElementChangedNotifications = false;
			invalidateRootElements();
			notifyRootElementsChanged();
		}

		// ------------------------------------------------------------------
		// Element creation helpers
		// ------------------------------------------------------------------

		private MarkdownHeading getOrCreateHeading(Heading node, MarkdownFactory factory, int index) {
			// Look for existing element at same position
			List<FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter>> elements = getElements();
			MarkdownHeading heading;
			if (index < elements.size() && elements.get(index) instanceof MarkdownHeading) {
				heading = (MarkdownHeading) elements.get(index);
				((MarkdownHeading.MarkdownHeadingImpl) heading).setHeadingNode(node);
				heading.setLevel(node.getLevel());
			}
			else {
				heading = factory.makeNewMarkdownHeading(node);
				internallyInsertElementAtIndex(heading, index);
			}
			// Assign the corresponding heading style so the hierarchy is computed correctly
			NamedDocStyle<MarkdownDocument, MarkdownTechnologyAdapter> style = getStyleByIdentifier("heading " + node.getLevel());
			if (style != null) {
				heading.setNamedStyle(style);
			}
			return heading;
		}

		private MarkdownParagraph getOrCreateParagraph(Paragraph node, MarkdownFactory factory, int index) {
			List<FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter>> elements = getElements();
			if (index < elements.size() && elements.get(index) instanceof MarkdownParagraph) {
				MarkdownParagraph existing = (MarkdownParagraph) elements.get(index);
				((MarkdownParagraph.MarkdownParagraphImpl) existing).setParagraphNode(node);
				return existing;
			}
			MarkdownParagraph paragraph = factory.makeNewMarkdownParagraph(node);
			internallyInsertElementAtIndex(paragraph, index);
			return paragraph;
		}

		private MarkdownCodeBlock getOrCreateCodeBlock(Node node, MarkdownFactory factory, int index) {
			List<FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter>> elements = getElements();
			if (index < elements.size() && elements.get(index) instanceof MarkdownCodeBlock) {
				MarkdownCodeBlock existing = (MarkdownCodeBlock) elements.get(index);
				((MarkdownCodeBlock.MarkdownCodeBlockImpl) existing).setCodeBlockNode(node);
				refreshCodeBlockContent(existing, node);
				return existing;
			}
			MarkdownCodeBlock cb = factory.makeNewMarkdownCodeBlock(node);
			internallyInsertElementAtIndex(cb, index);
			return cb;
		}

		private void refreshCodeBlockContent(MarkdownCodeBlock cb, Node node) {
			if (node instanceof FencedCodeBlock) {
				FencedCodeBlock fenced = (FencedCodeBlock) node;
				cb.setContent(fenced.getContentChars().toString());
				cb.setLanguage(fenced.getInfo().toString());
			}
			else if (node instanceof IndentedCodeBlock) {
				cb.setContent(((IndentedCodeBlock) node).getContentChars().toString());
				cb.setLanguage(null);
			}
		}

		// ------------------------------------------------------------------
		// Internal list management (mirrors DocXDocument pattern)
		// ------------------------------------------------------------------

		private void internallyInsertElementAtIndex(FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> element, int index) {
			performSuperAdder(ELEMENTS_KEY, element, index);
			invalidateRootElements();
			notifyRootElementsChanged();
		}

		private void internallyRemoveFromElements(FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> element) {
			performSuperRemover(ELEMENTS_KEY, element);
			invalidateRootElements();
			notifyRootElementsChanged();
		}

		// ------------------------------------------------------------------
		// Structuring styles for headings
		// ------------------------------------------------------------------

		private void registerHeadingStructuringStyles(MarkdownFactory factory) {
			for (int level = 1; level <= 6; level++) {
				String styleId = "heading " + level;
				NamedDocStyle<MarkdownDocument, MarkdownTechnologyAdapter> style = getStyleByIdentifier(styleId);
				if (style == null) {
					style = factory.makeMarkdownNamedStyle(styleId, "Heading " + level, level);
					addToStyles(style);
				}
				if (!getStructuringStyles().contains(style)) {
					addToStructuringStyles(style);
				}
			}
		}

		// ------------------------------------------------------------------
		// FlexoDocument API
		// ------------------------------------------------------------------

		@Override
		public void insertElementAtIndex(FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> element, int index) {
			internallyInsertElementAtIndex(element, index);
		}

		@Override
		public void addToElements(FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> element) {
			performSuperAdder(ELEMENTS_KEY, element);
			invalidateRootElements();
			notifyRootElementsChanged();
		}

		@Override
		public void removeFromElements(FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> element) {
			internallyRemoveFromElements(element);
		}

		@Override
		public void moveElementToIndex(FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> element, int index) {
			List<FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter>> elements = getElements();
			elements.remove(element);
			elements.add(index, element);
			invalidateRootElements();
			notifyRootElementsChanged();
		}

		@Override
		public FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> getElementWithIdentifier(String identifier) {
			for (FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> e : getElements()) {
				if (identifier.equals(e.getIdentifier())) {
					return e;
				}
			}
			return null;
		}

		@Override
		public List<FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter>> getElementsWithBaseIdentifier(String baseIdentifier) {
			List<FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter>> result = new ArrayList<>();
			for (FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> e : getElements()) {
				if (baseIdentifier.equals(e.getBaseIdentifier())) {
					result.add(e);
				}
			}
			return result;
		}

		@Override
		public NamedDocStyle<MarkdownDocument, MarkdownTechnologyAdapter> activateStyle(String styleId) {
			NamedDocStyle<MarkdownDocument, MarkdownTechnologyAdapter> style = getStyleByIdentifier(styleId);
			if (style == null) {
				// Lazily register heading styles on first access (e.g. for empty documents)
				registerHeadingStructuringStyles(getFactory());
				style = getStyleByIdentifier(styleId);
			}
			return style;
		}

		@Override
		public Collection<String> getKnownStyleIds() {
			List<String> ids = new ArrayList<>();
			for (int i = 1; i <= 6; i++) {
				ids.add("heading " + i);
			}
			return Collections.unmodifiableList(ids);
		}

		@Override
		public FlexoDocParagraph<MarkdownDocument, MarkdownTechnologyAdapter> addStyledParagraphOfText(
				NamedDocStyle<MarkdownDocument, MarkdownTechnologyAdapter> style, String text) {
			MarkdownParagraph p = getFactory().makeNewMarkdownParagraph(null);
			p.setRawText(text);
			p.setNamedStyle(style);
			addToElements(p);
			return p;
		}

		@Override
		public FlexoDocParagraph<MarkdownDocument, MarkdownTechnologyAdapter> insertStyledParagraphOfTextAtIndex(
				NamedDocStyle<MarkdownDocument, MarkdownTechnologyAdapter> style, String text, int index) {
			MarkdownParagraph p = getFactory().makeNewMarkdownParagraph(null);
			p.setRawText(text);
			p.setNamedStyle(style);
			insertElementAtIndex(p, index);
			return p;
		}

		@Override
		public FlexoDocTable<MarkdownDocument, MarkdownTechnologyAdapter> addTable(int rows, int cols) {
			throw new UnsupportedOperationException("Table support not yet implemented for Markdown adapter");
		}

		@Override
		public FlexoDocTable<MarkdownDocument, MarkdownTechnologyAdapter> insertTableAtIndex(int rows, int cols, int index) {
			throw new UnsupportedOperationException("Table support not yet implemented for Markdown adapter");
		}

		// ------------------------------------------------------------------
		// Markdown serialisation
		// ------------------------------------------------------------------

		@Override
		public String toMarkdown() {
			StringBuilder sb = new StringBuilder();
			for (FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> element : getElements()) {
				sb.append(renderElementToMarkdown(element)).append("\n");
			}
			return sb.toString();
		}

		private static String renderElementToMarkdown(FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> element) {
			if (element instanceof MarkdownHeading) {
				MarkdownHeading h = (MarkdownHeading) element;
				int level = Math.max(1, Math.min(h.getLevel(), 6));
				StringBuilder prefix = new StringBuilder();
				for (int i = 0; i < level; i++) {
					prefix.append('#');
				}
				return prefix + " " + h.getRawText();
			}
			if (element instanceof MarkdownCodeBlock) {
				MarkdownCodeBlock cb = (MarkdownCodeBlock) element;
				String lang = cb.getLanguage() != null ? cb.getLanguage() : "";
				String content = cb.getContent() != null ? cb.getContent() : "";
				if (!content.endsWith("\n")) {
					content += "\n";
				}
				return "```" + lang + "\n" + content + "```";
			}
			if (element instanceof MarkdownParagraph) {
				return ((MarkdownParagraph) element).getRawText();
			}
			return element.toString();
		}

		@Override
		public String debugContents() {
			StringBuilder sb = new StringBuilder();
			for (FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> e : getElements()) {
				sb.append(e.toString()).append("\n");
			}
			return sb.toString();
		}

		@Override
		public String debugStructuredContents() {
			StringBuilder sb = new StringBuilder();
			for (FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> e : getRootElements()) {
				sb.append(debugElement(e, 0));
			}
			return sb.toString();
		}

		private String debugElement(FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> e, int depth) {
			StringBuilder indent = new StringBuilder();
			for (int i = 0; i < depth; i++) {
				indent.append("  ");
			}
			StringBuilder sb = new StringBuilder(indent).append(e.toString()).append("\n");
			for (FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> child : e.getChildrenElements()) {
				sb.append(debugElement(child, depth + 1));
			}
			return sb.toString();
		}

		@Override
		public MarkdownFactory getFactory() {
			MarkdownFactory returned = (MarkdownFactory) super.getFactory();
			if (returned == null) {
				return _factory;
			}
			return returned;
		}
	}
}
