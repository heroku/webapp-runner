# Tomcat Runner

Tomcat runner is designed to allow you to launch an exploded or compressed war that is on your filesystem into a tomcat container with a simple `java -jar` command.

## Usage

### Clone and Build

    git clone git@github.com:jsimone/tomcat-runner.git
    mvn package

### Execute

    java -jar target/tomcat-runner.jar path/to/my/project

or

    java -jar target/tomcat-runner.jar myProject.war

### Help

    java -jar target/tomcat-runner.jar help

Prints out all arguments accepted
