import java.io.*;
import org.codehaus.plexus.util.FileUtils;
import java.util.UUID;

String appName = "mvn-" + UUID.randomUUID().toString().substring(0,16);

("heroku create " + appName).execute().waitFor();

FileUtils.fileWrite(new File(basedir, "test.properties").getAbsolutePath(), "heroku.appName=" + appName );

def process = "heroku addons:create memcachier -a${appName}".execute();
process.waitFor();
println(process.text)
