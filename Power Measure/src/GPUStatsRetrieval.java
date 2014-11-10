
public class GPUStatsRetrieval extends InfoRetrieval {
	private static final String POWER_FILE_GPU = "/sys/bus/i2c/drivers/INA231/4-0044/sensor_W";
	private static final String COMMAND_GPU_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_GPU);
	
	public static double getGPUPower(){
		double gpuPower = getPowerFromCommand(COMMAND_GPU_POWER);
		return gpuPower;
	}
	
}
