package webapp.runner.launch;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the command line parameters
 * 
 * @author jamesward
 * 
 */

public class CommandLineParams {

    @Parameter
    public List<String> paths = new ArrayList<String>();

    @Parameter(names = "--session-timeout", description = "The number of minutes of inactivity before a user's session is timed out.")
    public Integer sessionTimeout;
    
    @Parameter(names = "--port", description = "The port that the server will accept http requests on.")
    public Integer port = 8080;    

    @Parameter(names = "--context-xml", description = "The path to the context xml to use.")
    public String contextXml;

    @Parameter(names = "--path", description = "The context path")
    public String contextPath = "";
    
    @Parameter(names = "--shutdown-override", description = "Overrides the default behavior and casues Tomcat to ignore lifecycle failure events rather than shutting down when they occur.")
    public boolean shutdownOverride = false;
    
    @Parameter(names = "--session-store", description = "Session store to use (valid options are 'memcache' or 'redis')")
    public String sessionStore;
    
    @Parameter(names = "--session-store-operation-timeout", description = "Operation timeout for the memcache session store. (default is 5000ms)")
    public Integer sessionStoreOperationTimout = 5000;
    
    @Parameter(names = "--session-store-locking-mode", description = "Session locking mode for use with memcache session store. (default is all)")
    public String sessionStoreLockingMode = "all";
    
    @Parameter(names = "--session-store-ignore-pattern", description = "Request pattern to not track sessions for. Valid only with memcache session store. (default is '.*\\.(png|gif|jpg|css|js)$'")
    public String sessionStoreIgnorePattern = ".*\\.(png|gif|jpg|css|js)$";

    @Parameter(names = "--help", help = true)
    public boolean help;
    
    @Parameter(names = "--enable-compression", description = "Enable GZIP compression on responses")
    public boolean enableCompression;
    
    @Parameter(names = "--compressable-mime-types", description = "Comma delimited list of mime types that will be compressed when using GZIP compression.")
    public String compressableMimeTypes = "text/html,text/xml,text/plain,text/css,application/json,application/xml,text/javascript,application/javascript";

    @Parameter(names = "--enable-ssl", description = "Specify -Djavax.net.ssl.trustStore and -Djavax.net.ssl.trustStorePassword in JAVA_OPTS. Note: should not be used if a reverse proxy is terminating SSL for you (such as on Heroku)")
    public boolean enableSSL;

    @Parameter(names = "--enable-client-auth", description = "Specify -Djavax.net.ssl.keyStore and -Djavax.net.ssl.keyStorePassword in JAVA_OPTS")
    public boolean enableClientAuth;

    @Parameter(names = "--enable-basic-auth", description = "Secure the app with basic auth. Use with --basic-auth-user and --basic-auth-pw or --tomcat-users-location")
    public boolean enableBasicAuth = false;
    
    @Parameter(names = "--basic-auth-user", description = "Username to be used with basic auth. Defaults to BASIC_AUTH_USER env variable.")
    public String basicAuthUser;
    
    @Parameter(names = "--basic-auth-pw", description = "Password to be used with basic auth. Defaults to BASIC_AUTH_PW env variable.")
    public String basicAuthPw;
    
    @Parameter(names = "--tomcat-users-location", description = "Location of the tomcat-users.xml file. (relative to the location of the webapp-runner jar file)")
    public String tomcatUsersLocation;
    
    @Parameter(names = "--expand-war", description = "Expand the war file and set it as source")
    public boolean expandWar = false;

    @Parameter(names = "--uri-encoding", description = "Specify URIEncoding for connector")
    public String uriEncoding;
}
