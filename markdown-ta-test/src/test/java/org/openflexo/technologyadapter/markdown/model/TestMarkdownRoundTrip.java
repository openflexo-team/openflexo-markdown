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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.technologyadapter.markdown.AbstractTestMarkdown;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Verifies the parse → serialise round-trip:
 * element counts and content survive an unload/reload cycle.
 *
 * @author sylvain
 */
@RunWith(OrderedRunner.class)
public class TestMarkdownRoundTrip extends AbstractTestMarkdown {

	private static final Logger logger = Logger.getLogger(TestMarkdownRoundTrip.class.getPackage().getName());

	@Test
	@TestOrder(1)
	public void testInitializeServiceManager() throws Exception {
		instanciateTestServiceManagerForMarkdown();
	}

	// -----------------------------------------------------------------------
	// In-memory parse round-trip via MarkdownFactory
	// -----------------------------------------------------------------------

	@Test
	@TestOrder(2)
	public void testInMemoryParseRoundTrip() {
		String rawMarkdown =
				"# Hello\n\n" +
				"A paragraph.\n\n" +
				"## World\n\n" +
				"Another paragraph.\n\n" +
				"```python\nprint('hi')\n```\n";

		MarkdownDocumentResource resource = getDocumentResource("SimpleDocument.md");
		MarkdownDocument doc = resource.getFactory().makeNewMarkdownDocument(rawMarkdown);

		assertNotNull(doc);

		int headings = 0, paragraphs = 0, codeBlocks = 0;
		for (org.openflexo.foundation.doc.FlexoDocElement<?, ?> e : doc.getElements()) {
			if (e instanceof MarkdownHeading)   headings++;
			if (e instanceof MarkdownParagraph) paragraphs++;
			if (e instanceof MarkdownCodeBlock) codeBlocks++;
		}
		assertEquals(2, headings);
		assertEquals(2, paragraphs);
		assertEquals(1, codeBlocks);

		MarkdownHeading h1 = (MarkdownHeading) doc.getElements().get(0);
		assertEquals(1, h1.getLevel());
		assertEquals("Hello", h1.getRawText());

		MarkdownHeading h2 = (MarkdownHeading) doc.getElements().get(2);
		assertEquals(2, h2.getLevel());
		assertEquals("World", h2.getRawText());

		MarkdownCodeBlock cb = (MarkdownCodeBlock) doc.getElements().get(4);
		assertEquals("python", cb.getLanguage());
		assertTrue(cb.getContent().contains("print"));
	}

	// -----------------------------------------------------------------------
	// Unload/reload round-trip (resource-level)
	// -----------------------------------------------------------------------

	@Test
	@TestOrder(3)
	public void testUnloadReloadElementCount() {
		MarkdownDocumentResource resource = getDocumentResource("SimpleDocument.md");

		// Load
		MarkdownDocument doc1 = getDocument("SimpleDocument.md");
		int count1 = doc1.getElements().size();
		assertTrue(count1 > 0);

		// Unload
		resource.unloadResourceData(false);

		// Reload
		MarkdownDocument doc2 = getDocument("SimpleDocument.md");
		assertEquals("Element count must be identical after unload/reload", count1, doc2.getElements().size());
	}

	@Test
	@TestOrder(4)
	public void testUnloadReloadCodeBlocks() {
		MarkdownDocumentResource resource = getDocumentResource("DocumentWithCodeBlocks.md");

		MarkdownDocument doc1 = getDocument("DocumentWithCodeBlocks.md");
		long cbCount1 = doc1.getElements().stream().filter(e -> e instanceof MarkdownCodeBlock).count();
		resource.unloadResourceData(false);

		MarkdownDocument doc2 = getDocument("DocumentWithCodeBlocks.md");
		long cbCount2 = doc2.getElements().stream().filter(e -> e instanceof MarkdownCodeBlock).count();

		assertEquals("Code block count must be stable after reload", cbCount1, cbCount2);
	}

	// -----------------------------------------------------------------------
	// File-based save/reload
	// -----------------------------------------------------------------------

	@Test
	@TestOrder(5)
	public void testSaveAndReloadDocument() throws Exception {
		MarkdownTechnologyAdapter ta = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(MarkdownTechnologyAdapter.class);

		org.openflexo.foundation.resource.DirectoryResourceCenter newRc = makeNewDirectoryResourceCenter();

		org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResource newResource =
				ta.createNewMarkdownDocumentResource(newRc, "", "RoundTripTest.md", false);
		assertNotNull("Resource creation must succeed", newResource);

		// Build content
		org.openflexo.technologyadapter.markdown.model.MarkdownFactory factory = newResource.getFactory();
		MarkdownDocument doc = factory.makeNewMarkdownDocument(
				"# Round Trip\n\nContent preserved.\n\n## Section\n\n```\ncode\n```\n");
		newResource.setResourceData(doc);
		doc.setResource(newResource);

		// Save
		newResource.save();

		// Unload and reload
		newResource.unloadResourceData(false);
		MarkdownDocument reloaded = newResource.getResourceData();
		assertNotNull("Document must reload from disk", reloaded);

		assertEquals("Element count must survive save/reload",
				doc.getElements().size(), reloaded.getElements().size());

		MarkdownHeading h1 = (MarkdownHeading) reloaded.getElements().get(0);
		assertEquals(1, h1.getLevel());
		assertEquals("Round Trip", h1.getRawText());
	}
}
