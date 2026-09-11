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

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResource;

/**
 * Base class for Markdown technology adapter tests.
 * <p>
 * Boots a full {@link FlexoServiceManager} with the {@link MarkdownTechnologyAdapter} activated,
 * backed by the {@code markdown-ta-test-rc} resource center.
 *
 * @author sylvain
 */
public abstract class AbstractTestMarkdown extends OpenflexoProjectAtRunTimeTestCase {

	protected static final Logger logger = Logger.getLogger(AbstractTestMarkdown.class.getPackage().getName());

	/** URI declared in markdown-ta-test-rc META-INF. */
	protected static final String TEST_RC_URI = "http://openflexo.org/markdown-test";

	protected static final String TEST_RC_FOLDER = "TestResourceCenter";

	/**
	 * Boot a {@link FlexoServiceManager} with the Markdown technology adapter activated.
	 */
	@SuppressWarnings("unchecked")
	protected static FlexoServiceManager instanciateTestServiceManagerForMarkdown() {
		serviceManager = instanciateTestServiceManager(MarkdownTechnologyAdapter.class);
		return serviceManager;
	}

	@AfterClass
	public static void tearDownClass() {
		deleteProject();
		deleteTestResourceCenters();
		unloadServiceManager();
	}

	// ------------------------------------------------------------------
	// Resource lookup helpers
	// ------------------------------------------------------------------

	protected MarkdownDocumentResource getDocumentResource(String documentName) {
		FlexoResourceCenter<?> resourceCenter = serviceManager.getResourceCenterService().getFlexoResourceCenter(TEST_RC_URI);
		assertNotNull("Cannot find resource center " + TEST_RC_URI, resourceCenter);

		String documentURI = resourceCenter.getDefaultBaseURI() + "/" + TEST_RC_FOLDER + "/" + documentName;
		logger.info("Searching " + documentURI);

		MarkdownDocumentResource resource = (MarkdownDocumentResource) serviceManager.getResourceManager()
				.getResource(documentURI, null, MarkdownDocument.class);

		if (resource == null) {
			logger.warning("Cannot find document resource " + documentURI);
			for (FlexoResource<?> r : serviceManager.getResourceManager().getRegisteredResources()) {
				logger.info("  registered: " + r.getURI());
			}
		}
		assertNotNull("Cannot find Markdown resource " + documentName, resource);
		return resource;
	}

	protected MarkdownDocument getDocument(String documentName) {
		MarkdownDocumentResource resource = getDocumentResource(documentName);
		assertNotNull(resource);

		MarkdownDocument document = null;
		try {
			document = resource.getResourceData();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
		}
		catch (FlexoException e) {
			e.printStackTrace();
		}
		assertNotNull("Document " + documentName + " failed to load", document);
		return document;
	}
}
