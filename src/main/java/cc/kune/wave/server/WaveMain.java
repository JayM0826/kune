// @formatter:off

/**
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cc.kune.wave.server;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.jetty.servlets.ProxyServlet;
import org.waveprotocol.box.common.comms.WaveClientRpc.ProtocolWaveClientRpc;
import org.waveprotocol.box.server.CoreSettings;
import org.waveprotocol.box.server.ServerModule;
import org.waveprotocol.box.server.authentication.AccountStoreHolder;
import org.waveprotocol.box.server.authentication.SessionManager;
import org.waveprotocol.box.server.frontend.ClientFrontend;
import org.waveprotocol.box.server.frontend.ClientFrontendImpl;
import org.waveprotocol.box.server.frontend.WaveClientRpcImpl;
import org.waveprotocol.box.server.persistence.AccountStore;
import org.waveprotocol.box.server.persistence.PersistenceException;
import org.waveprotocol.box.server.persistence.PersistenceModule;
import org.waveprotocol.box.server.persistence.SignerInfoStore;
import org.waveprotocol.box.server.robots.RobotApiModule;
import org.waveprotocol.box.server.robots.RobotRegistrationServlet;
import org.waveprotocol.box.server.robots.active.ActiveApiServlet;
import org.waveprotocol.box.server.robots.agent.passwd.PasswordAdminRobot;
import org.waveprotocol.box.server.robots.agent.passwd.PasswordRobot;
import org.waveprotocol.box.server.robots.dataapi.DataApiOAuthServlet;
import org.waveprotocol.box.server.robots.dataapi.DataApiServlet;
import org.waveprotocol.box.server.robots.passive.RobotsGateway;
import org.waveprotocol.box.server.rpc.AttachmentServlet;
import org.waveprotocol.box.server.rpc.AuthenticationServlet;
import org.waveprotocol.box.server.rpc.FetchProfilesServlet;
import org.waveprotocol.box.server.rpc.FetchServlet;
import org.waveprotocol.box.server.rpc.SearchServlet;
import org.waveprotocol.box.server.rpc.ServerRpcProvider;
import org.waveprotocol.box.server.rpc.SignOutServlet;
import org.waveprotocol.box.server.rpc.UserRegistrationServlet;
import org.waveprotocol.box.server.waveserver.WaveBus;
import org.waveprotocol.box.server.waveserver.WaveServerException;
import org.waveprotocol.box.server.waveserver.WaveletProvider;
import org.waveprotocol.wave.crypto.CertPathStore;
import org.waveprotocol.wave.federation.FederationSettings;
import org.waveprotocol.wave.federation.FederationTransport;
import org.waveprotocol.wave.federation.noop.NoOpFederationModule;
import org.waveprotocol.wave.federation.xmpp.XmppFederationModule;
import org.waveprotocol.wave.model.version.HashedVersionFactory;
import org.waveprotocol.wave.model.wave.ParticipantIdUtil;
import org.waveprotocol.wave.util.logging.Log;
import org.waveprotocol.wave.util.settings.SettingsBinder;

import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * Wave Server entrypoint.
 */
public class WaveMain {

  @SuppressWarnings("serial")
  @Singleton
  public static class GadgetProxyServlet extends HttpServlet {

    ProxyServlet.Transparent proxyServlet;

    @Inject
    public GadgetProxyServlet(@Named(CoreSettings.GADGET_SERVER_HOSTNAME) final String gadgetServerHostname,
        @Named(CoreSettings.GADGET_SERVER_PORT) final int gadgetServerPort){

      LOG.info("Starting GadgetProxyServlet for " + gadgetServerHostname + ":" + gadgetServerPort);
      proxyServlet = new ProxyServlet.Transparent("/gadgets", "http", gadgetServerHostname,
          gadgetServerPort,"/gadgets");
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
      proxyServlet.init(config);
    }

    @Override
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
      proxyServlet.service(req, res);
    }
  }

  private static final Log LOG = Log.get(WaveMain.class);

  /**
   * This is the name of the system property used to find the server config file.
   */
  private static final String PROPERTIES_FILE_KEY = "wave.server.config";

  private static Module buildFederationModule(final Injector settingsInjector, final boolean enableFederation)
      throws ConfigurationException {
    Module federationModule;
    if (enableFederation) {
      federationModule = settingsInjector.getInstance(XmppFederationModule.class);
    } else {
      federationModule = settingsInjector.getInstance(NoOpFederationModule.class);
    }
    return federationModule;
  }

  private static void initializeFederation(final Injector injector) {
    final FederationTransport federationManager = injector.getInstance(FederationTransport.class);
    federationManager.startFederation();
  }

  private static void initializeFrontend(final Injector injector, final ServerRpcProvider server,
      final WaveBus waveBus) throws WaveServerException {
    final HashedVersionFactory hashFactory = injector.getInstance(HashedVersionFactory.class);

    final WaveletProvider provider = injector.getInstance(WaveletProvider.class);
    final ClientFrontend frontend =
        ClientFrontendImpl.create(hashFactory, provider, waveBus);

    final ProtocolWaveClientRpc.Interface rpcImpl = WaveClientRpcImpl.create(frontend, false);
    server.registerService(ProtocolWaveClientRpc.newReflectiveService(rpcImpl));
  }

  private static void initializeRobotAgents(final Injector injector, final ServerRpcProvider server) {
    final PasswordRobot passRobot = new PasswordRobot(injector);
    final PasswordAdminRobot passAdminRobot = new PasswordAdminRobot(injector);
    injector.injectMembers(passRobot);
    server.addServlet(PasswordRobot.ROBOT_URI + "/*", passRobot);
    server.addServlet(PasswordAdminRobot.ROBOT_URI + "/*", passAdminRobot);
  }

  private static void initializeRobots(final Injector injector, final WaveBus waveBus) {
    final RobotsGateway robotsGateway = injector.getInstance(RobotsGateway.class);
    waveBus.subscribe(robotsGateway);
  }

  private static void initializeServer(final Injector injector, final String waveDomain)
      throws PersistenceException, WaveServerException {
    final AccountStore accountStore = injector.getInstance(AccountStore.class);
    accountStore.initializeAccountStore();
    AccountStoreHolder.init(accountStore, waveDomain);

    // Initialize the SignerInfoStore.
    final CertPathStore certPathStore = injector.getInstance(CertPathStore.class);
    if (certPathStore instanceof SignerInfoStore) {
      ((SignerInfoStore)certPathStore).initializeSignerInfoStore();
    }

    // Initialize the server.
    final WaveletProvider waveServer = injector.getInstance(WaveletProvider.class);
    waveServer.initialize();
  }

  private static void initializeServlets(final Injector injector, final ServerRpcProvider server) {
    server.addServlet("/attachment/*", injector.getInstance(AttachmentServlet.class));

    server.addServlet(SessionManager.SIGN_IN_URL, injector.getInstance(AuthenticationServlet.class));
    server.addServlet("/auth/signout", injector.getInstance(SignOutServlet.class));
    server.addServlet("/auth/register", injector.getInstance(UserRegistrationServlet.class));
    server.addServlet("/fetch/*", injector.getInstance(FetchServlet.class));
    server.addServlet("/search/*", injector.getInstance(SearchServlet.class));

    server.addServlet("/robot/dataapi", injector.getInstance(DataApiServlet.class));
    server.addServlet(DataApiOAuthServlet.DATA_API_OAUTH_PATH + "/*", injector.getInstance(DataApiOAuthServlet.class));
    server.addServlet("/robot/dataapi/rpc", injector.getInstance(DataApiServlet.class));
    server.addServlet("/robot/register/*", injector.getInstance(RobotRegistrationServlet.class));
    server.addServlet("/robot/rpc", injector.getInstance(ActiveApiServlet.class));
    server.addServlet("/webclient/remote_logging", injector.getInstance(RemoteLoggingServiceImpl.class));
    server.addServlet("/profile/*", injector.getInstance(FetchProfilesServlet.class));

    final String gadgetHostName =
        injector
            .getInstance(Key.get(String.class, Names.named(CoreSettings.GADGET_SERVER_HOSTNAME)));
    final int port =
        injector.getInstance(Key.get(Integer.class, Names.named(CoreSettings.GADGET_SERVER_PORT)));
    final Map<String, String> initParams =
        Collections.singletonMap("HostHeader", gadgetHostName + ":" + port);
    server.addServlet("/gadgets/*", injector.getInstance(GadgetProxyServlet.class), initParams);

    //server.addServlet("/", injector.getInstance(WaveClientServlet.class));
  }

  public static void main(final String... args) {
    try {
      final Module coreSettings = SettingsBinder.bindSettings(PROPERTIES_FILE_KEY, CoreSettings.class);
      run(coreSettings);
      return;
    } catch (final PersistenceException e) {
      LOG.severe("PersistenceException when running server:", e);
    } catch (final ConfigurationException e) {
      LOG.severe("ConfigurationException when running server:", e);
    } catch (final WaveServerException e) {
      LOG.severe("WaveServerException when running server:", e);
    }
  }

  public static void run(final Module coreSettings) throws PersistenceException,
      ConfigurationException, WaveServerException {
    Injector settingsInjector = Guice.createInjector(coreSettings);
    final boolean enableFederation = settingsInjector.getInstance(Key.get(Boolean.class,
        Names.named(CoreSettings.ENABLE_FEDERATION)));

    if (enableFederation) {
      final Module federationSettings =
          SettingsBinder.bindSettings(PROPERTIES_FILE_KEY, FederationSettings.class);
      // This MUST happen first, or bindings will fail if federation is enabled.
      settingsInjector = settingsInjector.createChildInjector(federationSettings);
    }

    final Module federationModule = buildFederationModule(settingsInjector, enableFederation);
    final PersistenceModule persistenceModule = settingsInjector.getInstance(PersistenceModule.class);
    final Injector injector =
        settingsInjector.createChildInjector(new ServerModule(enableFederation),
            new RobotApiModule(), federationModule, persistenceModule);

    final ServerRpcProvider server = injector.getInstance(ServerRpcProvider.class);
    final WaveBus waveBus = injector.getInstance(WaveBus.class);

    final String domain =
      injector.getInstance(Key.get(String.class, Names.named(CoreSettings.WAVE_SERVER_DOMAIN)));
    if (!ParticipantIdUtil.isDomainAddress(ParticipantIdUtil.makeDomainAddress(domain))) {
      throw new WaveServerException("Invalid wave domain: " + domain);
    }

    initializeServer(injector, domain);
    initializeServlets(injector, server);
    initializeRobotAgents(injector, server);
    initializeRobots(injector, waveBus);
    initializeFrontend(injector, server, waveBus);
    initializeFederation(injector);

    LOG.info("Starting server");
    server.startWebSocketServer(injector);
  }
}
