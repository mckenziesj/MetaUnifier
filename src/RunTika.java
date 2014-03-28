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


public class RunTika {

	@SuppressWarnings("unused")
	public static ArrayList<String> analyseTika(String tikaInput, String tikaPath){
		//This will take the output of the finalProcess when it exits
		ArrayList<String> tikaResults = new ArrayList<String>();
		tikaResults.add("Tika Output,");
		
		//java -jar tika.jar -t filename
		
		//0 is directory, 1 is file
		String[] paths = ToolInterface.splitPath(tikaPath);
		
		//Tika run info:
		//Working Directory
		String tikaWorkingDir = paths[0]; //"C:\\personal\\Software\\Tika";
		//Execution Path
		String tikaExecutePath = paths[1]; //"C:\\personal\\Software\\Tika\\tika-app-1.3.jar";
		//Flag for plain text output
		String plainTextFlag = "-t";
		//Flag for just the metadata
		String metaOnlyFlag = "-m";
		
		//Create a new process builder and give it the arguments in the right order
		ProcessBuilder tikaBuilder  = new ProcessBuilder("java", "-jar", tikaExecutePath, 
		plainTextFlag, metaOnlyFlag, tikaInput);
		tikaBuilder.redirectErrorStream();
		
		//Set the working directory
		String workingDir = tikaWorkingDir;
		//Have to use a File object to store the working path!
		tikaBuilder.directory(new File(workingDir));
		
		try{
			System.out.println("\nTrying to execute Tika (analysis mode)...");
			Process finalProcess = tikaBuilder.start();
			
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
						new FileOutputStream("tika-output.csv"), "utf-8"));
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
			
			//Keep getting lines out of the process
			while ((line = br.readLine()) != null ){
				//Explode the latest line into a structure thing.
				String regex = ": ";
				String[] dataPairs = line.split(regex);
				
				//Send the two strings to have commas stripped and double quotes doubled
				String lineToWrite = TextHandlers.outputCleaner(dataPairs);

				tikaResults.add(lineToWrite);
				//We want to put line into a new file so we can make a nice output while testing
				try {
					writer.write(lineToWrite, 0, lineToWrite.length());
					writer.newLine();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}	
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
		    	System.out.println("\tTika execution complete!");
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
		
		return tikaResults;
	//end analyseTika
	}
	
	@SuppressWarnings("unused")
	public static ArrayList<String> extractTika(HashMap<String,String> tikaSchema, String tikaInput, String tikaPath){
		/**
		 * extractTika - run Tika and extract required metadata from it
		 * 
		 * Args: 	filePath	Path to the file to extract from
		 * 			schema		hashmap 
		 */
		
		ArrayList<String> tikaResults = new ArrayList<String>();
		
		//java -jar tika.jar -t filename
		
		//0 is directory, 1 is file
		String[] paths = ToolInterface.splitPath(tikaPath);
		
		//Tika run info:
		//Working Directory
		String tikaWorkingDir = paths[0]; //"C:\\personal\\Software\\Tika";
		//Execution Path
		String tikaExecutePath = paths[1]; //"C:\\personal\\Software\\Tika\\tika-app-1.3.jar";
		//Flag for plain text output
		String plainTextFlag = "-t";
		//Flag for just the metadata
		String metaOnlyFlag = "-m";
		
		//Create a new process builder and give it the arguments in the right order
		ProcessBuilder tikaBuilder  = new ProcessBuilder("java", "-jar", tikaExecutePath, 
		plainTextFlag, metaOnlyFlag, tikaInput);
		tikaBuilder.redirectErrorStream();
		
		//Set the working directory
		String workingDir = tikaWorkingDir;
		//Have to use a File object to store the working path!
		tikaBuilder.directory(new File(workingDir));
		
		try{
			System.out.println("\nTrying to execute Tika (extraction mode)...");
			Process finalProcess = tikaBuilder.start();
			
			//Get the input stream
			InputStream inStream = finalProcess.getInputStream();
			//Make an input stream reader object
			InputStreamReader reader = new InputStreamReader(inStream);
			//Make a buffered reader
			BufferedReader br = new BufferedReader(reader);
			
			//A temp string to use later
			String line = "";
			
			int valuesToGrab = tikaSchema.size();
			int valuesGrabbed = 0;
				
			//Keep getting lines out of the process
			while ((valuesGrabbed < valuesToGrab) && (line = br.readLine()) != null){
				//Explode the latest line into a structure thing.
				String regex = ": ";
				String[] dataPairs = line.split(regex);
				
				//If dataPairs[0] is in the hashmap make linetowrite and put it in the output
				if (tikaSchema.containsKey(dataPairs[0])){
					//Send the two strings to have commas stripped and double quotes doubled
					dataPairs[0] = tikaSchema.get(dataPairs[0]);
					//String lineToWrite = TextHandlers.outputCleaner(dataPairs);
					//tikaResults.add(lineToWrite);
					//System.out.println(lineToWrite);
					tikaResults.add(TextHandlers.outputCleaner(dataPairs));
					valuesGrabbed++;
				}
			}
			//Wait to get exit value
		    try {
		    	//Try to exit...
		    	int exitValue = finalProcess.waitFor();
		    	System.out.println("\tTika execution complete!");
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

		return tikaResults;
	//End extractTika
	}
	
}
