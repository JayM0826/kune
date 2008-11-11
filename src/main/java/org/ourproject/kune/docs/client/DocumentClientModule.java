/*
 *
 * Copyright (C) 2007-2008 The kune development team (see CREDITS for details)
 * This file is part of kune.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */package org.ourproject.kune.docs.client;

import org.ourproject.kune.docs.client.cnt.DocFolderContent;
import org.ourproject.kune.docs.client.cnt.DocFolderContentPanel;
import org.ourproject.kune.docs.client.cnt.DocFolderContentPresenter;
import org.ourproject.kune.docs.client.cnt.DocFolderContentView;
import org.ourproject.kune.docs.client.cnt.DocumentViewer;
import org.ourproject.kune.docs.client.cnt.DocumentViewerPanel;
import org.ourproject.kune.docs.client.cnt.DocumentViewerPresenter;
import org.ourproject.kune.docs.client.cnt.DocumentViewerView;
import org.ourproject.kune.docs.client.ctx.DocumentContext;
import org.ourproject.kune.docs.client.ctx.DocumentContextPresenter;
import org.ourproject.kune.docs.client.ctx.admin.DocContextPropEditor;
import org.ourproject.kune.docs.client.ctx.admin.DocContextPropEditorPanel;
import org.ourproject.kune.docs.client.ctx.admin.DocContextPropEditorPresenter;
import org.ourproject.kune.docs.client.ctx.admin.DocContextPropEditorView;
import org.ourproject.kune.platf.client.actions.ContentActionRegistry;
import org.ourproject.kune.platf.client.actions.ContentIconsRegistry;
import org.ourproject.kune.platf.client.actions.ContextActionRegistry;
import org.ourproject.kune.platf.client.actions.DragDropContentRegistry;
import org.ourproject.kune.platf.client.actions.RenamableContentRegistry;
import org.ourproject.kune.platf.client.actions.toolbar.ActionContentToolbar;
import org.ourproject.kune.platf.client.app.ToolGroup;
import org.ourproject.kune.platf.client.rpc.ContentServiceAsync;
import org.ourproject.kune.platf.client.rpc.GroupServiceAsync;
import org.ourproject.kune.platf.client.services.I18nTranslationService;
import org.ourproject.kune.platf.client.services.KuneErrorHandler;
import org.ourproject.kune.platf.client.state.Session;
import org.ourproject.kune.platf.client.state.StateManager;
import org.ourproject.kune.platf.client.tool.ToolSelector;
import org.ourproject.kune.platf.client.ui.download.FileDownloadUtils;
import org.ourproject.kune.platf.client.ui.upload.FileUploader;
import org.ourproject.kune.workspace.client.ctxnav.ContextNavigator;
import org.ourproject.kune.workspace.client.editor.TextEditor;
import org.ourproject.kune.workspace.client.entitylogo.EntityLogo;
import org.ourproject.kune.workspace.client.i18n.I18nUITranslationService;
import org.ourproject.kune.workspace.client.i18n.LanguageSelector;
import org.ourproject.kune.workspace.client.skel.WorkspaceSkeleton;
import org.ourproject.kune.workspace.client.tags.TagsSummary;
import org.ourproject.kune.workspace.client.themes.WsThemePresenter;
import org.ourproject.kune.workspace.client.title.EntitySubTitle;
import org.ourproject.kune.workspace.client.title.EntityTitle;

import com.calclab.suco.client.ioc.decorator.Singleton;
import com.calclab.suco.client.ioc.module.AbstractModule;
import com.calclab.suco.client.ioc.module.Factory;

public class DocumentClientModule extends AbstractModule {

    @Override
    public void onInstall() {

        register(ToolGroup.class, new Factory<DocumentClientTool>(DocumentClientTool.class) {
            @Override
            public DocumentClientTool create() {
                $(DocumentClientActions.class);
                return new DocumentClientTool($(I18nUITranslationService.class), $(ToolSelector.class),
                        $(WsThemePresenter.class), $(WorkspaceSkeleton.class), $(DragDropContentRegistry.class),
                        $(ContentIconsRegistry.class), $(RenamableContentRegistry.class));
            }
        });

        register(ToolGroup.class, new Factory<DocumentClientActions>(DocumentClientActions.class) {
            @Override
            public DocumentClientActions create() {
                return new DocumentClientActions($(I18nUITranslationService.class), $(ContextNavigator.class),
                        $(Session.class), $(StateManager.class), $$(ContentServiceAsync.class),
                        $$(GroupServiceAsync.class), $$(FileUploader.class), $(ContentActionRegistry.class),
                        $(ContextActionRegistry.class), $$(FileDownloadUtils.class), $(EntityLogo.class),
                        $$(TextEditor.class), $(KuneErrorHandler.class), $(DocumentViewer.class),
                        $$(DocContextPropEditor.class));
            }
        });

        register(Singleton.class, new Factory<DocContextPropEditor>(DocContextPropEditor.class) {
            @Override
            public DocContextPropEditor create() {
                final DocContextPropEditorPresenter presenter = new DocContextPropEditorPresenter($(Session.class),
                        $(StateManager.class), $$(TagsSummary.class), $$(ContentServiceAsync.class),
                        $(EntityTitle.class), $(EntitySubTitle.class));
                final DocContextPropEditorView view = new DocContextPropEditorPanel(presenter,
                        $(I18nUITranslationService.class), $(WorkspaceSkeleton.class), $$(LanguageSelector.class));
                presenter.init(view);
                return presenter;
            }
        });

        register(ToolGroup.class, new Factory<DocumentContext>(DocumentContext.class) {
            @Override
            public DocumentContext create() {
                final DocumentContextPresenter presenter = new DocumentContextPresenter($(StateManager.class),
                        $$(ContextNavigator.class), $$(DocContextPropEditor.class));
                return presenter;
            }
        });

        register(Singleton.class, new Factory<DocumentViewer>(DocumentViewer.class) {
            @Override
            public DocumentViewer create() {
                final DocumentViewerPresenter presenter = new DocumentViewerPresenter($(StateManager.class),
                        $(Session.class), $(I18nUITranslationService.class), $(ActionContentToolbar.class),
                        $(ContentActionRegistry.class), $$(FileDownloadUtils.class));
                final DocumentViewerView view = new DocumentViewerPanel($(WorkspaceSkeleton.class),
                        $(I18nTranslationService.class));
                presenter.init(view);
                return presenter;
            }
        });

        register(ToolGroup.class, new Factory<DocFolderContent>(DocFolderContent.class) {
            @Override
            public DocFolderContent create() {
                final DocFolderContentPresenter presenter = new DocFolderContentPresenter($(StateManager.class),
                        $(Session.class), $(ActionContentToolbar.class), $(ContentActionRegistry.class));
                final DocFolderContentView view = new DocFolderContentPanel($(WorkspaceSkeleton.class),
                        $(I18nTranslationService.class));
                presenter.init(view);
                return presenter;
            }
        });
    }
}
