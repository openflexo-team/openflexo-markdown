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

package org.openflexo.technologyadapter.markdown.fml.editionaction;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.UniqueFetchRequest;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.markdown.MarkdownModelSlot;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.technologyadapter.markdown.model.MarkdownParagraph;

/**
 * {@link UniqueFetchRequest} that returns a single {@link MarkdownParagraph} from a {@link MarkdownDocument}.
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(SelectUniqueMarkdownParagraph.AbstractSelectMarkdownParagraphImpl.class)
@XMLElement
@FML("SelectUniqueMarkdownParagraph")
public interface SelectUniqueMarkdownParagraph
		extends AbstractSelectMarkdownParagraph<MarkdownParagraph>,
		        UniqueFetchRequest<MarkdownModelSlot, MarkdownDocument, MarkdownParagraph> {
}
