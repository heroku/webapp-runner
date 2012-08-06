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
import java.util.Map;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;


/**
 * This is the main entry point to tomcat-runner. Helpers are called to parse the arguments.
 * Tomcat configuration and launching takes place here.
 * 
 * @author johnsimone
 *
 */
public class Main {
	
	/**
	 * Prints help text
	 */
	public static void printHelp() {
		System.out.println("Tomcat Runner runs a Java web application that is represented as an exploded war in a Tomcat container");
		System.out.println("Usage: java -jar tomcat-runner.jar [arguments...] path/to/webapp");
		System.out.println("Arguments:");
		for (Argument argument : Argument.values()) {
			System.out.format("%-40s%-60s%n", argument.argName(), argument.helpText());
		}
	}

    public static void main(String[] args) throws Exception {
    	
    	//print help text when asked for
    	if(args.length == 0 || "help".equals(args[0]) || "--help".equals(args[0])) {
    		printHelp();
    		System.exit(0);
    	}
    	
    	//parse the arguments
    	Map<Argument, String> argMap = null;
		try {
			argMap = ArgParser.parseArgs(args);
		} catch (ArgumentNotFoundException e) {
			System.out.println("Unexpected Argument: " + e.getArgName());
			System.out.println("For usage information run `java -jar tomcat-runner.jar help`");
			System.exit(1);
		} catch (MissingAppException e) {
			System.out.println("Application location not defined");
			System.out.println("For usage information run `java -jar tomcat-runner.jar help`");
			System.exit(1);
		}
    	
        Tomcat tomcat = new Tomcat();

        //set the port
        String webPort = 
        		argMap.containsKey(Argument.PORT) ? argMap.get(Argument.PORT) : "8080";

        // initialize the connector
        Connector nioConnector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        nioConnector.setPort(Integer.valueOf(webPort));

        tomcat.setConnector(nioConnector);
        tomcat.getService().addConnector(tomcat.getConnector());

        tomcat.setPort(Integer.valueOf(webPort));

        // set directory for temp files
        tomcat.setBaseDir(resolveTomcatBaseDir(webPort));

        //create a context with the webapp
        String path = argMap.containsKey(Argument.PATH) ? argMap.get(Argument.PATH) : "";
        
        //warn if the path doesn't start with a '/'. This causes issues serving content at the context root.
        if(path.length() > 0 && !path.startsWith("/")) {
        	System.out.println("WARNING: you entered a path: [" + path + "]. Your path should start with a '/'. Tomcat will update this for you, but you may still experience issues.");
        }
        
        Context ctx = tomcat.addWebapp(path, new File(argMap.get(Argument.APPLICATION_DIR)).getAbsolutePath());
        
        //set the session manager
        if(argMap.containsKey(Argument.SESSION_MANAGER)) {
           SessionManager.getInstance(argMap.get(Argument.SESSION_MANAGER)).configureSessionManager(argMap, ctx);
        }
        
        //set the context xml location
        if(argMap.containsKey(Argument.CONTEXT_XML)) {
        	System.out.println("Using context config: " + argMap.get(Argument.CONTEXT_XML));
        	ctx.setConfigFile(new File(argMap.get(Argument.CONTEXT_XML)).toURI().toURL());
        }
        
        //set the session timeout
        if(argMap.containsKey(Argument.SESSION_TIMEOUT)) {
        	ctx.setSessionTimeout(Integer.valueOf(argMap.get(Argument.SESSION_TIMEOUT)));
        }
        
        System.out.println("deploying app from: " + new File(argMap.get(Argument.APPLICATION_DIR)).getAbsolutePath());

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
    static String resolveTomcatBaseDir(String port) throws IOException {
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
