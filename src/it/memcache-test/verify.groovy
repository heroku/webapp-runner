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

    process = "curl https://${appName}.herokuapp.com/hello".execute()
    process.waitFor()
    process = "heroku ps:restart -a${appName}".execute();
    process.waitFor();
    println(process.text)

    process = "heroku config -a${appName}".execute()
    process.waitFor()
    output = process.text
    assert output.contains("MEMCACHIER_SERVERS"), "The Memcache add-on was not added: ${output}"

    Thread.sleep(10000)

    process = "heroku logs -a${appName}".execute()
    process.waitFor()
    output = process.text
    assert output.contains("--expand-war"), "Did not pick up WEBAPP_RUNNER_OPTS: ${output}"
    assert output.contains("--session-store memcache"), "Did not pick up WEBAPP_RUNNER_OPTS: ${output}"
    assert output.contains("de.javakaffee.web.msm.MemcachedSessionService startInternal"), "Did not use Memcache for session cache: ${output}"
    assert output.contains("INFO net.spy.memcached.auth.AuthThread:  Authenticated to"), "Did not use Memcache for session cache: ${output}"

    process = "curl https://${appName}.herokuapp.com".execute()
    process.waitFor()
    output = process.text
    assert output.contains("hello, JSP"), "app is not running: ${output}"

    process = "curl https://${appName}.herokuapp.com/hello".execute()
    process.waitFor()
    output = process.text
    assert output.contains("hello, world"), "Could not load /hello page!"
    assert output.contains("class org.apache.catalina.session.StandardSessionFacade"), "Did not use Memcache for session cache: ${output}"
} finally {
    ("heroku destroy " + appName + " --confirm " + appName).execute().waitFor();
}
