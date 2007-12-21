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

package org.ourproject.kune.sitebar.client.rpc;

import org.ourproject.kune.platf.client.dto.LicenseDTO;
import org.ourproject.kune.platf.client.dto.UserCompleteDTO;
import org.ourproject.kune.sitebar.client.Site;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SiteBarServiceMocked implements UserServiceAsync {

    public void login(final String nick, final String pass, final AsyncCallback callback) {
        Site.showProgress("Login");
        Timer timer = new Timer() {
            public void run() {
                Site.hideProgress();
                callback.onSuccess("ThisIsTheUserHash");
            }
        };
        timer.schedule(1000);
    }

    public void logout(final String userHash, final AsyncCallback callback) {
        timerAndSuccess(callback);
    }

    private void timerAndSuccess(final AsyncCallback callback) {
        Timer timer = new Timer() {
            public void run() {
                callback.onSuccess(null);
            }
        };
        timer.schedule(1000);
    }

    public void createUser(final String shortName, final String longName, final String email, final String paswd,
            final LicenseDTO license, final String lang, final String country, final String timezone,
            final AsyncCallback asyncCallback) {
        timerAndSuccess(asyncCallback);
    }

    public void createUser(final UserCompleteDTO user, final AsyncCallback asyncCallback) {
        timerAndSuccess(asyncCallback);
    }

    public void reloadUserInfo(final String userHash, final AsyncCallback asyncCallback) {
        timerAndSuccess(asyncCallback);
    }
}
