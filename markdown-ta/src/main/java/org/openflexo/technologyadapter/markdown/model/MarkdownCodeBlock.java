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

import java.util.Collections;
import java.util.List;

import com.vladsch.flexmark.util.ast.Node;

import org.openflexo.foundation.doc.FlexoDocElement;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;

/**
 * Represents a Markdown fenced or indented code block.
 * <pre>
 * ```java
 * System.out.println("hello");
 * ```
 * </pre>
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(MarkdownCodeBlock.MarkdownCodeBlockImpl.class)
@XMLElement
public interface MarkdownCodeBlock extends FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter> {

	// getIdentifier() / setIdentifier() are inherited from FlexoDocElement

	@PropertyIdentifier(type = String.class)
	String LANGUAGE_KEY = "language";

	@PropertyIdentifier(type = String.class)
	String CONTENT_KEY = "content";

	/** The info/language string for fenced code blocks (e.g. "java", "python"). May be null. */
	@Getter(LANGUAGE_KEY)
	@XMLAttribute
	String getLanguage();

	@Setter(LANGUAGE_KEY)
	void setLanguage(String language);

	/** The verbatim code content (without the surrounding fence markers). */
	@Getter(CONTENT_KEY)
	@XMLAttribute
	String getContent();

	@Setter(CONTENT_KEY)
	void setContent(String content);

	// -----------------------------------------------------------------------
	// Implementation
	// -----------------------------------------------------------------------

	abstract class MarkdownCodeBlockImpl
			extends FlexoDocElement.FlexoDocumentElementImpl<MarkdownDocument, MarkdownTechnologyAdapter>
			implements MarkdownCodeBlock {

		/** The underlying flexmark AST node – not serialised by PAMELA. */
		private Node codeBlockNode;

		public Node getCodeBlockNode() {
			return codeBlockNode;
		}

		public void setCodeBlockNode(Node node) {
			this.codeBlockNode = node;
		}

		@Override
		public List<FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter>> getChildrenElements() {
			return Collections.emptyList();
		}

		@Override
		public void invalidateChildrenElements() {
			// code blocks have no children
		}

		@Override
		public void notifyChildrenElementsChanged() {
			// no-op
		}

		@Override
		public String toString() {
			String lang = getLanguage() != null ? getLanguage() : "";
			return "MarkdownCodeBlock(" + getIdentifier() + ", lang=" + lang + ")";
		}
	}
}
