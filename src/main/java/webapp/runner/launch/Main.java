/**
 * Copyright (c) 2012, John Simone
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of John Simone nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package webapp.runner.launch;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.naming.CompositeName;
import javax.naming.StringRefAddr;
import javax.servlet.annotation.ServletSecurity.TransportGuarantee;

import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Role;
import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityCollection;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.startup.ExpandWar;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.users.MemoryUserDatabase;
import org.apache.catalina.users.MemoryUserDatabaseFactory;

import com.beust.jcommander.JCommander;


/**
 * This is the main entry point to webapp-runner. Helpers are called to parse the arguments.
 * Tomcat configuration and launching takes place here.
 */
public class Main {

  private static final String AUTH_ROLE = "user";

  public static void main(String[] args) throws Exception {

    CommandLineParams commandLineParams = new CommandLineParams();

    JCommander jCommander = new JCommander(commandLineParams, args);

    if (commandLineParams.help) {
      jCommander.usage();
      System.exit(1);
    }

    // default to src/main/webapp
    if (commandLineParams.paths.size() == 0) {
      commandLineParams.paths.add("src/main/webapp");
    }

    final Tomcat tomcat = new Tomcat();

    // set directory for temp files
    tomcat.setBaseDir(resolveTomcatBaseDir(commandLineParams.port, commandLineParams.tempDirectory));

    // initialize the connector
    Connector nioConnector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    nioConnector.setPort(commandLineParams.port);

    if (commandLineParams.enableSSL) {
      nioConnector.setSecure(true);
      nioConnector.setProperty("SSLEnabled", "true");
      String pathToTrustStore = System.getProperty("javax.net.ssl.trustStore");
      if (pathToTrustStore != null) {
        nioConnector.setProperty("sslProtocol", "tls");
        File truststoreFile = new File(pathToTrustStore);
        nioConnector.setAttribute("truststoreFile", truststoreFile.getAbsolutePath());
        System.out.println(truststoreFile.getAbsolutePath());
        nioConnector.setAttribute("trustStorePassword", System.getProperty("javax.net.ssl.trustStorePassword"));
      }
      String pathToKeystore = System.getProperty("javax.net.ssl.keyStore");
      if (pathToKeystore != null) {
        File keystoreFile = new File(pathToKeystore);
        nioConnector.setAttribute("keystoreFile", keystoreFile.getAbsolutePath());
        System.out.println(keystoreFile.getAbsolutePath());
        nioConnector.setAttribute("keystorePass", System.getProperty("javax.net.ssl.keyStorePassword"));
      }
      if (commandLineParams.enableClientAuth) {
        nioConnector.setAttribute("clientAuth", true);
      }
    }

    if (null != commandLineParams.uriEncoding) {
      nioConnector.setURIEncoding(commandLineParams.uriEncoding);
    }
    nioConnector.setUseBodyEncodingForURI(commandLineParams.useBodyEncodingForURI);

    if (commandLineParams.enableCompression) {
      nioConnector.setProperty("compression", "on");
      nioConnector.setProperty("compressableMimeType", commandLineParams.compressableMimeTypes);
    }

    if (!commandLineParams.bindOnInit) {
      nioConnector.setProperty("bindOnInit", "false");
    }

    tomcat.setConnector(nioConnector);

    tomcat.getService().addConnector(tomcat.getConnector());

    tomcat.setPort(commandLineParams.port);

    if (commandLineParams.paths.size() > 1) {
      System.out.println("WARNING: multiple paths are specified, but no longer supported. First path will be used.");
    }

    // Get the first path
    String path = commandLineParams.paths.get(0);

    Context ctx;

    File war = new File(path);

    if (!war.exists()) {
      System.err.println("The specified path \"" + path + "\" does not exist.");
      jCommander.usage();
      System.exit(1);
    }

    // Use the commandline context-path (or default)
    // warn if the contextPath doesn't start with a '/'. This causes issues serving content at the context root.
    if (commandLineParams.contextPath.length() > 0 && !commandLineParams.contextPath.startsWith("/")) {
      System.out.println("WARNING: You entered a path: [" + commandLineParams.contextPath + "]. Your path should start with a '/'. Tomcat will update this for you, but you may still experience issues.");
    }

    final String ctxName = commandLineParams.contextPath;

    if (commandLineParams.expandWar && war.isFile()) {
      File appBase = new File(System.getProperty(Globals.CATALINA_BASE_PROP), tomcat.getHost().getAppBase());
      if (appBase.exists()) {
        appBase.delete();
      }
      appBase.mkdir();
      URL fileUrl = new URL("jar:" + war.toURI().toURL() + "!/");
      String expandedDir = ExpandWar.expand(tomcat.getHost(), fileUrl, "/expanded");
      System.out.println("Expanding " + war.getName() + " into " + expandedDir);

      System.out.println("Adding Context " + ctxName + " for " + expandedDir);
      ctx = tomcat.addWebapp(ctxName, expandedDir);
    } else {
      System.out.println("Adding Context " + ctxName + " for " + war.getPath());
      ctx = tomcat.addWebapp(ctxName, war.getAbsolutePath());
    }

    // we'll do it ourselves (see above)
    ((StandardContext) ctx).setUnpackWAR(false);

    if (!commandLineParams.shutdownOverride) {
      // allow Tomcat to shutdown if a context failure is detected
      ctx.addLifecycleListener(new LifecycleListener() {
        public void lifecycleEvent(LifecycleEvent event) {
          if (event.getLifecycle().getState() == LifecycleState.FAILED) {
            Server server = tomcat.getServer();
            if (server instanceof StandardServer) {
              System.err.println("SEVERE: Context [" + ctxName + "] failed in [" + event.getLifecycle().getClass().getName() + "] lifecycle. Allowing Tomcat to shutdown.");
              ((StandardServer) server).stopAwait();
            }
          }
        }
      });
    }


    // set the context xml location if there is only one war
    if (commandLineParams.contextXml != null) {
      System.out.println("Using context config: " + commandLineParams.contextXml);
      ctx.setConfigFile(new File(commandLineParams.contextXml).toURI().toURL());
    }

    // set the session manager
    if (commandLineParams.sessionStore != null) {
      SessionStore.getInstance(commandLineParams.sessionStore).configureSessionStore(commandLineParams, ctx);
    }

    //set the session timeout
    if (commandLineParams.sessionTimeout != null) {
      ctx.setSessionTimeout(commandLineParams.sessionTimeout);
    }

    addShutdownHook(tomcat);

    if (commandLineParams.enableBasicAuth || commandLineParams.tomcatUsersLocation != null) {
      tomcat.enableNaming();
    }

    if (commandLineParams.enableBasicAuth) {
      enableBasicAuth(ctx, commandLineParams.enableSSL);
    }

    //start the server
    tomcat.start();

        /*
         * NamingContextListener.lifecycleEvent(LifecycleEvent event)
         * cannot initialize GlobalNamingContext for Tomcat until
         * the Lifecycle.CONFIGURE_START_EVENT occurs, so this block
         * must sit after the call to tomcat.start() and it requires
         * tomcat.enableNaming() to be called much earlier in the code.
         */
    if (commandLineParams.enableBasicAuth || commandLineParams.tomcatUsersLocation != null) {
      configureUserStore(tomcat, commandLineParams);
    }

    commandLineParams = null;

    tomcat.getServer().await();
  }

  /**
   * Gets or creates temporary Tomcat base directory within target dir
   *
   * @param port port of web process
   * @return absolute dir path
   * @throws IOException if dir fails to be created
   */
  static String resolveTomcatBaseDir(Integer port, String tempDirectory) throws IOException {
    final File baseDir = tempDirectory != null ?
      new File(tempDirectory) :
      new File(System.getProperty("user.dir") + "/target/tomcat." + port);

    if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
      throw new IOException("Could not create temp dir: " + baseDir);
    }

    try {
      return baseDir.getCanonicalPath();
    } catch (IOException e) {
      return baseDir.getAbsolutePath();
    }
  }

  /*
   * Set up basic auth security on the entire application
   */
  static void enableBasicAuth(Context ctx, boolean enableSSL) {
    LoginConfig loginConfig = new LoginConfig();
    loginConfig.setAuthMethod("BASIC");
    ctx.setLoginConfig(loginConfig);
    ctx.addSecurityRole(AUTH_ROLE);

    SecurityConstraint securityConstraint = new SecurityConstraint();
    securityConstraint.addAuthRole(AUTH_ROLE);
    if (enableSSL) {
      securityConstraint.setUserConstraint(TransportGuarantee.CONFIDENTIAL.toString());
    }
    SecurityCollection securityCollection = new SecurityCollection();
    securityCollection.addPattern("/*");
    securityConstraint.addCollection(securityCollection);
    ctx.addConstraint(securityConstraint);
  }

  static void configureUserStore(final Tomcat tomcat, final CommandLineParams commandLineParams) throws Exception {
    String tomcatUsersLocation = commandLineParams.tomcatUsersLocation;
    if (tomcatUsersLocation == null) {
      tomcatUsersLocation = "../../tomcat-users.xml";
    }

    javax.naming.Reference ref = new javax.naming.Reference("org.apache.catalina.UserDatabase");
    ref.add(new StringRefAddr("pathname", tomcatUsersLocation));
    MemoryUserDatabase memoryUserDatabase =
        (MemoryUserDatabase) new MemoryUserDatabaseFactory().getObjectInstance(
            ref,
            new CompositeName("UserDatabase"),
            null,
            null);

    // Add basic auth user
    if (commandLineParams.basicAuthUser != null && commandLineParams.basicAuthPw != null) {

      memoryUserDatabase.setReadonly(false);
      Role user = memoryUserDatabase.createRole(AUTH_ROLE, AUTH_ROLE);
      memoryUserDatabase.createUser(
          commandLineParams.basicAuthUser,
          commandLineParams.basicAuthPw,
          commandLineParams.basicAuthUser).addRole(user);
      memoryUserDatabase.save();

    } else if (System.getenv("BASIC_AUTH_USER") != null && System.getenv("BASIC_AUTH_PW") != null) {

      memoryUserDatabase.setReadonly(false);
      Role user = memoryUserDatabase.createRole(AUTH_ROLE, AUTH_ROLE);
      memoryUserDatabase.createUser(
          System.getenv("BASIC_AUTH_USER"),
          System.getenv("BASIC_AUTH_PW"),
          System.getenv("BASIC_AUTH_USER")).addRole(user);
      memoryUserDatabase.save();
    }

    // Register memoryUserDatabase with GlobalNamingContext
    System.out.println("MemoryUserDatabase: " + memoryUserDatabase);
    tomcat.getServer().getGlobalNamingContext().addToEnvironment("UserDatabase", memoryUserDatabase);

    org.apache.catalina.deploy.ContextResource ctxRes =
        new org.apache.catalina.deploy.ContextResource();
    ctxRes.setName("UserDatabase");
    ctxRes.setAuth("Container");
    ctxRes.setType("org.apache.catalina.UserDatabase");
    ctxRes.setDescription("User database that can be updated and saved");
    ctxRes.setProperty("factory", "org.apache.catalina.users.MemoryUserDatabaseFactory");
    ctxRes.setProperty("pathname", tomcatUsersLocation);
    tomcat.getServer().getGlobalNamingResources().addResource(ctxRes);
    tomcat.getEngine().setRealm(new org.apache.catalina.realm.UserDatabaseRealm());
  }

  /**
   * Stops the embedded Tomcat server.
   */
  static void addShutdownHook(final Tomcat tomcat) {

    // add shutdown hook to stop server
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        try {
          if (tomcat != null) {
            tomcat.getServer().stop();
          }
        } catch (LifecycleException exception) {
          throw new RuntimeException("WARNING: Cannot Stop Tomcat " + exception.getMessage(), exception);
        }
      }
    });
  }
}
