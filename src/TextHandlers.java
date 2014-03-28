
public class TextHandlers {

	/**
	 * @param args
	 */
	public static String fieldSanitiser(String stringIn){
		//Replaces " with "" then appends " around the ends on return
		String regex = "\"";
		String replacement = "\"\"";
		String stringOut = stringIn.replaceAll(regex, replacement);

		return "\"" + stringOut + "\"";
	}
	
	public static String fieldDesanitiser(String stringIn){
		//Exists in case someone needs it later 
		String regex = "\"\"";
		String replacement = "\"";
		String stringOut = stringIn.replaceAll(regex, replacement);
		
		return stringOut;
	}
	
	public static String outputCleaner(String[] arrayIn){
		//Strips commas from name-value pairs in an array
		//This is so a CSV file won't get messed up by the commas
		//Turns array into appropriate string
		String output = "";
		
		if (arrayIn.length == 2){
			//Turn string into sanitised version
			String replaceFirst = fieldSanitiser(arrayIn[0]);
			arrayIn[0] = replaceFirst;
			String replaceSecond = fieldSanitiser(arrayIn[1]);
			arrayIn[1] = replaceSecond;
			//Define the line to write
			output = arrayIn[0] + "," + arrayIn[1];
		}
		else if (arrayIn.length == 1){
			//Turn string into sanitised version
			String replaceFirst = fieldSanitiser(arrayIn[0]);
			arrayIn[0] = replaceFirst;
			//Define the line to write
			output = arrayIn[0] + ",";
		}
		else {
			//System.out.println("headings[0]=" + "NULL" + " headings[1]= " + "NULL");
			output = "" + "," + "";
		}
		
		return output;
	//End outputCleaner
	}
	
	public static String removeDrive(String path, String divider){
		String result = "";
		String[] pieces = path.split(divider);
		
		for (int i = 1; i<pieces.length;i++){
			result += pieces[i] + "-";
		}
		
		return result;
	}
	
	
//End TextHandlers class
}
