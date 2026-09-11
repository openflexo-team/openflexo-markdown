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

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import org.openflexo.foundation.doc.DocumentFactory;
import org.openflexo.foundation.doc.FlexoDocFragment;
import org.openflexo.foundation.doc.FlexoDocParagraph;
import org.openflexo.foundation.doc.FlexoDocSdtBlock;
import org.openflexo.foundation.doc.FlexoDocTable;
import org.openflexo.foundation.doc.FlexoDocTableCell;
import org.openflexo.foundation.doc.FlexoDocTableRow;
import org.openflexo.foundation.doc.FlexoDocUnmappedElement;
import org.openflexo.foundation.doc.FlexoDrawingRun;
import org.openflexo.foundation.doc.FlexoParagraphStyle;
import org.openflexo.foundation.doc.FlexoRunStyle;
import org.openflexo.foundation.doc.FlexoTextRun;
import org.openflexo.foundation.doc.NamedDocStyle;
import org.openflexo.pamela.PamelaMetaModelLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.EditingContext;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResource;

/**
 * PAMELA-based factory for creating and populating {@link MarkdownDocument} instances.
 * <p>
 * One instance is created per {@link MarkdownDocumentResource}.
 *
 * @author sylvain
 */
public class MarkdownFactory extends DocumentFactory<MarkdownDocument, MarkdownTechnologyAdapter> {

	private static final Logger logger = Logger.getLogger(MarkdownFactory.class.getPackage().getName());

	private static final Parser PARSER;

	static {
		MutableDataSet options = new MutableDataSet();
		PARSER = Parser.builder(options).build();
	}

	private final AtomicInteger idCounter = new AtomicInteger(0);

	public MarkdownFactory(MarkdownDocumentResource resource, EditingContext editingContext) throws ModelDefinitionException {
		super(PamelaMetaModelLibrary.retrieveMetaModel(MarkdownDocument.class, MarkdownNamedStyle.class), resource, editingContext);
	}

	// ------------------------------------------------------------------
	// DocumentFactory abstract methods
	// ------------------------------------------------------------------

	@Override
	public MarkdownDocument makeDocument() {
		MarkdownDocument doc = newInstance(MarkdownDocument.class);
		((MarkdownDocument.MarkdownDocumentImpl) doc)._factory = this;
		return doc;
	}

	@Override
	public String generateId() {
		return "md-" + idCounter.incrementAndGet();
	}

	@Override
	public FlexoDocParagraph<MarkdownDocument, MarkdownTechnologyAdapter> makeParagraph() {
		return makeNewMarkdownParagraph(null);
	}

	@Override
	public FlexoParagraphStyle<MarkdownDocument, MarkdownTechnologyAdapter> makeParagraphStyle() {
		// Paragraph styles are not used in the simple Markdown model
		return null;
	}

	@Override
	public FlexoRunStyle<MarkdownDocument, MarkdownTechnologyAdapter> makeRunStyle() {
		// Run styles are not used in the simple Markdown model
		return null;
	}

	@Override
	public FlexoTextRun<MarkdownDocument, MarkdownTechnologyAdapter> makeTextRun() {
		return newInstance(MarkdownTextRun.class);
	}

	@Override
	public FlexoTextRun<MarkdownDocument, MarkdownTechnologyAdapter> makeTextRun(String text) {
		MarkdownTextRun run = newInstance(MarkdownTextRun.class);
		run.setText(text);
		return run;
	}

	@Override
	public FlexoDrawingRun<MarkdownDocument, MarkdownTechnologyAdapter> makeDrawingRun() {
		throw new UnsupportedOperationException("Drawing runs are not supported in Markdown adapter");
	}

	@Override
	public FlexoDrawingRun<MarkdownDocument, MarkdownTechnologyAdapter> makeDrawingRun(File imageFile) {
		throw new UnsupportedOperationException("Drawing runs are not supported in Markdown adapter");
	}

	@Override
	public FlexoDrawingRun<MarkdownDocument, MarkdownTechnologyAdapter> makeDrawingRun(BufferedImage image) {
		throw new UnsupportedOperationException("Drawing runs are not supported in Markdown adapter");
	}

	@Override
	public FlexoDocTable<MarkdownDocument, MarkdownTechnologyAdapter> makeTable() {
		throw new UnsupportedOperationException("Table support is not yet implemented in Markdown adapter");
	}

	@Override
	public FlexoDocTableRow<MarkdownDocument, MarkdownTechnologyAdapter> makeTableRow() {
		throw new UnsupportedOperationException("Table support is not yet implemented in Markdown adapter");
	}

	@Override
	public FlexoDocTableCell<MarkdownDocument, MarkdownTechnologyAdapter> makeTableCell() {
		throw new UnsupportedOperationException("Table support is not yet implemented in Markdown adapter");
	}

	@Override
	public FlexoDocSdtBlock<MarkdownDocument, MarkdownTechnologyAdapter> makeSdtBlock() {
		// SdtBlocks are a Word-specific concept, not applicable to Markdown
		return null;
	}

	@Override
	public FlexoDocUnmappedElement<MarkdownDocument, MarkdownTechnologyAdapter> makeUnmappedElement() {
		return null;
	}

	@Override
	protected NamedDocStyle<MarkdownDocument, MarkdownTechnologyAdapter> makeNamedStyle() {
		return newInstance(MarkdownNamedStyle.class);
	}

	@Override
	protected FlexoDocFragment<MarkdownDocument, MarkdownTechnologyAdapter> makeFragment(
			MarkdownDocument document) {
		// Fragments (selection ranges) are not supported in the first version
		return null;
	}

	// ------------------------------------------------------------------
	// Document creation
	// ------------------------------------------------------------------

	/** Create a new empty {@link MarkdownDocument}. */
	public MarkdownDocument makeNewMarkdownDocument() {
		return makeDocument();
	}

	/**
	 * Parse raw Markdown text and build a {@link MarkdownDocument}.
	 */
	public MarkdownDocument makeNewMarkdownDocument(String rawMarkdown) {
		Document ast = PARSER.parse(rawMarkdown != null ? rawMarkdown : "");
		MarkdownDocument doc = makeDocument();
		doc.updateFromMarkdownDocument(ast, this);
		return doc;
	}

	/**
	 * Build a {@link MarkdownDocument} from a pre-parsed flexmark {@link Document} AST.
	 */
	public MarkdownDocument makeNewMarkdownDocument(Document ast) {
		MarkdownDocument doc = makeDocument();
		doc.updateFromMarkdownDocument(ast, this);
		return doc;
	}

	// ------------------------------------------------------------------
	// Element factories
	// ------------------------------------------------------------------

	public MarkdownHeading makeNewMarkdownHeading(Heading node) {
		MarkdownHeading heading = newInstance(MarkdownHeading.class);
		((MarkdownHeading.MarkdownHeadingImpl) heading)._factory = this;
		heading.setIdentifier(generateId());
		if (node != null) {
			((MarkdownHeading.MarkdownHeadingImpl) heading).setHeadingNode(node);
			heading.setLevel(node.getLevel());
			MarkdownTextRun run = newInstance(MarkdownTextRun.class);
			run.setText(node.getText().toString());
			heading.addToRuns(run);
		}
		return heading;
	}

	public MarkdownParagraph makeNewMarkdownParagraph(Paragraph node) {
		MarkdownParagraph paragraph = newInstance(MarkdownParagraph.class);
		((MarkdownParagraph.MarkdownParagraphImpl) paragraph)._factory = this;
		paragraph.setIdentifier(generateId());
		if (node != null) {
			((MarkdownParagraph.MarkdownParagraphImpl) paragraph).setParagraphNode(node);
			MarkdownTextRun run = newInstance(MarkdownTextRun.class);
			run.setText(node.getChars().toString());
			paragraph.addToRuns(run);
		}
		return paragraph;
	}

	public MarkdownCodeBlock makeNewMarkdownCodeBlock(Node node) {
		MarkdownCodeBlock cb = newInstance(MarkdownCodeBlock.class);
		cb.setIdentifier(generateId());
		if (node instanceof FencedCodeBlock) {
			FencedCodeBlock fenced = (FencedCodeBlock) node;
			cb.setContent(fenced.getContentChars().toString());
			String info = fenced.getInfo().toString();
			cb.setLanguage(info.isEmpty() ? null : info);
		}
		else if (node instanceof IndentedCodeBlock) {
			cb.setContent(((IndentedCodeBlock) node).getContentChars().toString());
			cb.setLanguage(null);
		}
		((MarkdownCodeBlock.MarkdownCodeBlockImpl) cb).setCodeBlockNode(node);
		return cb;
	}

	// ------------------------------------------------------------------
	// Named style creation
	// ------------------------------------------------------------------

	/**
	 * Create a named style for a Markdown heading level and set its properties.
	 */
	public MarkdownNamedStyle makeMarkdownNamedStyle(String styleId, String name, int level) {
		MarkdownNamedStyle style = newInstance(MarkdownNamedStyle.class);
		style.setStyleId(styleId);
		style.setName(name);
		style.setLevel(level);
		return style;
	}

	// ------------------------------------------------------------------
	// Parser access
	// ------------------------------------------------------------------

	/** Parse raw Markdown text and return the flexmark {@link Document} AST. */
	public Document parseMarkdown(String rawMarkdown) {
		return PARSER.parse(rawMarkdown != null ? rawMarkdown : "");
	}
}
