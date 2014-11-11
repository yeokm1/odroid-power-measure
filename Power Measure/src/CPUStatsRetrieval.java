
public class CPUStatsRetrieval extends InfoRetrieval{
	private static final String POWER_FILE_A7 = "/sys/bus/i2c/drivers/INA231/4-0045/sensor_W";
	private static final String POWER_FILE_A15 = "/sys/bus/i2c/drivers/INA231/4-0040/sensor_W";
	private static final String CPU_FREQ_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
	private static final String CPU_UTIL_FILE = "/proc/stat";

	private static final String COMMAND_A7_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_A7);
	private static final String COMMAND_A15_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_A15);
	private static final String COMMAND_CPU_FREQ = String.format(COMMAND_SHELL_FORMAT, CPU_FREQ_FILE);
	private static final String COMMAND_CPU_UTIL = String.format(COMMAND_SHELL_FORMAT, CPU_UTIL_FILE);

	private static long prevLoad = 0;
	private static long prevTotal = 0;

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

	public static double getCPUUtilisation() {

		String cpuOutput = runCommandAndGetOutput(COMMAND_CPU_UTIL);

		String[] toks = cpuOutput.split(" ");

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

		double util = ((((double) (currentLoad - prevLoad)) / (currentTotal - prevTotal)) * 100);

		prevLoad = currentLoad;
		prevTotal = currentTotal;

		return util;

	} 



}
