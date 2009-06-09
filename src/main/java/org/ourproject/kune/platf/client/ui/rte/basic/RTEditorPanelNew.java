package org.ourproject.kune.platf.client.ui.rte.basic;

import java.util.Date;

import org.ourproject.kune.platf.client.View;
import org.ourproject.kune.platf.client.actions.AbstractAction;
import org.ourproject.kune.platf.client.actions.ActionEvent;
import org.ourproject.kune.platf.client.actions.ui.AbstractComplexGuiItem;
import org.ourproject.kune.platf.client.actions.ui.ComplexToolbar;
import org.ourproject.kune.platf.client.actions.ui.GuiActionDescCollection;
import org.ourproject.kune.platf.client.actions.ui.GuiActionDescrip;
import org.ourproject.kune.platf.client.actions.ui.GuiBindingsRegister;
import org.ourproject.kune.platf.client.errors.UIException;
import org.ourproject.kune.platf.client.i18n.I18nUITranslationService;
import org.ourproject.kune.platf.client.shortcuts.GlobalShortcutRegister;
import org.ourproject.kune.platf.client.ui.noti.NotifyUser;
import org.ourproject.kune.platf.client.ui.rte.RichTextArea;
import org.ourproject.kune.platf.client.ui.rte.RichTextArea.BasicFormatter;
import org.ourproject.kune.platf.client.ui.rte.RichTextArea.ExtendedFormatter;
import org.ourproject.kune.platf.client.ui.rte.RichTextArea.FontSize;
import org.ourproject.kune.platf.client.ui.rte.RichTextArea.Justification;
import org.ourproject.kune.platf.client.ui.rte.insertlink.LinkExecutableUtils;
import org.ourproject.kune.platf.client.ui.rte.insertlink.LinkInfo;
import org.xwiki.gwt.dom.client.DocumentFragment;
import org.xwiki.gwt.dom.client.Range;
import org.xwiki.gwt.dom.client.Selection;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;

public class RTEditorPanelNew extends AbstractComplexGuiItem implements RTEditorViewNew, FocusHandler, BlurHandler {

    private final I18nUITranslationService i18n;
    private final BasicFormatter basic;
    private final ExtendedFormatter extended;
    private final RTEditorPresenterNew presenter;
    private final GlobalShortcutRegister globalShortcutReg;
    private final RTELinkPopup linkCtxMenu;
    private final RichTextArea rta;
    private final ComplexToolbar topBar;
    private final ComplexToolbar sndBar;

    public RTEditorPanelNew(final RTEditorPresenterNew presenter, final I18nUITranslationService i18n,
            final GlobalShortcutRegister globalShortcutReg, final GuiBindingsRegister bindReg) {
        super();
        this.presenter = presenter;
        this.i18n = i18n;
        this.globalShortcutReg = globalShortcutReg;
        rta = new RichTextArea();
        basic = rta.getBasicFormatter();
        extended = rta.getExtendedFormatter();
        topBar = new ComplexToolbar(bindReg);
        sndBar = new ComplexToolbar(bindReg);
        sndBar.setNormalStyle();
        rta.addFocusHandler(this);
        rta.addBlurHandler(this);
        linkCtxMenu = new RTELinkPopup();
        initWidget(rta);
        setWidth("96%");
        setHeight("100%");
    }

    public void addActions(final GuiActionDescCollection items) {
        super.addAll(items);
        for (final GuiActionDescrip item : items) {
            final String location = item.getLocation();
            if (location == null) {
                throw new UIException("Unknown location in action item: " + item);
            }
            if (location.equals(RTEditorNew.TOPBAR)) {
                topBar.add(item);
            } else if (location.equals(RTEditorNew.SNDBAR)) {
                sndBar.add(item);
            } else if (location.equals(RTEditorNew.LINKCTX)) {
                linkCtxMenu.add(item);
            } else {
                throw new UIException("Unknown location in action item: " + item);
            }
        }
    }

    public void adjustSize(final int height) {
        setHeight(Integer.toString(height));
    }

    public boolean canBeBasic() {
        return basic != null;
    }

    public boolean canBeExtended() {
        return extended != null;
    }

    public void copy() {
        extended.copy();
    }

    public void createLink(final String url) {
        extended.createLink(url);
    }

    public void cut() {
        extended.cut();
    }

    public void delete() {
        extended.delete();
    }

    public void focus() {
        setFocus(true);
    }

    public String getHTML() {
        return rta.getHTML();
    }

    public LinkInfo getLinkInfoIfHref() {
        LinkInfo linkinfo = null;
        final org.xwiki.gwt.dom.client.Element selectedAnchor = selectAndGetLink();
        if (selectedAnchor == null) {
            linkinfo = new LinkInfo(getSelectionText());
        } else {
            linkinfo = LinkInfo.parse(selectedAnchor);
        }
        Log.debug("Link info: " + linkinfo);
        return linkinfo;
    }

    public void getRangeInfo() {
        // Selection selection = getSelection();
        // String info = "range count: " + selection.getRangeCount() +
        // "<br/>focus offset: " + selection.getFocusOffset()
        // + "<br/>anchor offset:" + selection.getAnchorOffset() +
        // "<br/>range 0 as html: "
        // + selection.getRangeAt(0).toHTML();
        // NotifyUser.info(info);
        final String info = "range count: " + getFstRange().getCommonAncestorContainer().getFirstChild().getNodeName();
        NotifyUser.info(info);
    }

    public String getSelectionText() {
        return getFstRange().cloneContents().getInnerText();
    }

    public View getSndBar() {
        return sndBar;
    }

    public String getText() {
        return rta.getText();
    }

    public View getTopBar() {
        return topBar;
    }

    public void hideLinkCtxMenu() {
        linkCtxMenu.hide();
    }

    public void insertBlockquote() {
        final DocumentFragment extracted = getFstRange().cloneContents();
        // delete();
        insertHtml("<blockquote>" + extracted.getInnerHTML() + "</blockquote>");
        focus();
    }

    public void insertComment(final String author) {
        createCommentAndSelectIt(author, null);
    }

    public void insertCommentNotUsingSelection(final String author) {
        getFstRange().collapse(false);
        createCommentAndSelectIt(author, null);
        focus();
    }

    public void insertCommentUsingSelection(final String author) {
        final DocumentFragment extracted = getFstRange().cloneContents();
        extended.delete();
        final String comment = extracted.getInnerText();
        createCommentAndSelectIt(author, comment);
        focus();
    }

    public void insertHorizontalRule() {
        extended.insertHorizontalRule();
    }

    public void insertHtml(final String html) {
        extended.insertHtml(html);
    }

    public void insertImage(final String url) {
        extended.insertImage(url);
    }

    public void insertOrderedList() {
        extended.insertOrderedList();
    }

    public void insertUnorderedList() {
        extended.insertUnorderedList();
    }

    public boolean isAnythingSelected() {
        return !rta.getDocument().getSelection().isCollapsed();
    }

    public boolean isBold() {
        return basic.isBold();
    }

    public boolean isCollapsed() {
        return getFstRange().isCollapsed();
    }

    public boolean isCtxMenuVisible() {
        return linkCtxMenu.isVisible();
    }

    public boolean isItalic() {
        return basic.isItalic();
    }

    public boolean isLink() {
        boolean isLink = false;
        if (isAttached() && LinkExecutableUtils.getSelectedAnchor(rta) != null) {
            isLink = true;
        }
        return isLink;
    }

    public boolean isStrikethrough() {
        return extended.isStrikethrough();
    }

    public boolean isSubscript() {
        return basic.isSubscript();
    }

    public boolean isSuperscript() {
        return basic.isSuperscript();
    }

    public boolean isUnderlined() {
        return basic.isUnderlined();
    }

    public void justifyCenter() {
        basic.setJustification(Justification.CENTER);
    }

    public void justifyLeft() {
        basic.setJustification(Justification.LEFT);
    }

    public void justifyRight() {
        basic.setJustification(Justification.RIGHT);
    }

    public void leftIndent() {
        extended.leftIndent();
    }

    public void onBlur(final BlurEvent event) {
        presenter.onLostFocus();
    }

    @Override
    public void onBrowserEvent(final Event event) {
        switch (DOM.eventGetType(event)) {
        case Event.ONCLICK:
            updateStatus();
            updateLinkInfo();
            super.onBrowserEvent(event);
            break;
        case Event.ONKEYPRESS:
            final AbstractAction rtaActionItem = super.getAction(event);
            // FIXME
            // final Action actionItem = rtaActionItem == null ?
            // globalShortcutReg.get(event) : rtaActionItem;
            final AbstractAction actionItem = rtaActionItem == null ? null : rtaActionItem;
            if (actionItem == null) {
                Log.warn("rte action key null");
                super.onBrowserEvent(event);
                updateStatus();
                updateLinkInfo();
                if (isAnEditionKey(event.getKeyCode())) {
                    fireEdit();
                }
            } else {
                updateStatus();
                fireEdit();
                event.stopPropagation();
                event.preventDefault();
                actionItem.actionPerformed(new ActionEvent(this, event));
                updateStatus();
            }
            break;
        default:
            // Rest of events
            super.onBrowserEvent(event);
            updateStatus();
        }
    }

    public void onFocus(final FocusEvent event) {
        presenter.onEditorFocus();
    }

    public void paste() {
        extended.paste();
    }

    public void redo() {
        extended.redo();
    }

    public void removeFormat() {
        extended.removeFormat();
    }

    public void rightIndent() {
        extended.rightIndent();
    }

    public void selectAll() {
        basic.selectAll();
    }

    public void selectLink() {
        selectAndGetLink();
    }

    public void setBackColor(final String color) {
        basic.setBackColor(color);
    }

    public void setFocus(final boolean focused) {
        rta.setFocus(focused);
    }

    public void setFontName(final String name) {
        basic.setFontName(name);
    }

    public void setFontSize(final FontSize size) {
        basic.setFontSize(size);
    }

    public void setForeColor(final String color) {
        basic.setForeColor(color);
    }

    public void setHTML(final String html) {
        rta.setHTML(html);
    }

    public void setText(final String text) {
        rta.setText(text);
    }

    public void showLinkCtxMenu() {
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                final org.xwiki.gwt.dom.client.Element selectedAnchor = LinkExecutableUtils.getSelectedAnchor(rta);
                if (selectedAnchor != null) {
                    linkCtxMenu.show(RTEditorPanelNew.this.getAbsoluteLeft() + selectedAnchor.getAbsoluteLeft(),
                            RTEditorPanelNew.this.getAbsoluteTop() + selectedAnchor.getAbsoluteTop() + 20);
                }
            }
        });

    }

    public void toggleBold() {
        basic.toggleBold();
    }

    public void toggleItalic() {
        basic.toggleItalic();
    }

    public void toggleStrikethrough() {
        extended.toggleStrikethrough();
    }

    public void toggleSubscript() {
        basic.toggleSubscript();
    }

    public void toggleSuperscript() {
        basic.toggleSuperscript();
    }

    public void toggleUnderline() {
        basic.toggleUnderline();
    }

    public void undo() {
        extended.undo();
    }

    public void unlink() {
        extended.removeLink();
    }

    protected void fireEdit() {
        presenter.fireOnEdit();
    }

    private void createCommentAndSelectIt(final String author, final String comment) {
        final Element commentEl = createCommentElement(author, comment);
        final Range innerCommentRange = rta.getDocument().createRange();
        getFstRange().insertNode(commentEl);
        innerCommentRange.selectNodeContents(commentEl.getFirstChild());
        getSelection().addRange(innerCommentRange);
        fireEdit();
    }

    private Element createCommentElement(final String userName, final String insertComment) {
        final String time = i18n.formatDateWithLocale(new Date(), true);
        final Element span = rta.getDocument().createSpanElement();
        final String comment = insertComment == null ? i18n.t("type your comment here") : insertComment;
        span.setInnerHTML("<em>" + comment + "</em> -" + userName + " " + time);
        DOM.setElementProperty(span.<com.google.gwt.user.client.Element> cast(), "className", "k-rte-comment");
        // insertHtml("&nbsp;" + span.getString() + "&nbsp;");
        return span;
    }

    private Range getFstRange() {
        return getSelection().getRangeAt(0);
    }

    private Selection getSelection() {
        return rta.getDocument().getSelection();
    }

    private boolean isAnEditionKey(final int keyCode) {
        switch (keyCode) { // NOPMD by vjrj on 5/06/09 19:14
        case KeyCodes.KEY_HOME:
        case KeyCodes.KEY_END:
        case KeyCodes.KEY_UP:
        case KeyCodes.KEY_DOWN:
        case KeyCodes.KEY_LEFT:
        case KeyCodes.KEY_RIGHT:
        case KeyCodes.KEY_PAGEDOWN:
        case KeyCodes.KEY_PAGEUP:
        case KeyCodes.KEY_ESCAPE:
            return false;
        default:
            return true;
        }
    }

    private org.xwiki.gwt.dom.client.Element selectAndGetLink() {
        final org.xwiki.gwt.dom.client.Element selectedAnchor = LinkExecutableUtils.getSelectedAnchor(rta);
        if (selectedAnchor != null) {
            final Range range = rta.getDocument().createRange();
            range.selectNode(selectedAnchor);
            getSelection().addRange(range);
        }
        return selectedAnchor;
    }

    private void updateLinkInfo() {
        presenter.updateLinkInfo();
    }

    /**
     * Updates the status of all the stateful buttons.
     */
    private void updateStatus() {
        presenter.updateStatus();
    }
}
