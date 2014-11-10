
public class GPUStatsRetrieval extends InfoRetrieval {
	private static final String POWER_FILE_GPU = "/sys/bus/i2c/drivers/INA231/4-0044/sensor_W";
	private static final String GPU_FREQ_FILE = "/sys/devices/platform/pvrsrvkm.0/sgx_dvfs_cur_clk";
	
	private static final String COMMAND_GPU_POWER = String.format(COMMAND_SHELL_FORMAT, POWER_FILE_GPU);
	private static final String COMMAND_GPU_FREQ = String.format(COMMAND_SHELL_FORMAT, GPU_FREQ_FILE);
	
	public static double getGPUPower(){
		double gpuPower = getValueFromCommand(COMMAND_GPU_POWER);
		return gpuPower;
	}
	
	public static double getGPUFreq(){
		double gpuFreq = getValueFromCommand(COMMAND_GPU_FREQ);
		return gpuFreq;
	}
	
}
