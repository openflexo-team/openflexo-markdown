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

import com.vladsch.flexmark.ast.Paragraph;

import org.openflexo.foundation.doc.FlexoDocParagraph;
import org.openflexo.foundation.doc.FlexoDrawingRun;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;

/**
 * Represents a regular Markdown paragraph (a block of text separated by blank lines).
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(MarkdownParagraph.MarkdownParagraphImpl.class)
@XMLElement
@Imports({ @Import(MarkdownTextRun.class) })
public interface MarkdownParagraph extends FlexoDocParagraph<MarkdownDocument, MarkdownTechnologyAdapter> {
	// getIdentifier() / setIdentifier() are inherited from FlexoDocElement

	// -----------------------------------------------------------------------
	// Implementation
	// -----------------------------------------------------------------------

	abstract class MarkdownParagraphImpl
			extends FlexoDocParagraph.FlexoDocParagraphImpl<MarkdownDocument, MarkdownTechnologyAdapter>
			implements MarkdownParagraph {

		/** The underlying flexmark AST node – not serialised by PAMELA. */
		private Paragraph paragraph;

		/** Fallback factory used when the element has not yet been added to a document. */
		MarkdownFactory _factory;

		public Paragraph getParagraphNode() {
			return paragraph;
		}

		public void setParagraphNode(Paragraph p) {
			this.paragraph = p;
		}

		@Override
		public String getRawText() {
			if (paragraph != null) {
				return paragraph.getChars().toString();
			}
			// Fall back to the single run if any
			if (!getRuns().isEmpty()) {
				return getRuns().get(0).getRawText();
			}
			return "";
		}

		@Override
		public void setRawText(String someText) {
			if (getRuns().isEmpty()) {
				MarkdownFactory f = (getFlexoDocument() != null) ? getFlexoDocument().getFactory() : _factory;
				MarkdownTextRun run = f.newInstance(MarkdownTextRun.class);
				run.setText(someText);
				addToRuns(run);
			}
			else {
				((MarkdownTextRun) getRuns().get(0)).setText(someText);
			}
			if (paragraph != null) {
				// Note: flexmark AST nodes are immutable after parsing;
				// updating the in-memory text is sufficient for serialisation,
				// which is driven by the model (not the AST).
			}
		}

		@Override
		public void insertRunAtIndex(org.openflexo.foundation.doc.FlexoDocRun<MarkdownDocument, MarkdownTechnologyAdapter> run, int index) {
			performSuperAdder(RUNS_KEY, run, index);
		}

		@Override
		public void moveRunToIndex(org.openflexo.foundation.doc.FlexoDocRun<MarkdownDocument, MarkdownTechnologyAdapter> run, int index) {
			List<org.openflexo.foundation.doc.FlexoDocRun<MarkdownDocument, MarkdownTechnologyAdapter>> runs = getRuns();
			runs.remove(run);
			runs.add(index, run);
		}

		@Override
		public void fireTextChanged() {
			getPropertyChangeSupport().firePropertyChange("rawText", null, getRawText());
		}

		@Override
		public List<FlexoDrawingRun<MarkdownDocument, MarkdownTechnologyAdapter>> getDrawingRuns() {
			return Collections.emptyList();
		}

		@Override
		public String toString() {
			return "MarkdownParagraph(" + getIdentifier() + ") " + getRawText();
		}
	}
}
