package webapp.runner.launch;

/**
 * ArgumentNotFoundException is thrown when an argument that isn't understood
 * is passed in.
 * 
 * @author johnsimone
 *
 */
public class ArgumentNotFoundException extends Exception {
	
	private String argName;
	
	public ArgumentNotFoundException(String argName) {
		this.argName = argName;
	}

	public String getArgName() {
		return argName;
	}
	
	

}
