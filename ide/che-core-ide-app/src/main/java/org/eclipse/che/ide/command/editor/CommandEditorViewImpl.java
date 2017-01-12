/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.command.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.ui.window.Window;

/**
 * Implementation of {@link CommandEditorView}.
 *
 * @author Artem Zatsarynnyi
 */
public class CommandEditorViewImpl extends Composite implements CommandEditorView {

    private static final CommandEditorViewImplUiBinder UI_BINDER        = GWT.create(CommandEditorViewImplUiBinder.class);
    private static final Window.Resources              WINDOW_RESOURCES = GWT.create(Window.Resources.class);

    @UiField
    Button saveButton;

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    FlowPanel pagesPanel;

    /** The delegate to receive events from this view. */
    private ActionDelegate delegate;

    @Inject
    public CommandEditorViewImpl() {
        initWidget(UI_BINDER.createAndBindUi(this));

        setSaveEnabled(false);

        saveButton.addStyleName(WINDOW_RESOURCES.windowCss().primaryButton());
    }

    @Override
    public void addPage(IsWidget page, String title) {
        final DisclosurePanel panel = new DisclosurePanel(title);
        panel.setAnimationEnabled(true);
        panel.setContent(page.asWidget());

        // expand the 1`st panel only
        if (pagesPanel.getWidgetCount() == 0) {
            panel.setOpen(true);
        }

        pagesPanel.add(panel);
    }

    @Override
    public void setSaveEnabled(boolean enable) {
        saveButton.setEnabled(enable);
    }

    @UiHandler("saveButton")
    public void handleSaveButton(ClickEvent clickEvent) {
        delegate.onCommandSave();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    interface CommandEditorViewImplUiBinder extends UiBinder<Widget, CommandEditorViewImpl> {
    }
}