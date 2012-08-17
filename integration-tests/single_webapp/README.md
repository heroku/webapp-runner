Manual Tests
------------

### Build the webapp

    mvn package

### Test that without any params the webapp starts on "/"

    java -jar target/dependency/webapp-runner.jar

### Test that it works with the compiled classes and the webapp src

    java -cp target/classes -jar target/dependency/webapp-runner.jar src/main/webapp

### Test that it works with the exploded war

    java -jar target/dependency/webapp-runner.jar target/single_webapp

### Test that it works with the war

    java -jar target/dependency/webapp-runner.jar target/single_webapp.war
