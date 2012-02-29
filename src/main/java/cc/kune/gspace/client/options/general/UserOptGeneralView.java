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
 */
package cc.kune.gspace.client.options.general;

import cc.kune.core.shared.dto.EmailNotificationFrequency;
import cc.kune.core.shared.dto.I18nLanguageSimpleDTO;

import com.google.gwt.event.dom.client.HasClickHandlers;

public interface UserOptGeneralView extends EntityOptGeneralView {

  EmailNotificationFrequency getEmailNotif();

  I18nLanguageSimpleDTO getLanguage();

  String getLongName();

  HasClickHandlers getResendEmailVerif();

  void setEmailVerified(boolean verified);

  void setEmailNotifChecked(EmailNotificationFrequency freq);

  void setLanguage(I18nLanguageSimpleDTO language);

  void setLongName(String longName);

  void setResendEmailVerifEnabled(boolean enabled);

}
