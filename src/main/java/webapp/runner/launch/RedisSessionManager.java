package webapp.runner.launch;

import java.net.URISyntaxException;
import java.util.Map;
import org.apache.catalina.Context;
import java.net.URI;

import ru.zinin.redis.session.RedisManager;

class RedisSessionManager extends SessionManager {
    
    
    public void configureSessionManager(Map<Argument, String> argMap, Context ctx){
        RedisManager redisManager = new RedisManager();
        redisManager.setDisableListeners(true);

        if(System.getenv("REDIS_URL") == null){
            System.out.println("WARNING: redis session store being used, but the required environment variable isn't set.");
            System.out.println("Redis session store is configured with REDIS_URL");
        } else {
            try {
                URI redisUri = new URI(System.getenv("REDIS_URL"));

                if(redisUri.getHost() != null) {
                    redisManager.setRedisHostname(redisUri.getHost());
                }
                if(redisUri.getPort() != -1) {
                    redisManager.setRedisPort(redisUri.getPort());
                }
                if(redisUri.getPath() != null && redisUri.getPath().length() > 1) {
                    redisManager.setDbIndex(Integer.parseInt(redisUri.getPath().split("/")[1]));
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