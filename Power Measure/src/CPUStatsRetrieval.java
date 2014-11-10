
public class CPUStatsRetrieval extends InfoRetrieval{
	private static final String POWER_FILE_A7 = "/sys/bus/i2c/drivers/INA231/4-0045/sensor_W";
	private static final String POWER_FILE_A15 = "/sys/bus/i2c/drivers/INA231/4-0040/sensor_W";
	private static final String CPU_FREQ_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
	
	private static final String COMMAND_A7_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_A7);
	private static final String COMMAND_A15_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_A15);
	private static final String COMMAND_CPU_FREQ = String.format(COMMAND_SHELL_FORMAT, CPU_FREQ_FILE);
	
	
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
	
	
}
