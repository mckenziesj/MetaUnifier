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


public class RunExifTool {

	public static ArrayList<String> analyseExifTool(String exifToolInput, String exifPath){
		//This will take the output of the finalProcess when it exits
		ArrayList<String> exifResults = new ArrayList<String>();
		exifResults.add("ExifTool Output,");
		
		//0 is directory, 1 is file
		String[] paths = ToolInterface.splitPath(exifPath);
		
		//Used to create the file object that sets the processBuilder's directory.
		String exifToolWorkingDir = paths[0]; //"C:\\personal\\exiftoolgui\\exiftoolgui";
		//Execution Path
		String exifToolExecutePath = paths[1]; //"C:\\personal\\exiftoolgui\\exiftoolgui\\exiftool.exe";
			
		//Trial Run: Spit out that really long config info and put it in a text file
		ProcessBuilder exifToolBuilder = new ProcessBuilder(exifToolExecutePath, exifToolInput);
			
		//Set the working directory
		String workingDir = exifToolWorkingDir; 
		//Have to use a File object to store the working path!
		exifToolBuilder.directory(new File(workingDir));
			
		try{
			System.out.println("\nTrying to execute exifTool (analysis mode)...");
			Process finalProcess = exifToolBuilder.start();
				
			//Get the input stream
			InputStream inStream = finalProcess.getInputStream();
			//Make an input stream reader object
			InputStreamReader reader = new InputStreamReader(inStream);
			//Make a buffered reader
			BufferedReader br = new BufferedReader(reader);

			//A temp string
			String line;

			//Create the write-to-file thing
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("exifTool-output.csv"), "utf-8"));
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
				
			//Keep getting lines out of the process
			while ((line = br.readLine()) != null ){
				//Print the latest line
				//System.out.println(line);
				
				//Explode the latest line into a structure thing.
				String regex = " {2,}: {1}";
				String[] headings = line.split(regex);
					
				//Send the two strings to have commas stripped
				String lineToWrite = TextHandlers.outputCleaner(headings); 
						
				exifResults.add(lineToWrite);
				//We want to put line into the new file so we can make a nice output with all the flags.
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
		    	//System.out.println("\nExit Value (0 means done) is: " + exitValue);
		    	System.out.println("\nExit Value (0 means done) is: " + exitValue + 
		    			"\nExiftool execution complete!");
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
		return exifResults;
	//end exexcuteExifTool
	}
	
	public static ArrayList<String> extractExifTool(HashMap<String,String> exifSchema, String exifToolInput, String exifPath){
		ArrayList<String> exifResults = new ArrayList<String>();
		
		//0 is directory, 1 is file
		String[] paths = ToolInterface.splitPath(exifPath);
		
		//Build the process
		//Used to create the file object that sets the processBuilder's directory.
		String exifToolWorkingDir = paths[0]; //"C:\\personal\\exiftoolgui\\exiftoolgui";
		//Execution Path
		String exifToolExecutePath = paths[1]; //"C:\\personal\\exiftoolgui\\exiftoolgui\\exiftool.exe";
			
		//Trial Run: Spit out that really long config info and put it in a text file
		ProcessBuilder exifToolBuilder = new ProcessBuilder(exifToolExecutePath, exifToolInput);
			
		//Set the working directory
		String workingDir = exifToolWorkingDir; 
		//Have to use a File object to store the working path!
		exifToolBuilder.directory(new File(workingDir));
		
		//Run the process
		try {
			System.out.println("\nTrying to execute exifTool (extraction mode)...");
			Process finalProcess = exifToolBuilder.start();
				
			//Get the input stream
			InputStream inStream = finalProcess.getInputStream();
			//Make an input stream reader object
			InputStreamReader reader = new InputStreamReader(inStream);
			//Make a buffered reader
			BufferedReader br = new BufferedReader(reader);
			
			//A temp string
			String line;
			
			int valuesToGrab = exifSchema.size();
			int valuesGrabbed = 0;
			
			//Keep getting lines out of the process
			while ((valuesGrabbed < valuesToGrab) && (line = br.readLine()) != null){
				//Print the latest line
				//System.out.println(line);
				
				//Explode the latest line into a structure thing.
				String regex = " {2,}: {1}";
				String[] dataPairs = line.split(regex);
				
				//If the name of the output line is in the schema, package it and put it in the output
				if (exifSchema.containsKey(dataPairs[0])){
					//Send the two strings to have commas stripped and double quotes doubled
					dataPairs[0] = exifSchema.get(dataPairs[0]);
					exifResults.add(TextHandlers.outputCleaner(dataPairs));
					//String lineToWrite = TextHandlers.outputCleaner(dataPairs);
					//exifResults.add(lineToWrite);
					valuesGrabbed++;
				}
			}
			//Wait to get exit value to close the process
		    try {
		    	//Try to exit...
		    	int exitValue = finalProcess.waitFor();
		    	//System.out.println("\nExit Value (0 means done) is: " + exitValue);
		    	System.out.println("\nExit Value (0 means done) is: " + exitValue + 
		    			"\nExiftool execution complete!");
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
		
		return exifResults;
	}
	
}
