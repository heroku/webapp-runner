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
                                  <groupId>com.github.jsimone</groupId>
                                  <artifactId>webapp-runner</artifactId>
                                  <version>7.0.22.3</version>
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

## Store your sessions in memcache

In versions 7.0.22.3 and newer support for a [session manager](http://code.google.com/p/memcached-session-manager/) that stores sessions in memcache is built in.

To use it add `--session_manager memcache` to your startup command:

    $ java -jar target/dependency/webapp-runner.jar --session_manager memcache target/<appname>.war

Then make sure that three environment variables are available for configuration: MEMCACHE_SERVERS, MEMCACHE_USERNAME, MEMCACHE_PASSWORD

## Running your application in Eclipse

Since your application will just be a standard webapp you can still use WTP and the traditional Tomcat integration points to run your application within Eclipse. However the containerless nature of webapp runner allows you to run from within Eclipse in a simpler way.

Start by importing your project into Eclipse. It is best to import it as an existing Maven project using the [m2eclipse plugin](http://eclipse.org/m2e/).

### Make your application dependant on webapp runner

Add the following dependency to your pom.xml:

    <dependency>
      <groupId>com.github.jsimone</groupId>
      <artifactId>webapp-runner</artifactId>
      <version>7.0.22.3</version>
      <scope>provided</scope>
    </dependency>

This will cause Eclipse to include webapp-runner on the classpath of your project so that it can be used for launching. It won't affect the final artifact built for your application.

### Create a launch configuration

1. Right-click on your project and choose 'Debug As -> Debug Configurations...'
2. From the 'Debug Configuration' window create a new 'Java Application' launch configuration by double-clicking on 'Java Application' in the left hand list or right-clicking on it and selecting 'New'
3. Give your launch configuration a sensible name. Then enter the name of your project in the 'Project' box
4. Enter 'webapp.runner.launch.Main' in the 'Main Class' box
5. Click on the 'Arguments' tab and enter './src/main/webapp' in the 'Program Arguments' box
6. Click 'Apply' and then 'Run'

Your application should start and you should see the log output in the Eclipse console. Since you have a debugger attached to your application you'll now see changes to your code get automatically loaded without restarting and can set breakpoints.

You can stop the application from the red square in the console pane or from the debug perspective. It can be restarted by right-clicking on the project and choosing your new launch configuration from the 'Debug As' menu or from the debug menu in the Eclipse toolbar (the icon with the little bug).

### Maven Central
Note: webapp runner is now available in Maven Central. The version scheme has also chanaged to match the version of Tomcat that it relies on. The format is <tomcat version>.<minor webapp runner version>. The latest version is now 7.0.22.x. Versions 0.0.1 to 0.0.7 are still available at http://jsimone.github.com/webapp-runner/repository.
     
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
