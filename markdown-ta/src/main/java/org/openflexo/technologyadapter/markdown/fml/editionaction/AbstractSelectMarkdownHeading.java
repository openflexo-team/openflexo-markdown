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

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.technologyadapter.markdown.MarkdownModelSlot;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.technologyadapter.markdown.model.MarkdownHeading;

/**
 * Abstract base for {@link SelectMarkdownHeading} and {@link SelectUniqueMarkdownHeading}.
 * <p>
 * Optionally filters by heading level when the {@code level} binding is set.
 *
 * @author sylvain
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectMarkdownHeading.AbstractSelectMarkdownHeadingImpl.class)
public interface AbstractSelectMarkdownHeading<AT>
		extends AbstractFetchRequest<MarkdownModelSlot, MarkdownDocument, MarkdownHeading, AT> {

	@PropertyIdentifier(type = DataBinding.class)
	String LEVEL_KEY = "level";

	@Getter(value = LEVEL_KEY)
	@XMLAttribute
	@FMLAttribute(value = LEVEL_KEY, required = false, description = "Filter by heading level (1-6). Leave unset to select all levels.")
	DataBinding<Integer> getLevel();

	@Setter(LEVEL_KEY)
	void setLevel(DataBinding<Integer> level);

	abstract class AbstractSelectMarkdownHeadingImpl<AT>
			extends AbstractFetchRequestImpl<MarkdownModelSlot, MarkdownDocument, MarkdownHeading, AT>
			implements AbstractSelectMarkdownHeading<AT> {

		private static final Logger logger = Logger.getLogger(AbstractSelectMarkdownHeading.class.getPackage().getName());

		private DataBinding<Integer> level;

		@Override
		public Type getFetchedType() {
			return MarkdownHeading.class;
		}

		@Override
		public List<MarkdownHeading> performExecute(RunTimeEvaluationContext evaluationContext) {
			MarkdownDocument document = getReceiver(evaluationContext);
			if (document == null) {
				return new ArrayList<>();
			}

			Integer filterLevel = null;
			if (getLevel() != null && getLevel().isSet()) {
				try {
					filterLevel = getLevel().getBindingValue(evaluationContext);
				}
				catch (TypeMismatchException | NullReferenceException | ReflectiveOperationException e) {
					logger.warning("SelectMarkdownHeading: level binding evaluation failed: " + e.getMessage());
				}
			}

			List<MarkdownHeading> candidates = document.getElements(MarkdownHeading.class);
			if (filterLevel != null) {
				List<MarkdownHeading> filtered = new ArrayList<>();
				for (MarkdownHeading h : candidates) {
					if (h.getLevel() == filterLevel) {
						filtered.add(h);
					}
				}
				candidates = filtered;
			}

			return filterWithConditions(candidates, evaluationContext);
		}

		@Override
		public DataBinding<Integer> getLevel() {
			if (level == null) {
				level = new DataBinding<>(this, Integer.class, DataBinding.BindingDefinitionType.GET);
				level.setBindingName("level");
			}
			return level;
		}

		@Override
		public void setLevel(DataBinding<Integer> level) {
			if (level != null) {
				level.setOwner(this);
				level.setDeclaredType(Integer.class);
				level.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				level.setBindingName("level");
			}
			this.level = level;
		}
	}
}
