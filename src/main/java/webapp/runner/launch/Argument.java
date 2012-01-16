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

/**
 * The argument enum holds which arguments are supported.
 * 
 * @author johnsimone
 *
 */
public enum Argument {
	SESSION_TIMEOUT ("--session-timeout", "The number of minutes of inactivity before a user's session is timed out"),
	PORT ("--port", "The port that the server will accept http requests on"),
	CONTEXT_XML ("--context_xml", "The parth to the context xml to use"),
	PATH ("--path", "context path (default is /)"),
	APPLICATION_DIR ("", "");
	
	private String argName;
	private String helpText;
	
	Argument(String argName, String helpText) {
		this.argName = argName;
		this.helpText = helpText;
	}
	
	public String argName() { return argName; }
	public String helpText() { return helpText; }
	
	/**
	 * Returns an argument based on the argument name passed in
	 * 
	 * @param argName
	 * @return
	 * @throws ArgumentNotFoundException if the requested argument isn't in the enum
	 */
	public static Argument getArgFor(String argName) throws ArgumentNotFoundException{
		for (Argument argument : Argument.values()) {
			if(argName.equalsIgnoreCase(argument.argName)) {
				return argument;
			}
		}
		
		throw new ArgumentNotFoundException(argName);
	}
}
