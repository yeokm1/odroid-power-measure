odroid-power-measure
====================

A Java program that dynamically shows FPS, CPU/GPU frequency and power of Odroid-XU.


##Usage

Go to Settings -> Developer Options. Enable "Show CPU usage" option.

```bash
java -jar powermeasure.jar [n] [-freq] [-fps] [-power] [-chart] [extpower:(com port name)]
n: number of samples to take at once/second (>=0)"
-freq: Don't poll for frequency
-fps: Don't poll for FPS
-util: Don't poll for utilisation
-power: Don't poll for power
-chart: Don't show chart
extpower:(com port name) : Read external system power from COM Port. Requires power poll to be active

```

Outputs current power usage in watts and final total power use in Joules. Current FPS is shown. Current utilisation is shown. At the end, average FPS, minimum, maximum and standard deviation are shown.

##Sampling rate
Sample rate is 1/second.

##Dependencies
The `adb shell` command.

##Files used
A7 CPU Power: "/sys/bus/i2c/drivers/INA231/4-0045/sensor_W"  
A15 CPU Power: "/sys/bus/i2c/drivers/INA231/4-0040/sensor_W"  
Memory Power: "/sys/bus/i2c/drivers/INA231/4-0041/sensor_W"  
GPU Power: "/sys/bus/i2c/drivers/INA231/4-0044/sensor_W"  

CPU Freq: "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"  
GPU Freq: "/sys/devices/platform/pvrsrvkm.0/sgx_dvfs_cur_clk"

CPU Util: "/proc/stat"  
GPU Util: "/sys/module/pvrsrvkm/parameters/sgx_gpu_utilization"

##FPS
FPS command: "dumpsys SurfaceFlinger --latency SurfaceView"

##External system power
This has to be used in conjuction with my [Arduino hardware tool](https://github.com/yeokm1/power_measure_tool).
