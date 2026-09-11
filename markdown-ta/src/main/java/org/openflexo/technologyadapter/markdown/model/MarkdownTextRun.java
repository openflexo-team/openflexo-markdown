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

import org.openflexo.foundation.doc.FlexoTextRun;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;

/**
 * A simple inline text run for Markdown paragraphs.
 * <p>
 * Extends {@link FlexoTextRun} which already provides the {@code text} property.
 * In the first version each paragraph/heading is backed by exactly one run.
 * Inline formatting (bold, italic, code spans…) can be introduced in a future version.
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(MarkdownTextRun.MarkdownTextRunImpl.class)
@XMLElement
public interface MarkdownTextRun extends FlexoTextRun<MarkdownDocument, MarkdownTechnologyAdapter> {

	// getText() / setText() are inherited from FlexoTextRun (property key "text")

	// -----------------------------------------------------------------------
	// Implementation
	// -----------------------------------------------------------------------

	abstract class MarkdownTextRunImpl extends FlexoTextRun.FlexoTextRunImpl<MarkdownDocument, MarkdownTechnologyAdapter>
			implements MarkdownTextRun {

		@Override
		public String getRawText() {
			return getText() != null ? getText() : "";
		}
	}
}
