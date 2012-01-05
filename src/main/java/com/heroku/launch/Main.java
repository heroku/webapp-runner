package com.heroku.launch;
import java.io.File;
import java.util.Map;

import org.apache.catalina.Context;
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
			System.out.println(argument.argName() + "     " + argument.helpText());
		}
	}

    public static void main(String[] args) throws Exception {
    	
    	//print help text when asked for
    	if("help".equals(args[0]) || "--help".equals(args[0])) {
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
		}
    	
        Tomcat tomcat = new Tomcat();

        //set the port
        String webPort = 
        		argMap.containsKey(Argument.PORT) ? argMap.get(Argument.PORT) : "8080";

        tomcat.setPort(Integer.valueOf(webPort));

        //create a context with the webapp
        Context ctx = tomcat.addWebapp("/", new File(argMap.get(Argument.APPLICATION_DIR)).getAbsolutePath());
        
        //set the session timeout
        if(argMap.containsKey(Argument.SESSION_TIMEOUT)) {
        	ctx.setSessionTimeout(Integer.valueOf(argMap.get(Argument.SESSION_TIMEOUT)));
        }
        
        System.out.println("configuring app at: " + new File(argMap.get(Argument.APPLICATION_DIR)).getAbsolutePath());

        //start the server
        tomcat.start();
        tomcat.getServer().await();  
    }
}
