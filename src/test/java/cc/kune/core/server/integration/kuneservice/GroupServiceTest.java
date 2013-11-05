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
package cc.kune.core.server.integration.kuneservice;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import cc.kune.core.client.errors.GroupLongNameInUseException;
import cc.kune.core.client.errors.GroupShortNameInUseException;
import cc.kune.core.client.errors.UserMustBeLoggedException;
import cc.kune.core.client.rpcservices.GroupService;
import cc.kune.core.server.UserSession;
import cc.kune.core.server.integration.IntegrationTest;
import cc.kune.core.server.integration.IntegrationTestHelper;
import cc.kune.core.shared.domain.AdmissionType;
import cc.kune.core.shared.domain.GroupListMode;
import cc.kune.core.shared.domain.utils.StateToken;
import cc.kune.core.shared.dto.GroupDTO;
import cc.kune.core.shared.dto.GroupType;
import cc.kune.core.shared.dto.LicenseDTO;
import cc.kune.domain.Group;
import cc.kune.domain.finders.GroupFinder;

import com.google.inject.Inject;

public class GroupServiceTest extends IntegrationTest {

  @Inject
  GroupFinder groupFinder;

  @Inject
  GroupService service;

  @Inject
  UserSession session;

  @Test
  public void createClosed() throws Exception {
    doLogin();

    final GroupDTO group = new GroupDTO("ysei", "Yellow Submarine Environmental Initiative",
        GroupType.CLOSED);

    final LicenseDTO license = new LicenseDTO();
    license.setShortName("by-sa-v3.0");
    group.setDefaultLicense(license);
    service.createNewGroup(session.getHash(), group, "Public desc", "tag1 tag2", null);

    final Group groupCreated = groupFinder.findByShortName("ysei");
    assertEquals(groupCreated.getShortName(), group.getShortName());
    assertEquals(groupCreated.getLongName(), group.getLongName());
    assertEquals(groupCreated.getAdmissionType(), AdmissionType.Closed);
    assertEquals(groupCreated.getGroupType(), GroupType.CLOSED);
    assertEquals(GroupListMode.NORMAL,
        groupCreated.getSocialNetwork().getAccessLists().getViewers().getMode());
  }

  @Test
  public void createCommunity() throws Exception {
    doLogin();

    final GroupDTO group = new GroupDTO("ysei", "Yellow Submarine Environmental Initiative",
        GroupType.COMMUNITY);

    final LicenseDTO license = new LicenseDTO();
    license.setShortName("by-sa-v3.0");
    group.setDefaultLicense(license);
    service.createNewGroup(session.getHash(), group, "Public desc", "tag1 tag2", null);

    final Group groupCreated = groupFinder.findByShortName("ysei");
    assertEquals(groupCreated.getShortName(), group.getShortName());
    assertEquals(groupCreated.getLongName(), group.getLongName());

    assertEquals(groupCreated.getAdmissionType(), AdmissionType.Open);
    assertEquals(groupCreated.getGroupType(), GroupType.COMMUNITY);
  }

  private GroupDTO createGroup(final String shortName, final String longName) {
    final GroupDTO group = new GroupDTO(shortName, longName, GroupType.ORGANIZATION);

    final LicenseDTO license = new LicenseDTO();
    license.setShortName("by-sa-v3.0");
    group.setDefaultLicense(license);
    service.createNewGroup(session.getHash(), group, "Public desc", "tag1 tag2", null);
    final Group groupCreated = groupFinder.findByShortName(shortName);
    group.setId(groupCreated.getId());
    return group;
  }

  @Test(expected = Exception.class)
  public void createGroupNotLogged() throws Exception {
    final GroupDTO group = new GroupDTO("ysei", "Yellow Submarine Environmental Initiative",
        GroupType.PROJECT);
    service.createNewGroup(session.getHash(), group, "Public desc", "tag1 tag2", null);
  }

  @Test(expected = UserMustBeLoggedException.class)
  public void createGroupNullUserHash() throws Exception {
    doLogin();
    final GroupDTO group = new GroupDTO("ysei", "Yellow Submarine Environmental Initiative",
        GroupType.PROJECT);
    service.createNewGroup(null, group, "Public desc", "tag1 tag2", null);
  }

  @Test
  public void createGroupUserLogged() throws Exception {
    doLogin();

    final GroupDTO group = new GroupDTO("ysei", "Yellow Submarine Environmental Initiative",
        GroupType.PROJECT);

    final LicenseDTO license = new LicenseDTO();
    license.setShortName("by-sa-v3.0");
    group.setDefaultLicense(license);
    service.createNewGroup(session.getHash(), group, "Public desc", "tag1 tag2", null);

    final Group groupCreated = groupFinder.findByShortName("ysei");
    assertEquals(groupCreated.getShortName(), group.getShortName());
    assertEquals(groupCreated.getLongName(), group.getLongName());

    assertEquals(groupCreated.getAdmissionType(), AdmissionType.Moderated);
    assertEquals(groupCreated.getGroupType(), GroupType.PROJECT);
  }

  @Test
  public void createOrganization() throws Exception {
    doLogin();

    final GroupDTO group = new GroupDTO("ysei", "Yellow Submarine Environmental Initiative",
        GroupType.ORGANIZATION);

    final LicenseDTO license = new LicenseDTO();
    license.setShortName("by-sa-v3.0");
    group.setDefaultLicense(license);
    service.createNewGroup(session.getHash(), group, "Public desc", "tag1 tag2", null);

    final Group groupCreated = groupFinder.findByShortName("ysei");
    assertEquals(groupCreated.getShortName(), group.getShortName());
    assertEquals(groupCreated.getLongName(), group.getLongName());
    assertEquals(groupCreated.getAdmissionType(), AdmissionType.Moderated);
    assertEquals(groupCreated.getGroupType(), GroupType.ORGANIZATION);
  }

  @Test
  public void createOrphanedProject() throws Exception {
    doLogin();

    final GroupDTO group = new GroupDTO("ysei", "Yellow Submarine Environmental Initiative",
        GroupType.ORPHANED_PROJECT);

    final LicenseDTO license = new LicenseDTO();
    license.setShortName("by-sa-v3.0");
    group.setDefaultLicense(license);
    service.createNewGroup(session.getHash(), group, "Public desc", "tag1 tag2", null);

    final Group groupCreated = groupFinder.findByShortName("ysei");
    assertEquals(groupCreated.getShortName(), group.getShortName());
    assertEquals(groupCreated.getLongName(), group.getLongName());
    assertEquals(groupCreated.getAdmissionType(), AdmissionType.Open);
    assertEquals(groupCreated.getGroupType(), GroupType.ORPHANED_PROJECT);
    assertEquals(0, groupCreated.getSocialNetwork().getAccessLists().getAdmins().getList().size());
    assertEquals(0, groupCreated.getSocialNetwork().getAccessLists().getEditors().getList().size());
  }

  @Test(expected = GroupLongNameInUseException.class)
  public void createSameLongName() throws Exception {
    doLogin();
    final GroupDTO group = createGroup("ysei", "Yellow Submarine Environmental Initiative");
    group.setLongName(getDefSiteLongName());
    service.updateGroup(getHash(), group.getStateToken(), group);
  }

  @Test(expected = GroupShortNameInUseException.class)
  public void createSameShortName() throws Exception {
    doLogin();
    final GroupDTO group = createGroup("ysei", "Yellow Submarine Environmental Initiative");
    group.setShortName(getDefSiteShortName());
    service.updateGroup(getHash(), new StateToken("ysei"), group);
  }

  @Before
  public void init() {
    new IntegrationTestHelper(true, this);
  }

}
