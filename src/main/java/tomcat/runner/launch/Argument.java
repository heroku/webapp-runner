package tomcat.runner.launch;

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
