package com.sim.cit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;
/**
 * Test USB/UART helper class
 * @author chaichuanfa
 * 20140928
 */
public class NodeHelper {

    private static final String TAG = "NodeHelper";
    private static FileOutputStream wrt;
    private static FileInputStream fis;
    //USB
    public static final byte[] DEVICE_TYPE_USB = { '4' };  //USB
    public static final byte[] FIRST_SWITCH_POWER_ON = { '2','-','1','-','1' };   //first way power on 5V
    public static final byte[] SECOND_SWITCH_POWER_ON = { '3','-','1','-','1' };   //second way power on 5V
    public static final byte[] FIRST_SWITCH_POWER_OFF = { '2','-','0','-','1' };   //first way power off
    public static final byte[] SECOND_SWITCH_POWER_OFF = { '3','-','0','-','1' };   //second way power off
    //UART
    public static final byte[] DEVICE_TYPE_UART = { '2' };  //UART
    public static final byte[] UART_POWER_ON = { '1','-','1','-','1' };   //UART power on 3.3V
    public static final byte[] UART_POWER_OFF = { '1','-','0','-','1' };   //UART power off
    
    public static final String UART_NODE = "/sys/class/switch/xpand-sim/power_3_state"; //judge 3.3V
    public static final byte[] DEVICE_TYPE_OTG = { '5' };
    
    /**
     * Enable USB
     * write success when return true
     */
    public static boolean writeUSBNode() {
        boolean ret = true;
        try {
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_power");
            wrt.write(FIRST_SWITCH_POWER_ON);
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_state");
            wrt.write(DEVICE_TYPE_USB);
            
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_power");
            wrt.write(SECOND_SWITCH_POWER_ON);
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_state");
            wrt.write(DEVICE_TYPE_USB);
            wrt.close();
        } catch (FileNotFoundException e) {
            ret = false;
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            ret = false;
            Log.e(TAG, e.toString());
        }
        return ret;
    }
    
    /**
     * Enable UART
     * write success when return true
     */
    public static boolean writeUARTNode() {
        boolean ret = true;
        try {
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_power");
            wrt.write(UART_POWER_ON);
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_state");
            wrt.write(DEVICE_TYPE_UART);
            wrt.close();
        } catch (FileNotFoundException e) {
            ret = false;
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            ret = false;
            Log.e(TAG, e.toString());
        }
        return ret;
    }
    
    //return true when disable USB
    public static boolean disableUSB () {
        boolean ret = true;
        try {
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_power");
            wrt.write(FIRST_SWITCH_POWER_OFF);
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_state");
            wrt.write(DEVICE_TYPE_USB);
            
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_power");
            wrt.write(SECOND_SWITCH_POWER_OFF);
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_state");
            wrt.write(DEVICE_TYPE_USB);
            wrt.close();
        } catch (FileNotFoundException e) {
            ret = false;
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            ret = false;
            Log.e(TAG, e.toString());
        }
        return ret;
    }

    //return true when disable UART
    public static boolean disableUART () {
        boolean ret = true;
        try {
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_power");
            wrt.write(UART_POWER_OFF);
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_state");
            wrt.write(DEVICE_TYPE_UART);
            wrt.close();
        } catch (FileNotFoundException e) {
            ret = false;
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            ret = false;
            Log.e(TAG, e.toString());
        }
        return ret;
    }
    
    /**
     * @return 
     *   0 : power on 3.3V
     *   1 : power off 3.3V
     * null: read fail
     */
    public static String readUARTNode () {
        String ret = null;
        try {
            fis= new FileInputStream(UART_NODE);
            fis.available();
            byte [] buffer = new byte[2];
            fis.read(buffer);
            fis.close();
            ret =(new String(buffer)).substring(0, 1);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return ret;
    }

    public static boolean writeOTG() {
        boolean ret = true;
        try {
            wrt = new FileOutputStream("/sys/class/switch/xpand-sim/switch_state");
            wrt.write(DEVICE_TYPE_OTG);
            wrt.close();
        } catch (FileNotFoundException e) {
            ret = false;
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            ret = false;
            Log.e(TAG, e.toString());
        }
        return ret;
    }
    
}
