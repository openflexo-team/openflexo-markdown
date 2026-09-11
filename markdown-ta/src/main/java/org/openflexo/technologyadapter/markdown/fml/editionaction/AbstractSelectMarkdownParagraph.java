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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.markdown.MarkdownModelSlot;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.technologyadapter.markdown.model.MarkdownParagraph;

/**
 * Abstract base for {@link SelectMarkdownParagraph} and {@link SelectUniqueMarkdownParagraph}.
 *
 * @author sylvain
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectMarkdownParagraph.AbstractSelectMarkdownParagraphImpl.class)
public interface AbstractSelectMarkdownParagraph<AT>
		extends AbstractFetchRequest<MarkdownModelSlot, MarkdownDocument, MarkdownParagraph, AT> {

	abstract class AbstractSelectMarkdownParagraphImpl<AT>
			extends AbstractFetchRequestImpl<MarkdownModelSlot, MarkdownDocument, MarkdownParagraph, AT>
			implements AbstractSelectMarkdownParagraph<AT> {

		private static final Logger logger = Logger.getLogger(AbstractSelectMarkdownParagraph.class.getPackage().getName());

		@Override
		public Type getFetchedType() {
			return MarkdownParagraph.class;
		}

		@Override
		public List<MarkdownParagraph> performExecute(RunTimeEvaluationContext evaluationContext) {
			MarkdownDocument document = getReceiver(evaluationContext);
			if (document == null) {
				return new ArrayList<>();
			}
			List<MarkdownParagraph> candidates = document.getElements(MarkdownParagraph.class);
			return filterWithConditions(candidates, evaluationContext);
		}
	}
}
