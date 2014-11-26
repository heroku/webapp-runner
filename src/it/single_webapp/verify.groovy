def testProcess = { String command, String expectedText, String errorMessage ->
    def stdOut = new StringBuffer()
    def stdErr = new StringBuffer()
    def process = command.execute()
    process.consumeProcessOutput(stdOut, stdErr)
    def output = ""
    def foundText = false
    Thread.start{
        10.times {
            sleep 1000
            output = stdOut.toString() + stdErr.toString() // it ends up in StrErr for some reason
            foundText = output.contains(expectedText)
            if (foundText) process.destroy()
        }
    }
    process.waitForOrKill(10000)
    assert foundText, "${errorMessage}: ${output}"
}

testProcess(
        "java -cp target/classes -jar ${basedir}/target/dependency/webapp-runner.jar ${basedir}/src/main/webapp",
        "Starting ProtocolHandler",
        "The web process did not start"
)

testProcess(
        "java -jar ${basedir}/target/dependency/webapp-runner.jar ${basedir}/target/single_webapp",
        "Starting ProtocolHandler",
        "The web process did not start"
)

testProcess(
        "java -jar ${basedir}/target/dependency/webapp-runner.jar ${basedir}/target/single_webapp.war",
        "Starting ProtocolHandler",
        "The web process did not start"
)

testProcess(
        "java -jar ${basedir}/target/dependency/webapp-runner.jar --port 5000 ${basedir}/target/single_webapp.war",
        "Starting ProtocolHandler [\"http-nio-5000\"]",
        "The web process did not start"
)
