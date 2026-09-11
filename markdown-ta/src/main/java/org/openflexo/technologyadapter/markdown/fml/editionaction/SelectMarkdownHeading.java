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

import java.util.List;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.FetchRequest;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.markdown.MarkdownModelSlot;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.technologyadapter.markdown.model.MarkdownHeading;

/**
 * {@link FetchRequest} that returns all {@link MarkdownHeading} elements of a
 * {@link MarkdownDocument}, optionally filtered by level and/or condition.
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(SelectMarkdownHeading.AbstractSelectMarkdownHeadingImpl.class)
@XMLElement
@FML("SelectMarkdownHeading")
public interface SelectMarkdownHeading
		extends AbstractSelectMarkdownHeading<List<MarkdownHeading>>,
		        FetchRequest<MarkdownModelSlot, MarkdownDocument, MarkdownHeading> {
}
