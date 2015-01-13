import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class PowerMeasureMain {


	public static final String VERSION = "v1.5";


	public static final String ARG_NO_FREQ = "-freq";
	public static final String ARG_NO_FPS = "-fps";
	public static final String ARG_NO_POWER = "-power";
	public static final String ARG_NO_CHART = "-chart";
	public static final String ARG_NO_UTIL = "-util";
	public static final String ARG_EXT_POWER = "extpower:";
	public static final String ARG_HELP = "help";

	public static final String TEXT_HELP= "Usage: java -jar powermeasure.jar [n] [-freq] [-fps] [-power] [-chart] [extpower:(com port name)]\n"
			+ "n: number of samples to take at once/second (>=0)\n"
			+ "-freq: Don't poll for frequency\n" 
			+ "-fps: Don't poll for FPS\n" 
			+ "-util: Don't show utilisation\n"
			+ "-power: Don't poll for power\n"
			+ "-chart: Don't show GUI chart\n"
			+ "extpower:(com port name) : Read external system power from COM Port. Requires power poll to be active\n"
			+ VERSION;

	public static final String TEXT_HELP_OFFER = "Add the \"help\" argument to know more"; 
	public static final String TEXT_HELP_INVALID_NUMBER = "Invalid arguments. Please supply correct number of samples to take at once/second.";
	public static final String TEXT_HELP_NO_POLL = "Invalid arguments. You need to poll at least for something.";
	public static final String TEXT_SERIAL_PORT_CANNOT_OPEN = "Cannot open serial port. External system power measurement turned off."; 

	public static final String TEXT_FPS_PROGRESS_FORMAT = 	"FPS(n)    %04d: Now: %d, Average: %d";
	public static final String TEXT_POWER_PROGRESS_FORMAT = "Power(W)  %04d: A15: %08.3f, A7: %08.3f, GPU: %08.3f, MEM: %08.3f";
	public static final String TEXT_EXTERNAL_POWER_PROGRESS_FORMAT = "\nE-Power(W)%04d: Now: %.3f, Total : %.1fJ";
	public static final String TEXT_FREQ_PROGRESS_FORMAT =  "Freq(MHz) %04d: CPU: %.0f, GPU: %.0f";
	public static final String TEXT_UTIL_PROGRESS_FORMAT =  "CPU(%%)    %04d: All: %.1f, Core0: %.1f, Core1: %.1f, Core2: %.1f, Core3: %.1f\n"
			+ "GPU(%%)    %04d: GPU: %.2f";

	public static final String TEXT_TOTAL_FORMAT =    		"Total(J)  %04d: A15: %08.3f, A7: %08.3f, GPU: %08.3f, MEM: %08.3f";
	public static final String TEXT_INDEFINITE_SAMPLING = "Now sampling indefinitely at once/sec for FPS, utilisation, freqency and power with charts shown";

	public static final String TEXT_SAMPLE_TYPES = "FPS: %s, Util: %s, Freq: %s, Power: %s, Chart: %s, External Power: %s, COM Port: %s";	
	public static final String TEXT_SAMPLES_REQUIRED = "Going for %d sample(s) at once/second";


	public static final String TEXT_FINAL_INDV_FORMAT = "Total(J): A15: %.2f, A7: %.2f, GPU: %.2f, MEM: %.2f\n";
	public static final String TEXT_FINAL_INTERNAL_POWER = "Total internal power used over %d samples: %.2fJ";
	public static final String TEXT_FINAL_EXTERNAL_POWER = "Total external power used over %d samples: %.2fJ";
	public static final String TEXT_FINAL_FPS = "FPS over %d samples, Average: %d, SD: %.2f, Min: %d, Max: %d";

	public static final long SAMPLE_RATE = 1000;



	public static final long INDEFINITE_SAMPLING = -1;
	private static long totalSamplesRequired = INDEFINITE_SAMPLING;

	private static final float TIME_INTERVAL_NANO_SECONDS = 1000000000;


	private static double totalA7Power = 0;
	private static double totalA15Power = 0;
	private static double totalGPUPower = 0;
	private static double totalMemPower = 0;

	private static double totalExternalPower = 0;

	private static int numSamples = 0;

	private static int minFPS = Integer.MAX_VALUE;
	private static int maxFPS = Integer.MIN_VALUE;

	private static List<Integer> fpsData;

	private static long totalFPS = 0;
	private static int numFPSSamples = 0;

	private static boolean shouldPollFPS = true;
	private static boolean shouldPollUtil = true;
	private static boolean shouldPollFreq = true;
	private static boolean shouldPollPower  = true;
	private static boolean shouldShowChart = true;
	private static boolean shouldPollExtPower = false;


	private static boolean isPreviousCommandStillRunning = false;

	private static Chart fpsChart;
	private static Chart cpuUtilChart;
	private static Chart gpuUtilChart;
	private static Chart cpuFreqChart;
	private static Chart gpuFreqChart;
	private static Chart powerChart;

	private static String serialPortname = "N/A";

	public static void main(String[] args) {

		if(args.length > 0){
			try{

				for(String arg : args){
					switch(arg){
					case ARG_NO_FPS:
						shouldPollFPS = false;
						break;
					case ARG_NO_UTIL:
						shouldPollUtil = false;
						break;
					case ARG_NO_FREQ:
						shouldPollFreq = false;
						break;
					case ARG_NO_POWER:
						shouldPollPower = false;
						break;
					case ARG_NO_CHART:
						shouldShowChart = false;
						break;
					case ARG_HELP:
						printToScreen(TEXT_HELP);
						return;
					default:
						if(arg.startsWith(ARG_EXT_POWER)){
							serialPortname = arg.replaceAll(ARG_EXT_POWER, "");
							shouldPollExtPower = true;
						} else {
							totalSamplesRequired = Long.parseLong(arg);
							if(totalSamplesRequired < 0){
								throw new NumberFormatException();
							}
						}
					}
				}

				printToScreen(TEXT_HELP_OFFER);
				
				if(!shouldPollPower){
					shouldPollExtPower = false;
				}
				if(!shouldPollFPS && !shouldPollFreq && !shouldPollPower && !shouldPollUtil && !shouldPollExtPower){
					printToScreen(String.format(TEXT_HELP_NO_POLL));
					return;
				}

				if(totalSamplesRequired >= 0){
					printToScreen(String.format(TEXT_SAMPLES_REQUIRED, totalSamplesRequired));
				} else {
					printToScreen(TEXT_INDEFINITE_SAMPLING);
				}

				printToScreen(String.format(TEXT_SAMPLE_TYPES, shouldPollFPS, shouldPollUtil, shouldPollFreq, shouldPollPower, shouldShowChart, shouldPollExtPower, serialPortname));

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

		if(shouldPollExtPower && shouldPollPower){
			boolean serialPortStartStatus = ExternalPowerRetrieval.startSystemPowerRetrieval(serialPortname);

			if(!serialPortStartStatus){
				shouldPollExtPower = false;
				printToScreen(TEXT_SERIAL_PORT_CANNOT_OPEN);
			}
		} else {
			shouldPollExtPower = false;
		}


		if(shouldShowChart){
			if(shouldPollFPS){
				initFPSChart();
			}

			if(shouldPollUtil){
				initCPUUtilChart();
				initGPUUtilChart();
			}

			if(shouldPollFreq){
				initCPUFreqChart();
				initGPUFreqChart();
			}

			if(shouldPollPower){
				initPowerChart();
			}

		}

		InitADB.initADB();


		printToScreen("Press enter to begin...");
		Scanner keyboard = new Scanner(System.in);
		keyboard.nextLine();
		keyboard.close();


		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if(!isPreviousCommandStillRunning){
					isPreviousCommandStillRunning = true;

					if(shouldContinueSampling()){


						int currentSample = numSamples;
						numSamples++;
						try{
							if(shouldPollFPS){
								String fpsStr = pollFPS();
								printToScreen(fpsStr);
							}

							if(shouldPollUtil){
								String utilStr = pollUtil();
								printToScreen(utilStr);
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

					} else {
						endMessages();
						ExternalPowerRetrieval.stopSystemPowerRetrieval();
						System.exit(0);
					}
					isPreviousCommandStillRunning = false;
				}
			}
		}, 0, SAMPLE_RATE, TimeUnit.MILLISECONDS);

	}

	public static void endMessages(){
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
			String powerString = String.format(TEXT_FINAL_INTERNAL_POWER, numSamples, totalPower);

			printToScreen(indvPowerString);
			printToScreen(powerString);

			if(shouldPollExtPower){
				String externalPowerString = String.format(TEXT_EXTERNAL_POWER_PROGRESS_FORMAT, numSamples, totalExternalPower);
				printToScreen(externalPowerString);
			}


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

		if(shouldShowChart) {
			fpsChart.addData(0, numSamples, fps);
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

		double totalCurrentPower = currentA15Power + currentA7Power + currentGPUPower + currentMEMPower;

		if(shouldShowChart) {
			powerChart.addData(0, numSamples, totalCurrentPower);
		}

		String currentPower = String.format(TEXT_POWER_PROGRESS_FORMAT, numSamples, currentA15Power, currentA7Power, currentGPUPower, currentMEMPower);

		if(shouldPollExtPower){
			double externalPower = ExternalPowerRetrieval.getPower();
			totalExternalPower += externalPower;
			if(shouldShowChart){
				powerChart.addData(1, numSamples, externalPower);
			}

			String externalPowerProgress = String.format(TEXT_EXTERNAL_POWER_PROGRESS_FORMAT, numSamples, externalPower, totalExternalPower);
			currentPower += externalPowerProgress;
		}


		return currentPower;
	}


	public static String pollFreq(){
		double currentCPUFreq = CPUStatsRetrieval.getCPUFreq() / 1000;
		double currentGPUFreq = GPUStatsRetrieval.getGPUFreq();

		if(shouldShowChart) {
			cpuFreqChart.addData(0, numSamples, currentCPUFreq);
			gpuFreqChart.addData(0, numSamples, currentGPUFreq);
		}
		String currentFreq = String.format(TEXT_FREQ_PROGRESS_FORMAT, numSamples, currentCPUFreq, currentGPUFreq);
		return currentFreq;
	}

	public static String pollUtil(){
		double[] cpuUtil = CPUStatsRetrieval.getCPUCoresUtilisation();
		double gpuUtil = GPUStatsRetrieval.getGPUUtilisation();

		double totalCPUUtil = 0;

		for(double coreUtil : cpuUtil){
			totalCPUUtil += coreUtil;
		}

		double averageCPUUtil = totalCPUUtil / cpuUtil.length;


		if(shouldShowChart){
			for(int i = 0; i < cpuUtil.length; i++){
				cpuUtilChart.addData(i, numSamples, cpuUtil[i]);
			}

			gpuUtilChart.addData(0, numSamples, gpuUtil);
		}

		String currentUtil = String.format(TEXT_UTIL_PROGRESS_FORMAT, numSamples, averageCPUUtil, cpuUtil[0], cpuUtil[1], cpuUtil[2], cpuUtil[3], numSamples, gpuUtil);
		return currentUtil;
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



	public static void initFPSChart(){
		fpsChart = openChart("FPS", "FPS", new String[]{"FPS"}, 0, 70, true, 0, true, false, true);
	}

	public static void initCPUFreqChart(){
		cpuFreqChart = openChart("CPU Frequency (Mhz)", "Freq (Mhz)", new String[]{"Freq"}, 0, 1800, false, 0, false, false, false);
	}

	public static void initGPUFreqChart(){
		gpuFreqChart = openChart("GPU Frequency (Mhz)", "Freq (Mhz)", new String[]{"Freq"}, 0, 700, false, 0, false, false, false);
	}

	public static void initCPUUtilChart(){
		cpuUtilChart = openChart("CPU Utilisation (%)", "Util (%)", new String[]{"Core0", "Core1", "Core2", "Core3"}, 0, 105, false, 0, false, true, false);
	}

	public static void initGPUUtilChart(){
		gpuUtilChart = openChart("GPU Utilisation (%)", "Util (%)", new String[]{"Util"}, 0, 105, false, 0, false, false, false);
	}

	public static void initPowerChart(){

		if(shouldPollExtPower){
			powerChart = openChart("Power use (W)", "Power (W)", new String[]{"CPU + GPU + RAM", "System Total"}, 0, 10, true, 2, true, true, true);
		} else {
			powerChart = openChart("Power use (W)", "Power (W)", new String[]{"CPU + GPU + RAM"}, 0, 5, true, 2, true, true, true);
		}

	}


	public static Chart openChart(String title, String yAxisLabel, String[] dataSetLabels, double minY, double maxY, boolean showPointValues, int numDecimalPlaces, boolean largeChart, boolean showLegend, boolean showCurrentAverage){
		Chart chart;
		if(largeChart){
			chart = new Chart(title, yAxisLabel, dataSetLabels, 830, 250, minY, maxY, showPointValues, numDecimalPlaces, showLegend, showCurrentAverage);
		} else {
			chart = new Chart(title, yAxisLabel, dataSetLabels, 300, 250, minY, maxY, showPointValues, numDecimalPlaces, showLegend, showCurrentAverage);
		}
				
				
		chart.pack();
		chart.setVisible(true);
		return chart;
	}








}
