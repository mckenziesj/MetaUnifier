import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class RunDroid {
	
	@SuppressWarnings("unused")
	public static ArrayList<String> analyseDroid(String directoryPath, String droidPath){
		//This takes the output of the finalProcess when it exits
		ArrayList<String> droidResults = new ArrayList<String>();
		droidResults.add("Droid: Profile Mode,");
		
		//0 is directory, 1 is file
		String[] paths = ToolInterface.splitPath(droidPath);
		
		//The two-path execution model is obviously not optimal but until Droid is updated this is what we've got.
	
		//Used to create the file object that sets the processBuilder's directory.
		String workingDir = paths[0]; //"C:\\personal\\Software\\droid";
		//Execution Path
		String droidExecutePath = paths[1]; //"C:\\personal\\Software\\droid\\droid-cl.jar";

		//Other flags and paths
		//Adding files flag
		String addResourceFlag = "-a";
		//Profile flag
		String profileFlag = "-p";
		//Profile path (in theory only one is needed as it can be overwritten for a new file)
		String droidProfilePath = "\"" + workingDir + "\\analysisProfile.droid\""; //"\"C:\\personal\\Software\\droid\\analysisProfile.droid\"";
		
		//Droid needs quotes around the filename so we add them here 
		String tempFilePath = "\"" + directoryPath + "\"";
		
		//First we make this here to run Droid and create the profile
		//Create a new process builder and give it the arguments
		ProcessBuilder builder  = new ProcessBuilder("java", "-jar", droidExecutePath, addResourceFlag, 
				directoryPath, profileFlag, droidProfilePath);

		builder.redirectErrorStream();
		
		//Have to use a File object to store the working path!
		builder.directory(new File(workingDir));
		
		String profilePath = createDroidProfile(directoryPath, droidPath);
		
		//Now create a buffered reader, open the file, read the first line, explode it around "," and do stuff with it.
		//Try to make a BufferedReader
		BufferedReader reader = null;
		try {
			//reader = new BufferedReader(new FileReader("C:\\personal\\Software\\droid\\test-profile.csv"));
			reader = new BufferedReader(new FileReader(profilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Create the write-to-file thing
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("droid-output.csv"), "utf-8"));
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		
		//Start reading, y'all
		String firstLine = null;
		try {
			firstLine = reader.readLine();
		}
		catch (IOException e1){
			e1.printStackTrace();
		} 
		//Explode that into a structure thing.
		String regex = "\",\"|\"";
		String[] headings = firstLine.split(regex); 
		
		String secondLine = null;
		try{
			secondLine = reader.readLine();
		}
		catch (IOException e2){
			e2.printStackTrace();
		}
		String[] values = secondLine.split(regex);
		
		for(int i=1; i < values.length; i++){
			//System.out.println(headings[i] + " = " + values[i]);
			
			String[] dataPair = new String[2];
			dataPair[0] = headings[i];
			dataPair[1] = values[i];
			//Send the two strings to have commas stripped and double quotes doubled
			String lineToWrite = TextHandlers.outputCleaner(dataPair);

			droidResults.add(lineToWrite);
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
		
		//Try to close the reader or all is lost!
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("THE BUFFERED READER FAILED! NO!");
			e.printStackTrace();
		}
		
		return droidResults;
	//End executeDroid function
	}
	
	@SuppressWarnings("unused")
	public static String createDroidProfile(String targetPath, String droidPath){
		
		
		//0 is directory, 1 is file
		String[] paths = ToolInterface.splitPath(droidPath);
		
		//Used to create the file object that sets the processBuilder's directory.
		String workingDir = paths[0]; //"C:\\personal\\Software\\droid";
		//Execution Path
		String droidExecutePath = paths[1]; //"C:\\personal\\Software\\droid\\droid-cl.jar";
		//Other flags and paths
		//Adding files flag
		String addResourceFlag = "-a";
		//Profile flag
		String profileFlag = "-p";
		//Profile path (in theory only one is needed as it can be overwritten for a new file)
		String droidProfilePath = "\"" + workingDir + "\\analysisProfile.droid\""; 
				//"\"C:\\personal\\Software\\droid\\analysisProfile.droid\"";
			
		//Droid needs quotes around the filename so we add them here 
		String tempFilePath = "\"" + targetPath + "\"";
				
		//First we make this here to run Droid and create the profile
		//Create a new process builder and give it the arguments
		ProcessBuilder builder  = new ProcessBuilder("java", "-jar", droidExecutePath, addResourceFlag, 
				targetPath, "-R", profileFlag, droidProfilePath);

		builder.redirectErrorStream();
			
		//Have to use a File object to store the working path!
		builder.directory(new File(workingDir));
				
		//run Droid once: Create a profile
		try {			
			//Create the process and start it.
			System.out.println("\nRunning Droid in Profile Mode: Generating profile...");
			Process finalProcess = builder.start();
			//Wait to get exit value
	        try {
	        	//Try to exit...
	            int exitValue1 = finalProcess.waitFor();
	            System.out.println("\nExit Value (0 means done) is: " + exitValue1 + 
	            		"\n\tDroid Profile created!");
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
		
		String profilePath = workingDir + "\\test-profile.csv"; //"C:\\personal\\Software\\droid\\test-profile.csv";
		
		//Now we create another process to run Droid again to extract the information.
		//This will probably involve running Droid to make a CSV file
		//Then read the CSV file
		//Messy, but can be made NICE and EFFICIENT later
		ProcessBuilder builder2 = new ProcessBuilder("java", "-jar", droidExecutePath, "-p", droidProfilePath, 
				"-E", profilePath);
		builder2.redirectErrorStream();
		
		try {			
			//Create the process and start it.
			System.out.println("\nGenerating Droid output (createDroidProfile)...");
			Process finalProcess = builder2.start();
	        //Wait to get exit value
	        try {
	        	//Try to exit...
	            int exitValue2 = finalProcess.waitFor();
	            System.out.println("\nExit Value (0 means done) is: " + exitValue2 + 
	            		"\n\tDroid output file created!");
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
		System.out.println("\nFniished! (createDroidProfile)...");
		return profilePath;
	//End createDroidProfile
	}

}
