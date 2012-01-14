# Webapp Runner

Webapp runner is designed to allow you to launch an exploded or compressed war that is on your filesystem into a tomcat container with a simple `java -jar` command.

## Usage

### Clone and Build

    git clone git@github.com:jsimone/webapp-runner.git
    mvn package

### Execute

    java -jar target/webapp-runner.jar path/to/my/project

or

    java -jar target/webapp-runner.jar myProject.war

### Help

    java -jar target/webapp-runner.jar help

Prints out all arguments accepted

## Using with Maven in your project

You can use the Maven dependency plugin to download webapp-runner as part of your build. This will eliminate the need for any external dependencies other than those specified in your build to run your application.

### pom.xml

Add the following to your pom.xml:

    ...
    <repositories>
      <repository>
        <id>webapp-runner-repo</id>
        <name>webapp runner repository on GitHub</name>
        <url>http://jsimone.github.com/webapp-runner/repository/</url>
      </repository>
    </repositories>
    ...
    <build>
    ...
      <plugins>
          <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-dependency-plugin</artifactId>
              <version>2.3</version>
              <executions>
                  <execution>
                      <phase>package</phase>
                      <goals><goal>copy</goal></goals>
                      <configuration>
                          <artifactItems>
                              <artifactItem>
                                  <groupId>webapp.runner</groupId>
                                  <artifactId>webapp-runner</artifactId>
                                  <version>0.0.5</version>
                                  <destFileName>webapp-runner.jar</destFileName>
                              </artifactItem>
                          </artifactItems>
                      </configuration>
                  </execution>
              </executions>
          </plugin>
      </plugins>
    ...
    </build>

### launching

Now when you run `maven package` webapp runner will be downloaded for you. You can then launch your application with:

     $ java -jar target/dependency/webapp-runner.jar target/<appname>.war
