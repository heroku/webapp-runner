package webapp.runner.launch;

import java.util.Map;
import org.apache.catalina.Context;

public class SessionManager {
    
    public SessionManager(){
    }

    /**
     * Configures Memcache session manager
     *
     * @param sessionManager session manager name to instantiate
     * @return instance of session manager if available or itself
     */
    public static SessionManager getInstance(String sessionManager) {
        if(sessionManager == null){
            return null;
        }
        if("memcache".equals(sessionManager)){
            return new MemcacheSessionManager();
        } else if ("redis".equals(sessionManager)){
            return new RedisSessionManager();
        } else {
            return new SessionManager();
        }
    }

    /**
     * Configures default session manager - NOOP
     *
     * @param argMap Arguments map
     * @param ctx Tomcat context
     */
    public void configureSessionManager(Map<Argument, String> argMap, Context ctx){
        // do nothing, let tomcat use the default
        System.out.println("WARNING: session manager " + argMap.get(Argument.SESSION_MANAGER) + " unsupported using default");
    }
}


