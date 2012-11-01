/*
 *
 * Copyright (C) 2007-2012 The kune development team (see CREDITS for details)
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
package cc.kune.core.client.sn.actions;

import cc.kune.chat.client.ChatInstances;
import cc.kune.chat.client.ChatOptions;
import cc.kune.chat.client.LastConnectedManager;
import cc.kune.chat.client.actions.StartChatWithMemberAction;
import cc.kune.common.client.actions.AbstractAction;
import cc.kune.common.client.actions.PropertyChangeEvent;
import cc.kune.common.client.actions.PropertyChangeListener;
import cc.kune.common.client.actions.ui.descrip.ButtonDescriptor;
import cc.kune.core.client.contacts.SimpleContactManager;
import cc.kune.core.client.events.StateChangedEvent;
import cc.kune.core.client.events.StateChangedEvent.StateChangedHandler;
import cc.kune.core.client.state.Session;
import cc.kune.core.client.state.StateManager;
import cc.kune.core.client.ws.entheader.EntityHeader;
import cc.kune.core.shared.dto.GroupDTO;
import cc.kune.core.shared.dto.StateAbstractDTO;

import com.calclab.emite.im.client.roster.events.RosterItemChangedEvent;
import com.calclab.emite.im.client.roster.events.RosterItemChangedHandler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.inject.Inject;

public class BuddyLastConnectedHeaderLabel {

  private final ButtonDescriptor button;
  private final LastConnectedManager lastConnectedManager;

  @Inject
  public BuddyLastConnectedHeaderLabel(final StartChatWithMemberAction chatAction,
      final EntityHeader entityHeader, final StateManager stateManager, final Session session,
      final SimpleContactManager simpleContactManager, final LastConnectedManager lastConnectedManager,
      final ChatInstances chatInstances, final ChatOptions chatOptions) {
    this.lastConnectedManager = lastConnectedManager;
    button = new ButtonDescriptor(chatAction);
    button.setStyles("k-buddy-last-connected");
    chatAction.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(final PropertyChangeEvent event) {
        if (event.getPropertyName().equals(AbstractAction.ENABLED)) {
          button.setVisible((Boolean) event.getNewValue());
        }
      }
    });
    chatInstances.roster.addRosterItemChangedHandler(new RosterItemChangedHandler() {
      @Override
      public void onRosterItemChanged(final RosterItemChangedEvent event) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
          @Override
          public void execute() {
            final Object target = button.getTarget();
            if (target instanceof GroupDTO) {
              final String username = ((GroupDTO) target).getShortName();
              if (event.getRosterItem().getJID().equals(chatOptions.uriFrom(username))) {
                // Ok, is this user, we set the last connected info
                setLabelText(username);
              }
            }
          }

        });
      }
    });
    chatAction.setEnabled(false);
    entityHeader.addAction(button);
    stateManager.onStateChanged(true, new StateChangedHandler() {
      @Override
      public void onStateChanged(final StateChangedEvent event) {
        final StateAbstractDTO state = event.getState();
        final GroupDTO group = state.getGroup();
        final String groupName = group.getShortName();
        final boolean imLogged = session.isLogged();
        final boolean isBuddie = simpleContactManager.isBuddy(groupName);
        if (imLogged && group.isPersonal() && isBuddie
            && !session.getCurrentUser().getShortName().equals(groupName)) {
          button.setTarget(group);
          setLabelText(groupName);
          chatAction.setEnabled(true);
        } else {
          chatAction.setEnabled(false);
        }
      }
    });
  }

  private void setLabelText(final String username) {
    button.withText(lastConnectedManager.get(username, true));
  }
}
