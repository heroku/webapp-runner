package webapp.runner.launch;

import java.util.Map;
import org.apache.catalina.Context;

import de.javakaffee.web.msm.MemcachedBackupSessionManager;

class MemcacheSessionStore extends SessionStore {

    /**
     * Configures Memcache session manager
     *
     * @param argMap Arguments map
     * @param ctx Tomcat context
     */
    @Override
    public void configureSessionStore(Map<Argument, String> argMap, Context ctx){
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
            if(argMap.containsKey(Argument.SESSION_MANAGER_OPERATION_TIMEOUT)) {
                manager.setOperationTimeout(Integer.valueOf(argMap.get(Argument.SESSION_MANAGER_OPERATION_TIMEOUT)));
            } else {
                manager.setOperationTimeout(5000);
            }
            if(argMap.containsKey(Argument.SESSION_MANAGER_LOCKING_MODE)) {
                manager.setLockingMode(argMap.get(Argument.SESSION_MANAGER_LOCKING_MODE));
            } else {
                manager.setLockingMode("all");
            }
            if(argMap.containsKey(Argument.SESSION_MANAGER_IGNORE_PATTERN)) {
                manager.setRequestUriIgnorePattern(argMap.get(Argument.SESSION_MANAGER_IGNORE_PATTERN));
            } else {
                manager.setRequestUriIgnorePattern(".*\\.(png|gif|jpg|css|js)$");
            }
            ctx.setManager(manager);           
    }
}