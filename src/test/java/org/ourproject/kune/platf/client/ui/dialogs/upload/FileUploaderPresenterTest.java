/*
 *
 * Copyright (C) 2007-2011 The kune development team (see CREDITS for details)
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
 */
package org.ourproject.kune.platf.client.ui.dialogs.upload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ourproject.kune.workspace.client.upload.FileUploaderPresenter;
import org.ourproject.kune.workspace.client.upload.FileUploaderView;

import cc.kune.core.client.state.Session;
import cc.kune.core.shared.domain.utils.StateToken;
import cc.kune.docs.shared.DocsConstants;

public class FileUploaderPresenterTest {

    private static final String SOMEUSER_HASH = "someuserHash";
    private FileUploaderPresenter presenter;
    private Session session;
    private FileUploaderView view;

    @Before
    public void before() {
        session = Mockito.mock(Session.class);
        view = Mockito.mock(FileUploaderView.class);
        presenter = new FileUploaderPresenter(session);
        presenter.init(view);
        Mockito.when(session.getUserHash()).thenReturn(SOMEUSER_HASH);
    }

    @Test
    public void testFirstAddFromDocInSameContainer() {
        Mockito.when(session.getCurrentStateToken()).thenReturn(new StateToken("group.docs.1.1"));
        assertTrue(presenter.checkFolderChange());
        Mockito.verify(view, Mockito.times(1)).setUploadParams(SOMEUSER_HASH, "group.docs.1",
                DocsConstants.TYPE_UPLOADEDFILE);
    }

    @Test
    public void testFirstAddInSameContainer() {
        Mockito.when(session.getCurrentStateToken()).thenReturn(new StateToken("group.docs.1"));
        assertTrue(presenter.checkFolderChange());
        Mockito.verify(view, Mockito.times(1)).setUploadParams(SOMEUSER_HASH, "group.docs.1",
                DocsConstants.TYPE_UPLOADEDFILE);
    }

    @Test
    public void testSomeAddsInDiffContainersButNotUploading() {
        Mockito.when(session.getCurrentStateToken()).thenReturn(new StateToken("group.docs.1"));
        assertTrue(presenter.checkFolderChange());
        Mockito.when(view.hasUploadingFiles()).thenReturn(false);
        Mockito.when(session.getCurrentStateToken()).thenReturn(new StateToken("group.docs.2"));
        assertTrue(presenter.checkFolderChange());
        Mockito.verify(view, Mockito.times(1)).setUploadParams(SOMEUSER_HASH, "group.docs.1",
                DocsConstants.TYPE_UPLOADEDFILE);
        Mockito.verify(view, Mockito.times(1)).setUploadParams(SOMEUSER_HASH, "group.docs.2",
                DocsConstants.TYPE_UPLOADEDFILE);
    }

    @Test
    public void testSomeAddsInDiffContainersButUploading() {
        Mockito.when(session.getCurrentStateToken()).thenReturn(new StateToken("group.docs.1"));
        assertTrue(presenter.checkFolderChange());
        Mockito.when(view.hasUploadingFiles()).thenReturn(true);
        Mockito.when(session.getCurrentStateToken()).thenReturn(new StateToken("group.docs.2"));
        assertFalse(presenter.checkFolderChange());
        Mockito.verify(view, Mockito.times(1)).setUploadParams(SOMEUSER_HASH, "group.docs.1",
                DocsConstants.TYPE_UPLOADEDFILE);
        Mockito.verify(view, Mockito.never()).setUploadParams(SOMEUSER_HASH, "group.docs.2",
                DocsConstants.TYPE_UPLOADEDFILE);
    }

    @Test
    public void testSomeAddsInSameContainer() {
        Mockito.when(session.getCurrentStateToken()).thenReturn(new StateToken("group.docs.1"));
        assertTrue(presenter.checkFolderChange());
        Mockito.when(session.getCurrentStateToken()).thenReturn(new StateToken("group.docs.1"));
        assertTrue(presenter.checkFolderChange());
        Mockito.verify(view, Mockito.times(2)).setUploadParams(SOMEUSER_HASH, "group.docs.1",
                DocsConstants.TYPE_UPLOADEDFILE);
    }
}
