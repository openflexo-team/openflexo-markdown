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

import org.openflexo.foundation.doc.NamedDocStyle;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;

/**
 * Concrete implementation of {@link NamedDocStyle} for Markdown documents.
 * <p>
 * Heading styles ("heading 1"…"heading 6") are registered at load time and used
 * by the {@link org.openflexo.foundation.doc.FlexoDocument} structuring-style mechanism
 * to build the document hierarchy.
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(MarkdownNamedStyle.MarkdownNamedStyleImpl.class)
@XMLElement
public interface MarkdownNamedStyle extends NamedDocStyle<MarkdownDocument, MarkdownTechnologyAdapter> {

	abstract class MarkdownNamedStyleImpl extends NamedDocStyle.NamedDocStyleImpl<MarkdownDocument, MarkdownTechnologyAdapter>
			implements MarkdownNamedStyle {

		@Override
		public String toString() {
			return "MarkdownNamedStyle(" + getName() + ", level=" + getLevel() + ")";
		}
	}
}
