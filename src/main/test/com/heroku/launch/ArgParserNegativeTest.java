package com.heroku.launch;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.heroku.launch.ArgParser;
import com.heroku.launch.Argument;
import com.heroku.launch.ArgumentNotFoundException;

@RunWith(value = Parameterized.class)
public class ArgParserNegativeTest {

	String[] args;
	
	public ArgParserNegativeTest(String[] args) {
		this.args = args;
	}
	
	@Parameters
	 public static Collection<String[][]> data() {
	   String[][][] data = new String[][][] { 
			   { {"--fake-arg", "45", "/path/to/project"} }, 
			   { {Argument.SESSION_TIMEOUT.argName(), "45", "--fake-arg", "test", "/path/to/project"} },
			   { {Argument.SESSION_TIMEOUT.argName(), "45", "--fake-arg", "/path/to/project"} },
			   { {Argument.SESSION_TIMEOUT.argName(), "45", "test", "/path/to/project"} },
			   { {Argument.SESSION_TIMEOUT.argName(), "--fake-arg", "test", "/path/to/project"} },
			   { {"/path/to/project", Argument.SESSION_TIMEOUT.argName()} }
	   };
	   return Arrays.asList(data);
	 }
	
	@Test
	public void testFakeArgument() {
		try {
			ArgParser.parseArgs(args);
			Assert.fail("Parsing of arguments should have thrown exception");
		} catch (ArgumentNotFoundException e) {

		}		
	}
	
}
