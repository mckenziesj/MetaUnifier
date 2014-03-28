import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

public class ToolInterface {

	static double nanoDivisor = 1000000000;
	static HashMap<String, HashMap<String,String>> metaSchema = new HashMap<String, HashMap<String,String>>();
	static HashMap<String, String> toolKit = new HashMap<String, String>();
	static String mode = new String();
	static String fileSeparator = Pattern.quote(File.separator);
	
	public static void main(String[] args) {
		/**
		 * Config:
		 * 
		 * 		ToolInterface.jar -m [a|e] -t [config location] -f [directory or file] -s [schema map]
		 *
		 *		-m [a|e]				mode: a for tool analysis; e for metadata extraction
		 *		-t [config location]	path to tool config file
		 *		-f [directory or file]	path to directory or file to run tools on
		 *		-s [schema map]		path to the schema mapping
		 *
		 */
		
		//Timing whole execution
		double totalTimeStart = (double)System.nanoTime();
		//This gives a divisor to turn nanoseconds into seconds
		//final double nanoDivisor = 1000000000;
		//ArrayList of benchmark strings
		ArrayList<String> benchmarks = new ArrayList<String>();

		/*
		String runMode = "e";
		String toolConfigFile = "";
		String workingPath = "";
		String schemaPath = "C:\\personal\\Workspace\\ToolInterface\\schema.txt";
		*/
		/*
		ArrayList<String> arguments = new ArrayList<String>();
		for (int i = 0; i<args.length-1; i++){
			arguments.add(args[i]);
		}
		
		String[] settings = ArgumentHandler.parseArgs(arguments);
		*/
		
		String runMode = ""; // settings[0];
		String toolConfigFile = ""; // settings[1];
		String workingPath = ""; // settings[2];
		String schemaPath = ""; // settings[3];


		if (args.length < 1){
			System.out.println("You must supply arguments!\n\n");
			System.exit(0);
		}
		else if ((args[0].equals("h")) || (args[0].equals("-h")) || (args[0].equals("help")) || (args[0].equals("?")) || (args[0].equals("-?"))){
			System.out.println("ToolUnifier\n\n" +
					"Usage:\n\nMetaUnifer.jar -m Mode -s \"path to metadata schema\" -f \"file or directory path\" -t \"path to tools config file\"" +
					"\n" +
					"\nMode can be: a (analyse a single file), e [extract according to schema], t [speed test of extraction mode]");
			System.exit(0);
		}
		else {
			int i = 0;
			while (i < args.length){
				if (args[i].equals("-m")){
					i++;
					if ((i < args.length) && !args[i].equals(null) && (args[i].equals("e") || args[i].equals("a") || args[i].equals("t"))){
						runMode = args[i];
					}
					else {
						//throw new NullPointerException("Missing run mode argument! Must be a or e");
						System.out.println("Missing run mode argument! Must be a or e");
						System.exit(0);
					}
				}
				else if (args[i].equals("-t")){
					i++;
					if ((i < args.length) && !args[i].equals(null) && (new File(args[i]).isFile())){
						toolConfigFile = args[i];
					}
					else {
						System.out.println("Tool configuration argument is not a file!");
						System.exit(0);
					}
				}
				else if (args[i].equals("-f")){
					i++;
					if ( (i < args.length)&& !args[i].equals(null) && ( (new File(args[i]).isDirectory()) || (new File(args[i]).isFile())) ){
						workingPath = args[i];
					}
					else {
						System.out.println("Working path argument is not a directory or file!");
					}
				}
				else if(args[i].equals("-s")){
					i++;
					if ((i < args.length) && !args[i].equals(null) && (new File(args[i]).isFile())){
						schemaPath = args[i];
					}
					else {
						System.out.println("Schema path argument is not a file!");
						System.exit(0);
					}
				}
				else{
					System.out.println("Flagrant Error: Argument Problem!");
					System.out.println("args[" + i + "] = " + args[i]);
					System.exit(0);
				}
				i++;
			//end While loop
			}
		}
		
		//Load our tool info into the hashmap
		//toolKit gets used with analyse and extract methods
		toolKit = loadTools(toolConfigFile);
		//toolKit gets used with analyse and extract methods
		
		//Load our schema
		metaSchema = loadSchema(schemaPath);
		//Schema gets passed to the extract method
		
		//Set the mode
		mode = runMode;
		//A global mode means we can check it anywhere
				
		//Iterate our tools
		//Is there a data schema mapping for that tool?
		//	YES:	Run that tool using appropriate mode with the schema
		//			Collate the tool output
		//
		//	No: Next tool.
		

		//Our test files (this can be changed to any file, but remember to escape slashes!)
		//This is the path to the folder the test files are kept in
		//String samplePath = "V:\\DC_McKenzie\\SampleFilesForExtraction\\";
		//String samplePath = "C:\\personal\\";
		
		//These are filenames - uncomment the one to test, leave others commented.
		//String sampleFile = "bmpTest.bmp";
		//String sampleFile = "user-guide.doc";
		//String sampleFile = "encrypted-doc.doc";
		//String sampleFile = "cl0024_hst.gif";
		//String sampleFile = "vltlaser_beletsky_960.jpg";
		//String sampleFile = "DROID Help.pdf";
		//String sampleFile = "meta-extractor-developers-guide-v3.pdf";
		//String sampleFile = "Test-Presentation.ppt";
		//String sampleFile = "publisherSample.pub";
		//String sampleFile = "ProbabilityTable.xls";
		//String sampleFile = "Test-Presentation.ppt";
		
		//Compile our final path - this should come from arguments in the final version
		//The arguments should be either:
		//1: just directory, and recurse
		//2: Directory and filename to do a single file
		//How to handle the input of the argument is not decided yet 
		
		//Test path - comment out arguments before using
		//String workingPath = samplePath ;//+ sampleFile;
		
		File startPath = new File(workingPath);
		
		if ((runMode.equals("e") || runMode.equals("t")) && (startPath.isFile() || startPath.isDirectory())){
			benchmarks = extractMetadata(workingPath);
		}
		else if (runMode.equals("a") && startPath.isFile()){
			benchmarks = analyseTools(workingPath);
		}
		else if (runMode.equals("a") && startPath.isDirectory()){
			System.out.println("Analysis mode is for analysing tool outputs from a single file" +
					"\n Please run this program again with a single file as the argument" +
					"\n or use extraction mode to extract from all files in a directory");
			System.exit(0);
		}
		else if (runMode.equals("a") && !startPath.isFile()){
			System.out.println("Running analysis mode must be done with a file");
			System.exit(0);
		}
		else {
			System.out.println("No valid mode specified.");
			System.exit(0);
		}
		
		double totalTimeEnd = (double)System.nanoTime();
		double totalExecutionTime = totalTimeEnd - totalTimeStart;
		String totalBenchmark = "-Total tool execution time = " + totalExecutionTime + " nanoseconds " + "// " + 
				totalExecutionTime/nanoDivisor + " seconds";
		benchmarks.add(totalBenchmark);
		
		//Checking on the outputs
		System.out.println("\nTimes (metadata schema affects speed - some schemas will run faster)");
		for (int i=0; i<benchmarks.size(); i++){
			System.out.println(benchmarks.get(i));
		}
	//End main
	}
	
	public static ArrayList<String> extractMetadata(String workingPath){
		ArrayList<String> benchmarks = new ArrayList<String>();
		System.out.println("Time to extract some metadata!");
		System.out.println("");
		
		//Create a Droid profile and get the location of it back
		double droidTimeStart = (double)System.nanoTime();
		String profilePath = RunDroid.createDroidProfile(workingPath, toolKit.get("Droid"));
		double droidTimeEnd = (double)System.nanoTime();
		double droidTime = droidTimeEnd - droidTimeStart;
		String droidBenchmark = "Droid run time for profile creation = " + droidTime + " nanoseconds " + 
				"// " +  (droidTimeEnd - droidTimeStart)/nanoDivisor + " seconds";
		benchmarks.add(droidBenchmark);
		
		//Create a reader and process the Droid profile here - this is easier to set up initially than YAFC.
		
		//Create a buffered reader, open the file, read the first line, explode it around "," and do stuff with it.
		//Try to make a BufferedReader
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(profilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Start reading, y'all
		String line = null;
		try {
			line = reader.readLine();
		}
		catch (IOException e1){
			e1.printStackTrace();
		} 
		//Explode that into a structure thing.
		String regex = "\",\"|\"";
		String[] headings = line.split(regex);
		//The headings array holds all the names of fields in Droid so we iterate over that checking the hashmap
		
		//this stores the number of files we looked at
		int numFilesExtracted = 0;
		
		double tikaTotalTime = 0;
		double nlmetTotalTime = 0;
		double exifTotalTime = 0;
		double jhoveTotalTime = 0;
		
		//Read from the second line onward and run the tools in the schema HashMap on it
		try {
			while ((line = reader.readLine()) != null){
				String[] values = line.split(regex);
				//Get the File Path
				String currentFile = "";
				for (int i=1; i < values.length; i++){
					if (headings[i].equals("FILE_PATH")){
						currentFile = values[i];
					}
				}
				
				File fileCheck = new File(currentFile);
				//This makes sure we're extracting metadata from a file
				if (fileCheck.isFile()){
					ArrayList<String> output = new ArrayList<String>();
					String[] fileNameParts = currentFile.split(Pattern.quote(File.separator));
					output.add("\"" + fileNameParts[fileNameParts.length-1] + "\",");
					//for each of the headings...
					
					int valuesToGrab = metaSchema.get("Droid").size();
					int valuesGrabbed = 0;
					int index = 1;
					while ((valuesGrabbed < valuesToGrab) && index <= headings.length){
						//If it's in our schema...
						if (metaSchema.get("Droid").containsKey(headings[index])){
							//We put the schema name and the value into an array and send them to be cleaned. 
							String[] dataPair = new String[2];
							dataPair[0] = metaSchema.get("Droid").get(headings[index]);
							if (index <= values.length-1  && !values[index].equals(null)){
								dataPair[1] = values[index];
							}
							else {
								dataPair[1] = "";
							}
							valuesGrabbed++;
							//Add to output: 
							//  the result of two strings having commas stripped and double quotes doubled
							//String lineToWrite = TextHandlers.outputCleaner(dataPair);
							//output.add(lineToWrite);
							//System.out.println("Output grabbed: " + lineToWrite + " valuesGrabbed = " + valuesGrabbed);
							output.add(TextHandlers.outputCleaner(dataPair));
						}
						//else {
						//	System.out.println("headings[" + index + "] == " + headings[index]);
						//}
						index++;
					}
					
					//Get the keySet
					Set<String> toolSet = metaSchema.keySet();
					//Make an iterator for the keys set
					Iterator<String> iter = toolSet.iterator();
					String currentTool = "";
					while (iter.hasNext()){
						currentTool = iter.next();

					
						if (currentTool.equals("ExifTool")){					
							//ExifTool
							//Create the timer
							double exifTimeStart = (double)System.nanoTime();
							//Get the results from ExifTool and add them to our output
							output.addAll(RunExifTool.extractExifTool(metaSchema.get("ExifTool"), currentFile, toolKit.get("ExifTool")));
							//Work out the time at the end and add it to the benchmark list 
							double exifTimeEnd = (double)System.nanoTime();
							exifTotalTime += exifTimeEnd - exifTimeStart;
						}
						
						if (currentTool.equals("NLMET")){
							//National Library Metadata Extraction Tool
							//
							//DISABLED FOR EXTRACTION MODE UNTIL IT'S UPDATED TO OUTPUT TO STD OUT
							//Create the timer
							double nlmetTimeStart = (double)System.nanoTime();				
							//Get the results from NLMET and add them to our output
							output.addAll(RunNLMET.extractNLMET(metaSchema.get("NLMET"), currentFile, toolKit.get("NLMET")));
							//Work out the time and add it to the benchmark list
							double nlmetTimeEnd = (double)System.nanoTime();
							nlmetTotalTime += nlmetTimeEnd - nlmetTimeStart;
						}
					
						if (currentTool.equals("Tika")){
							//Tika
							//Create the timer
							double tikaTimeStart = System.nanoTime();
							//Get the results from Tika and add them to our output
							output.addAll(RunTika.extractTika(metaSchema.get("Tika"), currentFile, toolKit.get("Tika")));
							//Work out the time and add it to the benchmark list
							double tikaTimeEnd = (double)System.nanoTime();
							tikaTotalTime += tikaTimeEnd - tikaTimeStart;
						}
						
						if (currentTool.equals("Jhove")){
							//Jhove
							//Create the timer
							double jhoveTimeStart = System.nanoTime();
							//Get the results from Jhove and add them to our output
							System.out.println("Extracting from Jhove");
							output.addAll(RunJhove.extractJhove(metaSchema.get("Jhove"), currentFile, toolKit.get("Jhove")));
							//Work out the time and add it to the benchmark list
							double jhoveTimeEnd = (double)System.nanoTime();
							jhoveTotalTime += jhoveTimeEnd - jhoveTimeStart;
						}
						
					}
					
					//Write the current file's metadata to its own output file
					//Make a file writer
					//Create the write-to-file thing
					BufferedWriter writer = null;
					try {
						if (mode.equals("t")){
							//This is extraction speed test mode: It overwrites the file every time!
							writer = new BufferedWriter(new OutputStreamWriter(
									new FileOutputStream("Test-Mode-MD.csv"), "utf-8"));
						}
						else if (mode.equals("e")){
							System.out.println(currentFile);
							String outputFileName = TextHandlers.removeDrive(currentFile.replace(".", "-"), fileSeparator) + "Metadata.csv"; 
							//This is real extraction mode: It makes a new file every time!
							writer = new BufferedWriter(new OutputStreamWriter(
									new FileOutputStream(outputFileName), "utf-8"));
						}
					}
					catch(IOException ex){
						ex.printStackTrace();
					}
					
					//Write output ArrayList to the file
					for (int i=0; i<output.size();i++){
						try {
							String lineToWrite = output.get(i);
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
					numFilesExtracted++;
					System.out.println("\nFinished extracting: " + currentFile + " File number " + numFilesExtracted + "\n");
				//End if check for if the FILE_NAME is an actual file: Bloody hell, that was long!
				}
			//End while loop for reading lines from reader
			}
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//Try to close the reader or all is lost!
		try {
			reader.close();
		} catch (IOException e) {
			System.out.println("Failed to close buffered reader!");
			e.printStackTrace();
		}
		
		benchmarks.add("-Number of files run through extraction: " + numFilesExtracted);

		double exifAvgTime = exifTotalTime/numFilesExtracted;
		String exifBenchmark = "ExifTool average run time and array creation = " + exifAvgTime + " nanoseconds " + 
				"// " +  exifAvgTime/nanoDivisor + " seconds";
		benchmarks.add(exifBenchmark);
		
		double nlmetAvgTime = nlmetTotalTime / numFilesExtracted;
		String nlmetBenchmark = "NLMET average run time and array creation = " + nlmetAvgTime + " nanoseconds " + 
				"// " +  nlmetAvgTime/nanoDivisor + " seconds";
		benchmarks.add(nlmetBenchmark);
		//String nlmetBenchmark = "NLMET is not running in extraction mode in this version";
		
		double tikaAvgTime = tikaTotalTime/numFilesExtracted;
		String tikaBenchmark = "Tika average run time and array creation = " + tikaAvgTime + " nanoseconds " + 
				"// " +  tikaAvgTime/nanoDivisor + " seconds";
		benchmarks.add(tikaBenchmark);
		
		double jhoveAvgTime = jhoveTotalTime/numFilesExtracted;
		String jhoveBenchmark = "Jhove average run time and array creation = " + jhoveAvgTime + " nanoseconds " + 
				"// " +  jhoveAvgTime/nanoDivisor + " seconds";
		benchmarks.add(jhoveBenchmark);
		
		return benchmarks;
	}
	
	public static ArrayList<String> analyseTools(String filePath){
		ArrayList<String> benchmarks = new ArrayList<String>();
		
		System.out.println("Analysis Mode");
		
		//Running the tools
		
		//Containers for tool output
		ArrayList<ArrayList<String>> extractionOutputContainer = new ArrayList<ArrayList<String>>();
		
		//Verify filetype using...
		
		//Droid
		double droidTimeStart = (double)System.nanoTime();
		ArrayList<String> droidResults = new ArrayList<String>();
		droidResults = RunDroid.analyseDroid(filePath, toolKit.get("Droid"));
		double droidTimeEnd = (double)System.nanoTime();
		double droidTime = droidTimeEnd - droidTimeStart;
		String droidBenchmark = "Droid run time and array creation = " + droidTime + " nanoseconds " + 
				"// " +  (droidTimeEnd - droidTimeStart)/nanoDivisor + " seconds";
		benchmarks.add(droidBenchmark);
		//Add results to container
		extractionOutputContainer.add(droidResults);
		
		//Get the metadata out using... (more to be added later)
		
		//ExifTool
		double exifTimeStart = (double)System.nanoTime();
		ArrayList<String> exifResults = new ArrayList<String>();
		exifResults = RunExifTool.analyseExifTool(filePath, toolKit.get("ExifTool"));
		double exifTimeEnd = (double)System.nanoTime();
		double exifTime = exifTimeEnd - exifTimeStart;
		String exifBenchmark = "Exiftool run time and array creation = " + exifTime + " nanoseconds " + 
				"// " +  (exifTimeEnd - exifTimeStart)/nanoDivisor + " seconds";
		benchmarks.add(exifBenchmark);
		//Add results to container
		extractionOutputContainer.add(exifResults); 
		
		//National Library Metadata Extraction Tool
		double nlmetTimeStart = (double)System.nanoTime();
		ArrayList<String> nlmetResults = new ArrayList<String>();
		nlmetResults = RunNLMET.analyseNLMET(filePath, toolKit.get("NLMET"));
		double nlmetTimeEnd = (double)System.nanoTime();
		double nlmetTime = nlmetTimeEnd - nlmetTimeStart;
		String nlmetBenchmark = "NLMET run time and array creation = " + nlmetTime + " nanoseconds " + 
				"// " +  (nlmetTimeEnd - nlmetTimeStart)/nanoDivisor + " seconds";
		benchmarks.add(nlmetBenchmark);
		//Add results to container
		extractionOutputContainer.add(nlmetResults);
		
		//Tika
		double tikaTimeStart = System.nanoTime();
		ArrayList<String> tikaResults = new ArrayList<String>();
		tikaResults = RunTika.analyseTika(filePath, toolKit.get("Tika"));
		double tikaTimeEnd = (double)System.nanoTime();
		double tikaTime = tikaTimeEnd - tikaTimeStart;
		String tikaBenchmark = "Tika run time and array creation = " + tikaTime + " nanoseconds " + 
				"// " +  (tikaTimeEnd - tikaTimeStart)/nanoDivisor + " seconds";
		benchmarks.add(tikaBenchmark);
		//Add results to container
		extractionOutputContainer.add(tikaResults);
		
		//Jhove
		//Jhove is going last because some of the cells will be huge
		double jhoveTimeStart = System.nanoTime();
		ArrayList<String> jhoveResults = new ArrayList<String>();
		jhoveResults = RunJhove.analyseJhove(filePath, toolKit.get("Jhove"));
		double jhoveTimeEnd = (double)System.nanoTime();
		double jhoveTime = jhoveTimeEnd - jhoveTimeStart;
		String jhoveBenchmark = "Jhove run time and array creation = " + jhoveTime + " nanoseconds " + 
				"// " +  (jhoveTimeEnd - jhoveTimeStart)/nanoDivisor + " seconds";
		benchmarks.add(jhoveBenchmark);
		//Add results to container
		extractionOutputContainer.add(jhoveResults);
		
		//ArrayList<String> finalDroidOutputs = outputCollator(droidOutputContainer);
		ArrayList<String> finalExtractionOutputs = outputCollator(extractionOutputContainer);
		
		//createOutput(finalDroidOutputs, "collated-droid-outputs");
		createOutput(finalExtractionOutputs, "collated-tool-outputs");

		return benchmarks;
	}
	
	public static ArrayList<String> outputCollator(ArrayList<ArrayList<String>> toolResults){
		//Collates output for analysis 
		ArrayList<String> finalResults = new ArrayList<String>();
		int maxSize = 0;
		for (int i=0; i<toolResults.size(); i++){
			int currentSize = toolResults.get(i).size();
			if (currentSize > maxSize){
				maxSize = currentSize;
			}
		}
		//Okay, let's go!
		for (int i=0; i < maxSize; i++){
			//OH GOD WE'RE DOING A NESTED LOOP I AM A BAD COMPUTER SCIENTIST *SOBS*
			//At least it's not doing more than one pass over each list
			String currentRow = "";
			for (int j=0; j<toolResults.size(); j++){
				if (toolResults.get(j).size()>i){
					currentRow = currentRow + "," + toolResults.get(j).get(i);
					//System.out.println("currentRow = " + currentRow);
				}
				else{
					currentRow = currentRow + ",,";
				}
			}
			finalResults.add(currentRow);
		}		
		return finalResults;
	//End OutputCollator 
	}
	
	public static void createOutput(ArrayList<String> toolOutputs, String fileName){
		//Writes the list of comma separated values to a file
		//Final output should be a CSV file that represents columns of output data from tools
		
		System.out.println("\nCreating output file...");
		
		//Create the write-to-file thing
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName + ".csv"), "utf-8"));
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		
		for (int i=0; i < toolOutputs.size(); i++){
			String lineToWrite = toolOutputs.get(i);
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
		
		return;
	//End CreateOutput
	}
	
	public static HashMap<String, String> loadTools(String toolConfigPath){
		HashMap<String, String> tools = new HashMap<String, String>();
		
		//We create a BufferedReader to read in the tools file
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(toolConfigPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//We read in each line and split it around , 
		//first, create our temp string
		String line = null;
		//Try to read a line
		try {
			//While there's something there...
			while ((line = reader.readLine()) != null){		
				
				//split the line around our delimiter
				String[] pieces = line.split(" ");
				//Remember this syntax:
				// Leading # means comment
				// pieces[0] = Name of the tool
				// pieces[1..n] = The tool's path
				
				//Comment handling
				if (!pieces[0].equals("#") && !pieces[0].equals("")){
					//It's not a comment so we can do stuff
					String currentToolName = pieces[0];
					String currentToolPath = "";
					
					if (pieces.length>1){
						currentToolPath = pieces[1];
					}
					
					if (pieces.length>2){
						for (int i = 2; i < pieces.length-1; i++){
							currentToolPath += " " + pieces[i]; 
						}
					}
					
					if (!tools.containsKey(currentToolName)){
						tools.put(currentToolName, currentToolPath);
					}
					else {
						System.out.println("\nDuplicate tool detected in config file!" +
								"\nAborting run of Tool Unifier" +
								"\n\nCheck your tool config file and try again.");
						System.exit(0);
					}
					
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		//close our fileReader
		try {
			reader.close();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		
		return tools;
	//End loadTools
	}
	
	public static HashMap<String, HashMap<String,String>> loadSchema(String schemaPath){
		//Let's make our container which gets returned
		//Let's also weep for this insanity
		HashMap<String, HashMap<String,String>> schema = new HashMap<String, HashMap<String,String>>();
		
		//We create a BufferedReader to read in the schema file
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(schemaPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//We read in each line and split it around , 
		//first, create our temp string
		String line = null;
		//Try to read a line
		try {
			//While there's something there...
			while ((line = reader.readLine()) != null){
				//There should be a check for the right input from the file
				// if a line is not string,string,string then it's no good			
				
				//split the line around our delimiter
				String[] pieces = line.split(",");
				//Remember this syntax:
				// pieces[0] = Name for our schema
				// pieces[1] = Name of the tool we get it from
				// pieces[2] = Name of the field in that tool with the correct data 
				
				String schemaName = pieces[0];
				String tool = pieces[1];
				String toolName = pieces[2];
				
				if (schema.containsKey(tool)){
					//If the tool mapping is there, put the array into the relevant arraylist
					schema.get(tool).put(toolName, schemaName);
					//System.out.println(schema.get(tool));
				}
				else  {
					//If the tool mapping isn't there, make the HashMap and put it in
					//This should only happen once per tool we find
					HashMap<String,String> tempMap = new HashMap<String,String>();
					//Map the tool field name to the schema field name
					tempMap.put(toolName, schemaName);
					//Map that hashmap to the tool name
					schema.put(tool, tempMap);
					//System.out.println(toolName + "maps to " + tempMap.get(toolName));
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			reader.close();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		
		return schema;
	//End loadSchema
	}
	
	public static String[] splitPath(String path){
		//Splits a filepath into
		//	[0] the full directory
		//	[1] the full filepath
		String[] paths = new String[2];
		String [] pieces = path.split(Pattern.quote(File.separator));
		
		paths[1] = path;
		String directory = "";
		for (int i = 0; i < pieces.length-1; i++){
			directory += pieces[i] + File.separator;
		}
		paths[0] = directory;
		
		return paths;
	}

//End ToolInterface class
}
