import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class NlmetOutputParser {
	
	public static ArrayList<String> parseXML(String fileName){
		ArrayList<String> output = new ArrayList<String>();
		output.add("National Library Meta Extractor,");
		
		//File to parse: NZNL Meta Extractor output
		//String filePath = "C:\\personal\\MetadataExtractor\\metadata-extractor\\harvested\\native\\meta-extractor-developers-guide-v3.pdf.xml";
		//String filePath = "C:\\personal\\MetadataExtractor\\metadata-extractor\\harvested\\native\\" + fileName + ".xml";	
		
		//Represent it as a file object
		File file = new File(fileName);
				
		//Make a scanner!
		try {
			Scanner scan = new Scanner(file);
			scan.useDelimiter("</[-a-zA-Z]+>");
			scan.skip("<\\?[\\w\\s\\d\\p{Punct}]+\\?>");
			scan.skip("<!--[\\w\\s\\d\\p{Punct}]+-->");
			while(scan.hasNext()){
				String item = parseFile(scan);
				output.add(item);
			}

			scan.close();		
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	private static String parseFile(Scanner s) {
		String output = "";
		//output.add("National Library Meta Extractor,");
			
			String line = s.next();
			if (line.contains("<CREATION-DATE>")){
				//System.out.println("Creation date tag found, going to builder");
				String tempString = buildCreationDate(line, s);
				output = tempString;
			}
			else if (line.contains("<MODIFIED-DATE>")){
				//System.out.println("modified date tag found, going to builder");
				String tempString = buildModifiedDate(line, s);
				output = tempString;
			}
			else {
				String[] pieces = line.split(">");
				int pSize = pieces.length;
				if (pSize > 1){
					String oldChar = "<";
					String newChar = "";
					String[] outputPieces = {pieces[pSize-2].replace(oldChar, newChar),pieces[pSize-1]};
					//String finalString = pieces[pSize-2].replace(oldChar, newChar) + "," + pieces[pSize-1];
					output = TextHandlers.outputCleaner(outputPieces);
				}
				else {
					String oldChar = "<";
					String newChar = "";
					String[] outputPieces = {pieces[0].replace(oldChar, newChar),};
					//String finalString = pieces[0].replace(oldChar, newChar) + ",";
					output = TextHandlers.outputCleaner(outputPieces);
				}
			}
		return output;
	//End parseFile
	}

	private static String buildCreationDate(String line, Scanner s) {
		String[] outputPieces = new String[2];
		outputPieces[0] = "Creation date & time,";

		String[] pieces = line.split(">"); //Split it in half around the close bracket
		String outputValue = pieces[pieces.length-1] + "-"; //append date to the output
		s.next(); //Moves past date format info
		String temp = s.next(); //Grab the creation time
		pieces = temp.split(">"); //split the string with the time up
		outputValue = outputValue + pieces[1]; //append creation time to the output
		s.next(); //move past the time format
		outputPieces[1] = outputValue;
		
		return TextHandlers.outputCleaner(outputPieces);
	//End buildCreationDate
	}
		
	private static String buildModifiedDate(String line, Scanner s) {
		String[] outputPieces = new String[2];
		outputPieces[0] = "Last Modified date & time,";
		
		String[] pieces = line.split(">"); //Split it in half around the close bracket
		String outputValue = pieces[pieces.length-1] + "-"; //append date to the output
		s.next(); //Moves past date format info
		String temp = s.next(); //Grab the modified time
		pieces = temp.split(">"); //split the string with the time up
		outputValue = outputValue + pieces[1]; //append creation time to the output
		s.next(); //move past the time format
		outputPieces[1] = outputValue;
		
		return TextHandlers.outputCleaner(outputPieces);
	//End buildModifiedDate
	}
	
	public static ArrayList<String> extractXML(HashMap<String,String> nlmetSchema, String fileName){
		ArrayList<String> results = new ArrayList<String>();
		
		//Represent it as a file object
		File file = new File(fileName);
		
		//Make a scanner!
		try {
			Scanner scan = new Scanner(file);
			scan.useDelimiter("</[-a-zA-Z]+>");
			
			//So it knows when to give up
			int valuesToGrab = nlmetSchema.size();
			int valuesGrabbed = 0;
			
			while((valuesGrabbed < valuesToGrab) && scan.hasNext()){
				String item = parseFile(scan);
				if (!item.equals(",")){
					String[] pieces = item.split(",");
					String key = pieces[0].replace("\"", "");
					if (nlmetSchema.containsKey(key)){
						pieces[0] = "\"" + nlmetSchema.get(key) + "\"";
						results.add(pieces[0] + "," + pieces[1]);
						valuesGrabbed++;
					}
				}
			}
			
			//We're done with the scanner
			scan.close();		
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Delete the NLMET output file to save disk space
		file.setWritable(true);
		file.delete();

		return results;
	}

}