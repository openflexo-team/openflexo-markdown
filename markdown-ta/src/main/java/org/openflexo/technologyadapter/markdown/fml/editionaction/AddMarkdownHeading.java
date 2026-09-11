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
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.doc.NamedDocStyle;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.editionaction.TechnologySpecificActionDefiningReceiver;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.markdown.MarkdownModelSlot;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.technologyadapter.markdown.model.MarkdownHeading;

/**
 * {@link TechnologySpecificActionDefiningReceiver} that adds a new {@link MarkdownHeading}
 * to a {@link MarkdownDocument}.
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(AddMarkdownHeading.AddMarkdownHeadingImpl.class)
@XMLElement
@FML("AddMarkdownHeading")
public interface AddMarkdownHeading
		extends TechnologySpecificActionDefiningReceiver<MarkdownModelSlot, MarkdownDocument, MarkdownHeading> {

	@PropertyIdentifier(type = DataBinding.class)
	String LEVEL_KEY = "level";

	@PropertyIdentifier(type = DataBinding.class)
	String TEXT_KEY = "text";

	@Getter(value = LEVEL_KEY)
	@XMLAttribute
	@FMLAttribute(value = LEVEL_KEY, required = false, description = "Heading level (1-6)")
	DataBinding<Integer> getLevel();

	@Setter(LEVEL_KEY)
	void setLevel(DataBinding<Integer> level);

	@Getter(value = TEXT_KEY)
	@XMLAttribute
	@FMLAttribute(value = TEXT_KEY, required = false, description = "Heading text")
	DataBinding<String> getText();

	@Setter(TEXT_KEY)
	void setText(DataBinding<String> text);

	abstract class AddMarkdownHeadingImpl
			extends TechnologySpecificActionDefiningReceiverImpl<MarkdownModelSlot, MarkdownDocument, MarkdownHeading>
			implements AddMarkdownHeading {

		private static final Logger logger = Logger.getLogger(AddMarkdownHeading.class.getPackage().getName());

		private DataBinding<Integer> level;
		private DataBinding<String> text;

		@Override
		public Type getAssignableType() {
			return MarkdownHeading.class;
		}

		@Override
		public MarkdownHeading execute(RunTimeEvaluationContext evaluationContext) {
			MarkdownDocument document = getReceiver(evaluationContext);
			if (document == null) {
				logger.warning("AddMarkdownHeading: receiver document is null");
				return null;
			}
			int headingLevel = 1;
			String headingText = "";
			try {
				Integer lvl = getLevel().getBindingValue(evaluationContext);
				if (lvl != null) {
					headingLevel = Math.max(1, Math.min(lvl, 6));
				}
				String t = getText().getBindingValue(evaluationContext);
				if (t != null) {
					headingText = t;
				}
			}
			catch (TypeMismatchException | NullReferenceException | ReflectiveOperationException e) {
				logger.warning("AddMarkdownHeading: binding evaluation failed: " + e.getMessage());
			}

			MarkdownHeading heading = document.getFactory().makeNewMarkdownHeading(null);
			heading.setLevel(headingLevel);
			heading.setRawText(headingText);
			NamedDocStyle<MarkdownDocument, MarkdownTechnologyAdapter> style = document.activateStyle("heading " + headingLevel);
			if (style != null) {
				heading.setNamedStyle(style);
			}
			document.addToElements(heading);
			return heading;
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

		@Override
		public DataBinding<String> getText() {
			if (text == null) {
				text = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				text.setBindingName("text");
			}
			return text;
		}

		@Override
		public void setText(DataBinding<String> text) {
			if (text != null) {
				text.setOwner(this);
				text.setDeclaredType(String.class);
				text.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				text.setBindingName("text");
			}
			this.text = text;
		}
	}
}
