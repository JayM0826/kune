package org.ourproject.kune.platf.client.workspace;

import org.ourproject.kune.platf.client.View;

public interface WorkspaceComponent {
    View getView();

    void detach();

    void attach();

    void setReferences(String ctxRef, String cntRef);
}
