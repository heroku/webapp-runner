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
     
### License

 Copyright (c) 2012, John Simone
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, are permitted provided
 that the following conditions are met:

    Redistributions of source code must retain the above copyright notice, this list of conditions and the
    following disclaimer.

    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
    the following disclaimer in the documentation and/or other materials provided with the distribution.

    Neither the name of John Simone nor the names of its contributors may be used to endorse or
    promote products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
