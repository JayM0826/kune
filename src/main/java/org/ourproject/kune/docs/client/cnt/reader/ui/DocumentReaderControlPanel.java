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

package org.ourproject.kune.docs.client.cnt.reader.ui;

import org.ourproject.kune.docs.client.cnt.reader.DocumentReaderControlView;
import org.ourproject.kune.docs.client.cnt.reader.DocumentReaderListener;
import org.ourproject.kune.platf.client.services.Kune;
import org.ourproject.kune.platf.client.ui.CustomPushButton;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DocumentReaderControlPanel extends HorizontalPanel implements DocumentReaderControlView {
    private final CustomPushButton editBtn;

    public DocumentReaderControlPanel(final DocumentReaderListener listener) {
        editBtn = new CustomPushButton(Kune.I18N.tWithNT("Edit", "in button"), new ClickListener() {
            public void onClick(final Widget sender) {
                listener.onEdit();
            }
        });
        add(editBtn);
        setEditEnabled(false);
    }

    public void setEditEnabled(final boolean isEnabled) {
        editBtn.setEnabled(isEnabled);
    }

}
