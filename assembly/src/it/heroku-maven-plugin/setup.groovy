import java.io.*;
import org.codehaus.plexus.util.FileUtils;
import java.util.UUID;

String appName = "mvn-" + UUID.randomUUID().toString().substring(0,16);

("heroku create " + appName).execute().waitFor();

FileUtils.fileWrite(new File(basedir, "test.properties").getAbsolutePath(), "heroku.appName=" + appName );

def process = "heroku addons:add heroku-redis -a${appName}".execute();
process.waitFor()
println(process.text)

process = "heroku addons:wait heroku-redis -a${appName}".execute();
process.waitFor()
println(process.text)