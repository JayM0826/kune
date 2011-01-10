package cc.kune.core.client.ui.dialogs;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasDirectionalText;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets.ForIsWidget;

public interface TwoButtonsView {

    HasText getFirstBtnText();

    HasText getSecondBtnText();

    HasClickHandlers getSecondBtn();

    HasClickHandlers getFirstBtn();

    HasDirectionalText getTitleText();

    ForIsWidget getInnerPanel();

}