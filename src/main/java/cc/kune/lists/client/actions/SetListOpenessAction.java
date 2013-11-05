/*
 *
 * Copyright (C) 2007-2013 Licensed to the Comunes Association (CA) under
 * one or more contributor license agreements (see COPYRIGHT for details).
 * The CA licenses this file to you under the GNU Affero General Public
 * License version 3, (the "License"); you may not use this file except in
 * compliance with the License. This file is part of kune.
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
package cc.kune.lists.client.actions;

import cc.kune.common.client.actions.ActionEvent;
import cc.kune.common.client.notify.NotifyUser;
import cc.kune.common.shared.i18n.I18nTranslationService;
import cc.kune.core.client.actions.RolAction;
import cc.kune.core.client.rpcservices.AsyncCallbackSimple;
import cc.kune.core.client.state.Session;
import cc.kune.core.client.state.StateManager;
import cc.kune.core.shared.dto.AccessRolDTO;
import cc.kune.core.shared.dto.StateContainerDTO;
import cc.kune.lists.client.rpc.ListsServiceAsync;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SetListOpenessAction extends RolAction {

  public static final String ISPUBLIC = "stla-ispublic";

  private final I18nTranslationService i18n;
  private final Provider<ListsServiceAsync> listService;
  private final Session session;
  private final StateManager stateManager;

  @Inject
  public SetListOpenessAction(final I18nTranslationService i18n, final StateManager stateManager,
      final Session session, final Provider<ListsServiceAsync> listService) {
    super(AccessRolDTO.Administrator, true);
    this.i18n = i18n;
    this.stateManager = stateManager;
    this.session = session;
    this.listService = listService;
  }

  @Override
  public void actionPerformed(final ActionEvent event) {
    NotifyUser.showProgress();
    final boolean setPublic = !isPublic();
    listService.get().setPublic(session.getUserHash(), session.getCurrentStateToken(), setPublic,
        new AsyncCallbackSimple<StateContainerDTO>() {
          @Override
          public void onSuccess(final StateContainerDTO result) {
            NotifyUser.info(setPublic ? i18n.t("This list is now public")
                : i18n.t("This list is now restricted to the public"));
            stateManager.setRetrievedState(result);
            stateManager.refreshCurrentState();
            NotifyUser.hideProgress();
          }
        });
  }

  private Boolean isPublic() {
    return (Boolean) getValue(ISPUBLIC);
  }

}
