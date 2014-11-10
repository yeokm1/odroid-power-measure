
public class InitADB extends InfoRetrieval {
	private static final String COMMAND_INIT_ADB = "adb devices";
	
	public static void initADB(){
		runCommandAndGetOutput(COMMAND_INIT_ADB);
	}

	
}
