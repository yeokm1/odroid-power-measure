import java.util.List;


public class FPSRetrieval extends InfoRetrieval {
	private static final String FPS_DATA = "dumpsys SurfaceFlinger --latency SurfaceView";
	public static final String COMMAND_FPS = "adb shell " + FPS_DATA;
	
	public static final int MAX_FPS_ALLOWED = 60;
	public static final int NO_FPS_CALCULATED = -1;
	
	
	//Returns NO_FPS_CALCULATED if no value
	public static int getFPS(double timeIntervalNanoSeconds){

		try{
			List<String> output = runCommandAndGetOutputAsLines(COMMAND_FPS);

			if(output.size() == 0){
				return NO_FPS_CALCULATED;
			}

			//First line is not used
			
			int indexOfLastLine = output.size() - 1;

			String lastLine = output.get(indexOfLastLine);
			String[] split = splitLine(lastLine);
			String lastFrameFinishTimeStr = split[2];

			double lastFrameFinishTime = Double.parseDouble(lastFrameFinishTimeStr);
			int frameCount = 0;
			


			for(int i = 1; i <= indexOfLastLine ; i++){
				String[] splitted = splitLine(output.get(i));
				String thisFrameFinishTimeStr = splitted[2];
				double thisFrameFirstTime = Double.parseDouble(thisFrameFinishTimeStr);
				if((lastFrameFinishTime - thisFrameFirstTime) <= timeIntervalNanoSeconds){
					frameCount++;
				}

			}

			if(frameCount > MAX_FPS_ALLOWED){
				return MAX_FPS_ALLOWED;
			} else if(frameCount == 1){
				return NO_FPS_CALCULATED;
			} else{
				return frameCount;
			}
		}catch (Exception e){
			return NO_FPS_CALCULATED;
		}
	}
	
	private static String[] splitLine(String input){
		String[] result = input.split("\t");
		return result;
	}
	
	
}
