import java.io.*;
import org.codehaus.plexus.util.FileUtils;

def props = new Properties()
new File(basedir, "test.properties").withInputStream {
    stream -> props.load(stream)
}
String appName = props["heroku.appName"]

try {
    def log = FileUtils.fileRead(new File(basedir, "build.log"));
    if (!log.contains("BUILD SUCCESS")) {
        throw new RuntimeException("the build was not successful")
    }

    process = "heroku config -a${appName}".execute()
    process.waitFor()
    output = process.text
    assert output.contains("REDISTOGO_URL"), "The Redis add-on was not added: ${output}"

    Thread.sleep(15000)

    process = "heroku logs -a${appName}".execute()
    process.waitFor()
    output = process.text
    assert output.contains("--expand-war"), "Did not pick up WEBAPP_RUNNER_OPTS: ${output}"
    assert output.contains("--session-store redis"), "Did not pick up WEBAPP_RUNNER_OPTS: ${output}"
    assert output.contains("WARNING: JedisPool not found in JNDI"), "Did not use Redis for session cache: ${output}"

    process = "curl https://${appName}.herokuapp.com".execute()
    process.waitFor()
    output = process.text
    if (!output.contains("hello, JSP")) {
        throw new RuntimeException("app is not running: ${output}")
    }

    process = "curl https://${appName}.herokuapp.com/hello".execute()
    process.waitFor()
    output = process.text
    if (!output.contains("hello, world")) {
        throw new RuntimeException("servlet is not serving: ${output}")
    }
} finally {
    ("heroku destroy " + appName + " --confirm " + appName).execute().waitFor();
}