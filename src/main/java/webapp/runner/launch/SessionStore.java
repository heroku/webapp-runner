package webapp.runner.launch;

import org.apache.catalina.Context;

public class SessionStore {
    
    public SessionStore(){
    }

    /**
     * Configures session store
     *
     * @param sessionStore session store name to instantiate
     * @return instance of session manager if available or itself
     */
    public static SessionStore getInstance(String sessionStore) {
        if(sessionStore == null){
            return null;
        }
        if("memcache".equals(sessionStore)){
            return new MemcacheSessionStore();
        } else if ("redis".equals(sessionStore)){
            return new RedisSessionStore();
        } else {
            return new SessionStore();
        }
    }

    /**
     * Configures default session manager - NOOP
     *
     * @param commandLineParams Arguments map
     * @param ctx Tomcat context
     */
    public void configureSessionStore(CommandLineParams commandLineParams, Context ctx){
        // do nothing, let tomcat use the default
        System.out.println("WARNING: session manager " + commandLineParams.sessionStore + " unsupported using default");
    }
}


