import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;


public class RunNLMET {
	
	public static ArrayList<String> analyseNLMET(String nlmetInput, String nlmetPath){
		ArrayList<String> nlmetResults = new ArrayList<String>();
		
		//String nlmetInput = filePath + fileName;
		
		//0 is directory, 1 is file
		String[] paths = ToolInterface.splitPath(nlmetPath);
		
		//MET info
		//Working Directory
		String nlmetWorkingDir = paths[0]; //"C:\\personal\\MetadataExtractor\\metadata-extractor";
		//Execution Path
		//String nlmetExecutePath = "C:\\personal\\NL-Metadata-Extractor\\metadata-extractor\\extract.bat";
		String nlmetExecutePath = paths[1]; //"C:\\personal\\MetadataExtractor\\metadata-extractor\\extract.bat";
		
		//It's executing a batch file so no need for java -jar
		ProcessBuilder nlmetBuilder = new ProcessBuilder(nlmetExecutePath, /*"show", "config"*/ "extract", 
				"\"Extract in Native form\"", "Default", "complex", "Object1", "99", nlmetInput);
		/*\"NLNZ Data Dictionary\"*/
		
		//Set the working directory
		String workingDir = nlmetWorkingDir;
		//Have to use a File object to store the working path!
		nlmetBuilder.directory(new File(workingDir));
		
		try{
			System.out.println("\nTrying to execute NLMET (analysis mode)...");
			Process finalProcess = nlmetBuilder.start();
			
		    //Wait to get exit value
		    try {
		    	//Try to exit...
		    	int exitValue = finalProcess.waitFor();
		    	System.out.println("\nExit Value for nlmet (0 means done) is: " + exitValue + 
		    			"\n\tNLMET execution complete!");
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
		
		//Read in the report, parse out the name-value pairs, put it into a CSV
		
		//Turn the filePath into just the filename
		String regex = Pattern.quote(File.separator);
		String[] pieces = nlmetInput.split(regex);
		String fileName = pieces[pieces.length-1];

		//nlmetResults = NlmetOutputParser.parseXML("C:\\personal\\MetadataExtractor\\metadata-extractor\\harvested\\native\\" + fileName + ".xml");
		nlmetResults = NlmetOutputParser.parseXML(nlmetWorkingDir + "harvested\\native\\" + fileName + ".xml");
		
		//Need to parse the report as read in to discard the huge amount of metadata extractor logs
		//Unless there's a text-only output.				
		return nlmetResults;
	//end executeNLMET
	}
	
	public static ArrayList<String> extractNLMET(HashMap<String,String> nlmetSchema, String nlmetInput, String nlmetPath){
		ArrayList<String> nlmetResults = new ArrayList<String>();
		
		//0 is directory, 1 is file
		String[] paths = ToolInterface.splitPath(nlmetPath);
		
		//MET info
		//Working Directory
		String nlmetWorkingDir = paths[0]; //"C:\\personal\\MetadataExtractor\\metadata-extractor";
		//Execution Path
		String nlmetExecutePath = paths[1]; //"C:\\personal\\MetadataExtractor\\metadata-extractor\\extract.bat";
		
		//It's executing a batch file so no need for java -jar
		ProcessBuilder nlmetBuilder = new ProcessBuilder(nlmetExecutePath, /*"show", "config"*/ "extract", 
				"\"Extract in Native form\"", "Default", "complex", "Object1", "99", nlmetInput);
		
		//Set the working directory
		String workingDir = nlmetWorkingDir;
		//Have to use a File object to store the working path!
		nlmetBuilder.directory(new File(workingDir));
		
		try{
			System.out.println("\nExecuting NLMET (extraction mode)...");
			Process finalProcess = nlmetBuilder.start();
			
		    //Wait to get exit value
		    try {
		    	//Try to exit...
		    	int exitValue = finalProcess.waitFor();
		    	System.out.println("\nExit Value for nlmet (0 means done) is: " + exitValue + 
		    			"\n\tNLMET execution complete!");
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
		
		//We have to read in one line at a time - argh argh argh argh argh argh.
		// for this purpose there's an NlmetOutputParser.extractXML function that takes the hashmap

		//Turn the filePath into just the filename
		String regex = Pattern.quote(File.separator);
		String[] pieces = nlmetInput.split(regex);
		String fileName = pieces[pieces.length-1];

		nlmetResults = NlmetOutputParser.extractXML(nlmetSchema, ("C:\\personal\\MetadataExtractor\\metadata-extractor\\harvested\\native\\" + fileName + ".xml"));
		
		return nlmetResults;
		
	//End extractNLMET
	}

//End RunNLMET class
}