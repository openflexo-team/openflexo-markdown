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

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.AbstractCreateResource;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.fml.rt.ReturnException;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.markdown.MarkdownModelSlot;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.technologyadapter.markdown.rm.MarkdownDocumentResource;

/**
 * {@link org.openflexo.foundation.fml.editionaction.EditionAction} that creates a new empty
 * {@link MarkdownDocumentResource}.
 *
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(CreateMarkdownDocumentResource.CreateMarkdownDocumentResourceImpl.class)
@XMLElement
@FML("CreateMarkdownDocumentResource")
public interface CreateMarkdownDocumentResource
		extends AbstractCreateResource<MarkdownModelSlot, MarkdownDocument, MarkdownTechnologyAdapter> {

	abstract class CreateMarkdownDocumentResourceImpl
			extends AbstractCreateResourceImpl<MarkdownModelSlot, MarkdownDocument, MarkdownTechnologyAdapter>
			implements CreateMarkdownDocumentResource {

		private static final Logger logger = Logger.getLogger(CreateMarkdownDocumentResource.class.getPackage().getName());

		@Override
		public Type getAssignableType() {
			return MarkdownDocument.class;
		}

		@Override
		public MarkdownDocument execute(RunTimeEvaluationContext evaluationContext) throws ReturnException, FMLExecutionException {

			String resourceName = getResourceName(evaluationContext);
			String resourceURI = getResourceURI(evaluationContext);
			FlexoResourceCenter<?> rc = getResourceCenter(evaluationContext);

			logger.info("CreateMarkdownDocumentResource: name=" + resourceName + " uri=" + resourceURI
					+ " relativePath=" + getRelativePath() + " rc=" + rc);

			MarkdownTechnologyAdapter markdownTA = getServiceManager().getTechnologyAdapterService()
					.getTechnologyAdapter(MarkdownTechnologyAdapter.class);

			MarkdownDocumentResource newResource = markdownTA.createNewMarkdownDocumentResource(
					(FileSystemBasedResourceCenter) rc, getRelativePath(), resourceName, true);

			if (newResource != null) {
				try {
					return newResource.getResourceData();
				}
				catch (FileNotFoundException e) {
					throw new FMLExecutionException(e);
				}
				catch (ResourceLoadingCancelledException e) {
					throw new FMLExecutionException(e);
				}
				catch (FlexoException e) {
					throw new FMLExecutionException(e);
				}
			}

			logger.warning("Cannot create MarkdownDocumentResource!");
			return null;
		}
	}
}
