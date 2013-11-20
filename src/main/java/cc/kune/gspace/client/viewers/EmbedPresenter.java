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

package cc.kune.gspace.client.viewers;

import org.waveprotocol.wave.util.escapers.GwtWaverefEncoder;

import cc.kune.common.client.log.Log;
import cc.kune.common.client.notify.NotifyUser;
import cc.kune.common.shared.i18n.I18n;
import cc.kune.core.client.embed.EmbedSitebar;
import cc.kune.core.client.events.UserSignInOrSignOutEvent;
import cc.kune.core.client.events.UserSignInOrSignOutEvent.UserSignInOrSignOutHandler;
import cc.kune.core.client.i18n.I18nUITranslationService;
import cc.kune.core.client.rpcservices.AsyncCallbackSimple;
import cc.kune.core.client.rpcservices.ContentServiceAsync;
import cc.kune.core.client.rpcservices.SiteServiceAsync;
import cc.kune.core.client.state.Session;
import cc.kune.core.client.state.TokenMatcher;
import cc.kune.core.client.state.impl.HistoryUtils;
import cc.kune.core.shared.domain.utils.StateToken;
import cc.kune.core.shared.dto.InitDataDTO;
import cc.kune.core.shared.dto.StateAbstractDTO;
import cc.kune.core.shared.dto.StateContentDTO;
import cc.kune.core.shared.dto.StateNoContentDTO;
import cc.kune.wave.client.kspecific.WaveClientManager;
import cc.kune.wave.client.kspecific.WaveClientProvider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

/**
 * The Class EmbedPresenter is used to embed kune waves in other CMSs (for
 * instance)
 * 
 * @author vjrj@ourproject.org (Vicente J. Ruiz Jurado)
 */
@Singleton
public class EmbedPresenter extends Presenter<EmbedPresenter.EmbedView, EmbedPresenter.EmbedProxy> {

  /**
   * The Interface EmbedProxy.
   */
  @ProxyStandard
  public interface EmbedProxy extends Proxy<EmbedPresenter> {
  }

  /**
   * The Interface EmbedView.
   */
  public interface EmbedView extends WaveViewerView {
  }

  private final TokenMatcher matcher;
  private final ContentServiceAsync service;
  private final Session session;
  private final Provider<EmbedSitebar> sitebar;

  /**
   * Instantiates a new embed presenter.
   * 
   * @param eventBus
   *          the event bus
   * @param view
   *          the view
   * @param proxy
   *          the proxy
   * @param siteService
   *          the site service
   * @param service
   *          the service
   * @param waveClientManager
   *          the wave client manager
   * @param waveClient
   *          the wave client
   * @param matcher
   *          the matcher
   * @param session
   *          the session
   */
  private Timer timer;

  @Inject
  public EmbedPresenter(final EventBus eventBus, final EmbedView view, final EmbedProxy proxy,
      final SiteServiceAsync siteService, final ContentServiceAsync service,
      final WaveClientManager waveClientManager, final WaveClientProvider waveClient,
      final I18nUITranslationService i18n, final TokenMatcher matcher, final Session session,
      final Provider<EmbedSitebar> sitebar) {
    super(eventBus, view, proxy);
    this.service = service;
    this.matcher = matcher;
    this.session = session;
    this.sitebar = sitebar;
    Log.info("Started embed presenter");
    matcher.init(GwtWaverefEncoder.INSTANCE);
    i18n.setAskChangeToYourLanguage(false);
    siteService.getInitData(session.getUserHash(), new AsyncCallbackSimple<InitDataDTO>() {
      @Override
      public void onSuccess(final InitDataDTO initData) {
        session.setInitData(initData);
        session.setCurrentUserInfo(initData.getUserInfo(), null);
        getContentFromHash();
        // In user sign-in... get again content
        timer = new Timer() {
          @Override
          public void run() {
            getContentFromHash();
          }
        };

        session.onUserSignInOrSignOut(false, new UserSignInOrSignOutHandler() {
          @Override
          public void onUserSignInOrSignOut(final UserSignInOrSignOutEvent event) {
            timer.schedule(1000);
          }
        });
      }
    });

  }

  private void getContentFromHash() {
    final String currentHash = HistoryUtils.undoHashbang(History.getToken());
    if (matcher.isGroupToken(currentHash)) {
      // Ok is a token like group.tool.number
      final StateToken currentToken = new StateToken(currentHash);
      service.getContent(session.getUserHash(), currentToken,
          new AsyncCallbackSimple<StateAbstractDTO>() {
            @Override
            public void onSuccess(final StateAbstractDTO state) {
              NotifyUser.hideProgress();
              if (state.getStateToken().equals(currentToken)) {
                onGetContentSucessful(session, state);
              } else {
                // getContent returns def content if content not found
                notFound();
              }
            }
          });
    } else {
      if (matcher.isWaveToken(currentHash)) {
        service.getContentByWaveRef(session.getUserHash(), currentHash,
            new AsyncCallbackSimple<StateAbstractDTO>() {
              @Override
              public void onSuccess(final StateAbstractDTO state) {
                NotifyUser.hideProgress();
                if (!(state instanceof StateNoContentDTO)) {
                  onGetContentSucessful(session, state);
                } else {
                  // getContent returns def content if content not found
                  notFound();
                }
              }
            });
      } else {
        // Do something
        notFound();
      }
    }
  }

  /**
   * Not found.
   */
  private void notFound() {
    NotifyUser.important(I18n.t("Content not found"));
  }

  private void onGetContentSucessful(final Session session, final StateAbstractDTO state) {
    getView().clear();
    final StateContentDTO stateContent = (StateContentDTO) state;
    // final AccessRights rights = stateContent.getContentRights();
    // Log.info("Content rights: " + rights);

    if (session.isLogged() && stateContent.isParticipant()) {
      getView().setEditableContent(stateContent);
    } else {
      getView().setContent(stateContent);
    }

    // FIXME use GWTP here
    GWT.runAsync(new RunAsyncCallback() {
      @Override
      public void onFailure(final Throwable reason) {
        // By now, do nothing
      }

      @Override
      public void onSuccess() {
        sitebar.get();
      }

    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.gwtplatform.mvp.client.Presenter#revealInParent()
   */
  @Override
  protected void revealInParent() {
    RevealRootContentEvent.fire(this, this);
  }
}