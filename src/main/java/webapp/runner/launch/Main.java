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
import java.util.Arrays;

import com.beust.jcommander.JCommander;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FilenameUtils;


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

        Tomcat tomcat = new Tomcat();

        // set directory for temp files
        tomcat.setBaseDir(resolveTomcatBaseDir(commandLineParams.port));

        // initialize the connector
        Connector nioConnector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        nioConnector.setPort(commandLineParams.port);

        tomcat.setConnector(nioConnector);
        tomcat.getService().addConnector(tomcat.getConnector());

        tomcat.setPort(commandLineParams.port);
        
        if (commandLineParams.paths.size() > 1) {
            System.out.println("WARNING: Since you specified more than one path, the context paths will be automatically set to the name of the path without the extension. A path that resolves to a context path of \"/ROOT\" will be replaced with \"/\"");
        }

        for (String path : commandLineParams.paths) {
            File war = new File(path);
            
            if (!war.exists()) {
                System.err.println("The specified path \"" + path + "\" does not exist.");
                jCommander.usage();
                System.exit(1);
            }
            
            String ctxName = "";

            // Use the commandline context-path (or default) if there is only one war
            if (commandLineParams.paths.size() == 1) {
                // warn if the contextPath doesn't start with a '/'. This causes issues serving content at the context root.
                if (commandLineParams.contextPath.length() > 0 && !commandLineParams.contextPath.startsWith("/")) {
                    System.out.println("WARNING: You entered a path: [" + commandLineParams.contextPath + "]. Your path should start with a '/'. Tomcat will update this for you, but you may still experience issues.");
                }
                
                ctxName = commandLineParams.contextPath;
            }
            else {
                ctxName = "/" + FilenameUtils.removeExtension(war.getName());

                if (ctxName.equals("/ROOT") || (commandLineParams.paths.size() == 1)) {
                    ctxName = "/";
                }
            }
                
            System.out.println("Adding Context " + ctxName + " for " + war.getPath());
            Context ctx = tomcat.addWebapp(ctxName, war.getAbsolutePath());

            // set the session manager
            if (commandLineParams.sessionStore != null) {
                SessionStore.getInstance(commandLineParams.sessionStore).configureSessionStore(commandLineParams, ctx);
            }

            // set the context xml location
            // todo: how do we handle this with multiple wars?
            //if(argMap.containsKey(Argument.CONTEXT_XML)) {
            //    System.out.println("Using context config: " + argMap.get(Argument.CONTEXT_XML));
            //    ctx.setConfigFile(new File(argMap.get(Argument.CONTEXT_XML)).toURI().toURL());
            //}

            //set the session timeout
            if(commandLineParams.sessionTimeout != null) {
                ctx.setSessionTimeout(commandLineParams.sessionTimeout);
            }
        }

        //start the server
        tomcat.start();
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
}
