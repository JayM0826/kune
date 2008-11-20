/*
 *
 * Copyright (C) 2007-2008 The kune development team (see CREDITS for details)
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
package org.ourproject.kune.platf.server.state;

import org.ourproject.kune.platf.client.dto.GroupType;
import org.ourproject.kune.platf.client.services.I18nTranslationService;
import org.ourproject.kune.platf.server.access.AccessRightsService;
import org.ourproject.kune.platf.server.content.ContentManager;
import org.ourproject.kune.platf.server.domain.Container;
import org.ourproject.kune.platf.server.domain.Content;
import org.ourproject.kune.platf.server.domain.Group;
import org.ourproject.kune.platf.server.domain.License;
import org.ourproject.kune.platf.server.domain.Revision;
import org.ourproject.kune.platf.server.domain.User;
import org.ourproject.kune.platf.server.manager.GroupManager;
import org.ourproject.kune.platf.server.manager.SocialNetworkManager;
import org.ourproject.kune.platf.server.manager.TagManager;
import org.ourproject.kune.platf.server.manager.UserManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StateServiceDefault implements StateService {

    private final AccessRightsService rightsService;
    private final UserManager userManager;
    private final SocialNetworkManager socialNetworkManager;
    private final GroupManager groupManager;
    private final TagManager tagManager;
    private final ContentManager contentManager;
    private final I18nTranslationService i18n;

    @Inject
    public StateServiceDefault(UserManager userManager, GroupManager groupManager,
            SocialNetworkManager socialNetworkManager, ContentManager contentManager, TagManager tagManager,
            AccessRightsService rightsService, I18nTranslationService i18n) {
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.socialNetworkManager = socialNetworkManager;
        this.contentManager = contentManager;
        this.tagManager = tagManager;
        this.rightsService = rightsService;
        this.i18n = i18n;
    }

    public StateContainer create(User userLogged, Container container) {
        final StateContainer state = new StateContainer();
        state.setTitle(container.getName());
        state.setTypeId(container.getTypeId());
        state.setLanguage(container.getLanguage());
        state.setStateToken(container.getStateToken());
        state.setRootContainer(calculateRootContainer(container));
        state.setLicense(container.getOwner().getDefaultLicense());
        state.setAccessLists(container.getAccessLists());
        Group group = container.getOwner();
        setCommon(state, userLogged, group, container);
        return state;
    }

    public StateContent create(User userLogged, Content content) {
        StateContent state = new StateContent();
        state.setTypeId(content.getTypeId());
        state.setMimeType(content.getMimeType());
        state.setDocumentId(content.getId().toString());
        state.setLanguage(content.getLanguage());
        state.setPublishedOn(content.getPublishedOn());
        state.setAuthors(content.getAuthors());
        state.setTags(content.getTagsAsString());
        state.setStatus(content.getStatus());
        state.setStateToken(content.getStateToken());
        Revision revision = content.getLastRevision();
        state.setTitle(revision.getTitle());
        state.setVersion(content.getVersion());
        char[] text = revision.getBody();
        state.setContent(text == null ? null : new String(text));
        Container container = content.getContainer();
        state.setRootContainer(calculateRootContainer(container));
        License license = content.getLicense();
        Group group = container.getOwner();
        state.setLicense(license == null ? group.getDefaultLicense() : license);
        state.setContentRights(rightsService.get(userLogged, content.getAccessLists()));
        state.setAccessLists(content.getAccessLists());
        setCommon(state, userLogged, group, container);
        if (userLogged != User.UNKNOWN_USER) {
            state.setCurrentUserRate(contentManager.getRateContent(userLogged, content));
        }
        // FIXME: user RateResult
        Double rateAvg = contentManager.getRateAvg(content);
        state.setRate(rateAvg != null ? rateAvg : 0D);
        Long rateByUsers = contentManager.getRateByUsers(content);
        state.setRateByUsers(rateByUsers != null ? rateByUsers.intValue() : 0);
        return state;
    }

    public StateNoContent createNoHome(User userLogged, String groupShortName) {
        Group group = groupManager.findByShortName(groupShortName);
        assert (group.getGroupType().equals(GroupType.PERSONAL));
        StateNoContent state = new StateNoContent();
        state.setGroup(group);
        state.setGroupRights(rightsService.get(userLogged, group.getAccessLists()));
        state.setEnabledTools(groupManager.findEnabledTools(group.getId()));
        setSocialNetwork(state, userLogged, group);
        state.setStateToken(group.getStateToken());
        state.setTitle("<h2>" + i18n.t("This user doesn't have a homepage") + "</h2>");
        return state;
    }

    private Container calculateRootContainer(Container container) {
        return container.isRoot() ? container : container.getAbsolutePath().get(0);
    }

    private void setCommon(StateContainer state, User userLogged, Group group, Container container) {
        state.setToolName(container.getToolName());
        state.setGroup(group);
        state.setContainer(container);
        state.setGroupRights(rightsService.get(userLogged, group.getAccessLists()));
        state.setContainerRights(rightsService.get(userLogged, container.getAccessLists()));
        state.setEnabledTools(groupManager.findEnabledTools(group.getId()));
        state.setTagCloudResult(tagManager.getTagCloudResultByGroup(group));
        setSocialNetwork(state, userLogged, group);
    }

    private void setSocialNetwork(StateAbstract state, User userLogged, Group group) {
        state.setGroupMembers(socialNetworkManager.get(userLogged, group));
        state.setParticipation(socialNetworkManager.findParticipation(userLogged, group));
        if (group.getGroupType().equals(GroupType.PERSONAL)) {
            state.setUserBuddies(userManager.getUserBuddies(group.getShortName()));
        }
    }
}
