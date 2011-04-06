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
package cc.kune.core.server.rpc;

import cc.kune.core.client.errors.DefaultException;
import cc.kune.core.client.rpcservices.SocialNetworkService;
import cc.kune.core.server.UserSession;
import cc.kune.core.server.auth.ActionLevel;
import cc.kune.core.server.auth.Authenticated;
import cc.kune.core.server.auth.Authorizated;
import cc.kune.core.server.manager.GroupManager;
import cc.kune.core.server.manager.SocialNetworkManager;
import cc.kune.core.server.mapper.Mapper;
import cc.kune.core.shared.domain.AccessRol;
import cc.kune.core.shared.domain.utils.StateToken;
import cc.kune.core.shared.dto.SocialNetworkDataDTO;
import cc.kune.core.shared.dto.SocialNetworkRequestResult;
import cc.kune.domain.Group;
import cc.kune.domain.User;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class SocialNetworkRPC implements SocialNetworkService, RPC {

    private final GroupManager groupManager;
    private final Mapper mapper;
    private final SocialNetworkManager socialNetworkManager;
    private final Provider<UserSession> userSessionProvider;

    @Inject
    public SocialNetworkRPC(final Provider<UserSession> userSessionProvider, final GroupManager groupManager,
            final SocialNetworkManager socialNetworkManager, final Mapper mapper) {
        this.userSessionProvider = userSessionProvider;
        this.groupManager = groupManager;
        this.socialNetworkManager = socialNetworkManager;
        this.mapper = mapper;
    }

    @Override
    @Authenticated
    @Authorizated(actionLevel = ActionLevel.group, accessRolRequired = AccessRol.Administrator)
    @Transactional
    public SocialNetworkDataDTO acceptJoinGroup(final String hash, final StateToken groupToken,
            final String groupToAcceptShortName) throws DefaultException {
        final UserSession userSession = getUserSession();
        final User userLogged = userSession.getUser();
        final Group group = groupManager.findByShortName(groupToken.getGroup());
        checkIsNotPersonalGroup(group);
        final Group groupToAccept = groupManager.findByShortName(groupToAcceptShortName);
        socialNetworkManager.acceptJoinGroup(userLogged, groupToAccept, group);
        return generateResponse(userLogged, group);
    }

    @Override
    @Authenticated
    @Authorizated(actionLevel = ActionLevel.group, accessRolRequired = AccessRol.Administrator)
    @Transactional
    public SocialNetworkDataDTO addAdminMember(final String hash, final StateToken groupToken,
            final String groupToAddShortName) throws DefaultException {
        final UserSession userSession = getUserSession();
        final User userLogged = userSession.getUser();
        final Group group = groupManager.findByShortName(groupToken.getGroup());
        checkIsNotPersonalGroup(group);
        final Group groupToAdd = groupManager.findByShortName(groupToAddShortName);
        socialNetworkManager.addGroupToAdmins(userLogged, groupToAdd, group);
        return generateResponse(userLogged, group);
    }

    @Override
    @Authenticated
    @Authorizated(actionLevel = ActionLevel.group, accessRolRequired = AccessRol.Administrator)
    @Transactional
    public SocialNetworkDataDTO addCollabMember(final String hash, final StateToken groupToken,
            final String groupToAddShortName) throws DefaultException {
        final UserSession userSession = getUserSession();
        final User userLogged = userSession.getUser();
        final Group group = groupManager.findByShortName(groupToken.getGroup());
        checkIsNotPersonalGroup(group);
        final Group groupToAdd = groupManager.findByShortName(groupToAddShortName);
        socialNetworkManager.addGroupToCollabs(userLogged, groupToAdd, group);
        return generateResponse(userLogged, group);
    }

    @Override
    @Authenticated
    @Authorizated(actionLevel = ActionLevel.group, accessRolRequired = AccessRol.Administrator)
    @Transactional
    public SocialNetworkDataDTO addViewerMember(final String hash, final StateToken groupToken,
            final String groupToAddShortName) throws DefaultException {
        final UserSession userSession = getUserSession();
        final User userLogged = userSession.getUser();
        final Group group = groupManager.findByShortName(groupToken.getGroup());
        checkIsNotPersonalGroup(group);
        final Group groupToAdd = groupManager.findByShortName(groupToAddShortName);
        socialNetworkManager.addGroupToViewers(userLogged, groupToAdd, group);
        return generateResponse(userLogged, group);
    }

    private void checkIsNotPersonalGroup(final Group group) {
        if (group.isPersonal()) {
            throw new DefaultException();
        }
        ;
    }

    @Override
    @Authenticated
    @Authorizated(actionLevel = ActionLevel.group, accessRolRequired = AccessRol.Administrator)
    @Transactional
    public SocialNetworkDataDTO deleteMember(final String hash, final StateToken groupToken,
            final String groupToDeleleShortName) throws DefaultException {
        final UserSession userSession = getUserSession();
        final User userLogged = userSession.getUser();
        final Group group = groupManager.findByShortName(groupToken.getGroup());
        checkIsNotPersonalGroup(group);
        final Group groupToDelete = groupManager.findByShortName(groupToDeleleShortName);
        socialNetworkManager.deleteMember(userLogged, groupToDelete, group);
        return generateResponse(userLogged, group);
    }

    @Override
    @Authenticated
    @Authorizated(actionLevel = ActionLevel.group, accessRolRequired = AccessRol.Administrator)
    @Transactional
    public SocialNetworkDataDTO denyJoinGroup(final String hash, final StateToken groupToken,
            final String groupToDenyShortName) throws DefaultException {
        final UserSession userSession = getUserSession();
        final User userLogged = userSession.getUser();
        final Group group = groupManager.findByShortName(groupToken.getGroup());
        checkIsNotPersonalGroup(group);
        final Group groupToDenyJoin = groupManager.findByShortName(groupToDenyShortName);
        socialNetworkManager.denyJoinGroup(userLogged, groupToDenyJoin, group);
        return generateResponse(userLogged, group);
    }

    private SocialNetworkDataDTO generateResponse(final User userLogged, final Group group) {
        return mapper.map(socialNetworkManager.getSocialNetworkData(userLogged, group), SocialNetworkDataDTO.class);
    }

    @Override
    @Authenticated(mandatory = false)
    // At least you can access as Viewer to the Group
    @Authorizated(actionLevel = ActionLevel.group, accessRolRequired = AccessRol.Viewer)
    @Transactional
    public SocialNetworkDataDTO getSocialNetwork(final String hash, final StateToken groupToken)
            throws DefaultException {
        final UserSession userSession = getUserSession();
        final User user = userSession.getUser();
        final Group group = groupManager.findByShortName(groupToken.getGroup());
        return generateResponse(user, group);
    }

    private UserSession getUserSession() {
        return userSessionProvider.get();
    }

    @Override
    @Authenticated
    @Transactional
    public SocialNetworkRequestResult requestJoinGroup(final String hash, final StateToken groupToken)
            throws DefaultException {
        final UserSession userSession = getUserSession();
        final User user = userSession.getUser();
        final Group group = groupManager.findByShortName(groupToken.getGroup());
        checkIsNotPersonalGroup(group);
        return socialNetworkManager.requestToJoin(user, group);
    }

    @Override
    @Authenticated
    @Authorizated(actionLevel = ActionLevel.group, accessRolRequired = AccessRol.Administrator)
    @Transactional
    public SocialNetworkDataDTO setAdminAsCollab(final String hash, final StateToken groupToken,
            final String groupToSetCollabShortName) throws DefaultException {
        final UserSession userSession = getUserSession();
        final User userLogged = userSession.getUser();
        final Group group = groupManager.findByShortName(groupToken.getGroup());
        checkIsNotPersonalGroup(group);
        final Group groupToSetCollab = groupManager.findByShortName(groupToSetCollabShortName);
        socialNetworkManager.setAdminAsCollab(userLogged, groupToSetCollab, group);
        return generateResponse(userLogged, group);
    }

    @Override
    @Authenticated
    @Authorizated(actionLevel = ActionLevel.group, accessRolRequired = AccessRol.Administrator)
    @Transactional
    public SocialNetworkDataDTO setCollabAsAdmin(final String hash, final StateToken groupToken,
            final String groupToSetAdminShortName) throws DefaultException {
        final UserSession userSession = getUserSession();
        final User userLogged = userSession.getUser();
        final Group group = groupManager.findByShortName(groupToken.getGroup());
        checkIsNotPersonalGroup(group);
        final Group groupToSetAdmin = groupManager.findByShortName(groupToSetAdminShortName);
        socialNetworkManager.setCollabAsAdmin(userLogged, groupToSetAdmin, group);
        return generateResponse(userLogged, group);
    }

    @Override
    @Authenticated
    @Transactional
    public SocialNetworkDataDTO unJoinGroup(final String hash, final StateToken groupToken) throws DefaultException {
        final UserSession userSession = getUserSession();
        final User userLogged = userSession.getUser();
        final Group group = groupManager.findByShortName(groupToken.getGroup());
        checkIsNotPersonalGroup(group);
        socialNetworkManager.unJoinGroup(userLogged.getUserGroup(), group);
        return generateResponse(userLogged, group);
    }

}
