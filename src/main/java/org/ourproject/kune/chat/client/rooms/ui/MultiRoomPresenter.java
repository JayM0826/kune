/*
 *
 * Copyright (C) 2007 The kune development team (see CREDITS for details)
 * This file is part of kune.
 *
 * Kune is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kune is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.ourproject.kune.chat.client.rooms.ui;

import org.ourproject.kune.chat.client.rooms.MultiRoom;
import org.ourproject.kune.chat.client.rooms.Room;
import org.ourproject.kune.chat.client.rooms.ui.RoomUser.UserType;
import org.ourproject.kune.chat.client.ui.ChatFactory;

public class MultiRoomPresenter implements MultiRoom {
    private MultiRoomPanel view;
    private Room currentRoom;
    private final MultiRoomListener listener;

    public MultiRoomPresenter(final MultiRoomListener listener) {
	this.listener = listener;
    }

    public void init(final MultiRoomPanel view) {
	this.view = view;
    }

    public Room createRoom(final String roomName, final String userAlias, final UserType type) {
	RoomUserList userList = ChatFactory.createUserList();
	RoomPresenter room = new RoomPresenter(roomName, userAlias, type, userList);
	room.addUser(userAlias, type);
	view.createRoom(room);
	currentRoom = room;
	RoomUserList roomUserList = room.getUsersList();
	view.addRoomUsersPanel(roomUserList.getView());
	return currentRoom;
    }

    public void show() {
	view.show();
    }

    public void onSend() {
	listener.onSendMessage(currentRoom, view.getInputText());
    }

    protected void onSend(final int key, final boolean isCtrl) {
	if (key == 13 && !isCtrl) {
	    onSend();
	}
    }

    // TODO: vicente, mira esto
    protected void onOLDSend() {
	// TODO: Call to xmpp, meawhile:
	String userAlias = currentRoom.getSessionUserAlias();

	currentRoom.addMessage(userAlias, view.getInputText());
	currentRoom.clearSavedInput();
	view.clearInputText();
	// view.sendBtnEnable(false);

	// CUIDADO: DETALLE DE VISTA
	// if (key == KeyboardListener.KEY_ENTER) {
	// if (mod == KeyboardListener.MODIFIER_CTRL) {
	// view.insertReturnInInput();
	// } else {
	// view.addMessage(currentRoom, currentUserAlias, view.getInputText());
	// view.clearTextArea();
	// }
	// }
    }

    protected void onNoRooms() {
	// TODO
	view.hide();
    }

    public void closeRoom(final RoomPresenter room) {
	room.doClose();
    }

    public void activateRoom(final Room nextRoom) {
	currentRoom.saveInput(view.getInputText());
	currentRoom = nextRoom;
	String savedInput = currentRoom.getSavedInput();
	if (!savedInput.equals("")) {
	    view.setInputText(savedInput);
	}
	view.setSubject(currentRoom.getSubject());
	view.showUserList(currentRoom.getUsersListView());
    }

}
