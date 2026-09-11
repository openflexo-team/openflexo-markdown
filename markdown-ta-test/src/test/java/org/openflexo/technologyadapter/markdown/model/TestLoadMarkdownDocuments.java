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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.technologyadapter.markdown.AbstractTestMarkdown;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentRepository;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Verifies that all {@code .md} files in the test resource center can be discovered
 * and loaded without errors.
 *
 * @author sylvain
 */
@RunWith(OrderedRunner.class)
public class TestLoadMarkdownDocuments extends AbstractTestMarkdown {

	private static final Logger logger = Logger.getLogger(TestLoadMarkdownDocuments.class.getPackage().getName());

	@Test
	@TestOrder(1)
	public void testInitializeServiceManager() throws Exception {
		instanciateTestServiceManagerForMarkdown();
		assertNotNull(serviceManager);
		assertNotNull(serviceManager.getTechnologyAdapterService().getTechnologyAdapter(MarkdownTechnologyAdapter.class));
	}

	@Test
	@TestOrder(2)
	public void testMarkdownResourceCenterIsPresent() {
		FlexoResourceCenter<?> rc = serviceManager.getResourceCenterService().getFlexoResourceCenter(TEST_RC_URI);
		assertNotNull("Markdown test resource center not found (URI=" + TEST_RC_URI + ")", rc);
		logger.info("Resource center found: " + rc.getDefaultBaseURI());
	}

	@Test
	@TestOrder(3)
	public void testDiscoverAllMarkdownDocuments() {
		MarkdownTechnologyAdapter ta = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(MarkdownTechnologyAdapter.class);

		int total = 0;
		for (FlexoResourceCenter<?> rc : serviceManager.getResourceCenterService().getResourceCenters()) {
			MarkdownDocumentRepository<?> repo = ta.getMarkdownDocumentRepository(rc);
			assertNotNull(repo);
			Collection<MarkdownDocumentResource> resources = repo.getAllResources();
			for (MarkdownDocumentResource r : resources) {
				logger.info("Found Markdown resource: " + r.getURI());
				total++;
			}
		}
		// We have 5 .md files in the test resource center (EmptyDocument counts too)
		assertTrue("Expected at least 5 Markdown resources, got " + total, total >= 5);
	}

	@Test
	@TestOrder(4)
	public void testLoadAllMarkdownDocuments() {
		MarkdownTechnologyAdapter ta = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(MarkdownTechnologyAdapter.class);

		for (FlexoResourceCenter<?> rc : serviceManager.getResourceCenterService().getResourceCenters()) {
			MarkdownDocumentRepository<?> repo = ta.getMarkdownDocumentRepository(rc);
			for (MarkdownDocumentResource resource : repo.getAllResources()) {
				try {
					resource.loadResourceData();
				}
				catch (FileNotFoundException | ResourceLoadingCancelledException | FlexoException e) {
					e.printStackTrace();
				}
				assertNotNull("Resource failed to load: " + resource.getURI(), resource.getLoadedResourceData());
				logger.info("Loaded: " + resource.getURI());
			}
		}
	}

	@Test
	@TestOrder(5)
	public void testSimpleDocumentLoading() {
		MarkdownDocument doc = getDocument("SimpleDocument.md");
		assertNotNull(doc);
		assertFalse("SimpleDocument must have elements", doc.getElements().isEmpty());
		logger.info("SimpleDocument:\n" + doc.debugStructuredContents());
	}

	@Test
	@TestOrder(6)
	public void testEmptyDocumentLoading() {
		MarkdownDocument doc = getDocument("EmptyDocument.md");
		assertNotNull(doc);
		assertTrue("EmptyDocument must have no elements", doc.getElements().isEmpty());
		assertTrue("EmptyDocument must have no root elements", doc.getRootElements().isEmpty());
	}

	@Test
	@TestOrder(7)
	public void testUnloadAllDocuments() {
		MarkdownTechnologyAdapter ta = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(MarkdownTechnologyAdapter.class);

		for (FlexoResourceCenter<?> rc : serviceManager.getResourceCenterService().getResourceCenters()) {
			MarkdownDocumentRepository<?> repo = ta.getMarkdownDocumentRepository(rc);
			for (MarkdownDocumentResource resource : repo.getAllResources()) {
				resource.unloadResourceData(false);
			}
		}
	}
}
