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

import javax.naming.CompositeName;
import javax.naming.StringRefAddr;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.users.MemoryUserDatabase;
import org.apache.catalina.users.MemoryUserDatabaseFactory;

import com.beust.jcommander.JCommander;


/**
 * This is the main entry point to webapp-runner. Helpers are called to parse the arguments.
 * Tomcat configuration and launching takes place here.
 *
 * @author johnsimone
 * @author jamesward
 *
 */
public class Main {

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
        tomcat.setBaseDir(resolveTomcatBaseDir(commandLineParams.port));

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

		boolean enableBasicAuth = commandLineParams.enableBasicAuth;
		if (commandLineParams.enableBasicAuth) {
			tomcat.enableNaming();
		}

        if(commandLineParams.enableCompression) {
        	nioConnector.setProperty("compression", "on");
        	nioConnector.setProperty("compressableMimeType", commandLineParams.compressableMimeTypes);
        }

        tomcat.setConnector(nioConnector);
        /*if (commandLineParams.enableSSL) {
			tomcat.getConnector().setSecure(true);
			tomcat.getConnector().setProperty("SSLEnabled", "true");
			tomcat.enableNaming();
        }*/
        tomcat.getService().addConnector(tomcat.getConnector());

        tomcat.setPort(commandLineParams.port);
        
        if (commandLineParams.paths.size() > 1) {
            System.out.println("WARNING: multiple paths are specified, but no longer supported. First path will be used.");
        }
        
        // Get the first path
        String path = commandLineParams.paths.get(0);

        Context ctx = null;
        
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
            
        System.out.println("Adding Context " + ctxName + " for " + war.getPath());
        
        ctx = tomcat.addWebapp(ctxName, war.getAbsolutePath());
          
        if(!commandLineParams.shutdownOverride) {          
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
        if(commandLineParams.contextXml != null) {
            System.out.println("Using context config: " + commandLineParams.contextXml);
            ctx.setConfigFile(new File(commandLineParams.contextXml).toURI().toURL());
        }

        // set the session manager
        if (commandLineParams.sessionStore != null) {
            SessionStore.getInstance(commandLineParams.sessionStore).configureSessionStore(commandLineParams, ctx);
        }

        //set the session timeout
        if(commandLineParams.sessionTimeout != null) {
            ctx.setSessionTimeout(commandLineParams.sessionTimeout);
        }
        
        commandLineParams = null;

        addShutdownHook(tomcat);
        
        //start the server
        tomcat.start();

        /*
         * NamingContextListener.lifecycleEvent(LifecycleEvent event)
         * cannot initialize GlobalNamingContext for Tomcat until
         * the Lifecycle.CONFIGURE_START_EVENT occurs, so this block 
         * must sit after the call to tomcat.start() and it requires
         * tomcat.enableNaming() to be called much earlier in the code.
         */
        if (enableBasicAuth) {
    		javax.naming.Reference ref = new javax.naming.Reference("org.apache.catalina.UserDatabase");
    		ref.add(new StringRefAddr("pathname", "../../tomcat-users.xml"));
    		MemoryUserDatabase memoryUserDatabase = 
    				(MemoryUserDatabase) new MemoryUserDatabaseFactory().getObjectInstance(
    				ref,
    				new CompositeName("UserDatabase"),
    				null,
    				null);
    		// Register memoryUserDatabase with GlobalNamingContext
    		System.out.println("MemoryUserDatabase: " + memoryUserDatabase);
    		System.out.println(tomcat.getServer());
    		System.out.println("GlobalNamingContext: " + tomcat.getServer().getGlobalNamingContext());
    		tomcat.getServer().getGlobalNamingContext().addToEnvironment("UserDatabase", memoryUserDatabase);

    		org.apache.catalina.deploy.ContextResource ctxRes =
    				new org.apache.catalina.deploy.ContextResource();
    		ctxRes.setName("UserDatabase");
    		ctxRes.setAuth("Container");
    		ctxRes.setType("org.apache.catalina.UserDatabase");
    		ctxRes.setDescription("User database that can be updated and saved");
    		ctxRes.setProperty("factory", "org.apache.catalina.users.MemoryUserDatabaseFactory");
    		ctxRes.setProperty("pathname", "../../tomcat-users.xml");
    		System.out.println("ContextResource: " + ctxRes);
    		System.out.println(tomcat.getServer());
    		System.out.println("GlobalNamingResources: " + tomcat.getServer().getGlobalNamingResources());
    		tomcat.getServer().getGlobalNamingResources().addResource(ctxRes);
    		tomcat.getEngine().setRealm(new org.apache.catalina.realm.UserDatabaseRealm());
        }

        tomcat.getServer().await();
    }

    /**
     * Gets or creates temporary Tomcat base directory within target dir
     *
     * @param port port of web process
     * @return absolute dir path
     * @throws IOException if dir fails to be created
     */
    static String resolveTomcatBaseDir(Integer port) throws IOException {
        final File baseDir = new File(System.getProperty("user.dir") + "/target/tomcat." + port);

        if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
            throw new IOException("Could not create temp dir: " + baseDir);
        }

        try {
            return baseDir.getCanonicalPath();
        } catch (IOException e) {
            return baseDir.getAbsolutePath();
        }
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
