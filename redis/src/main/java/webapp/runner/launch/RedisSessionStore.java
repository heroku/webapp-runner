package webapp.runner.launch;

import org.apache.catalina.Context;
import org.redisson.config.Config;
import org.redisson.tomcat.RedissonSessionManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RedisSessionStore extends SessionStore {

  /**
   * Configures Redis session manager
   *
   * @param commandLineParams Arguments map
   * @param ctx               Tomcat context
   */
  @Override
  public void configureSessionStore(CommandLineParams commandLineParams, Context ctx) {

    String redisUri;
    if (System.getenv("REDIS_URL") == null && System.getenv("REDISTOGO_URL") == null && System.getenv("REDISCLOUD_URL") == null) {
      System.out.println("WARNING: redis session store being used, but the required environment variable isn't set.");
      System.out.println("Redis session store is configured with REDIS_URL, REDISTOGO_URL or REDISCLOUD_URL");
    } else {
      if (System.getenv("REDIS_URL") != null) {
        redisUri = System.getenv("REDIS_URL");
      } else if (System.getenv("REDISTOGO_URL") != null) {
        redisUri = System.getenv("REDISTOGO_URL");
      } else {
        redisUri = System.getenv("REDISCLOUD_URL");
      }

      Config config = new Config();
      config.useClusterServers().addNodeAddress(redisUri);

      try {
        File temp = File.createTempFile("tempfile", ".tmp");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp));) {
          bw.write(config.toJSON());
          RedissonSessionManager redisManager = new RedissonSessionManager();
          ctx.setManager(redisManager);
        }
      } catch (IOException e) {
        System.out.printf("WARNING: could not write redis configuration for %s\n", redisUri);
      }
    }
  }
}