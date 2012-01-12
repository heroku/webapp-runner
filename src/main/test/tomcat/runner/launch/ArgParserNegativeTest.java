package tomcat.runner.launch;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import tomcat.runner.launch.ArgParser;
import tomcat.runner.launch.Argument;
import tomcat.runner.launch.ArgumentNotFoundException;
import tomcat.runner.launch.MissingAppException;

public class ArgParserNegativeTest {

	@DataProvider(name = "Negative-Test-Provider")
	public Object[][] parameterIntTestProvider() {
		return new Object[][]{
			   { new String[] {"--fake-arg", "45", "/path/to/project"}, ArgumentNotFoundException.class }, 
			   { new String[] {Argument.SESSION_TIMEOUT.argName(), "45", "--fake-arg", "test", "/path/to/project"}, ArgumentNotFoundException.class },
			   { new String[] {Argument.SESSION_TIMEOUT.argName(), "45", "--fake-arg", "/path/to/project"}, ArgumentNotFoundException.class },
			   { new String[] {Argument.SESSION_TIMEOUT.argName(), "45", "test", "/path/to/project"}, ArgumentNotFoundException.class },
			   { new String[] {Argument.SESSION_TIMEOUT.argName(), "--fake-arg", "test", "/path/to/project"}, ArgumentNotFoundException.class },
			   { new String[] {"/path/to/project", Argument.SESSION_TIMEOUT.argName()}, ArgumentNotFoundException.class },
			   { new String[] {Argument.SESSION_TIMEOUT.argName(), "--fake-arg", "45", "test", "/path/to/project"}, ArgumentNotFoundException.class },
			   { new String[] {Argument.SESSION_TIMEOUT.argName(), "45", Argument.PORT.argName(), "9090"}, MissingAppException.class },
			   { new String[] {Argument.SESSION_TIMEOUT.argName(), "45", Argument.PORT.argName()}, MissingAppException.class }
	       };
	}
	
	@Test(dataProvider = "Negative-Test-Provider")
	public void testFakeArgument(String[] args, Class<Exception> excpectedException) {
		try {
			ArgParser.parseArgs(args);
			Assert.fail("Parsing of arguments should have thrown exception");
		} catch (Exception e) {
			Assert.assertTrue(e.getClass().isAssignableFrom(excpectedException), "Wrong exception thrown for negative test");
		}		
	}
	
}
