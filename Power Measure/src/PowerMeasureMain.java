import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class PowerMeasureMain {

	
	
	public static final String POWER_FILE_A7 = "/sys/bus/i2c/drivers/INA231/4-0045/sensor_W";
	public static final String POWER_FILE_A15 = "/sys/bus/i2c/drivers/INA231/4-0040/sensor_W";
	public static final String POWER_FILE_MEM = "/sys/bus/i2c/drivers/INA231/4-0041/sensor_W";
	public static final String POWER_FILE_GPU = "/sys/bus/i2c/drivers/INA231/4-0044/sensor_W";
	
	public static final String COMMAND_SHELL_FORMAT = "adb shell cat %s";
	public static final String COMMAND_INIT_ADB = "adb devices";
	
	public static final String COMMAND_A7_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_A7);
	public static final String COMMAND_A15_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_A15);
	public static final String COMMAND_MEM_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_MEM);
	public static final String COMMAND_GPU_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_GPU);
	
	
	public static final String TEXT_HELP = "Insufficient/Invalid arguments. Please supply duration to sample in milliseconds";
	public static final String TEXT_PROGRESS_FORMAT = "Current(W) %04d: A15:%010.5f, A7:%010.5f, GPU:%010.5f, MEM:%010.5f";
	public static final String TEXT_TOTAL_FORMAT =    "Total  (J) %04d: A15:%010.5f, A7:%010.5f, GPU:%010.5f, MEM:%010.5f\n";
	
	public static final String TEXT_FINAL_INDV_FORMAT = "Total[J): A15: %.2f, A7: %.2f, GPU: %.2f, MEM: %.2f\n";
	public static final String TEXT_FINAL_POWER = "Total Power used over %d samples: %.2fJ";
	
	public static final long SAMPLE_RATE = 1000;
	
	private static long startTime;
	
	private static long sampleDuration;
	
	private static double totalA7Power = 0;
	private static double totalA15Power = 0;
	private static double totalGPUPower = 0;
	private static double totalMemPower = 0;
	
	private static int numSamples = 0;

	public static void main(String[] args) {
		
		if(args.length < 1){
			printToScreen(TEXT_HELP);
			return;
		}
		

		try{
			sampleDuration = Long.parseLong(args[0]);
		} catch (NumberFormatException e){
			printToScreen(TEXT_HELP);
			return;
		}
		
		
		runCommandAndGetOutput(COMMAND_INIT_ADB);
		
		startTime = System.currentTimeMillis();

		while(shouldContinueSampling()){
			numSamples++;
			double currentA15Power = getPowerFromCommand(COMMAND_A15_POWER);
			double currentA7Power = getPowerFromCommand(COMMAND_A7_POWER);
			double currentGPUPower = getPowerFromCommand(COMMAND_GPU_POWER);
			double currentMEMPower = getPowerFromCommand(COMMAND_MEM_POWER);
			
			totalA15Power += currentA15Power;
			totalA7Power += currentA7Power;
			totalGPUPower += currentGPUPower;
			totalMemPower += currentMEMPower;
			
			String current = String.format(TEXT_PROGRESS_FORMAT, numSamples, currentA15Power, currentA7Power, currentGPUPower, currentMEMPower);
			String total = String.format(TEXT_TOTAL_FORMAT, numSamples, totalA15Power, totalA7Power, totalGPUPower, totalMemPower);
			
			printToScreen(current);
			printToScreen(total);
			try {
				Thread.sleep(SAMPLE_RATE);
			} catch (InterruptedException e) {
			}
		}
		
		
		
		double totalPower = totalA15Power + totalA7Power + totalGPUPower + totalMemPower;
		
		
		String indvPowerString = String.format(TEXT_FINAL_INDV_FORMAT, totalA15Power, totalA7Power, totalGPUPower, totalMemPower);
		String powerString = String.format(TEXT_FINAL_POWER, numSamples, totalPower);
		
		printToScreen("\n");
		printToScreen(indvPowerString);
		printToScreen(powerString);
		
		
		

	}
	
	public static boolean shouldContinueSampling(){
		long currentTime = System.currentTimeMillis();
		long difference = currentTime - startTime;
		
		if(difference > sampleDuration){
			return false;
		} else {
			return true;
		}
		
		
	}
	
	public static void printToScreen(String output){
		System.out.println(output);
	}
	
	public static double getPowerFromCommand(String command){
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

}
