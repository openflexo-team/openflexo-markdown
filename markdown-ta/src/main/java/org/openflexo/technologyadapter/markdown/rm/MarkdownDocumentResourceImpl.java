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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FileIODelegate;
import org.openflexo.foundation.resource.FileWritingLock;
import org.openflexo.foundation.resource.PamelaResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.task.Progress;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.technologyadapter.markdown.model.MarkdownFactory;

/**
 * Concrete implementation of {@link MarkdownDocumentResource}.
 * <p>
 * Reads a {@code .md} file as a UTF-8 text and hands the raw content to
 * {@link MarkdownFactory#makeNewMarkdownDocument(String)} for parsing.
 * Serialisation walks the PAMELA model and reconstructs Markdown text.
 *
 * @author sylvain
 */
public abstract class MarkdownDocumentResourceImpl
		extends PamelaResourceImpl<MarkdownDocument, MarkdownFactory>
		implements MarkdownDocumentResource {

	private static final Logger logger = Logger.getLogger(MarkdownDocumentResourceImpl.class.getPackage().getName());

	// ------------------------------------------------------------------
	// Load
	// ------------------------------------------------------------------

	@Override
	protected MarkdownDocument performLoad() throws IOException, Exception {

		if (getFlexoIOStreamDelegate() == null) {
			throw new FlexoException("Cannot load Markdown document with this IO/delegate: " + getIODelegate());
		}

		Progress.progress(getLocales().localizedForKey("loading") + " " + getIODelegate().getSerializationArtefact());

		String rawMarkdown = readFully(getInputStream());
		return getFactory().makeNewMarkdownDocument(rawMarkdown);
	}

	// ------------------------------------------------------------------
	// Save
	// ------------------------------------------------------------------

	@Override
	protected void performSave(boolean clearIsModified) throws SaveResourceException {

		if (getFlexoIOStreamDelegate() == null) {
			throw new SaveResourceException(getIODelegate());
		}

		FileWritingLock lock = getFlexoIOStreamDelegate().willWriteOnDisk();

		if (logger.isLoggable(Level.INFO)) {
			logger.info("Saving Markdown resource " + this + " : " + getIODelegate().getSerializationArtefact());
		}

		if (getFlexoIOStreamDelegate() instanceof FileIODelegate) {
			File temporaryFile = null;
			try {
				File fileToSave = ((FileIODelegate) getFlexoIOStreamDelegate()).getFile();
				makeLocalCopy(fileToSave);
				temporaryFile = ((FileIODelegate) getIODelegate()).createTemporaryArtefact(".md");
				try (FileOutputStream fos = new FileOutputStream(temporaryFile)) {
					writeMarkdown(fos);
				}
				FileUtils.rename(temporaryFile, fileToSave);
				if (clearIsModified) {
					notifyResourceStatusChanged();
				}
			}
			catch (IOException e) {
				if (temporaryFile != null) {
					temporaryFile.delete();
				}
				getFlexoIOStreamDelegate().hasWrittenOnDisk(lock);
				throw new SaveResourceException(getIODelegate(), e);
			}
		}
		else {
			writeMarkdown(getOutputStream());
		}

		getFlexoIOStreamDelegate().hasWrittenOnDisk(lock);
		if (clearIsModified) {
			notifyResourceStatusChanged();
		}
	}

	// ------------------------------------------------------------------
	// Serialisation: model → Markdown text
	// ------------------------------------------------------------------

	private void writeMarkdown(OutputStream out) throws SaveResourceException {
		try (Writer w = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
			w.write(getDocument().toMarkdown());
		}
		catch (IOException e) {
			throw new SaveResourceException(getIODelegate(), e);
		}
	}

	// ------------------------------------------------------------------
	// FlexoDocumentResource API
	// ------------------------------------------------------------------

	@Override
	public MarkdownDocument getDocument() {
		try {
			return getResourceData();
		}
		catch (ResourceLoadingCancelledException | FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		catch (FlexoException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Class<MarkdownDocument> getResourceDataClass() {
		return MarkdownDocument.class;
	}

	// ------------------------------------------------------------------
	// Utility
	// ------------------------------------------------------------------

	private static String readFully(InputStream in) throws IOException {
		try {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int read;
			while ((read = in.read(buffer)) != -1) {
				result.write(buffer, 0, read);
			}
			return result.toString(StandardCharsets.UTF_8.name());
		}
		finally {
			try {
				in.close();
			}
			catch (IOException ignored) {
			}
		}
	}
}
