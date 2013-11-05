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
package cc.kune.core.client.rpcservices;

import cc.kune.common.client.notify.NotifyUser;
import cc.kune.common.client.utils.OnAcceptCallback;
import cc.kune.common.shared.i18n.I18nTranslationService;
import cc.kune.core.client.events.MyGroupsChangedEvent;
import cc.kune.core.client.state.Session;
import cc.kune.core.client.state.StateManager;
import cc.kune.core.shared.domain.utils.StateToken;
import cc.kune.core.shared.dto.SocialNetworkDataDTO;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class SocialNetServiceHelper {

  private final EventBus eventbus;
  private final I18nTranslationService i18n;
  private final Session session;
  private final Provider<SocialNetServiceAsync> snServiceProvider;
  private final StateManager stateManager;

  @Inject
  public SocialNetServiceHelper(final StateManager stateManager, final Session session,
      final EventBus eventbus, final I18nTranslationService i18n,
      final Provider<SocialNetServiceAsync> snServiceProvider) {
    this.stateManager = stateManager;
    this.session = session;
    this.eventbus = eventbus;
    this.i18n = i18n;
    this.snServiceProvider = snServiceProvider;
  }

  public void changeToAdmin(final String shortName) {
    NotifyUser.showProgress();
    snServiceProvider.get().setCollabAsAdmin(session.getUserHash(),
        session.getCurrentState().getStateToken(), shortName,
        new AsyncCallbackSimple<SocialNetworkDataDTO>() {
          @Override
          public void onSuccess(final SocialNetworkDataDTO result) {
            SocialNetServiceHelper.this.onSuccess(result);
          }
        });
  }

  public void changeToCollab(final String shortName) {
    NotifyUser.showProgress();
    snServiceProvider.get().setAdminAsCollab(session.getUserHash(),
        session.getCurrentState().getStateToken(), shortName,
        new AsyncCallbackSimple<SocialNetworkDataDTO>() {
          @Override
          public void onSuccess(final SocialNetworkDataDTO result) {
            SocialNetServiceHelper.this.onSuccess(result);
          }
        });
  }

  private void onSuccess(final SocialNetworkDataDTO result) {
    NotifyUser.hideProgress();
    NotifyUser.info(i18n.t("Member type changed"));
    stateManager.setSocialNetwork(result);
  }

  public void unJoinGroup(final StateToken groupToken) {
    NotifyUser.askConfirmation(i18n.t("Leave this group"), i18n.t("Are you sure?"),
        new OnAcceptCallback() {
          @Override
          public void onSuccess() {
            NotifyUser.showProgress();
            snServiceProvider.get().unJoinGroup(session.getUserHash(), groupToken,
                new AsyncCallbackSimple<Void>() {
                  @Override
                  public void onSuccess(final Void result) {
                    NotifyUser.hideProgress();
                    NotifyUser.info(i18n.t("Removed as member"));
                    stateManager.refreshCurrentStateWithoutCache();
                    MyGroupsChangedEvent.fire(eventbus);
                  }
                });
          }
        });
  }
}
