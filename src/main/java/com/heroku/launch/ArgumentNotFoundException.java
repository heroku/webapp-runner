package com.heroku.launch;

public class ArgumentNotFoundException extends Exception {
	
	private String argName;
	
	public ArgumentNotFoundException(String argName) {
		this.argName = argName;
	}

	public String getArgName() {
		return argName;
	}
	
	

}
