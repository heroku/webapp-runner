package webapp.runner.launch;

import java.util.HashMap;
import java.util.Map;

/**
 * Util class for parsing arguments from the command line.
 * 
 * @author johnsimone
 *
 */
public class ArgParser {
	
	/**
	 * Parse the argument list. The Argument enum takes care of deciding which arguments
	 * are understood
	 * 
	 * @param args
	 * @return
	 * @throws ArgumentNotFoundException
	 */
	public static Map<Argument, String> parseArgs(String[] args) throws ArgumentNotFoundException, MissingAppException {
		Map<Argument, String> argMap = new HashMap<Argument, String>();
		
		for(int i=0; i < args.length-1; i+=2) {
			argMap.put(Argument.getArgFor(args[i]), args[i+1]);
		}

		//if there are even number of arguments then we didn't define the app location
		if(args.length % 2 == 0 || args[args.length-1].startsWith("--")) {
			throw new MissingAppException();
		}
		
		argMap.put(Argument.APPLICATION_DIR, args[args.length-1]);
		
		return argMap;
	}
	
}
