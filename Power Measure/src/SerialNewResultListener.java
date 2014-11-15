import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;


public abstract class SerialNewResultListener implements SerialPortEventListener {

	private SerialPort serialPort;
	private StringBuilder message = new StringBuilder();

	public void setPort(SerialPort port){
		this.serialPort = port;
	}

	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) {
		if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {//If data is available
			try {
				byte buffer[] = serialPort.readBytes();
				for (byte b: buffer) {
					//Keep reading until encounter newline characters
					if (b == '\r' || b == '\n') {
						if(message.length() > 0){
							latestStringResult(message.toString());
							message.setLength(0);
						}
					} else {
						message.append((char)b);
					}
				}                
			}  catch (SerialPortException ex) {
			}
		}
	}

	public abstract void latestStringResult(String result);
}
