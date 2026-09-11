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

import java.util.HashMap;
import java.util.Map;

import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResource;

/**
 * Context manager for the Markdown technology adapter.
 * <p>
 * Keeps a registry of all known {@link MarkdownDocumentResource} instances indexed by URI.
 *
 * @author sylvain
 */
public class MarkdownTechnologyContextManager extends TechnologyContextManager<MarkdownTechnologyAdapter> {

	/** Stores all known Markdown document resources indexed by URI. */
	protected final Map<String, MarkdownDocumentResource> markdownDocuments = new HashMap<>();

	public MarkdownTechnologyContextManager(MarkdownTechnologyAdapter adapter,
			FlexoResourceCenterService resourceCenterService) {
		super(adapter, resourceCenterService);
	}

	public MarkdownDocumentResource getMarkdownDocumentResource(String uri) {
		return markdownDocuments.get(uri);
	}

	public void registerMarkdownDocumentResource(MarkdownDocumentResource resource) {
		registerResource(resource);
		markdownDocuments.put(resource.getURI(), resource);
	}
}
