package org.ourproject.kune.platf.client.actions.ui;

import org.ourproject.kune.platf.client.actions.PropertyChangeEvent;
import org.ourproject.kune.platf.client.actions.PropertyChangeListener;

public class PushButton extends DefaultButton {

    public PushButton(final PushButtonDescriptor btn) {
        super(btn, true);
        setPressed(btn.isPushed());
        action.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent event) {
                if (event.getPropertyName().equals(PushButtonDescriptor.PUSHED)) {
                    setPressed(btn.isPushed());
                }
            }
        });
    }

}