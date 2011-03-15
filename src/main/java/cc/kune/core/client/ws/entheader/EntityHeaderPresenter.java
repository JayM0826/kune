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
 \*/
package cc.kune.core.client.ws.entheader;

import cc.kune.common.client.actions.ui.descrip.GuiActionDescrip;
import cc.kune.core.client.state.GroupChangedEvent;
import cc.kune.core.client.state.GroupChangedEvent.GroupChangedHandler;
import cc.kune.core.client.state.Session;
import cc.kune.core.client.state.StateManager;
import cc.kune.core.shared.domain.utils.StateToken;
import cc.kune.core.shared.dto.GroupDTO;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class EntityHeaderPresenter extends
        Presenter<EntityHeaderPresenter.EntityHeaderView, EntityHeaderPresenter.EntityHeaderProxy> implements
        EntityHeader {

    @ProxyCodeSplit
    public interface EntityHeaderProxy extends Proxy<EntityHeaderPresenter> {
    }
    public interface EntityHeaderView extends View {
        void addAction(GuiActionDescrip descriptor);

        void addWidget(IsWidget widget);

        void reloadImage(GroupDTO group);

        void setLargeFont();

        void setLogoImage(StateToken stateToken);

        void setLogoImageVisible(boolean visible);

        void setLogoText(final String groupName);

        void setMediumFont();

        void setSmallFont();

        // void setTheme(final WsTheme oldTheme, WsTheme newTheme);

        void showDefUserLogo();
    }

    private static final int LARGE_NAME_LIMIT = 17;
    private static final int MEDIUM_NAME_LIMIT = 90;
    private final Session session;

    @Inject
    public EntityHeaderPresenter(final EventBus eventBus, final EntityHeaderView view, final EntityHeaderProxy proxy,
            final StateManager stateManager, final Session session) {
        super(eventBus, view, proxy);
        this.session = session;
        stateManager.onGroupChanged(true, new GroupChangedHandler() {
            @Override
            public void onGroupChanged(final GroupChangedEvent event) {
                setGroupLogo(session.getCurrentState().getGroup());
            }
        });
        // theme.addOnThemeChanged(new Listener2<WsTheme, WsTheme>() {
        // @Override
        // public void onEvent(final WsTheme oldTheme, final WsTheme newTheme) {
        // // getView().setTheme(oldTheme, newTheme);
        // }
        // });
    }

    @Override
    public void addAction(final GuiActionDescrip descriptor) {
        getView().addAction(descriptor);
    }

    @Override
    public void addWidget(final IsWidget widget) {
        getView().addWidget(widget);
    }

    @Override
    public void refreshGroupLogo() {
        setGroupLogo(session.getCurrentState().getGroup());
    }

    @Override
    public void reloadGroupLogoImage() {
        getView().reloadImage(session.getCurrentState().getGroup());
    }

    @Override
    protected void revealInParent() {
        RevealRootContentEvent.fire(this, this);
    }

    void setGroupLogo(final GroupDTO group) {
        if (group.hasLogo()) {
            setLogoText(group.getLongName());
            getView().setLogoImage(group.getStateToken());
            getView().setLogoImageVisible(true);
        } else {
            setLogoText(group.getLongName());
            if (group.isPersonal()) {
                getView().showDefUserLogo();
                getView().setLogoImageVisible(true);
            } else {
                getView().setLogoImageVisible(false);
            }
        }
    }

    void setLogoText(final String name) {
        final int length = name.length();
        if (length <= LARGE_NAME_LIMIT) {
            getView().setLargeFont();
        } else if (length <= MEDIUM_NAME_LIMIT) {
            getView().setMediumFont();
        } else {
            getView().setSmallFont();
        }
        getView().setLogoText(name);
    }
}
