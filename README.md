odroid-power-measure
====================

A Java program that measure total power usage and average FPS of Odroid-XU.


##Usage

Go to Settings -> Developer Options. Enable "Show CPU usage" option.

```bash
#To measure power use over 10 samples
java -jar powermeasure.jar 10
```

Outputs current power usage in watts and final total power use in Joules. Current FPS is shown. At the end, average FPS, minimum, maximum and standard deviation are shown.

##Sampling rate
Sample rate is 1/second.

##Dependencies
The `adb shell` command.

##Files used
A7 CPU: "/sys/bus/i2c/drivers/INA231/4-0045/sensor_W"  
A15 CPU: "/sys/bus/i2c/drivers/INA231/4-0040/sensor_W"  
Memory: "/sys/bus/i2c/drivers/INA231/4-0041/sensor_W"  
GPU: "/sys/bus/i2c/drivers/INA231/4-0044/sensor_W"  

##FPS
FPS command: "dumpsys SurfaceFlinger --latency SurfaceView"
