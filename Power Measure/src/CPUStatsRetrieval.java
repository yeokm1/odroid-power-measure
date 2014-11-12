
public class CPUStatsRetrieval extends InfoRetrieval{
	private static final String POWER_FILE_A7 = "/sys/bus/i2c/drivers/INA231/4-0045/sensor_W";
	private static final String POWER_FILE_A15 = "/sys/bus/i2c/drivers/INA231/4-0040/sensor_W";
	private static final String CPU_FREQ_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
	private static final String CPU_UTIL_FILE = "/proc/stat";

	private static final String COMMAND_A7_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_A7);
	private static final String COMMAND_A15_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_A15);
	private static final String COMMAND_CPU_FREQ = String.format(COMMAND_SHELL_FORMAT, CPU_FREQ_FILE);
	private static final String COMMAND_CPU_UTIL = String.format(COMMAND_SHELL_FORMAT, CPU_UTIL_FILE);

	private static final int NUM_CORES = 4;
	
	private static long prevAllCoresLoad = 0;
	private static long prevAllCoresTotal = 0;
	
	private static double[] prevCoreLoad = new double[NUM_CORES];
	private static double[] prevCoreTotal = new double[NUM_CORES];

	public static double getA7Power(){
		double a7Power = getValueFromCommand(COMMAND_A7_POWER);
		return a7Power;
	}

	public static double getA15Power(){
		double a15Power = getValueFromCommand(COMMAND_A15_POWER);
		return a15Power;
	}


	public static double getTotalCPUPower(){
		return getA7Power() + getA15Power();
	}

	public static double getCPUFreq(){
		double cpuFreq = getValueFromCommand(COMMAND_CPU_FREQ);
		return cpuFreq;
	}
	
	private static String[] getCPUCoreUtilTokens(){
		String cpuOutput = runCommandAndGetOutput(COMMAND_CPU_UTIL);
		String[] toks = cpuOutput.split(" ");
		return toks;
	}

	public static double getCPUUtilisation() {
		String[] toks = getCPUCoreUtilTokens();

		// From here http://www.linuxhowtos.org/System/procstat.htm
		long user = Long.parseLong(toks[2]);
		long nice = Long.parseLong(toks[3]);
		long system = Long.parseLong(toks[4]);
		long currentIdle = Long.parseLong(toks[5]);
		long iowait = Long.parseLong(toks[6]);
		long irq = Long.parseLong(toks[7]);
		long softirq = Long.parseLong(toks[8]);

		long currentLoad = user + nice + system + iowait + irq + softirq;
		long currentTotal = currentLoad + currentIdle;

		double util = ((((double) (currentLoad - prevAllCoresLoad)) / (currentTotal - prevAllCoresTotal)) * 100);

		prevAllCoresLoad = currentLoad;
		prevAllCoresTotal = currentTotal;

		return util;

	} 
	
	
	public static double[] getCPUCoresUtilisation(){
		String[] toks = getCPUCoreUtilTokens();
		double[] util = new double[NUM_CORES];
		int initialOffset = 11; //The initial offset is to skip the all cores fields
		int subsequentOffset = 10;
		
		for(int i = 0; i < NUM_CORES; i++){
			int start = initialOffset + (i * subsequentOffset);
			
			long user = Long.parseLong(toks[start + 1]);
			long nice = Long.parseLong(toks[start + 2]);
			long system = Long.parseLong(toks[start + 3]);
			long currentIdle = Long.parseLong(toks[start + 4]);
			long iowait = Long.parseLong(toks[start + 5]);
			long irq = Long.parseLong(toks[start + 6]);
			long softirq = Long.parseLong(toks[start + 7]);

			long currentLoad = user + nice + system + iowait + irq + softirq;
			long currentTotal = currentLoad + currentIdle;

			util[i] = ((((double) (currentLoad - prevCoreLoad[i])) / (currentTotal - prevCoreTotal[i])) * 100);

			prevCoreLoad[i] = currentLoad;
			prevCoreTotal[i] = currentTotal;
		}
		
		return util;
		
		
	}



}
