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

package org.openflexo.technologyadapter.markdown;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.doc.fml.FlexoDocumentModelSlot;
import org.openflexo.foundation.doc.fml.ParagraphActorReference;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.annotations.DeclareActorReferences;
import org.openflexo.foundation.fml.annotations.DeclareEditionActions;
import org.openflexo.foundation.fml.annotations.DeclareFetchRequests;
import org.openflexo.foundation.fml.annotations.DeclareFlexoRoles;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.markdown.fml.MarkdownCodeBlockActorReference;
import org.openflexo.technologyadapter.markdown.fml.MarkdownCodeBlockRole;
import org.openflexo.technologyadapter.markdown.fml.MarkdownHeadingRole;
import org.openflexo.technologyadapter.markdown.fml.MarkdownParagraphRole;
import org.openflexo.technologyadapter.markdown.fml.editionaction.AddMarkdownCodeBlock;
import org.openflexo.technologyadapter.markdown.fml.editionaction.AddMarkdownHeading;
import org.openflexo.technologyadapter.markdown.fml.editionaction.AddMarkdownParagraph;
import org.openflexo.technologyadapter.markdown.fml.editionaction.CreateMarkdownDocumentResource;
import org.openflexo.technologyadapter.markdown.fml.editionaction.SelectMarkdownHeading;
import org.openflexo.technologyadapter.markdown.fml.editionaction.SelectMarkdownParagraph;
import org.openflexo.technologyadapter.markdown.fml.editionaction.SelectUniqueMarkdownHeading;
import org.openflexo.technologyadapter.markdown.fml.editionaction.SelectUniqueMarkdownParagraph;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResource;

/**
 * ModelSlot for connecting a Markdown document to an FML virtual model.
 *
 * @author sylvain
 */
@DeclareFlexoRoles({ MarkdownHeadingRole.class, MarkdownParagraphRole.class, MarkdownCodeBlockRole.class })
@DeclareActorReferences({ ParagraphActorReference.class, MarkdownCodeBlockActorReference.class })
@DeclareEditionActions({ AddMarkdownHeading.class, AddMarkdownParagraph.class, AddMarkdownCodeBlock.class,
		CreateMarkdownDocumentResource.class })
@DeclareFetchRequests({ SelectMarkdownHeading.class, SelectUniqueMarkdownHeading.class,
		SelectMarkdownParagraph.class, SelectUniqueMarkdownParagraph.class })
@ModelEntity
@ImplementationClass(MarkdownModelSlot.MarkdownModelSlotImpl.class)
@XMLElement
@FML("MDModelSlot")
public interface MarkdownModelSlot extends FlexoDocumentModelSlot<MarkdownDocument, MarkdownDocumentResource, MarkdownTechnologyAdapter> {

	abstract class MarkdownModelSlotImpl
			extends FlexoDocumentModelSlot.FlexoDocumentModelSlotImpl<MarkdownDocument, MarkdownDocumentResource, MarkdownTechnologyAdapter>
			implements MarkdownModelSlot {

		private static final Logger logger = Logger.getLogger(MarkdownModelSlot.class.getPackage().getName());

		@Override
		public Class<MarkdownTechnologyAdapter> getTechnologyAdapterClass() {
			return MarkdownTechnologyAdapter.class;
		}

		@Override
		public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> patternRoleClass) {
			if (MarkdownHeadingRole.class.isAssignableFrom(patternRoleClass)) {
				return "heading";
			}
			if (MarkdownParagraphRole.class.isAssignableFrom(patternRoleClass)) {
				return "paragraph";
			}
			if (MarkdownCodeBlockRole.class.isAssignableFrom(patternRoleClass)) {
				return "codeBlock";
			}
			return "document";
		}

		@Override
		public Type getType() {
			return MarkdownDocument.class;
		}

		@Override
		public MarkdownTechnologyAdapter getModelSlotTechnologyAdapter() {
			return (MarkdownTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}

		@Override
		public String getURIForObject(MarkdownDocument resourceData, Object o) {
			// TODO
			return null;
		}

		@Override
		public Object retrieveObjectWithURI(MarkdownDocument resourceData, String objectURI) {
			// TODO
			return null;
		}
	}
}
