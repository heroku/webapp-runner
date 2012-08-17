Manual Tests
------------

### Build the webapp

    mvn package

### Test that without any params the webapp starts on "/"

    java -jar target/dependency/webapp-runner-7.0.29.1-SNAPSHOT.jar

### Test that it works with the compiled classes and the webapp src

    java -cp target/classes -jartarget/dependency/webapp-runner-7.0.29.1-SNAPSHOT.jar src/main/webapp

### Test that it works with the exploded war

    java -jar target/dependency/webapp-runner-7.0.29.1-SNAPSHOT.jar target/hellojavawebapprunner

### Test that it works with the war

    java -jar target/dependency/webapp-runner-7.0.29.1-SNAPSHOT.jar target/hellojavawebapprunner.war
