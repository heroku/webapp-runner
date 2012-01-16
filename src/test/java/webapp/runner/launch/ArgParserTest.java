/**
 * Copyright (c) 2012, John Simone
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of John Simone nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
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
