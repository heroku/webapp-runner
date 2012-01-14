package webapp.runner.launch;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import webapp.runner.launch.ArgParser;
import webapp.runner.launch.Argument;
import webapp.runner.launch.ArgumentNotFoundException;
import webapp.runner.launch.MissingAppException;


public class ArgParserTest {
	
	@Test
	public void testArgumentParsingWithArg() throws ArgumentNotFoundException, MissingAppException {
		String[] args = {Argument.SESSION_TIMEOUT.argName(), "45", Argument.PORT.argName(), "8888", "/path/to/project"};
		Map<Argument, String> argMap = ArgParser.parseArgs(args);
		
		Assert.assertEquals(argMap.get(Argument.SESSION_TIMEOUT), "45", "Expected session timeout to match that which was set");
		Assert.assertEquals(argMap.get(Argument.PORT), "8888", "Expected port to match that which was set");
		Assert.assertEquals(argMap.get(Argument.APPLICATION_DIR), "/path/to/project", "Expected directory to math that which was set");
	}
	
	@Test
	public void testArgumentParsingNoArg() throws ArgumentNotFoundException, MissingAppException {
		String[] args = {"/path/to/project"};
		Map<Argument, String> argMap = ArgParser.parseArgs(args);
		
		Assert.assertEquals(argMap.get(Argument.APPLICATION_DIR), "/path/to/project", "Expected directory to math that which was set");
	}
	
}
