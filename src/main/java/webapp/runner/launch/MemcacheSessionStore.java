package webapp.runner.launch;

import org.apache.catalina.Context;

import de.javakaffee.web.msm.MemcachedBackupSessionManager;

class MemcacheSessionStore extends SessionStore {

    /**
     * Configures Memcache session manager
     *
     * @param commandLineParams Arguments map
     * @param ctx Tomcat context
     */
    @Override
    public void configureSessionStore(CommandLineParams commandLineParams, Context ctx){        
        MemcachedBackupSessionManager manager = new MemcachedBackupSessionManager();
        
        if(System.getenv("MEMCACHE_SERVERS") != null) { //Using Memcache
            if(System.getenv("MEMCACHE_USERNAME") == null
                    || System.getenv("MEMCACHE_PASSWORD") == null) {
                printNoConfigError();
                return;
            }             
            manager.setMemcachedNodes(System.getenv("MEMCACHE_SERVERS") + ":11211");
            manager.setUsername(System.getenv("MEMCACHE_USERNAME"));
            manager.setPassword(System.getenv("MEMCACHE_PASSWORD"));
        } else if(System.getenv("MEMCACHIER_SERVERS") != null) { //Using Memcachier
            if(System.getenv("MEMCACHIER_USERNAME") == null
                    || System.getenv("MEMCACHIER_PASSWORD") == null) {
                printNoConfigError();
                return;
            }            
            manager.setMemcachedNodes(System.getenv("MEMCACHIER_SERVERS"));
            manager.setUsername(System.getenv("MEMCACHIER_USERNAME"));
            manager.setPassword(System.getenv("MEMCACHIER_PASSWORD"));
        } else {
            printNoConfigError();
            return;            
        }
        
        manager.setMemcachedProtocol("binary");
        manager.setSticky(false);
        manager.setSessionBackupAsync(false);
        manager.setEnabled(true);
        manager.setEnableStatistics(true);
        manager.setOperationTimeout(commandLineParams.sessionStoreOperationTimout);
        manager.setLockingMode(commandLineParams.sessionStoreLockingMode);
        manager.setRequestUriIgnorePattern(commandLineParams.sessionStoreIgnorePattern);
        ctx.setManager(manager);
    }
    
    private void printNoConfigError() {
        System.out.println("WARNING: memcache session store being used, but the required environment variables aren't set.");
        System.out.println("Memcache session store is configured with MEMCACHE_SERVERS, MEMCACHE_USERNAME, MEMCACHE_PASSWORD");
        System.out.println("or MEMCACHIER_SERVERS, MEMCACHIER_USERNAME, MEMCACHIER_PASSWORD");        
    }
}