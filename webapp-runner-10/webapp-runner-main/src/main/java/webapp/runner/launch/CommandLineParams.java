package webapp.runner.launch;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the command line parameters
 *
 * @author jamesward
 */
public class CommandLineParams {

  @Parameter public List<String> paths = new ArrayList<String>();

  @Parameter(
      names = "--session-timeout",
      description = "The number of minutes of inactivity before a user's session is timed out.")
  public Integer sessionTimeout;

  @Parameter(
      names = "--port",
      description = "The port that the server will accept http requests on.")
  public Integer port = 8080;

  @Parameter(names = "--context-xml", description = "The path to the context xml to use.")
  public String contextXml;

  @Parameter(names = "--path", description = "The context path")
  public String contextPath = "";

  @Parameter(
      names = "--shutdown-override",
      description =
          "Overrides the default behavior and casues Tomcat to ignore lifecycle failure events"
              + " rather than shutting down when they occur.")
  public boolean shutdownOverride = false;

  @Parameter(
      names = "--session-store",
      description = "Session store to use (valid options are 'memcache' or 'redis')")
  public String sessionStore;

  @Parameter(
      names = "--session-store-pool-size",
      description =
          "Pool size of the session store connections (default is 10. Has no effect for"
              + " 'memcache')")
  public Integer sessionStorePoolSize = 10;

  @Parameter(
      names = "--session-store-operation-timeout",
      description = "Operation timeout for the memcache session store. (default is 5000ms)")
  public Integer sessionStoreOperationTimout = 5000;

  @Parameter(
      names = "--session-store-locking-mode",
      description =
          "Session locking mode for use with memcache session store. (default is all. Has no effect"
              + " for 'redis')")
  public String sessionStoreLockingMode = "all";

  @Parameter(
      names = "--session-store-ignore-pattern",
      description =
          "Request pattern to not track sessions for. Valid only with memcache session store."
              + " (default is '.*\\.(png|gif|jpg|css|js)$'. Has no effect for 'redis')")
  public String sessionStoreIgnorePattern = ".*\\.(png|gif|jpg|css|js)$";

  @Parameter(
      names = "--session-store-ssl-endpoint-identification",
      description =
          "Enables or disables SSL endpoint identification for the redis session store. (default is"
              + " true. Has no effect for 'memcache')",
      arity = 1)
  public boolean sessionStoreSslEndpointIdentification = true;

  @Parameter(names = "--help", help = true)
  public boolean help;

  @Parameter(names = "--enable-compression", description = "Enable GZIP compression on responses")
  public boolean enableCompression;

  @Parameter(
      names = "--compressable-mime-types",
      description =
          "Comma delimited list of mime types that will be compressed when using GZIP compression.")
  public String compressableMimeTypes =
      "text/html,text/xml,text/plain,text/css,application/json,application/xml,text/javascript,application/javascript";

  @Parameter(
      names = "--enable-ssl",
      description =
          "Specify -Djavax.net.ssl.keyStore, -Djavax.net.ssl.keystoreStorePassword,"
              + " -Djavax.net.ssl.trustStore and -Djavax.net.ssl.trustStorePassword in JAVA_OPTS."
              + " Note: should not be used if a reverse proxy is terminating SSL for you (such as"
              + " on Heroku)")
  public boolean enableSSL;

  @Parameter(
      names = "--enable-client-auth",
      description =
          "Specify -Djavax.net.ssl.keyStore and -Djavax.net.ssl.keyStorePassword in JAVA_OPTS")
  public boolean enableClientAuth;

  @Parameter(
      names = "--enable-basic-auth",
      description =
          "Secure the app with basic auth. Use with --basic-auth-user and --basic-auth-pw or"
              + " --tomcat-users-location")
  public boolean enableBasicAuth = false;

  @Parameter(
      names = "--basic-auth-user",
      description =
          "Username to be used with basic auth. Defaults to BASIC_AUTH_USER env variable.")
  public String basicAuthUser;

  @Parameter(
      names = "--basic-auth-pw",
      description = "Password to be used with basic auth. Defaults to BASIC_AUTH_PW env variable.")
  public String basicAuthPw;

  @Parameter(
      names = "--tomcat-users-location",
      description =
          "Location of the tomcat-users.xml file. (relative to the location of the webapp-runner"
              + " jar file)")
  public String tomcatUsersLocation;

  @Parameter(
      names = "--secure-error-report-valve",
      description =
          "Set true to set ErrorReportValve properties showReport and showServerInfo to false")
  public boolean secureErrorReportValve = false;

  // Not actually useful because it can only be set to true. We're keeping it here for backward
  // compatibility.
  @Parameter(names = "--expand-war", hidden = true)
  public boolean expandWar = true;

  @Parameter(
      names = "--expand-war-file",
      description = "Expand the war file and set it as source",
      arity = 1)
  public boolean expandWarFile = true;

  @Parameter(
      names = "--expanded-dir-name",
      description = "The name of the directory the WAR file will be expanded into.")
  public String expandedDirName = "expanded";

  @Parameter(
      names = "--uri-encoding",
      description = "Set the URI encoding to be used for the Connector.")
  public String uriEncoding;

  @Parameter(
      names = "--use-body-encoding-for-uri",
      description = "Set if the entity body encoding should be used for the URI.")
  public boolean useBodyEncodingForURI = false;

  @Parameter(
      names = "--scanBootstrapClassPath",
      description = "Set jar scanner scan bootstrap classpath.")
  public boolean scanBootstrapClassPath = false;

  @Parameter(
      names = "--temp-directory",
      description = "Define the temp directory, default value: ./target/tomcat.PORT")
  public String tempDirectory = null;

  @Parameter(
      names = "--bind-on-init",
      description =
          "Controls when the socket used by the connector is bound. By default it is bound when the"
              + " connector is initiated and unbound when the connector is destroyed., default"
              + " value: true",
      arity = 1)
  public boolean bindOnInit = true;

  @Parameter(
      names = "--proxy-base-url",
      description = "Set proxy URL if tomcat is running behind reverse proxy")
  public String proxyBaseUrl = "";

  @Parameter(names = "--max-threads", description = "Set the maximum number of worker threads")
  public Integer maxThreads = 0;

  @Parameter(
      names = "--memcached-transcoder-factory-class",
      description =
          "The class name of the factory that creates the transcoder to use for"
              + " serializing/deserializing sessions to/from memcached.")
  public String memcachedTranscoderFactoryClass = null;

  @DynamicParameter(
      names = "-A",
      description = "Allows setting HTTP connector attributes. For example: -Acompression=on")
  public Map<String, String> attributes = new HashMap<String, String>();

  @Parameter(names = "--enable-naming", description = "Enables JNDI naming")
  public boolean enableNaming = false;

  @Parameter(names = "--access-log", description = "Enables AccessLogValue to STDOUT")
  public boolean accessLog = false;

  @Parameter(
      names = "--access-log-pattern",
      description = "If --access-log is enabled, sets the logging pattern")
  public String accessLogPattern = "common";
}
