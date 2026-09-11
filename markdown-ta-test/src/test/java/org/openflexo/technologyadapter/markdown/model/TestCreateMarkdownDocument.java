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
import org.openflexo.foundation.doc.FlexoDocElement;
import org.openflexo.foundation.doc.NamedDocStyle;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.technologyadapter.markdown.AbstractTestMarkdown;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Tests programmatic creation of Markdown documents via the model API.
 *
 * @author sylvain
 */
@RunWith(OrderedRunner.class)
public class TestCreateMarkdownDocument extends AbstractTestMarkdown {

	private static final Logger logger = Logger.getLogger(TestCreateMarkdownDocument.class.getPackage().getName());

	private static DirectoryResourceCenter newResourceCenter;
	private static MarkdownTechnologyAdapter ta;

	@Test
	@TestOrder(1)
	public void testInitializeServiceManager() throws Exception {
		instanciateTestServiceManagerForMarkdown();
		ta = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(MarkdownTechnologyAdapter.class);
		newResourceCenter = makeNewDirectoryResourceCenter();
		assertNotNull(newResourceCenter);
	}

	// -----------------------------------------------------------------------
	// Empty document
	// -----------------------------------------------------------------------

	@Test
	@TestOrder(2)
	public void testCreateEmptyDocument() throws Exception {
		MarkdownDocumentResource resource = ta.createNewMarkdownDocumentResource(
				newResourceCenter, "", "EmptyCreated.md", false);
		assertNotNull(resource);

		MarkdownFactory factory = resource.getFactory();
		MarkdownDocument doc = factory.makeNewMarkdownDocument();
		resource.setResourceData(doc);
		doc.setResource(resource);

		assertTrue(doc.getElements().isEmpty());
		assertTrue(doc.getRootElements().isEmpty());

		resource.save();
		logger.info("Empty document URI: " + resource.getURI());
	}

	// -----------------------------------------------------------------------
	// Document built with addToElements
	// -----------------------------------------------------------------------

	@Test
	@TestOrder(3)
	public void testCreateDocumentWithElements() throws Exception {
		MarkdownDocumentResource resource = ta.createNewMarkdownDocumentResource(
				newResourceCenter, "", "Built.md", false);
		assertNotNull(resource);

		MarkdownFactory factory = resource.getFactory();
		MarkdownDocument doc = factory.makeNewMarkdownDocument();
		resource.setResourceData(doc);
		doc.setResource(resource);

		// Add a heading
		MarkdownHeading heading = factory.makeNewMarkdownHeading(null);
		heading.setLevel(1);
		heading.setRawText("Created Heading");
		doc.addToElements(heading);

		// Add a paragraph
		MarkdownParagraph paragraph = factory.makeNewMarkdownParagraph(null);
		paragraph.setRawText("This paragraph was created programmatically.");
		doc.addToElements(paragraph);

		// Add a code block
		MarkdownCodeBlock cb = factory.makeNewMarkdownCodeBlock(null);
		cb.setLanguage("java");
		cb.setContent("System.out.println(\"Hello\");\n");
		doc.addToElements(cb);

		assertEquals(3, doc.getElements().size());

		FlexoDocElement<?, ?> e0 = doc.getElements().get(0);
		FlexoDocElement<?, ?> e1 = doc.getElements().get(1);
		FlexoDocElement<?, ?> e2 = doc.getElements().get(2);

		assertTrue(e0 instanceof MarkdownHeading);
		assertTrue(e1 instanceof MarkdownParagraph);
		assertTrue(e2 instanceof MarkdownCodeBlock);

		assertEquals(1,                 ((MarkdownHeading)   e0).getLevel());
		assertEquals("Created Heading", ((MarkdownHeading)   e0).getRawText());
		assertEquals("This paragraph was created programmatically.", ((MarkdownParagraph) e1).getRawText());
		assertEquals("java",            ((MarkdownCodeBlock) e2).getLanguage());

		resource.save();
		logger.info("Created document:\n" + doc.debugContents());
	}

	// -----------------------------------------------------------------------
	// addStyledParagraphOfText API
	// -----------------------------------------------------------------------

	@Test
	@TestOrder(4)
	public void testAddStyledParagraph() throws Exception {
		MarkdownDocumentResource resource = ta.createNewMarkdownDocumentResource(
				newResourceCenter, "", "Styled.md", false);
		assertNotNull(resource);

		MarkdownFactory factory = resource.getFactory();
		MarkdownDocument doc = factory.makeNewMarkdownDocument();
		resource.setResourceData(doc);
		doc.setResource(resource);

		NamedDocStyle<MarkdownDocument, MarkdownTechnologyAdapter> heading1Style = doc.activateStyle("heading 1");
		assertNotNull("heading 1 style must exist after document creation", heading1Style);

		doc.addStyledParagraphOfText(heading1Style, "API-Created Title");
		assertEquals(1, doc.getElements().size());

		MarkdownParagraph p = (MarkdownParagraph) doc.getElements().get(0);
		assertEquals("API-Created Title", p.getRawText());
		assertEquals(heading1Style, p.getNamedStyle());

		resource.save();
	}

	// -----------------------------------------------------------------------
	// insertElementAtIndex
	// -----------------------------------------------------------------------

	@Test
	@TestOrder(5)
	public void testInsertElementAtIndex() throws Exception {
		MarkdownDocumentResource resource = ta.createNewMarkdownDocumentResource(
				newResourceCenter, "", "Inserted.md", false);
		assertNotNull(resource);

		MarkdownFactory factory = resource.getFactory();
		MarkdownDocument doc = factory.makeNewMarkdownDocument();
		resource.setResourceData(doc);
		doc.setResource(resource);

		MarkdownHeading h1 = factory.makeNewMarkdownHeading(null);
		h1.setLevel(1); h1.setRawText("Title");
		doc.addToElements(h1);

		MarkdownParagraph pLast = factory.makeNewMarkdownParagraph(null);
		pLast.setRawText("Last paragraph.");
		doc.addToElements(pLast);

		// Insert in the middle
		MarkdownParagraph pMiddle = factory.makeNewMarkdownParagraph(null);
		pMiddle.setRawText("Middle paragraph.");
		doc.insertElementAtIndex(pMiddle, 1);

		assertEquals(3, doc.getElements().size());
		assertEquals(h1,      doc.getElements().get(0));
		assertEquals(pMiddle, doc.getElements().get(1));
		assertEquals(pLast,   doc.getElements().get(2));
	}
}
