package com.sim.cit;
public class SensorCalibration {
   static {
      System.loadLibrary("sensor_cal");
   }

 public static native void init_native();
 public static native void finalize_native();
 public static native int doCalibration(String type);
}
