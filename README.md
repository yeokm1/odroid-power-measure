odroid-power-measure
====================

A Java program that measure total power usage of Odroid-XU.


##Usage

```bash
#To measure power use over 5 seconds
java -jar powermeasure.jar 5000
```

Outputs current power usage in watts and final total power use in Joules.

##Sampling rate
Sample rate is 1/second.

##Dependencies
The `adb shell` command.

##Files used
A7 CPU: "/sys/bus/i2c/drivers/INA231/4-0045/sensor_W"  
A15 CPU: "/sys/bus/i2c/drivers/INA231/4-0040/sensor_W"  
Memory: "/sys/bus/i2c/drivers/INA231/4-0041/sensor_W"  
GPU: "/sys/bus/i2c/drivers/INA231/4-0044/sensor_W"  
