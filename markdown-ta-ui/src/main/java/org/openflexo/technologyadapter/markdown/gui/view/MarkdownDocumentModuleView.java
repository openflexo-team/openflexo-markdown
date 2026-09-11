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

package org.openflexo.technologyadapter.markdown.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.openflexo.components.doc.editorkit.widget.FIBFlexoDocumentBrowser;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.selection.SelectionListener;
import org.openflexo.selection.SelectionManager;
import org.openflexo.technologyadapter.markdown.model.MarkdownDocument;
import org.openflexo.view.SelectionSynchronizedModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * Module view for a {@link MarkdownDocument}.
 * <p>
 * Displays the Markdown source in an {@link RSyntaxTextArea} with Markdown syntax
 * highlighting (line numbers, code folding). A toolbar provides Save and Revert
 * actions.
 *
 * @author sylvain
 */
@SuppressWarnings("serial")
public class MarkdownDocumentModuleView extends JPanel
		implements SelectionSynchronizedModuleView<MarkdownDocument> {

	private static final Logger logger = Logger.getLogger(MarkdownDocumentModuleView.class.getPackage().getName());

	private final MarkdownDocument document;
	private final FlexoPerspective perspective;
	private final FlexoController controller;

	private final RSyntaxTextArea textArea;
	private final JLabel statusLabel;
	private boolean dirty = false;

	public MarkdownDocumentModuleView(MarkdownDocument document, FlexoController controller, FlexoPerspective perspective) {
		super(new BorderLayout());
		this.document = document;
		this.controller = controller;
		this.perspective = perspective;

		// ── Text area ────────────────────────────────────────────────────
		textArea = new RSyntaxTextArea();
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
		textArea.setCodeFoldingEnabled(true);
		textArea.setAntiAliasingEnabled(true);
		textArea.setTabSize(2);
		// Do NOT set lineWrap: RSyntaxTextArea forces horizontal scrollbar to NEVER
		// when wrapping is enabled, hiding the bottom slider entirely.

		applyTheme();

		textArea.setText(document.toMarkdown());
		textArea.setCaretPosition(0);
		textArea.discardAllEdits();

		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e)  { markDirty(); }
			@Override public void removeUpdate(DocumentEvent e)  { markDirty(); }
			@Override public void changedUpdate(DocumentEvent e) { markDirty(); }
		});

		add(new JScrollPane(textArea), BorderLayout.CENTER);

		// ── Document structure browser (right panel) ──────────────────────
		// Wrap in a fixed-width panel so BorderLayout EAST never computes a
		// growing preferred width — the recursion risk was the old willShow()
		// calling focusOnObject(); now that willShow() is empty this is safe.
		FIBFlexoDocumentBrowser browser = new FIBFlexoDocumentBrowser(document, controller);
		JPanel browserPanel = new JPanel(new BorderLayout());
		browserPanel.setPreferredSize(new java.awt.Dimension(230, 0));
		browserPanel.add(browser, BorderLayout.CENTER);
		add(browserPanel, BorderLayout.EAST);

		// ── Toolbar ──────────────────────────────────────────────────────
		statusLabel = new JLabel(" ");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		toolbar.add(new JButton(new AbstractAction("Save") {
			@Override public void actionPerformed(ActionEvent e) { saveDocument(); }
		}));

		toolbar.add(new JButton(new AbstractAction("Revert") {
			@Override public void actionPerformed(ActionEvent e) { revertDocument(); }
		}));

		toolbar.addSeparator();
		toolbar.add(statusLabel);
		add(toolbar, BorderLayout.NORTH);

		// ── Deletion listener ─────────────────────────────────────────────
		document.getPropertyChangeSupport().addPropertyChangeListener(
				document.getDeletedProperty(), evt -> deleteModuleView());
	}

	// ── Theme ─────────────────────────────────────────────────────────────

	private void applyTheme() {
		try {
			Theme theme = Theme.load(
					getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/idea.xml"));
			theme.apply(textArea);
		}
		catch (IOException | NullPointerException e) {
			// Theme not found on classpath — keep default styling
			logger.fine("RSyntaxTextArea 'idea' theme not found, using default");
		}
	}

	// ── Dirty tracking ────────────────────────────────────────────────────

	private void markDirty() {
		if (!dirty) {
			dirty = true;
			statusLabel.setForeground(Color.DARK_GRAY);
			statusLabel.setText("Modified — unsaved changes");
		}
	}

	private void clearDirty() {
		dirty = false;
		statusLabel.setForeground(new Color(0, 120, 0));
		statusLabel.setText("Saved");
	}

	// ── Save / Revert ─────────────────────────────────────────────────────

	private void saveDocument() {
		String text = textArea.getText();
		// Re-parse and update the PAMELA model
		com.vladsch.flexmark.util.ast.Document ast = document.getFactory().parseMarkdown(text);
		document.updateFromMarkdownDocument(ast, document.getFactory());
		// Persist to disk
		try {
			document.getResource().save();
			textArea.discardAllEdits();
			clearDirty();
		}
		catch (Exception e) {
			logger.warning("Cannot save Markdown document: " + e.getMessage());
			statusLabel.setForeground(Color.RED);
			statusLabel.setText("Save failed: " + e.getMessage());
		}
	}

	private void revertDocument() {
		textArea.setText(document.toMarkdown());
		textArea.setCaretPosition(0);
		textArea.discardAllEdits();
		dirty = false;
		statusLabel.setText(" ");
	}

	// ── ModuleView ────────────────────────────────────────────────────────

	@Override
	public FlexoPerspective getPerspective() {
		return perspective;
	}

	@Override
	public MarkdownDocument getRepresentedObject() {
		return document;
	}

	@Override
	public void deleteModuleView() {
		document.getPropertyChangeSupport().removePropertyChangeListener(
				document.getDeletedProperty(), evt -> deleteModuleView());
		if (controller != null) {
			controller.removeModuleView(this);
		}
	}

	@Override
	public boolean isAutoscrolled() {
		// Return true so FlexoMainPane does NOT wrap this view in an outer JScrollPane.
		// When false, the entire panel is wrapped externally and the inner
		// JScrollPane(textArea) never gets constrained → its scrollbars never appear.
		return true;
	}

	@Override
	public void willHide() {
	}

	@Override
	public void willShow() {
	}

	@Override
	public void show(FlexoController controller, FlexoPerspective perspective) {
	}

	// ── SelectionSynchronizedModuleView ───────────────────────────────────

	@Override
	public List<SelectionListener> getSelectionListeners() {
		Vector<SelectionListener> reply = new Vector<>();
		reply.add(this);
		return reply;
	}

	@Override
	public SelectionManager getSelectionManager() {
		return controller != null ? controller.getSelectionManager() : null;
	}

	@Override
	public Vector<FlexoObject> getSelection() {
		return getSelectionManager() != null ? getSelectionManager().getSelection() : new Vector<>();
	}

	@Override
	public void resetSelection() {
		if (getSelectionManager() != null) getSelectionManager().resetSelection();
	}

	@Override
	public void addToSelected(FlexoObject object) {
		if (getSelectionManager() != null) getSelectionManager().addToSelected(object);
	}

	@Override
	public void removeFromSelected(FlexoObject object) {
		if (getSelectionManager() != null) getSelectionManager().removeFromSelected(object);
	}

	@Override
	public void addToSelected(Vector<? extends FlexoObject> objects) {
		if (getSelectionManager() != null) getSelectionManager().addToSelected(objects);
	}

	@Override
	public void removeFromSelected(Vector<? extends FlexoObject> objects) {
		if (getSelectionManager() != null) getSelectionManager().removeFromSelected(objects);
	}

	@Override
	public void setSelectedObjects(Vector<? extends FlexoObject> objects) {
		if (getSelectionManager() != null) getSelectionManager().setSelectedObjects(objects);
	}

	@Override
	public FlexoObject getFocusedObject() {
		return getSelectionManager() != null ? getSelectionManager().getFocusedObject() : null;
	}

	@Override
	public boolean mayRepresents(FlexoObject anObject) {
		return false;
	}

	@Override
	public void fireObjectSelected(FlexoObject object) {
	}

	@Override
	public void fireObjectDeselected(FlexoObject object) {
	}

	@Override
	public void fireResetSelection() {
	}

	@Override
	public void fireBeginMultipleSelection() {
	}

	@Override
	public void fireEndMultipleSelection() {
	}
}
