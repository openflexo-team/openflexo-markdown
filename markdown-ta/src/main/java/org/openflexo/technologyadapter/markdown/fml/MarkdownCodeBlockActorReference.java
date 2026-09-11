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

package org.openflexo.technologyadapter.markdown.fml;

import java.util.logging.Logger;

import org.openflexo.foundation.doc.FlexoDocument;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.ModelSlotInstance;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.technologyadapter.markdown.model.MarkdownCodeBlock;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;

/**
 * {@link ActorReference} for a {@link MarkdownCodeBlock}.
 * <p>
 * The code block is persisted by its PAMELA element identifier so it can be
 * re-located after a save/reload cycle via
 * {@link MarkdownDocument#getElementWithIdentifier(String)}.
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(MarkdownCodeBlockActorReference.MarkdownCodeBlockActorReferenceImpl.class)
@XMLElement
@FML("MarkdownCodeBlockActorReference")
public interface MarkdownCodeBlockActorReference extends ActorReference<MarkdownCodeBlock> {

	@PropertyIdentifier(type = String.class)
	String ELEMENT_ID_KEY = "elementIdentifier";

	@Getter(value = ELEMENT_ID_KEY)
	@XMLAttribute
	String getElementIdentifier();

	@Setter(ELEMENT_ID_KEY)
	void setElementIdentifier(String id);

	// -----------------------------------------------------------------------
	// Implementation
	// -----------------------------------------------------------------------

	abstract class MarkdownCodeBlockActorReferenceImpl extends ActorReferenceImpl<MarkdownCodeBlock>
			implements MarkdownCodeBlockActorReference {

		private static final Logger logger = FlexoLogger.getLogger(MarkdownCodeBlockActorReference.class.getPackage().getName());

		private String elementIdentifier;
		private MarkdownCodeBlock codeBlock;

		private MarkdownDocument getMarkdownDocument() {
			ModelSlotInstance<?, ?> msInstance = getModelSlotInstance();
			if (msInstance != null && msInstance.getAccessedResourceData() instanceof MarkdownDocument) {
				return (MarkdownDocument) msInstance.getAccessedResourceData();
			}
			return null;
		}

		@Override
		public String getElementIdentifier() {
			if (codeBlock != null) {
				return codeBlock.getIdentifier();
			}
			return elementIdentifier;
		}

		@Override
		public void setElementIdentifier(String id) {
			this.elementIdentifier = id;
		}

		@Override
		public MarkdownCodeBlock getModellingElement(boolean forceLoading) {
			if (codeBlock == null) {
				MarkdownDocument document = getMarkdownDocument();
				if (document != null && StringUtils.isNotEmpty(elementIdentifier)) {
					codeBlock = (MarkdownCodeBlock) document.getElementWithIdentifier(elementIdentifier);
				}
			}
			return codeBlock;
		}

		@Override
		public void setModellingElement(MarkdownCodeBlock aCodeBlock) {
			if (aCodeBlock != codeBlock) {
				MarkdownCodeBlock oldValue = codeBlock;
				codeBlock = aCodeBlock;
				getPropertyChangeSupport().firePropertyChange(MODELLING_ELEMENT_KEY, oldValue, codeBlock);
			}
		}
	}
}
