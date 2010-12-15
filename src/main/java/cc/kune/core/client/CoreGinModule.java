package cc.kune.core.client;

import cc.kune.core.client.errors.ErrorHandler;
import cc.kune.core.client.i18n.I18nUITranslationService;
import cc.kune.core.client.notify.SpinerPresenter;
import cc.kune.core.client.notify.SpinerViewImpl;
import cc.kune.core.client.notify.UserNotifierPresenter;
import cc.kune.core.client.notify.UserNotifierPresenter.UserNotifierProxy;
import cc.kune.core.client.notify.UserNotifierViewImpl;
import cc.kune.core.client.state.ContentProvider;
import cc.kune.core.client.state.ContentProviderDefault;
import cc.kune.core.client.state.HistoryWrapper;
import cc.kune.core.client.state.HistoryWrapperDefault;
import cc.kune.core.client.state.Session;
import cc.kune.core.client.state.SessionDefault;
import cc.kune.core.client.ws.CorePlaceManager;
import cc.kune.core.client.ws.CorePresenter;
import cc.kune.core.client.ws.CoreViewImpl;
import cc.kune.core.shared.i18n.I18nTranslationService;
import cc.kune.core.ws.armor.client.Body;
import cc.kune.core.ws.armor.client.IBody;

import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.DefaultEventBus;
import com.gwtplatform.mvp.client.DefaultProxyFailureHandler;
import com.gwtplatform.mvp.client.EventBus;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;

/**
 * The Class Core GinModule.
 */
public class CoreGinModule extends AbstractPresenterModule {

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.inject.client.AbstractGinModule#configure()
     */
    @Override
    protected void configure() {
        bind(EventBus.class).to(DefaultEventBus.class).in(Singleton.class);
        bind(PlaceManager.class).to(CorePlaceManager.class).in(Singleton.class);
        bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(Singleton.class);
        bind(RootPresenter.class).asEagerSingleton();
        bind(ProxyFailureHandler.class).to(DefaultProxyFailureHandler.class).in(Singleton.class);

        // Presenters
        bindPresenter(CorePresenter.class, CorePresenter.CoreView.class, CoreViewImpl.class,
                CorePresenter.CoreProxy.class);
        bindPresenter(SpinerPresenter.class, SpinerPresenter.SpinerView.class, SpinerViewImpl.class,
                SpinerPresenter.SpinerProxy.class);
        bindPresenter(UserNotifierPresenter.class, UserNotifierPresenter.UserNotifierView.class,
                UserNotifierViewImpl.class, UserNotifierProxy.class);

        bind(IBody.class).to(Body.class).in(Singleton.class);
        bind(Session.class).to(SessionDefault.class).in(Singleton.class);
        bind(I18nTranslationService.class).to(I18nUITranslationService.class).in(Singleton.class);
        bind(ErrorHandler.class).in(Singleton.class);
        bind(ContentProvider.class).to(ContentProviderDefault.class).in(Singleton.class);
        bind(HistoryWrapper.class).to(HistoryWrapperDefault.class).in(Singleton.class);
        // bind(StateManager.class).to(StateManagerDefault.class).in(Singleton.class);
    }

}
