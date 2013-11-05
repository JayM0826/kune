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

package cc.kune.core.client.invitation;

import cc.kune.core.client.auth.UserFieldFactory;
import cc.kune.core.client.i18n.I18nUITranslationService;

public class SiteInvitationConfirmDialog extends AbstractInvitationConfirmDialog {
  public static class Builder extends AbstractInvitationConfirmDialog.Builder {

    public static final String DIALOG_ID = "k-site-inv-confirm-diag-id";

    public Builder(final String redirect, final I18nUITranslationService i18n,
        final String userWhoInvites) {
      super(
          DIALOG_ID,
          i18n.t("Invitation to join this site"),
          i18n.t(
              "[%s] has invited you to join [%s]. Please [%s] to accept the invitation and register in this site, or just [%s] if you already have an account.",
              userWhoInvites, i18n.getSiteCommonName(),
              UserFieldFactory.getRegisterLink(i18n.t("click here"), redirect),
              UserFieldFactory.getSignInLink(redirect)));
    }

  }

  protected SiteInvitationConfirmDialog(final SiteInvitationConfirmDialog.Builder builder) {
    super(builder);
  }

}
