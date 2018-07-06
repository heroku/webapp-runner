package webapp.runner.launch;

import org.apache.catalina.Context;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.tomcat.RedissonSessionManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

public class RedisSessionStore extends SessionStore {

  /**
   * Configures Redis session manager
   *
   * @param commandLineParams Arguments map
   * @param ctx               Tomcat context
   */
  @Override
  public void configureSessionStore(CommandLineParams commandLineParams, Context ctx) {
    System.out.println("Using redis session store: org.redisson.tomcat.RedissonSessionManager");

    String redisUriString;
    if (System.getenv("REDIS_URL") == null && System.getenv("REDISTOGO_URL") == null && System.getenv("REDISCLOUD_URL") == null) {
      System.out.println("WARNING: using redis session store, but the required environment variable isn't set.");
      System.out.println("Redis session store is configured with REDIS_URL, REDISTOGO_URL or REDISCLOUD_URL");
    } else {
      if (System.getenv("REDIS_URL") != null) {
        redisUriString = System.getenv("REDIS_URL");
      } else if (System.getenv("REDISTOGO_URL") != null) {
        redisUriString = System.getenv("REDISTOGO_URL");
      } else {
        redisUriString = System.getenv("REDISCLOUD_URL");
      }

      URI redisUri = URI.create(redisUriString);

      Config config = new Config();
      SingleServerConfig serverConfig = config.useSingleServer()
          .setAddress(redisUriString)
          .setConnectionPoolSize(commandLineParams.sessionStorePoolSize)
          .setConnectionMinimumIdleSize(commandLineParams.sessionStorePoolSize)
          .setTimeout(commandLineParams.sessionStoreOperationTimout);

      if (redisUri.getUserInfo() != null) {
        serverConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(":")+1));
      }

      try {
        File configFile = File.createTempFile("redisson", ".json");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(configFile));) {
          bw.write(config.toJSON());
          RedissonSessionManager redisManager = new RedissonSessionManager();
          redisManager.setConfigPath(configFile.getAbsolutePath());
          ctx.setManager(redisManager);
        }
      } catch (IOException e) {
        System.out.printf("WARNING: could not write redis configuration for %s\n", redisUri);
      }
    }
  }
}