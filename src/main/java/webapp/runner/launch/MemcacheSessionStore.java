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
        if(System.getenv("MEMCACHE_SERVERS") == null
                || System.getenv("MEMCACHE_USERNAME") == null
                || System.getenv("MEMCACHE_PASSWORD") == null) {
            System.out.println("WARNING: memcache session store being used, but the required environment variables aren't set.");
            System.out.println("Memcache session store is configured with MEMCACHE_SERVERS, MEMCACHE_USERNAME, MEMCACHE_PASSWORD");
        }
        MemcachedBackupSessionManager manager = new MemcachedBackupSessionManager();
        manager.setMemcachedNodes(System.getenv("MEMCACHE_SERVERS") + ":11211");
        manager.setMemcachedProtocol("binary");
        manager.setUsername(System.getenv("MEMCACHE_USERNAME"));
        manager.setPassword(System.getenv("MEMCACHE_PASSWORD"));
        manager.setSticky(false);
        manager.setSessionBackupAsync(false);
        manager.setEnabled(true);
        manager.setEnableStatistics(true);
        manager.setOperationTimeout(commandLineParams.sessionStoreOperationTimout);
        manager.setLockingMode(commandLineParams.sessionStoreLockingMode);
        manager.setRequestUriIgnorePattern(commandLineParams.sessionStoreIgnorePattern);
        ctx.setManager(manager);
    }
}