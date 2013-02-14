package webapp.runner.launch;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
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
    
    @Parameter(names = "--compressable-mime-types", description = "Comma delimited list of mime types that will be compressed when using GZIP compression")
    public String compressableMimeTypes = "text/html,text/xml,text/plain,text/css,application/json,application/xml,text/javascript,application/javascript";

}
