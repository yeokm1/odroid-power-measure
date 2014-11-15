import jssc.SerialPort;
import jssc.SerialPortException;


public class ExternalPowerRetrieval {

	private static SerialPort port;
	private static double powerSoFar;
	
	public static boolean startSystemPowerRetrieval(String comPort){
		port = createSerialPort(comPort, new PowerResults());
		if(port == null){
			return false;
		} else {
			return true;
		}
	}
	
	public static void stopSystemPowerRetrieval(){
		if(port != null){
			try {
				port.closePort();
			} catch (SerialPortException e) {
			}
			port = null;
		}

	}
	
	public static double getPower(){
		return powerSoFar;
	}
	
	
	private static SerialPort createSerialPort(String comPortName, SerialNewResultListener listener){
		SerialPort serialPort = new SerialPort(comPortName);
		listener.setPort(serialPort);
		try {
			serialPort.openPort();
			serialPort.addEventListener(listener);
			serialPort.setParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			return serialPort;
		} catch (SerialPortException e) {
			return null;
		}
	}
	
	
	static class PowerResults extends SerialNewResultListener{

		@Override
		public void latestStringResult(String result) {
			String[] splitted = result.split(" ");
			if(splitted.length == 5){
				try{
					powerSoFar = Double.parseDouble(splitted[0]);
				} catch(NumberFormatException e){
					//Ignore
				}
			}
			
		}
		
	}
	
	
	
}
