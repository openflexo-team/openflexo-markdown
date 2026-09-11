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

import com.vladsch.flexmark.ast.Heading;

import org.openflexo.foundation.doc.FlexoDocParagraph;
import org.openflexo.foundation.doc.FlexoDrawingRun;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;

/**
 * Represents a Markdown ATX heading ({@code # H1}, {@code ## H2}, …, {@code ###### H6}).
 * <p>
 * A heading is modelled as a specialised paragraph so it integrates naturally with
 * the {@link org.openflexo.foundation.doc.FlexoDocument} structuring-style mechanism.
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(MarkdownHeading.MarkdownHeadingImpl.class)
@XMLElement
@Imports({ @Import(MarkdownTextRun.class) })
public interface MarkdownHeading extends FlexoDocParagraph<MarkdownDocument, MarkdownTechnologyAdapter> {

	// getIdentifier() / setIdentifier() are inherited from FlexoDocElement

	@PropertyIdentifier(type = Integer.class)
	String LEVEL_KEY = "level";

	/**
	 * Returns the heading level (1 = H1 … 6 = H6).
	 */
	@Getter(value = LEVEL_KEY, defaultValue = "1")
	@XMLAttribute
	int getLevel();

	@Setter(LEVEL_KEY)
	void setLevel(int level);

	// -----------------------------------------------------------------------
	// Implementation
	// -----------------------------------------------------------------------

	abstract class MarkdownHeadingImpl
			extends FlexoDocParagraph.FlexoDocParagraphImpl<MarkdownDocument, MarkdownTechnologyAdapter>
			implements MarkdownHeading {

		/** The underlying flexmark AST node – not serialised by PAMELA. */
		private Heading heading;

		/** Fallback factory used when the element has not yet been added to a document. */
		MarkdownFactory _factory;

		public Heading getHeadingNode() {
			return heading;
		}

		public void setHeadingNode(Heading h) {
			this.heading = h;
		}

		@Override
		public String getRawText() {
			if (heading != null) {
				return heading.getText().toString();
			}
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
			return "MarkdownHeading(H" + getLevel() + ", " + getIdentifier() + ") " + getRawText();
		}
	}
}
