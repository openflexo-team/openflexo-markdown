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

package org.openflexo.technologyadapter.markdown.controller;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.editionaction.EditionAction;
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
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.gina.utils.InspectorGroup;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.technologyadapter.markdown.gui.MarkdownIconLibrary;
import org.openflexo.technologyadapter.markdown.gui.view.MarkdownDocumentModuleView;
import org.openflexo.technologyadapter.markdown.model.MarkdownCodeBlock;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.technologyadapter.markdown.model.MarkdownHeading;
import org.openflexo.technologyadapter.markdown.model.MarkdownParagraph;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * {@link TechnologyAdapterController} for the Markdown technology adapter.
 * <p>
 * Provides icons, module views, and inspector configuration for Markdown objects.
 *
 * @author sylvain
 */
public class MarkdownAdapterController extends TechnologyAdapterController<MarkdownTechnologyAdapter> {

	static final Logger logger = Logger.getLogger(MarkdownAdapterController.class.getPackage().getName());

	@Override
	public Class<MarkdownTechnologyAdapter> getTechnologyAdapterClass() {
		return MarkdownTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {
		// No custom actions in the first version
	}

	// ------------------------------------------------------------------
	// Icons
	// ------------------------------------------------------------------

	@Override
	public ImageIcon getTechnologyBigIcon() {
		return MarkdownIconLibrary.MARKDOWN_TECHNOLOGY_BIG_ICON;
	}

	@Override
	public ImageIcon getTechnologyIcon() {
		return MarkdownIconLibrary.MARKDOWN_TECHNOLOGY_ICON;
	}

	@Override
	public ImageIcon getModelIcon() {
		return MarkdownIconLibrary.MARKDOWN_FILE_ICON;
	}

	@Override
	public ImageIcon getMetaModelIcon() {
		return MarkdownIconLibrary.MARKDOWN_FILE_ICON;
	}

	@Override
	public ImageIcon getIconForTechnologyObject(TechnologyObject<?> object) {
		if (object instanceof MarkdownHeading) {
			return MarkdownIconLibrary.HEADING_ICON;
		}
		if (object instanceof MarkdownParagraph) {
			return MarkdownIconLibrary.PARAGRAPH_ICON;
		}
		if (object instanceof MarkdownCodeBlock) {
			return MarkdownIconLibrary.CODE_BLOCK_ICON;
		}
		if (object instanceof MarkdownDocument) {
			return MarkdownIconLibrary.MARKDOWN_FILE_ICON;
		}
		return super.getIconForTechnologyObject(object);
	}

	@Override
	public ImageIcon getIconForTechnologyObject(Class<? extends TechnologyObject<?>> objectClass) {
		if (MarkdownDocument.class.isAssignableFrom(objectClass)) {
			return MarkdownIconLibrary.MARKDOWN_FILE_ICON;
		}
		if (MarkdownHeading.class.isAssignableFrom(objectClass)) {
			return MarkdownIconLibrary.HEADING_ICON;
		}
		if (MarkdownParagraph.class.isAssignableFrom(objectClass)) {
			return MarkdownIconLibrary.PARAGRAPH_ICON;
		}
		if (MarkdownCodeBlock.class.isAssignableFrom(objectClass)) {
			return MarkdownIconLibrary.CODE_BLOCK_ICON;
		}
		return null;
	}

	@Override
	public ImageIcon getIconForEditionAction(Class<? extends EditionAction> editionActionClass) {
		if (AddMarkdownHeading.class.isAssignableFrom(editionActionClass)) {
			return MarkdownIconLibrary.HEADING_ICON;
		}
		if (SelectMarkdownHeading.class.isAssignableFrom(editionActionClass)
				|| SelectUniqueMarkdownHeading.class.isAssignableFrom(editionActionClass)) {
			return MarkdownIconLibrary.HEADING_ICON;
		}
		if (AddMarkdownParagraph.class.isAssignableFrom(editionActionClass)) {
			return MarkdownIconLibrary.PARAGRAPH_ICON;
		}
		if (SelectMarkdownParagraph.class.isAssignableFrom(editionActionClass)
				|| SelectUniqueMarkdownParagraph.class.isAssignableFrom(editionActionClass)) {
			return MarkdownIconLibrary.PARAGRAPH_ICON;
		}
		if (AddMarkdownCodeBlock.class.isAssignableFrom(editionActionClass)) {
			return MarkdownIconLibrary.CODE_BLOCK_ICON;
		}
		if (CreateMarkdownDocumentResource.class.isAssignableFrom(editionActionClass)) {
			return MarkdownIconLibrary.MARKDOWN_FILE_ICON;
		}
		return super.getIconForEditionAction(editionActionClass);
	}

	@Override
	public ImageIcon getIconForFlexoRole(Class<? extends FlexoRole<?>> roleClass) {
		if (MarkdownHeadingRole.class.isAssignableFrom(roleClass)) {
			return MarkdownIconLibrary.HEADING_ICON;
		}
		if (MarkdownParagraphRole.class.isAssignableFrom(roleClass)) {
			return MarkdownIconLibrary.PARAGRAPH_ICON;
		}
		if (MarkdownCodeBlockRole.class.isAssignableFrom(roleClass)) {
			return MarkdownIconLibrary.CODE_BLOCK_ICON;
		}
		return null;
	}

	// ------------------------------------------------------------------
	// Module view
	// ------------------------------------------------------------------

	@Override
	public boolean isRepresentableInModuleView(TechnologyObject<MarkdownTechnologyAdapter> object) {
		return object instanceof MarkdownDocument;
	}

	@Override
	public FlexoObject getRepresentableMasterObject(TechnologyObject<MarkdownTechnologyAdapter> object) {
		return object;
	}

	@Override
	public ModuleView<?> createModuleViewForMasterObject(TechnologyObject<MarkdownTechnologyAdapter> object,
			FlexoController controller, FlexoPerspective perspective) {
		if (object instanceof MarkdownDocument) {
			return new MarkdownDocumentModuleView((MarkdownDocument) object, controller, perspective);
		}
		return null;
	}

	@Override
	public String getWindowTitleforObject(TechnologyObject<MarkdownTechnologyAdapter> object, FlexoController controller) {
		if (object instanceof MarkdownDocument) {
			return ((MarkdownDocument) object).getName();
		}
		return null;
	}

	// ------------------------------------------------------------------
	// Inspectors
	// ------------------------------------------------------------------

	private InspectorGroup markdownInspectorGroup;

	@Override
	protected void initializeInspectors(FlexoController controller) {
		markdownInspectorGroup = controller.loadInspectorGroup("Markdown", getTechnologyAdapter().getLocales(),
				getFMLTechnologyAdapterInspectorGroup());
	}

	@Override
	public InspectorGroup getTechnologyAdapterInspectorGroup() {
		return markdownInspectorGroup;
	}
}
