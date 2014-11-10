import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public abstract class InfoRetrieval {
	
	public static final String COMMAND_SHELL_FORMAT = "adb shell cat %s";

	public static double getValueFromCommand(String command){
		String output = runCommandAndGetOutput(command);
		double powerUse = Double.parseDouble(output);
		return powerUse;
	}
	
	
	public static String runCommandAndGetOutput(String command){

		try{
			Process proc = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			String line = "";
			String output = "";
			while((line = reader.readLine()) != null){
				output += line;
			}

			proc.waitFor();

			return output;

		} catch (IOException | InterruptedException e){
			return "0";
		}


	}

	public static List<String> runCommandAndGetOutputAsLines(String command){

		List<String> outputData = new ArrayList<String>();
		try{
			Process proc = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			String line = "";
			while((line = reader.readLine()) != null){
				if(!line.isEmpty()){
					outputData.add(line);
				}
			}

			proc.waitFor();


		} catch (IOException | InterruptedException e){

		}
		return outputData;

	}
	

	
	
}
