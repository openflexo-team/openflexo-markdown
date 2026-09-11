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

import java.util.logging.Logger;

import org.openflexo.foundation.fml.annotations.DeclareModelSlots;
import org.openflexo.foundation.fml.annotations.DeclareResourceFactories;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentRepository;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResource;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResourceFactory;

/**
 * Technology adapter for Markdown (.md) documents.
 * <p>
 * Provides support for reading and writing Markdown files inside OpenFlexo, using
 * the flexmark-java library for parsing and rendering.
 *
 * @author sylvain
 */
@DeclareModelSlots({ MarkdownModelSlot.class })
@DeclareResourceFactories({ MarkdownDocumentResourceFactory.class })
public class MarkdownTechnologyAdapter extends TechnologyAdapter<MarkdownTechnologyAdapter> {

	protected static final Logger logger = Logger.getLogger(MarkdownTechnologyAdapter.class.getPackage().getName());

	public MarkdownTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "Markdown Technology Adapter";
	}

	@Override
	public String getIdentifier() {
		return "MARKDOWN";
	}

	@Override
	protected String getLocalizationDirectory() {
		return "FlexoLocalization/MarkdownTechnologyAdapter";
	}

	@Override
	public MarkdownTechnologyContextManager createTechnologyContextManager(FlexoResourceCenterService service) {
		return new MarkdownTechnologyContextManager(this,
				getTechnologyAdapterService().getServiceManager().getResourceCenterService());
	}

	@Override
	public MarkdownTechnologyContextManager getTechnologyContextManager() {
		return (MarkdownTechnologyContextManager) super.getTechnologyContextManager();
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		return null;
	}

	@Override
	public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
		return false;
	}

	/**
	 * Return (or lazily create) the {@link MarkdownDocumentRepository} for the given resource center.
	 */
	public <I> MarkdownDocumentRepository<I> getMarkdownDocumentRepository(FlexoResourceCenter<I> resourceCenter) {
		MarkdownDocumentRepository<I> returned = resourceCenter.retrieveRepository(MarkdownDocumentRepository.class, this);
		if (returned == null) {
			returned = MarkdownDocumentRepository.instanciateNewRepository(this, resourceCenter);
			resourceCenter.registerRepository(returned, MarkdownDocumentRepository.class, this);
		}
		return returned;
	}

	public MarkdownDocumentResourceFactory getMarkdownDocumentResourceFactory() {
		return getResourceFactory(MarkdownDocumentResourceFactory.class);
	}

	/**
	 * Convenience method to create a new {@link MarkdownDocumentResource} in a given resource center.
	 *
	 * @param resourceCenter the target resource center
	 * @param relativePath   sub-path inside the resource center (e.g. "subdir")
	 * @param filename       file name with extension (e.g. "MyDoc.md")
	 * @param createEmptyContents when true, an empty document is immediately saved to disk
	 */
	public <I> org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResource createNewMarkdownDocumentResource(
			org.openflexo.foundation.resource.FlexoResourceCenter<I> resourceCenter,
			String relativePath, String filename, boolean createEmptyContents) {
		try {
			I artefact = resourceCenter.createEntry(filename,
					relativePath != null && !relativePath.isEmpty()
							? resourceCenter.createEntry(relativePath, resourceCenter.getBaseArtefact())
							: resourceCenter.getBaseArtefact());
			return getMarkdownDocumentResourceFactory().makeResource(artefact, resourceCenter, filename, null, createEmptyContents);
		}
		catch (Exception e) {
			logger.warning("Cannot create MarkdownDocumentResource: " + e.getMessage());
			return null;
		}
	}
}
