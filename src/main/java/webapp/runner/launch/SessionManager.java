package webapp.runner.launch;

import java.util.Map;
import org.apache.catalina.Context;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class SessionManager {
    
    public SessionManager(){
    }
    
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
    
    public void configureSessionManager(Map<Argument, String> argMap, Context ctx){
        // do nothing, let tomcat use the default
        System.out.println("WARNING: session manager " + argMap.get(Argument.SESSION_MANAGER) + " unsupported using default");
    }
}


