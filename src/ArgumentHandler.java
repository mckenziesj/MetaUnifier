import java.io.File;
import java.util.ArrayList;


public class ArgumentHandler {

	/**
	 * Parses the args[] from main
	 * 
	 * When returned, main uses the output array like this:
	 * 
	 * String runMode = settings[0];
	 * String toolConfigFile = settings[1];
	 * String workingPath = settings[2];
	 * String schemaPath = settings[3];
	 */
	@SuppressWarnings({ "unchecked", "null" })
	public static String[] parseArgs(ArrayList<String> args){
		String[] results = null;
		
		//These are for the removal of the " characters when we send them to make files
		String oldChar = "\"";
		String newChar = "";
		
		if (args.size() < 1){
			System.out.println("You must supply arguments!\n\n");
			System.exit(0);
		}
		else if ((args.get(0).equals("h")) || (args.get(0).equals("-h")) || (args.get(0).equals("help")) || (args.get(0).equals("?")) || (args.get(0).equals("-?"))){
			System.out.println("ToolUnifier\n\n" +
					"Usage:\n\nMetaUnifer.jar -m Mode -s \"path to metadata schema\" -f \"file or directory path\" -t \"path to tools config file\"" +
					"\n" +
					"\nMode can be: a (analyse a single file), e [extract according to schema], t [speed test of extraction mode]");
			System.exit(0);
		}
		else {
			int i = 0;
			while (i < args.size()){
				if (args.get(i).equals("-m")){
					i++;
					if (i < args.size()){
						//Object[] data = findArg((ArrayList<String>) args.subList(i, args.size()));
						String currentArg = args.get(i);
						if (currentArg.equals("e") || currentArg.equals("a") || currentArg.equals("t")){
							results[0] = currentArg;
						}
						else {
							//throw new NullPointerException("Missing run mode argument! Must be a or e");
							System.out.println("Missing run mode argument! Must be a, e, or t");
							System.exit(0);
						}
					}
					else {
						System.out.println("Missing run mode argument! Must be a, e, or t");
						System.exit(0);
					}
				}
				else if (args.get(i).equals("-t")){
					i++;
					if (i < args.size()){
						Object[] data = findArg((ArrayList<String>) args.subList(i, args.size()));
						args = (ArrayList<String>) data[1];
						String currentArg = (String) data[0];
						currentArg.replace(oldChar, newChar);
						if (new File(currentArg).isFile()){
							results[1] = currentArg;
						}
						else {
							System.out.println("Tool configuration argument is not a file!");
							System.exit(0);
						}
					}
					else {
						System.out.println("Not enough arguments (Tool configuration argument)");
						System.exit(0);
					}
				}
				else if (args.get(i).equals("-f")){
					i++;
					if (i < args.size()){
						Object[] data = findArg((ArrayList<String>) args.subList(i, args.size()));
						args = (ArrayList<String>) data[1];
						String currentArg = (String) data[0];
						currentArg.replace(oldChar, newChar);
						if ((new File(currentArg).isDirectory() || new File(currentArg).isFile() )){
							results[2] = currentArg;
						}
						else {
							System.out.println("Working path argument is not a directory or file!");
						}
					}
					else {
						System.out.println("Not enough arguments (Working path argument)");
						System.exit(0);
					}
				}
				else if(args.get(i).equals("-s")){
					i++;
					Object[] data = findArg((ArrayList<String>) args.subList(i, args.size()));
					args = (ArrayList<String>) data[1];
					String currentArg = (String) data[0];
					currentArg.replace(oldChar, newChar);
					if (i < args.size()){
						if (new File(currentArg).isFile()){
							results[2] = currentArg;
						}
						else {
							System.out.println("Schema path argument is not a file!");
							System.exit(0);
						}
					}
					else {
						System.out.println("Not enough arguments (Schema path argument)");
						System.exit(0);
					}
				}
				else{
					System.out.println("Flagrant Error: Argument Problem!");
					System.out.println("args[" + i + "] = " + args.get(i));
					System.exit(0);
				}
				i++;
			//end While loop
			}
		}
		
		return results;
	//end parseArgs
	}
	
	public static Object[] findArg(ArrayList<String> input){
		
		if (input.get(0).charAt(0) != '"'){
			//It's not valid, quit
			System.out.println("Missing \" for start of argument: Ending! ");
			System.exit(0);
		}
		
		Object[] output = new Object[2];
		String argument = "";
		
		for (int i = 0; i<input.size(); i++){
			argument += input.get(i);
			
			if (argument.charAt(0) == '"' && argument.charAt(0) == '"'){
				//Argument is complete: Return it!
				output[0] = argument;
				output[1] = input.subList(i, input.size());
				return output;
			}
		}
		
		//We got to the end of the argument list without finding a closing argument
		//This means we have to abort the program
		System.out.println("No closing \" found for argument: Ending!");
		System.exit(0);
		
		//This return statement is so we can compile the code 
		return output;
	}

}
