Manual Tests
------------

### Build the webapp

    mvn package

### Test that without any params the webapp does not start because `src/main/webapp` does not exist

    java -jar target/dependency/webapp-runner.jar

### Test that all 3 wars start and that webapp1 is on the "/" context

    java -jar target/dependency/webapp-runner.jar */target/*.war
