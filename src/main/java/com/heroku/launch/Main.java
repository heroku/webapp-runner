package com.heroku.launch;
import java.io.File;
import java.util.Map;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

public class Main {
	
	public static void printHelp() {
		System.out.println("Tomcat Runner runs a Java web application that is represented as an exploded war in a Tomcat container");
		System.out.println("Usage: java -jar tomcat-runner.jar [arguments...] path/to/webapp");
		System.out.println("Arguments:");
		for (Argument argument : Argument.values()) {
			System.out.println(argument.argName() + "     " + argument.helpText());
		}
	}

    public static void main(String[] args) throws Exception {

    	if("help".equals(args[0]) || "--help".equals(args[0])) {
    		printHelp();
    		System.exit(0);
    	}
    	
    	Map<Argument, String> argMap = null;
		try {
			argMap = ArgParser.parseArgs(args);
		} catch (ArgumentNotFoundException e) {
			System.out.println("Unexpected Argument!!!");
			System.out.println("For usage information run `java -jar tomcat-runner.jar help`");
			System.exit(1);
		}
    	
        Tomcat tomcat = new Tomcat();

        String webPort = 
        		argMap.containsKey(Argument.PORT) ? argMap.get(Argument.PORT) : "8080";

        tomcat.setPort(Integer.valueOf(webPort));

        Context ctx = tomcat.addWebapp("/", new File(argMap.get(Argument.APPLICATION_DIR)).getAbsolutePath());
        
        if(argMap.containsKey(Argument.SESSION_TIMEOUT)) {
        	ctx.setSessionTimeout(Integer.valueOf(argMap.get(Argument.SESSION_TIMEOUT)));
        }
        
        System.out.println("configuring app with basedir: " + new File(argMap.get(Argument.APPLICATION_DIR)).getAbsolutePath());

        tomcat.start();
        tomcat.getServer().await();  
    }
}
