import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class RunJhove {
	
	@SuppressWarnings("unused")
	public static ArrayList<String> analyseJhove(String jhoveInput, String jhoveDir){
		//This will take the output of the finalProcess when it exits
		ArrayList<String> jhoveResults = new ArrayList<String>();
		jhoveResults.add("Jhove Output,");
		
		//Jhove run info:
		//Working Directory
		String jhoveWorkingDir = jhoveDir;
		//Execution Path
		String jhoveExecutePath = jhoveDir + "\\bin\\JhoveApp.jar";
		//Flag for the config file
		String jhoveConfigFlag = "-c";
		//The path for the config file
		String jhoveConfigPath = "C:\\personal\\Software\\jhove\\jhove\\conf\\jhove.conf";
		
		//Create a new process builder and give it the arguments in the right order
		ProcessBuilder jhoveBuilder  = new ProcessBuilder(/*"cmd",*/ "java", "-jar", jhoveExecutePath,
				jhoveConfigFlag, jhoveConfigPath, jhoveInput);
		jhoveBuilder.redirectErrorStream();
		
		//Set the working directory
		String workingDir = jhoveWorkingDir;
		//Have to use a File object to store the working path!
		jhoveBuilder.directory(new File(workingDir));
					
		try{
			System.out.println("\nTrying to execute Jhove (analysis mode)...");
			Process finalProcess = jhoveBuilder.start();
			
			//Get the input stream
			InputStream inStream = finalProcess.getInputStream();
			//Make an input stream reader object
			InputStreamReader reader = new InputStreamReader(inStream);
			//Make a buffered reader
			BufferedReader br = new BufferedReader(reader);
			
			//A temp string to use later
			String line;
			
			//Create the write-to-file thing
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("jhove-output.csv"), "utf-8"));
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
			
			//Keep getting lines out of the process
			while ((line = br.readLine()) != null ){
				//Explode the latest line into a structure thing.
				String regex = ": ";
				String[] dataPairs = line.split(regex);
					
				//This block handles the XML block in Jhove output
				CharSequence seq = "XMP";
				if (dataPairs[0].contains(seq)){
					int readAheadLimit = 8;
					br.mark(readAheadLimit);
					line = br.readLine();
					CharSequence s = "Pages";
					while (line.contains(s) != true) {
						String replacement = dataPairs[0] + line;
						dataPairs[1] = dataPairs[1] + replacement;
						br.mark(readAheadLimit);
						line = br.readLine();
					}
				//End if
				}
					
				//Send the two strings to have commas stripped and double quotes doubled
				String lineToWrite = TextHandlers.outputCleaner(dataPairs);

				jhoveResults.add(lineToWrite);
				//We want to put line into a new file so we can make a nice output while testing
				try {
					writer.write(lineToWrite, 0, lineToWrite.length());
					writer.newLine();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			//End of: while ((line = br.readLine()) != null )
			}
			//Done writing output lines so close the writer
			try{
				writer.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}

		    //Wait to get exit value
		    try {
		    	//Try to exit...
		    	int exitValue = finalProcess.waitFor();
		    	System.out.println("\tJhove execution complete!");
		    }
		    catch (InterruptedException e) {
		    	//Something's gone wrong, what could it be?
		    	e.printStackTrace();
		    }
		}
		catch (Exception ex){
			//Something went horribly wrong, print out the stack trace
			ex.printStackTrace();
		}
		return jhoveResults;
	//end analyseJhove
	}
	
	@SuppressWarnings("unused")
	public static ArrayList<String> extractJhove(HashMap<String, String> jhoveSchema, String jhoveInput, String jhoveDir){
		ArrayList<String> jhoveResults = new ArrayList<String>();
		
		//Jhove run info:
		//Working Directory
		String jhoveWorkingDir = jhoveDir; //"C:\\personal\\Software\\jhove\\jhove";
		//Execution Path
		String jhoveExecutePath = jhoveDir + "\\bin\\JhoveApp.jar"; //"C:\\personal\\Software\\jhove\\jhove\\bin\\JhoveApp.jar";
		//Flag for the config file
		String jhoveConfigFlag = "-c";
		//The path for the config file
		String jhoveConfigPath = jhoveDir + "\\conf\\jhove.conf"; //"C:\\personal\\Software\\jhove\\jhove\\conf\\jhove.conf";
		
		//Create a new process builder and give it the arguments in the right order
		ProcessBuilder jhoveBuilder  = new ProcessBuilder(/*"cmd",*/ "java", "-jar", jhoveExecutePath,
				jhoveConfigFlag, jhoveConfigPath, jhoveInput);
		jhoveBuilder.redirectErrorStream();
		
		//Set the working directory
		String workingDir = jhoveWorkingDir;
		//Have to use a File object to store the working path!
		jhoveBuilder.directory(new File(workingDir));
		
		try {
			System.out.println("\nTrying to execute Jhove (extraction mode)...");
			Process finalProcess = jhoveBuilder.start();
			
			//Get the input stream
			InputStream inStream = finalProcess.getInputStream();
			//Make an input stream reader object
			InputStreamReader reader = new InputStreamReader(inStream);
			//Make a buffered reader
			BufferedReader br = new BufferedReader(reader);
			
			//A temp string to use later
			String line = "";
			
			int valuesToGrab = jhoveSchema.size();
			int valuesGrabbed = 0;
			
			//Keep getting lines out of the process
			while ((valuesGrabbed < valuesToGrab) && (line = br.readLine()) != null ){
				//Explode the latest line into a structure thing.
				String regex = ": ";
				String[] dataPairs = line.split(regex);
					
				//This block handles the XML block in Jhove output
				CharSequence seq = "XMP";
				if (dataPairs[0].contains(seq)){
					int readAheadLimit = 8;
					br.mark(readAheadLimit);
					line = br.readLine();
					CharSequence s = "Pages";
					while (line.contains(s) != true) {
						String replacement = dataPairs[0] + line;
						dataPairs[1] = dataPairs[1] + replacement;
						br.mark(readAheadLimit);
						line = br.readLine();
					}
				//End if
				}
				if (jhoveSchema.containsKey(dataPairs[0].trim())){
					//Set our field name to the one from the schema
					dataPairs[0] = jhoveSchema.get(dataPairs[0].trim());
					//Send the two strings to have their commas stripped and double quotes doubled					
					jhoveResults.add(TextHandlers.outputCleaner(dataPairs));
					valuesGrabbed++;
				}
			//End of: while ((line = br.readLine()) != null )
			}
			//Wait to get the exit value
			try {
		    	//Try to exit...
		    	int exitValue = finalProcess.waitFor();
		    	System.out.println("\tJhove execution complete!");
		    }
		    catch (InterruptedException e) {
		    	//Something's gone wrong, what could it be?
		    	e.printStackTrace();
		    }
			
			
		}
		catch (Exception ex){
			//Something went horribly wrong, print out the stack trace
			ex.printStackTrace();
		}

		return jhoveResults;
	//end extractJhove
	}

}
