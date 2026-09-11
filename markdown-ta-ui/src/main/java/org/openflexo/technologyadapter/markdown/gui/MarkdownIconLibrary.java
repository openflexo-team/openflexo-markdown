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

package org.openflexo.technologyadapter.markdown.gui;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.icon.ImageIconResource;
import org.openflexo.rm.ResourceLocator;

/**
 * Icon resources for the Markdown technology adapter UI.
 *
 * @author sylvain
 */
public class MarkdownIconLibrary {

	private static final Logger logger = Logger.getLogger(MarkdownIconLibrary.class.getPackage().getName());

	public static final ImageIcon MARKDOWN_TECHNOLOGY_BIG_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/MarkdownIcon_32x32.png"));
	public static final ImageIcon MARKDOWN_TECHNOLOGY_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/MarkdownIcon_16x16.png"));
	public static final ImageIcon MARKDOWN_FILE_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/MarkdownFile_16x16.png"));

	public static final ImageIcon HEADING_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Heading_16x16.png"));
	public static final ImageIcon PARAGRAPH_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Paragraph_16x16.png"));
	public static final ImageIcon CODE_BLOCK_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/CodeBlock_16x16.png"));
}
