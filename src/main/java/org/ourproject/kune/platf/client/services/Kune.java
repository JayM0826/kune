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
 */

package org.ourproject.kune.platf.client.services;

import org.ourproject.kune.platf.client.KunePlatform;
import org.ourproject.kune.platf.client.app.Application;
import org.ourproject.kune.platf.client.state.Session;
import org.ourproject.kune.platf.client.state.StateManager;
import org.ourproject.kune.workspace.client.i18n.I18nUITranslationService;
import org.ourproject.kune.workspace.client.workspace.Workspace;

import com.calclab.emiteuimodule.client.EmiteUIDialog;
import com.calclab.modular.client.container.Container;
import com.calclab.modular.client.container.DelegatedContainer;
import com.calclab.modular.client.modules.Module;
import com.calclab.modular.client.modules.ModuleBuilder;

public class Kune extends DelegatedContainer {

    public static Kune create(final Module... modules) {
	final ModuleBuilder container = new ModuleBuilder();
	container.add(modules);
	return container.getInstance(Kune.class);
    }

    protected Kune(final Container container) {
	super(container);
    }

    public ColorTheme getColorTheme() {
	return this.getInstance(ColorTheme.class);
    }

    public EmiteUIDialog getEmiteUIDialog() {
	return this.getInstance(EmiteUIDialog.class);
    }

    public I18nUITranslationService getI18N() {
	return this.getInstance(I18nUITranslationService.class);
    }

    public KunePlatform getPlatform() {
	return this.getInstance(KunePlatform.class);
    }

    public Session getSession() {
	return this.getInstance(Session.class);
    }

    public StateManager getStateManager() {
	return this.getInstance(StateManager.class);
    }

    public Workspace getWorkspace() {
	return this.getInstance(Application.class).getWorkspace();
    }

}
