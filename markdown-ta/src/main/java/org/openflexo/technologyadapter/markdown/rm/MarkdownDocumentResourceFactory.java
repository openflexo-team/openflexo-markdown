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

package org.openflexo.technologyadapter.markdown.rm;

import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.TechnologySpecificPamelaResourceFactory;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.technologyadapter.markdown.model.MarkdownFactory;

/**
 * Factory responsible for building and registering {@link MarkdownDocumentResource} instances.
 *
 * @author sylvain
 */
public class MarkdownDocumentResourceFactory
		extends TechnologySpecificPamelaResourceFactory<MarkdownDocumentResource, MarkdownDocument, MarkdownTechnologyAdapter, MarkdownFactory> {

	private static final Logger logger = Logger.getLogger(MarkdownDocumentResourceFactory.class.getPackage().getName());

	public static final String MD_FILE_EXTENSION = ".md";

	public MarkdownDocumentResourceFactory() throws ModelDefinitionException {
		super(MarkdownDocumentResource.class);
	}

	@Override
	public MarkdownFactory makeModelFactory(MarkdownDocumentResource resource,
			TechnologyContextManager<MarkdownTechnologyAdapter> technologyContextManager)
			throws ModelDefinitionException {
		return new MarkdownFactory(resource, technologyContextManager.getServiceManager().getEditingContext());
	}

	@Override
	public MarkdownDocument makeEmptyResourceData(MarkdownDocumentResource resource) {
		return resource.getFactory().makeNewMarkdownDocument();
	}

	@Override
	public <I> boolean isValidArtefact(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		return resourceCenter.retrieveName(serializationArtefact).endsWith(MD_FILE_EXTENSION);
	}

	@Override
	public <I> MarkdownDocumentResource registerResource(MarkdownDocumentResource resource,
			FlexoResourceCenter<I> resourceCenter) {
		super.registerResource(resource, resourceCenter);

		// Register in the MarkdownDocumentRepository of this resource center
		registerResourceInResourceRepository(resource,
				getTechnologyAdapter(resourceCenter.getServiceManager()).getMarkdownDocumentRepository(resourceCenter));

		return resource;
	}
}
