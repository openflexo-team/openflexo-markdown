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
import org.openflexo.technologyadapter.markdown.model.MarkdownCodeBlock;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;

/**
 * {@link TechnologySpecificActionDefiningReceiver} that adds a new {@link MarkdownCodeBlock}
 * to a {@link MarkdownDocument}.
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(AddMarkdownCodeBlock.AddMarkdownCodeBlockImpl.class)
@XMLElement
@FML("AddMarkdownCodeBlock")
public interface AddMarkdownCodeBlock
		extends TechnologySpecificActionDefiningReceiver<MarkdownModelSlot, MarkdownDocument, MarkdownCodeBlock> {

	@PropertyIdentifier(type = DataBinding.class)
	String LANGUAGE_KEY = "language";

	@PropertyIdentifier(type = DataBinding.class)
	String CONTENT_KEY = "content";

	@Getter(value = LANGUAGE_KEY)
	@XMLAttribute
	@FMLAttribute(value = LANGUAGE_KEY, required = false, description = "Language identifier (e.g. 'java', 'python')")
	DataBinding<String> getLanguage();

	@Setter(LANGUAGE_KEY)
	void setLanguage(DataBinding<String> language);

	@Getter(value = CONTENT_KEY)
	@XMLAttribute
	@FMLAttribute(value = CONTENT_KEY, required = false, description = "Code block content")
	DataBinding<String> getContent();

	@Setter(CONTENT_KEY)
	void setContent(DataBinding<String> content);

	abstract class AddMarkdownCodeBlockImpl
			extends TechnologySpecificActionDefiningReceiverImpl<MarkdownModelSlot, MarkdownDocument, MarkdownCodeBlock>
			implements AddMarkdownCodeBlock {

		private static final Logger logger = Logger.getLogger(AddMarkdownCodeBlock.class.getPackage().getName());

		private DataBinding<String> language;
		private DataBinding<String> content;

		@Override
		public Type getAssignableType() {
			return MarkdownCodeBlock.class;
		}

		@Override
		public MarkdownCodeBlock execute(RunTimeEvaluationContext evaluationContext) {
			MarkdownDocument document = getReceiver(evaluationContext);
			if (document == null) {
				logger.warning("AddMarkdownCodeBlock: receiver document is null");
				return null;
			}
			String lang = null;
			String code = "";
			try {
				lang = getLanguage().getBindingValue(evaluationContext);
				String c = getContent().getBindingValue(evaluationContext);
				if (c != null) {
					code = c;
				}
			}
			catch (TypeMismatchException | NullReferenceException | ReflectiveOperationException e) {
				logger.warning("AddMarkdownCodeBlock: binding evaluation failed: " + e.getMessage());
			}

			MarkdownCodeBlock cb = document.getFactory().makeNewMarkdownCodeBlock(null);
			cb.setLanguage(lang);
			cb.setContent(code);
			document.addToElements(cb);
			return cb;
		}

		@Override
		public DataBinding<String> getLanguage() {
			if (language == null) {
				language = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				language.setBindingName("language");
			}
			return language;
		}

		@Override
		public void setLanguage(DataBinding<String> language) {
			if (language != null) {
				language.setOwner(this);
				language.setDeclaredType(String.class);
				language.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				language.setBindingName("language");
			}
			this.language = language;
		}

		@Override
		public DataBinding<String> getContent() {
			if (content == null) {
				content = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				content.setBindingName("content");
			}
			return content;
		}

		@Override
		public void setContent(DataBinding<String> content) {
			if (content != null) {
				content.setOwner(this);
				content.setDeclaredType(String.class);
				content.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				content.setBindingName("content");
			}
			this.content = content;
		}
	}
}
