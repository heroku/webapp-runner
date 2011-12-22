package launch;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ArgParserTest {
	
	@Test
	public void testArgumentParsingWithArg() throws ArgumentNotFoundException {
		String[] args = {Argument.SESSION_TIMEOUT.argName(), "45", Argument.PORT.argName(), "8888", "/path/to/project"};
		Map<Argument, String> argMap = ArgParser.parseArgs(args);
		
		Assert.assertEquals("Expected session timeout to match that which was set", "45", argMap.get(Argument.SESSION_TIMEOUT));
		Assert.assertEquals("Expected port to match that which was set", "8888", argMap.get(Argument.PORT));
		Assert.assertEquals("Expected directory to math that which was set", "/path/to/project", argMap.get(Argument.APPLICATION_DIR));
	}
	
	@Test
	public void testArgumentParsingNoArg() throws ArgumentNotFoundException {
		String[] args = {"/path/to/project"};
		Map<Argument, String> argMap = ArgParser.parseArgs(args);
		
		Assert.assertEquals("Expected directory to math that which was set", "/path/to/project", argMap.get(Argument.APPLICATION_DIR));
	}
	
}
