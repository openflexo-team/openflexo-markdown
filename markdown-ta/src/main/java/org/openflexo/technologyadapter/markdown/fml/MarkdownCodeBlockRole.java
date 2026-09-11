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

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstanceModelFactory;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.technologyadapter.markdown.model.MarkdownCodeBlock;

/**
 * A {@link FlexoRole} that binds an FML concept property to a {@link MarkdownCodeBlock}.
 * <p>
 * Unlike headings and paragraphs, a code block is not a {@code FlexoDocParagraph}, so this
 * role extends {@link FlexoRole} directly and uses a dedicated
 * {@link MarkdownCodeBlockActorReference} for persistence.
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(MarkdownCodeBlockRole.MarkdownCodeBlockRoleImpl.class)
@XMLElement
@FML("MarkdownCodeBlockRole")
public interface MarkdownCodeBlockRole extends FlexoRole<MarkdownCodeBlock> {

	abstract class MarkdownCodeBlockRoleImpl extends FlexoRoleImpl<MarkdownCodeBlock>
			implements MarkdownCodeBlockRole {

		@Override
		public Type getType() {
			return MarkdownCodeBlock.class;
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

		@Override
		@SuppressWarnings("rawtypes")
		public ActorReference<MarkdownCodeBlock> makeActorReference(MarkdownCodeBlock object, FlexoConceptInstance fci) {
			AbstractVirtualModelInstanceModelFactory factory = fci.getFactory();
			MarkdownCodeBlockActorReference ref = factory.newInstance(MarkdownCodeBlockActorReference.class);
			ref.setFlexoRole(this);
			ref.setFlexoConceptInstance(fci);
			ref.setModellingElement(object);
			return ref;
		}
	}
}
