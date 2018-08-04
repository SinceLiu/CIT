package com.sim.cit;

public class FMRadio {
	static {
		System.loadLibrary("CITFMRadio");
	}

	public static native int init();

	public static native int GetCurFreq();

	public static native boolean setVolume(int nVolume);

	public static native boolean forceMono(boolean bMono);

	public static native boolean setMute(boolean bMute);

	public static native int getRssi();

	public static native boolean Tune(int nFreq);

	public static native int searchFreq(int nfreq);

	public static native boolean close();
}
