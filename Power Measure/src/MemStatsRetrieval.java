
public class MemStatsRetrieval extends InfoRetrieval {
	private static final String POWER_FILE_MEM = "/sys/bus/i2c/drivers/INA231/4-0041/sensor_W";
	private static final String COMMAND_MEM_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_MEM);
	
	public static double getMemPower(){
		double gpuPower = getValueFromCommand(COMMAND_MEM_POWER);
		return gpuPower;
	}
	
	
}
