package org.ourproject.kune.platf.integration.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.ourproject.kune.docs.client.DocumentClientTool;
import org.ourproject.kune.docs.server.DocumentServerTool;
import org.ourproject.kune.platf.client.dto.AccessRightsDTO;
import org.ourproject.kune.platf.client.dto.ContainerDTO;
import org.ourproject.kune.platf.client.dto.ContainerSimpleDTO;
import org.ourproject.kune.platf.client.dto.ContentSimpleDTO;
import org.ourproject.kune.platf.client.dto.StateContainerDTO;
import org.ourproject.kune.platf.client.dto.StateContentDTO;
import org.ourproject.kune.platf.client.dto.StateToken;
import org.ourproject.kune.platf.client.errors.ContentNotFoundException;
import org.ourproject.kune.platf.client.errors.UserMustBeLoggedException;
import org.ourproject.kune.platf.integration.IntegrationTestHelper;

public class ContentServiceAddTest extends ContentServiceIntegrationTest {

    String groupName;
    private StateContentDTO defaultContent;

    @Before
    public void init() throws Exception {
        new IntegrationTestHelper(this);
        groupName = getDefSiteGroupName();
    }

    @Test(expected = UserMustBeLoggedException.class)
    public void noLoggedInShouldThrowIllegalAccess() throws ContentNotFoundException, Exception {
        defaultContent = getSiteDefaultContent();
        contentService.addContent(session.getHash(), defaultContent.getContainer().getStateToken(), "a name",
                DocumentServerTool.TYPE_DOCUMENT);
    }

    @Test
    public void testAddContent() throws Exception {
        doLogin();
        defaultContent = getSiteDefaultContent();
        assertEquals(1, defaultContent.getContainer().getContents().size());
        final AccessRightsDTO cntRights = defaultContent.getContentRights();
        final AccessRightsDTO ctxRights = defaultContent.getContainerRights();
        final AccessRightsDTO groupRights = defaultContent.getGroupRights();

        final String title = "New Content Title";
        final StateContentDTO added = contentService.addContent(session.getHash(),
                defaultContent.getContainer().getStateToken(), title, DocumentServerTool.TYPE_DOCUMENT);
        assertNotNull(added);
        final List<ContentSimpleDTO> contents = added.getContainer().getContents();
        assertEquals(title, added.getTitle());
        assertEquals(2, contents.size());
        assertEquals(cntRights, added.getContentRights());
        assertEquals(ctxRights, added.getContainerRights());
        assertEquals(groupRights, added.getGroupRights());
        assertNotNull(added.getGroupMembers());
        assertNotNull(added.getParticipation());
        assertNotNull(added.getAccessLists());

        final StateToken newState = added.getStateToken();
        final StateContentDTO sameAgain = (StateContentDTO) contentService.getContent(session.getHash(), newState);
        assertNotNull(sameAgain);
        assertEquals(2, sameAgain.getContainer().getContents().size());

    }

    @Test
    public void testAddFolder() throws Exception {
        doLogin();
        defaultContent = getSiteDefaultContent();
        final ContainerDTO parent = defaultContent.getContainer();
        final String title = "folder name";
        final StateContainerDTO newState = contentService.addFolder(session.getHash(), parent.getStateToken(), title,
                DocumentClientTool.TYPE_FOLDER);
        assertNotNull(newState);
        assertNotNull(newState.getGroupMembers());
        assertNotNull(newState.getParticipation());
        assertNotNull(newState.getAccessLists());
        assertNotNull(newState.getContainerRights());
        assertNotNull(newState.getGroupRights());
        assertNotNull(newState.getRootContainer().getContents().get(0).getRights());

        final ContainerDTO parentAgain = getSiteDefaultContent().getContainer();
        final ContainerSimpleDTO child = parentAgain.getChilds().get(0);
        assertEquals(parent.getId(), child.getParentFolderId());

        assertEquals(parent.getId(), parentAgain.getId());
        assertEquals(1, parentAgain.getChilds().size());
    }

    // @Test
    public void testAddRoom() throws Exception {
        doLogin();
        defaultContent = getSiteDefaultContent();
        final ContainerDTO parent = defaultContent.getContainer();
        final String roomName = "testroom";
        final StateContainerDTO newState = contentService.addRoom(session.getHash(), parent.getStateToken(), roomName);
        assertNotNull(newState);
    }

    @Test
    public void testAddTwoFolders() throws Exception {
        doLogin();
        defaultContent = getSiteDefaultContent();
        final ContainerDTO parent = defaultContent.getContainer();
        final String title = "folder name";
        final StateContainerDTO newState = contentService.addFolder(session.getHash(), parent.getStateToken(), title,
                DocumentClientTool.TYPE_FOLDER);
        assertNotNull(newState);

        final StateContainerDTO newState2 = contentService.addFolder(session.getHash(), parent.getStateToken(), title,
                DocumentClientTool.TYPE_FOLDER);
        assertNotNull(newState2);

        final ContainerDTO parentAgain = getSiteDefaultContent().getContainer();

        assertEquals(parent.getId(), parentAgain.getId());
        assertEquals(2, parentAgain.getChilds().size());
    }

}
