package org.ourproject.kune.docs.client.ui.ctx.folder;

import org.ourproject.kune.workspace.client.dto.ContextItemDTO;

public interface NavigationListener {
    void contextChanged(String contextRef, ContextItemDTO selectedItem);
}
