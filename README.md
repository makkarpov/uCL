μCL — CLion for Microcontrollers
================================

![image](https://user-images.githubusercontent.com/1329592/29126009-e943f5a6-7d25-11e7-824a-89821f566638.png)

**μCL** is a tiny CLion plugin that brings CLion and OpenOCD together. Upload firmware and start debugging just in one click!

**Get it from [JetBrains plugin repository](https://plugins.jetbrains.com/plugin/9915--cl)**

**Tested on STM32F407**

Getting started
---------------

1. Set path to `arm-none-eabi-gdb` executable as global GDB path
2. Set path to `openocd` executable in plugin settings
3. Copy appropriate OpenOCD script to the parameters of launch configuration
4. Just click **Run** (upload firmware without debugging) or **Debug** and enjoy.
