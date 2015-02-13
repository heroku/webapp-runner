package webapp.runner.launch;

import java.net.URISyntaxException;

import org.apache.catalina.Context;
import java.net.URI;

import ru.zinin.redis.session.RedisManager;

class RedisSessionStore extends SessionStore {

    /**
     * Configures Redis session manager
     *
     * @param commandLineParams Arguments map
     * @param ctx Tomcat context
     */
    @Override
    public void configureSessionStore(CommandLineParams commandLineParams, Context ctx){
        RedisManager redisManager = new RedisManager();
        redisManager.setDisableListeners(true);

        if(System.getenv("REDIS_URL") == null && System.getenv("REDISTOGO_URL") == null && System.getenv("REDISCLOUD_URL") == null){
            System.out.println("WARNING: redis session store being used, but the required environment variable isn't set.");
            System.out.println("Redis session store is configured with REDIS_URL, REDISTOGO_URL or REDISCLOUD_URL");
        } else {
            try {
                URI redisUri;
                if(System.getenv("REDIS_URL") != null) {                    
                    redisUri = new URI(System.getenv("REDIS_URL"));                    
                } else if(System.getenv("REDISTOGO_URL") != null)  {
                  redisUri = new URI(System.getenv("REDISTOGO_URL"));
                } else {
                    redisUri = new URI(System.getenv("REDISCLOUD_URL"));
                }

                if(redisUri.getHost() != null) {
                    redisManager.setRedisHostname(redisUri.getHost());
                }
                if(redisUri.getPort() != -1) {
                    redisManager.setRedisPort(redisUri.getPort());
                }
                if(redisUri.getUserInfo() != null) {
                    redisManager.setRedisPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(":")+1));
                }
                
            } catch (URISyntaxException e){
                System.out.println("WARNING: redis session store environment variable invalid "+System.getenv("REDIS_URL"));
            }
        }
        ctx.setManager(redisManager);
    }
    
    
}