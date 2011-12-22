# Tomcat Runner

Tomcat runner is designed to allow you to launch an exploded war that is on your filesystem into a tomcat container with a simple `java -jar` command.

## Usage

### Clone and Build

    git clone git@github.com:jsimone/tomcat-runner.git
    mvn package

### Execute

    java -jar target/tomcat-runner-jar-with-dependencies.jar path/to/my/project
