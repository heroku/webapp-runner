Manual Tests
------------

### Build the webapp

    mvn package

### Test that without any params the webapp does not start because `src/main/webapp` does not exist

    java -jar target/dependency/webapp-runner-7.0.29.1-SNAPSHOT.jar

### Test that all 3 wars start and that webapp1 is on the "/" context

    java -jar target/dependency/webapp-runner-7.0.29.1-SNAPSHOT.jar */target/*.war
