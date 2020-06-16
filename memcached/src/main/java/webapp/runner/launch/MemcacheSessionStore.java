package webapp.runner.launch;

import de.javakaffee.web.msm.MemcachedBackupSessionManager;
import org.apache.catalina.Context;

public class MemcacheSessionStore extends SessionStore {

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
            manager.setMemcachedNodes(System.getenv("MEMCACHE_SERVERS") + ":11211");
            manager.setUsername(System.getenv("MEMCACHE_USERNAME"));
            manager.setPassword(System.getenv("MEMCACHE_PASSWORD"));
        } else if(System.getenv("MEMCACHIER_SERVERS") != null) { //Using Memcachier

            String[] servers = System.getenv("MEMCACHIER_SERVERS").split(",");
            if (servers.length > 1) {
              for (int i = 0; i < servers.length; ++i) {
                servers[i] = "mc" + i + ":" + servers[i];
              }
            }
            String serversStr = servers[0];
            for (int i = 1; i < servers.length; ++i) {
              serversStr += "," + servers[i];
            }
            manager.setMemcachedNodes(serversStr);
            manager.setUsername(System.getenv("MEMCACHIER_USERNAME"));
            manager.setPassword(System.getenv("MEMCACHIER_PASSWORD"));
        } else if (System.getenv("MEMCACHEDCLOUD_SERVERS") != null) { //Using Memcached Cloud

            manager.setMemcachedNodes(System.getenv("MEMCACHEDCLOUD_SERVERS"));
            manager.setUsername(System.getenv("MEMCACHEDCLOUD_USERNAME"));
            manager.setPassword(System.getenv("MEMCACHEDCLOUD_PASSWORD"));
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

        if (commandLineParams.memcachedTranscoderFactoryClass != null) {
            manager.setTranscoderFactoryClass(commandLineParams.memcachedTranscoderFactoryClass);
        }

        ctx.setManager(manager);
    }

    private void printNoConfigError() {
        System.out.println("WARNING: memcache session store being used, but the required environment variables aren't set.");
        System.out.println("Memcache session store is configured with MEMCACHE_SERVERS, MEMCACHE_USERNAME, MEMCACHE_PASSWORD");
        System.out.println("or MEMCACHIER_SERVERS, MEMCACHIER_USERNAME, MEMCACHIER_PASSWORD");
    }
}
