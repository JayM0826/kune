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
package cc.kune.gspace.client.licensewizard;

import cc.kune.common.client.notify.NotifyUser;
import cc.kune.common.shared.i18n.I18nTranslationService;
import cc.kune.common.shared.utils.SimpleResponseCallback;
import cc.kune.core.client.rpcservices.AsyncCallbackSimple;
import cc.kune.core.client.rpcservices.GroupServiceAsync;
import cc.kune.core.client.state.Session;
import cc.kune.core.client.state.StateManager;
import cc.kune.core.shared.domain.utils.StateToken;
import cc.kune.core.shared.dto.LicenseDTO;
import cc.kune.gspace.client.ui.footer.license.LicenseChangedEvent;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LicenseChangeAction {
  private final EventBus eventBus;
  private final Provider<GroupServiceAsync> groupService;
  private final I18nTranslationService i18n;
  private final Session session;
  private final StateManager stateManager;

  @Inject
  public LicenseChangeAction(final Provider<GroupServiceAsync> groupService, final Session session,
      final EventBus eventBus, final I18nTranslationService i18n, final StateManager stateManager) {
    this.groupService = groupService;
    this.session = session;
    this.eventBus = eventBus;
    this.i18n = i18n;
    this.stateManager = stateManager;
  }

  public void changeLicense(final StateToken token, final LicenseDTO license,
      final SimpleResponseCallback callback) {
    NotifyUser.showProgress();
    groupService.get().changeDefLicense(session.getUserHash(), token, license,
        new AsyncCallbackSimple<Void>() {
          @Override
          public void onFailure(final Throwable caught) {
            super.onFailure(caught);
            NotifyUser.hideProgress();
            NotifyUser.error(i18n.t("Error changing license"));
            callback.onCancel();
          }

          @Override
          public void onSuccess(final Void result) {
            stateManager.refreshCurrentState();
            callback.onSuccess();
            LicenseChangedEvent.fire(eventBus);
          }
        });
  }
}
