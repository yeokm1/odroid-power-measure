import java.util.ArrayList;
import java.util.List;


public class PowerMeasureMain {

	
	
	public static final String ARG_NO_FREQ = "-freq";
	public static final String ARG_NO_FPS = "-fps";
	public static final String ARG_NO_POWER = "-power";
	public static final String ARG_HELP = "help";

	public static final String TEXT_HELP= "Usage: java -jar powermeasure.jar [n] [-freq] [-fps] [-power]\n"
			+ "n: number of samples to take at once/second (>=0)\n"
			+ "-freq: Don't poll for frequency\n" 
			+ "-fps: Don't poll for FPS\n" 
			+ "-power: Don't poll for power\n"; 
	public static final String TEXT_HELP_OFFER = "Add the \"help\" argument to know more"; 
	public static final String TEXT_HELP_INVALID_NUMBER = "Invalid arguments. Please supply correct number of samples to take at once/second.";
	public static final String TEXT_HELP_NO_POLL = "Invalid arguments. You need to poll at least for something.";
	
	public static final String TEXT_FPS_PROGRESS_FORMAT = 	"FPS(n)    %04d: %d, Average: %d";

	public static final String TEXT_POWER_PROGRESS_FORMAT = "Power(W)  %04d: A15:%08.3f, A7:%08.3f, GPU:%08.3f, MEM:%08.3f";
	public static final String TEXT_FREQ_PROGRESS_FORMAT =  "Freq(MHz) %04d: CPU: %.0f, GPU: %.0f";

	public static final String TEXT_TOTAL_FORMAT =    		"Total(J)  %04d: A15:%08.3f, A7:%08.3f, GPU:%08.3f, MEM:%08.3f";
	public static final String TEXT_INDEFINITE_SAMPLING = "Now sampling indefinitely at once/sec for FPS, freqency and power.";
	
	public static final String TEXT_SAMPLE_TYPES = "FPS: %s, Freq: %s, Power: %s";	
	public static final String TEXT_SAMPLES_REQUIRED = "Going for %d sample(s) at once/second";


	public static final String TEXT_FINAL_INDV_FORMAT = "Total(J): A15: %.2f, A7: %.2f, GPU: %.2f, MEM: %.2f\n";
	public static final String TEXT_FINAL_POWER = "Total Power used over %d samples: %.2fJ";
	public static final String TEXT_FINAL_FPS = "FPS over %d samples, Average: %d, SD: %.2f, Min: %d, Max: %d";

	public static final long SAMPLE_RATE = 1000;



	public static final long INDEFINITE_SAMPLING = -1;
	private static long totalSamplesRequired = INDEFINITE_SAMPLING;

	private static final float TIME_INTERVAL_NANO_SECONDS = 1000000000;


	private static double totalA7Power = 0;
	private static double totalA15Power = 0;
	private static double totalGPUPower = 0;
	private static double totalMemPower = 0;

	private static int numSamples = 0;

	private static int minFPS = Integer.MAX_VALUE;
	private static int maxFPS = Integer.MIN_VALUE;

	private static List<Integer> fpsData;

	private static long totalFPS = 0;
	private static int numFPSSamples = 0;


	public static void main(String[] args) {
	
		boolean shouldPollPower = true;
		boolean shouldPollFPS = true;
		boolean shouldPollFreq = true;
		


		if(args.length > 0){
			try{

				
				for(String arg : args){
					switch(arg){
						case ARG_NO_FPS:
							shouldPollFPS = false;
							break;
						case ARG_NO_FREQ:
							shouldPollFreq = false;
							break;
						case ARG_NO_POWER:
							shouldPollPower = false;
							break;
						case ARG_HELP:
							printToScreen(TEXT_HELP);
							return;
						default:
							totalSamplesRequired = Long.parseLong(arg);
							if(totalSamplesRequired < 0){
								throw new NumberFormatException();
							}
								
					}
				}
				
				printToScreen(TEXT_HELP_OFFER);
				if(!shouldPollFPS && !shouldPollFreq && !shouldPollPower){
					printToScreen(String.format(TEXT_HELP_NO_POLL));
					return;
				}
				
				if(totalSamplesRequired >= 0){
					printToScreen(String.format(TEXT_SAMPLES_REQUIRED, totalSamplesRequired));
				} else {
					printToScreen(TEXT_INDEFINITE_SAMPLING);
				}

				printToScreen(String.format(TEXT_SAMPLE_TYPES, shouldPollFPS, shouldPollFreq, shouldPollPower));

			} catch (NumberFormatException e){
				printToScreen(TEXT_HELP_INVALID_NUMBER);
				return;
			}

		} else {
			printToScreen(TEXT_HELP_OFFER);
			printToScreen(TEXT_INDEFINITE_SAMPLING);
		}
		
		printToScreen("");

		fpsData = new ArrayList<Integer>();



		InitADB.initADB();



		while(shouldContinueSampling()){
			int currentSample = numSamples;
			numSamples++;
			try{
				if(shouldPollFPS){
					String fpsStr = pollFPS();
					printToScreen(fpsStr);
				}

				if(shouldPollFreq){
					String freqString = pollFreq();
					printToScreen(freqString);
				}

				if(shouldPollPower){
					String powerString = pollPower();
					printToScreen(powerString);
					String total = String.format(TEXT_TOTAL_FORMAT, numSamples, totalA15Power, totalA7Power, totalGPUPower, totalMemPower);
					printToScreen(total);
				}
				
				printToScreen("");
			} catch(Exception e){
				numSamples = currentSample;
			}



			try {
				Thread.sleep(SAMPLE_RATE);
			} catch (InterruptedException e) {
			}

		}

		printToScreen("");


		if(shouldPollFPS){
			int averageFPS = getAverageFPS();
			double sdFPS = getStandardDeviation(fpsData);
			String fpsString = String.format(TEXT_FINAL_FPS, numSamples, averageFPS, sdFPS, minFPS, maxFPS);
			printToScreen(fpsString);
		}

		if(shouldPollPower){
			double totalPower = totalA15Power + totalA7Power + totalGPUPower + totalMemPower;
			String indvPowerString = String.format(TEXT_FINAL_INDV_FORMAT, totalA15Power, totalA7Power, totalGPUPower, totalMemPower);
			String powerString = String.format(TEXT_FINAL_POWER, numSamples, totalPower);
			printToScreen(indvPowerString);
			printToScreen(powerString);
		}


	}

	public static String pollFPS() throws Exception{
		int fps = FPSRetrieval.getFPS(TIME_INTERVAL_NANO_SECONDS);

		if(fps != FPSRetrieval.NO_FPS_CALCULATED){
			fpsData.add(fps);
			totalFPS += fps;
			numFPSSamples++;

			if(fps < minFPS){
				minFPS = fps;
			}

			if(fps > maxFPS){
				maxFPS = fps;
			}
		} else {
			throw new Exception();
		}

		int averageFPS = getAverageFPS();
		String output = String.format(TEXT_FPS_PROGRESS_FORMAT, numSamples ,fps, averageFPS);
		return output;
	}

	public static String pollPower(){
		double currentA15Power = CPUStatsRetrieval.getA15Power();
		double currentA7Power = CPUStatsRetrieval.getA7Power();
		double currentGPUPower = GPUStatsRetrieval.getGPUPower();
		double currentMEMPower = MemStatsRetrieval.getMemPower();

		totalA15Power += currentA15Power;
		totalA7Power += currentA7Power;
		totalGPUPower += currentGPUPower;
		totalMemPower += currentMEMPower;


		String currentPower = String.format(TEXT_POWER_PROGRESS_FORMAT, numSamples, currentA15Power, currentA7Power, currentGPUPower, currentMEMPower);
		return currentPower;
	}


	public static String pollFreq(){
		double currentCPUFreq = CPUStatsRetrieval.getCPUFreq() / 1000;
		double currentGPUFreq = GPUStatsRetrieval.getGPUFreq();
		String currentFreq = String.format(TEXT_FREQ_PROGRESS_FORMAT, numSamples, currentCPUFreq, currentGPUFreq);
		return currentFreq;
	}


	public static double getStandardDeviation(List<Integer> values) {
		double deviation = 0.0;
		if ((values != null) && (values.size() > 1)) {
			double mean = getAverageFPS();
			for (int value : values) {
				double delta = value-mean;
				deviation += delta*delta;
			}
			deviation = Math.sqrt(deviation/values.size());
		}
		return deviation;
	}

	public static int getAverageFPS(){
		int averageFPS;
		if(numFPSSamples == 0){
			averageFPS = 0;
		} else {
			averageFPS = (int) (totalFPS / numFPSSamples);
		}

		return averageFPS;
	}

	public static boolean shouldContinueSampling(){

		if(totalSamplesRequired == INDEFINITE_SAMPLING){
			return true;
		}

		if(numSamples >= totalSamplesRequired){
			return false;
		} else {
			return true;
		}

	}

	public static void printToScreen(String output){
		System.out.println(output);
	}












}
