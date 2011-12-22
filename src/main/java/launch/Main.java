package launch;
import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

public class Main {
	
	private static String parseWebApppDir(String[] args) {
		return args[0];
	}

    public static void main(String[] args) throws Exception {

        Tomcat tomcat = new Tomcat();

        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if(webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        tomcat.setPort(Integer.valueOf(webPort));

        Context ctx = tomcat.addWebapp("/", new File(parseWebApppDir(args)).getAbsolutePath());
        ctx.setSessionTimeout(30);
        
        System.out.println("configuring app with basedir: " + new File(parseWebApppDir(args)).getAbsolutePath());

        tomcat.start();
        tomcat.getServer().await();  
    }
}
