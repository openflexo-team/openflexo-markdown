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

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.doc.FlexoDocElement;
import org.openflexo.foundation.doc.NamedDocStyle;
import org.openflexo.technologyadapter.markdown.AbstractTestMarkdown;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Verifies the structural model built from parsed Markdown:
 * element types, heading levels, styles, and document hierarchy.
 *
 * @author sylvain
 */
@RunWith(OrderedRunner.class)
public class TestMarkdownDocumentStructure extends AbstractTestMarkdown {

	private static final Logger logger = Logger.getLogger(TestMarkdownDocumentStructure.class.getPackage().getName());

	@Test
	@TestOrder(1)
	public void testInitializeServiceManager() throws Exception {
		instanciateTestServiceManagerForMarkdown();
	}

	// -----------------------------------------------------------------------
	// SimpleDocument.md
	// -----------------------------------------------------------------------

	@Test
	@TestOrder(2)
	public void testSimpleDocumentElementTypes() {
		MarkdownDocument doc = getDocument("SimpleDocument.md");
		logger.info("SimpleDocument:\n" + doc.debugStructuredContents());

		List<FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter>> elements = doc.getElements();
		assertNotNull(elements);

		// First element must be a H1 heading
		assertTrue("First element must be a MarkdownHeading", elements.get(0) instanceof MarkdownHeading);
		MarkdownHeading h1 = (MarkdownHeading) elements.get(0);
		assertEquals(1, h1.getLevel());
		assertEquals("Simple Document", h1.getRawText());

		// All headings must have valid levels
		for (FlexoDocElement<?, ?> e : elements) {
			if (e instanceof MarkdownHeading) {
				int level = ((MarkdownHeading) e).getLevel();
				assertTrue("Heading level must be between 1 and 6", level >= 1 && level <= 6);
			}
		}

		// Count headings and paragraphs
		int headingCount = 0, paragraphCount = 0;
		for (FlexoDocElement<?, ?> e : elements) {
			if (e instanceof MarkdownHeading)   headingCount++;
			if (e instanceof MarkdownParagraph) paragraphCount++;
		}
		assertTrue("SimpleDocument must have at least 4 headings", headingCount >= 4);
		assertTrue("SimpleDocument must have at least 4 paragraphs", paragraphCount >= 4);
	}

	@Test
	@TestOrder(3)
	public void testSimpleDocumentHierarchy() {
		MarkdownDocument doc = getDocument("SimpleDocument.md");

		// The document starts with H1 → it should be the sole root element
		List<FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter>> roots = doc.getRootElements();
		assertEquals("SimpleDocument should have exactly one root (the H1)", 1, roots.size());
		assertTrue(roots.get(0) instanceof MarkdownHeading);
		assertEquals(1, ((MarkdownHeading) roots.get(0)).getLevel());

		// The H1 must have children
		assertNotNull(roots.get(0).getChildrenElements());
		assertTrue("H1 must have children", !roots.get(0).getChildrenElements().isEmpty());
	}

	@Test
	@TestOrder(4)
	public void testSimpleDocumentStructuringStyles() {
		MarkdownDocument doc = getDocument("SimpleDocument.md");

		// All six heading styles must be registered
		for (int level = 1; level <= 6; level++) {
			String styleId = "heading " + level;
			NamedDocStyle<?, ?> style = doc.getStyleByIdentifier(styleId);
			assertNotNull("Style '" + styleId + "' must be registered", style);
		}

		// All six must be in structuring styles
		assertEquals(6, doc.getStructuringStyles().size());
	}

	// -----------------------------------------------------------------------
	// AllHeadingLevels.md
	// -----------------------------------------------------------------------

	@Test
	@TestOrder(5)
	public void testAllHeadingLevelsPresent() {
		MarkdownDocument doc = getDocument("AllHeadingLevels.md");
		logger.info("AllHeadingLevels:\n" + doc.debugStructuredContents());

		boolean[] levelFound = new boolean[7]; // index 1..6
		for (FlexoDocElement<?, ?> e : doc.getElements()) {
			if (e instanceof MarkdownHeading) {
				int lvl = ((MarkdownHeading) e).getLevel();
				if (lvl >= 1 && lvl <= 6) levelFound[lvl] = true;
			}
		}
		for (int i = 1; i <= 6; i++) {
			assertTrue("Heading level " + i + " not found in AllHeadingLevels.md", levelFound[i]);
		}
	}

	// -----------------------------------------------------------------------
	// DocumentWithCodeBlocks.md
	// -----------------------------------------------------------------------

	@Test
	@TestOrder(6)
	public void testCodeBlockElementTypes() {
		MarkdownDocument doc = getDocument("DocumentWithCodeBlocks.md");
		logger.info("DocumentWithCodeBlocks:\n" + doc.debugContents());

		boolean foundJava = false, foundPython = false, foundBash = false, foundNoLang = false;

		for (FlexoDocElement<?, ?> e : doc.getElements()) {
			if (e instanceof MarkdownCodeBlock) {
				MarkdownCodeBlock cb = (MarkdownCodeBlock) e;
				String lang = cb.getLanguage();
				assertNotNull("Code block content must not be null", cb.getContent());

				if ("java".equals(lang))   foundJava = true;
				if ("python".equals(lang)) foundPython = true;
				if ("bash".equals(lang))   foundBash = true;
				if (lang == null || lang.isEmpty()) foundNoLang = true;

				logger.info("  CodeBlock lang=" + lang + " content=" + cb.getContent().substring(0, Math.min(40, cb.getContent().length())));
			}
		}

		assertTrue("Java code block not found", foundJava);
		assertTrue("Python code block not found", foundPython);
		assertTrue("Bash code block not found", foundBash);
		assertTrue("No-language code block not found", foundNoLang);
	}

	@Test
	@TestOrder(7)
	public void testJavaCodeBlockContent() {
		MarkdownDocument doc = getDocument("DocumentWithCodeBlocks.md");

		for (FlexoDocElement<?, ?> e : doc.getElements()) {
			if (e instanceof MarkdownCodeBlock && "java".equals(((MarkdownCodeBlock) e).getLanguage())) {
				String content = ((MarkdownCodeBlock) e).getContent();
				assertTrue("Java code block must contain 'HelloWorld'", content.contains("HelloWorld"));
				assertTrue("Java code block must contain 'System.out.println'", content.contains("System.out.println"));
				return;
			}
		}
		assertTrue("Java code block was not found", false);
	}

	// -----------------------------------------------------------------------
	// TechnicalReport.md
	// -----------------------------------------------------------------------

	@Test
	@TestOrder(8)
	public void testTechnicalReportStructure() {
		MarkdownDocument doc = getDocument("TechnicalReport.md");
		logger.info("TechnicalReport:\n" + doc.debugStructuredContents());

		// Should have a H1 root
		List<FlexoDocElement<MarkdownDocument, MarkdownTechnologyAdapter>> roots = doc.getRootElements();
		assertTrue("TechnicalReport must have at least one root element", !roots.isEmpty());
		assertTrue("First root must be a H1 heading", roots.get(0) instanceof MarkdownHeading);
		assertEquals(1, ((MarkdownHeading) roots.get(0)).getLevel());

		// Must contain code blocks
		boolean hasCodeBlock = false;
		for (FlexoDocElement<?, ?> e : doc.getElements()) {
			if (e instanceof MarkdownCodeBlock) {
				hasCodeBlock = true;
				break;
			}
		}
		assertTrue("TechnicalReport must contain at least one code block", hasCodeBlock);
	}
}
