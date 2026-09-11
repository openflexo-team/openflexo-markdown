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

package org.openflexo.technologyadapter.markdown.fml;

import java.lang.reflect.Type;

import org.openflexo.foundation.doc.fml.FlexoParagraphRole;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.technologyadapter.markdown.model.MarkdownHeading;

/**
 * A {@link FlexoParagraphRole} that binds an FML concept property to a {@link MarkdownHeading}
 * inside a {@link MarkdownDocument}.
 * <p>
 * The heading is identified by its PAMELA identifier ({@code md-N}) so it survives
 * save/reload cycles.
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(MarkdownHeadingRole.MarkdownHeadingRoleImpl.class)
@XMLElement
@FML("MarkdownHeadingRole")
public interface MarkdownHeadingRole
		extends FlexoParagraphRole<MarkdownHeading, MarkdownDocument, MarkdownTechnologyAdapter> {

	abstract class MarkdownHeadingRoleImpl
			extends FlexoParagraphRoleImpl<MarkdownHeading, MarkdownDocument, MarkdownTechnologyAdapter>
			implements MarkdownHeadingRole {

		@Override
		public Type getType() {
			return MarkdownHeading.class;
		}

		@Override
		public RoleCloningStrategy defaultCloningStrategy() {
			return RoleCloningStrategy.Clone;
		}

		@Override
		public boolean defaultBehaviourIsToBeDeleted() {
			return true;
		}

		@Override
		public Class<? extends TechnologyAdapter> getRoleTechnologyAdapterClass() {
			return MarkdownTechnologyAdapter.class;
		}
	}
}
