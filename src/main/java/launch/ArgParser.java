package launch;

import java.util.HashMap;
import java.util.Map;

public class ArgParser {
	
	public static Map<Argument, String> parseArgs(String[] args) throws ArgumentNotFoundException {
		Map<Argument, String> argMap = new HashMap<Argument, String>();
		
		for(int i=0; i < args.length-1; i+=2) {
			argMap.put(Argument.getArgFor(args[i]), args[i+1]);
		}
		
		argMap.put(Argument.APPLICATION_DIR, args[args.length-1]);
		
		return argMap;
	}
	
}
