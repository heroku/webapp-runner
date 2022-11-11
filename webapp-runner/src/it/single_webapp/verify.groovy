def testProcess = { String command, String expectedText, String errorMessage ->
    def stdOut = new StringBuffer()
    def stdErr = new StringBuffer()
    def process = command.execute()
    process.consumeProcessOutput(stdOut, stdErr)
    def output = ""
    def foundText = false
    Thread.start{
        15.times {
            sleep 1000
            output = stdOut.toString() + stdErr.toString() // it ends up in StrErr for some reason
            foundText = output.contains(expectedText)
            if (foundText) process.destroy()
        }
    }
    process.waitForOrKill(15000)
    assert foundText, "${errorMessage}: ${output}"
}

testProcess(
        "java -cp target/classes -jar ${basedir}/target/dependency/webapp-runner.jar ${basedir}/src/main/webapp",
        "Starting Servlet engine",
        "The web process did not start (filesystem)"
)

testProcess(
        "java -jar ${basedir}/target/dependency/webapp-runner.jar ${basedir}/target/single_webapp",
        "Starting Servlet engine",
        "The web process did not start (target dir)"
)

testProcess(
        "java -jar ${basedir}/target/dependency/webapp-runner.jar ${basedir}/target/single_webapp.war",
        "Starting Servlet engine",
        "The web process did not start (war file)"
)

testProcess(
        "java -jar ${basedir}/target/dependency/webapp-runner.jar --port 5000 ${basedir}/target/single_webapp.war",
        "ProtocolHandler [\"http-nio-5000\"]",
        "The web process did not start (with port)"
)

testProcess(
        "java -jar ${basedir}/target/dependency/webapp-runner.jar --max-threads 42 ${basedir}/target/single_webapp.war",
        "Starting Servlet engine",
        "The web process did not set maxThreads"
)

testProcess(
        "java -jar ${basedir}/target/dependency/webapp-runner.jar -Aport=5000 -Acompression=on -AcompressionMinSize=1024 ${basedir}/target/single_webapp.war",
        "ProtocolHandler [\"http-nio-5000\"]",
        "Did not set custom Tomcat HTTP Connector Attributes"
)
