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

package org.openflexo.technologyadapter.markdown.rm;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResourceRepository;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.PamelaModelFactory;
import org.openflexo.technologyadapter.markdown.MarkdownTechnologyAdapter;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;

/**
 * Repository that indexes all {@link MarkdownDocumentResource} instances for a given resource center.
 *
 * @param <I> the serialisation artefact type of the resource center (usually {@link java.io.File})
 *
 * @author sylvain
 */
@ModelEntity
public interface MarkdownDocumentRepository<I>
		extends TechnologyAdapterResourceRepository<MarkdownDocumentResource, MarkdownTechnologyAdapter, MarkdownDocument, I> {

	static <I> MarkdownDocumentRepository<I> instanciateNewRepository(MarkdownTechnologyAdapter technologyAdapter,
			FlexoResourceCenter<I> resourceCenter) {
		try {
			PamelaModelFactory factory = new PamelaModelFactory(MarkdownDocumentRepository.class);
			@SuppressWarnings("unchecked")
			MarkdownDocumentRepository<I> repo = factory.newInstance(MarkdownDocumentRepository.class);
			repo.setTechnologyAdapter(technologyAdapter);
			repo.setResourceCenter(resourceCenter);
			repo.setBaseArtefact(resourceCenter.getBaseArtefact());
			repo.getRootFolder().setRepositoryContext(null);
			return repo;
		}
		catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}
}
