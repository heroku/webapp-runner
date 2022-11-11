import java.io.*;
import org.codehaus.plexus.util.FileUtils;

def props = new Properties()
new File(basedir, "test.properties").withInputStream {
    stream -> props.load(stream)
}
String appName = props["heroku.appName"]

try {
    def log = FileUtils.fileRead(new File(basedir, "build.log"));
    assert log.contains("BUILD SUCCESS"), "the build was not successful"

    process = "heroku config -a${appName}".execute()
    process.waitFor()
    output = process.text
    assert output.contains("MEMCACHIER_SERVERS"), "The Memcache add-on was not added: ${output}"

    process = "curl https://${appName}.herokuapp.com".execute()
    process.waitFor()
    output = process.text
    assert output.contains("hello, JSP"), "app is not running: ${output}"

    process = "curl https://${appName}.herokuapp.com/hello".execute()
    process.waitFor()
    output = process.text
    assert output.contains("hello, world"), "Could not load /hello page!"
    assert output.contains("de.javakaffee.web.msm.MemcachedBackupSession"), "Did not use Memcache for session cache: ${output}"
} finally {
    ("heroku destroy " + appName + " --confirm " + appName).execute().waitFor();
}
