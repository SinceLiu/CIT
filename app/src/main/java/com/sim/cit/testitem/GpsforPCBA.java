package com.sim.cit.testitem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
//Modify for passButton clickable when locate success by songguangyu 20140220 start
import android.widget.Button;
//Modify for passButton clickable when locate success by songguangyu 20140220 end

import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
public class GpsforPCBA extends TestActivity {

    private static final int MAX_SATELITE_COUNT = 50;
    private static final String TAG_GPS = "GPSTest";
    private static String strGpsFilePaht = "";
    private LocationManager m_mgr;
    private Location m_location;
    private GpsStatus m_gpsStatus;
    GpsInfo mGpsInfo[] = new GpsInfo[MAX_SATELITE_COUNT];
    private int mStateliteCount;
    boolean m_isGpsOpen = false;
    TextView tvSatelite[];
    private String mStrSnr;
    private String mStrPrn;
    private String mStrAzimuth;
    private String mStrElevation;
    private TextView mtv_Testtime;
    private TextView mtv_LocationSuccess;
    private int oldBrightValue;
    private boolean mStartLogGpsData = false;
    Timer st_timer ;
    private int mSecond = 0;
    st_TimerTask timerTask;
    //Modify for passButton clickable when locate success by songguangyu 20140220 start
    private Button passButton;
    private int mLocatetime = 0;
    //Modify for passButton clickable when locate success by songguangyu 20140220 end
    //File gpsFile;
    Handler hGpsHand = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mtv_Testtime.setText("" + mSecond);
        };
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.gpsforpcba;
        super.onCreate(savedInstanceState);
        strGpsFilePaht="/data/GpsData.txt";
        initAllControl();
        //if(isSDcardexist()){
        boolean bdeleteFile = deleteGpsDataFile(strGpsFilePaht);
        Log.d(TAG_GPS,"The bdeleteFile = " + bdeleteFile);
        //}
        m_mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(m_mgr==null)
        {
            Log.i("lvhongshan_gps", "LocationManager is null");

        }
        else
            Log.i("lvhongshan_gps", "LocationManager is not null");

        if (!m_mgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            m_isGpsOpen = true;
            openGPS();
        }
        mtv_LocationSuccess.setText(R.string.gps_success);

    }
    public boolean deleteGpsDataFile(String filename){
        boolean bDelete = true;
        File file = new File(filename);
        if(file.exists()){
            bDelete = file.delete();
        }else{
            return false;
        }
        return bDelete;
    }
    class st_TimerTask extends TimerTask {

        public void run() {
            mSecond ++;
            hGpsHand.sendEmptyMessage(0);
        }

    }

    private synchronized void setLogGpsData(boolean start) {
        mStartLogGpsData  = start;
    }

    private synchronized boolean getLogGpsData() {
        return mStartLogGpsData;
    }

    private void initAllControl() {

        for(int i = 0;i < MAX_SATELITE_COUNT;i ++){
            mGpsInfo[i] = new GpsInfo();
        }
        tvSatelite = new TextView[12];

        tvSatelite[0] = (TextView) findViewById(R.id.tv_st_1);
        tvSatelite[1] = (TextView) findViewById(R.id.tv_st_2);
        tvSatelite[2] = (TextView) findViewById(R.id.tv_st_3);
        tvSatelite[3] = (TextView) findViewById(R.id.tv_st_4);
        tvSatelite[4] = (TextView) findViewById(R.id.tv_st_5);
        tvSatelite[5] = (TextView) findViewById(R.id.tv_st_6);
        tvSatelite[6] = (TextView) findViewById(R.id.tv_st_7);
        tvSatelite[7] = (TextView) findViewById(R.id.tv_st_8);
        tvSatelite[8] = (TextView) findViewById(R.id.tv_st_9);
        tvSatelite[9] = (TextView) findViewById(R.id.tv_st_10);
        tvSatelite[10] = (TextView) findViewById(R.id.tv_st_11);
        tvSatelite[11] = (TextView) findViewById(R.id.tv_st_12);

        mtv_LocationSuccess = (TextView)findViewById(R.id.tv_st_success);
        mtv_Testtime = (TextView)findViewById(R.id.tv_st_time);

        mStrSnr = getString(R.string.gps_st_info_snr);
        mStrPrn = getString(R.string.gps_st_info_prn);
        mStrAzimuth = getString(R.string.gps_st_info_azimuth);
        mStrElevation = getString(R.string.gps_st_info_elevation);
        //Modify for passButton clickable when locate success by songguangyu 20140220 start
        passButton=(Button)findViewById(R.id.btn_pass);
        passButton.setEnabled(false);
        //Modify for passButton clickable when locate success by songguangyu 20140220 end
    }

    @Override
    protected void onResume() {
        super.onResume();

        m_mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
                locationListener);
        timerTask=new st_TimerTask();
        st_timer=new Timer();
        st_timer.schedule(timerTask, 100, 1000);

        /*if (!m_mgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS provider is disable", 3000).show();
        }*/
        /*boolean bsucess = m_mgr.addGpsStatusListener(statusListener);

        Log.e(TAG_GPS, "Add the statusListner is" + bsucess);

        if (!bsucess) {
            Toast.makeText(this, R.string.gps_open_error, 3000).show();
        }*/
        boolean bsucess = m_mgr.addNmeaListener(mNmeaListener);
        Log.e(TAG_GPS, "Add the statusListner is" + bsucess);
        if (!bsucess) {
            Toast.makeText(this, R.string.gps_open_error, 3000).show();
        }

        /*m_location = m_mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(m_location==null) {
            Log.i("lvhongshan_gps", "Location is null");
        } else {
            Log.i("lvhongshan_gps", "Location is not null");
        }*/
        //updateWithNewLocation(m_location);

    }

    private void openGPS() {
        boolean enabled = true;
        Settings.Secure.setLocationProviderEnabled(getContentResolver(),
        LocationManager.NETWORK_PROVIDER, enabled);
        Settings.Secure.setLocationProviderEnabled(getContentResolver(),
        LocationManager.GPS_PROVIDER, enabled);
    }

    private void closeGPS() {
        boolean enabled = false;
        Settings.Secure.setLocationProviderEnabled(getContentResolver(),
        LocationManager.GPS_PROVIDER, enabled);
        Settings.Secure.setLocationProviderEnabled(getContentResolver(),
        LocationManager.NETWORK_PROVIDER, enabled);

    }

    private final GpsStatus.NmeaListener mNmeaListener = new GpsStatus.NmeaListener() {

        public void onNmeaReceived(long timestamp, String nmea) {
            //if(isSDcardexist()){
            if(getLogGpsData()) {
                updateNmeaStatus(nmea);
                writeNeamDatainfile(nmea);
            }
            if (nmea != null) {
                passButton.setEnabled(true);
                tvSatelite[0].setText(nmea);
                mtv_LocationSuccess.setText("The test is successful");
            } else {
                tvSatelite[0].setText(nmea);
                mtv_LocationSuccess.setText("Unfortunately the test fails!");
            }
            
            //}else{
                //Log.d(TAG_GPS,"The sdcard is not exist");
            //}
        }
    };
    private void updateNmeaStatus(String strNmea){
        Log.d(TAG_GPS,"GPS:data = " + strNmea);
    }
    private boolean isSDcardexist(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    private boolean writeNeamDatainfile(String strNmea){
            boolean bresult = true;
        try {
            File gpsFile = new File(strGpsFilePaht);
            FileWriter fileWriter = new FileWriter(gpsFile,true);

            boolean bcanWrite = gpsFile.canWrite();
            if (bcanWrite) {
                Log.i("lvhongshan_gps", "writeNeamDatainfile is success");
                fileWriter.append(strNmea + "\r\n");
                fileWriter.flush();
            }
            fileWriter.close();

        } catch (IOException e) {
            bresult = false;
            Log.e("LOG_TAG", e.getLocalizedMessage());
        } finally {
        }

        return bresult;
    }
 
    private GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            m_gpsStatus = m_mgr.getGpsStatus(null);
            switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                int nfixTime = m_gpsStatus.getTimeToFirstFix();
                mtv_LocationSuccess.setText(R.string.gps_located);
                st_timer.cancel();
                setLogGpsData(true);
                Log.d(TAG_GPS, "GpsStatus.GPS_EVENT_FIRST_FIX the fix Time is " + nfixTime);
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                Log.d(TAG_GPS, "GpsStatus.GPS_EVENT_SATELLITE_STATUS");
                Iterable<GpsSatellite> allSatellites;
                allSatellites = m_gpsStatus.getSatellites();
                Iterator it = allSatellites.iterator();
                int iCount = 0;
                while (it.hasNext()) {
                    GpsSatellite satelite = (GpsSatellite) it.next();

                    mGpsInfo[iCount].prn = satelite.getPrn();
                    mGpsInfo[iCount].fAzimuth = satelite.getAzimuth();
                    mGpsInfo[iCount].fElevation = satelite.getElevation();
                    mGpsInfo[iCount].snr = satelite.getSnr();
                    mGpsInfo[iCount].iID = iCount;

                    iCount++;

                    Log.d(TAG_GPS, "mGpsInfo[iCount].prn is " + mGpsInfo[iCount].prn);
                    Log.d(TAG_GPS, "mGpsInfo[iCount].fAzimuth is " + mGpsInfo[iCount].fAzimuth);
                    Log.d(TAG_GPS, "mGpsInfo[iCount].fElevation" + mGpsInfo[iCount].fElevation);
                    Log.d(TAG_GPS, "mGpsInfo[iCount].snr" + mGpsInfo[iCount].snr);
                    Log.d(TAG_GPS, "mGpsInfo[iCount].iID" + mGpsInfo[iCount].iID);

                }
                mStateliteCount = iCount;
                Log.d(TAG_GPS, "the mStateliteCount is" + mStateliteCount);
                setStateliteinfo(iCount);
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                // Event sent when the GPS system has started.
                Log.d(TAG_GPS, "GpsStatus.GPS_EVENT_STARTED");
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                // Event sent when the GPS system has stopped.
                Log.d(TAG_GPS, "GpsStatus.GPS_EVENT_STOPPED");
                break;
            default:
                break;
            }
        }
    };

    private void setStateliteinfo(int validsatelite) {
        int ncount = 6;

        int bdIndex = 6;
        int gpsIndex = 0;
        for (int i = 0; i < validsatelite; i++) {
            if(mGpsInfo[i].prn < 32 && gpsIndex < ncount){
                tvSatelite[gpsIndex].setText("ID:" + (gpsIndex + 1) + "  " + mStrSnr + mGpsInfo[i].snr
                    + "  " + mStrPrn + mGpsInfo[i].prn + "\n" + mStrAzimuth + "  " +
                    + mGpsInfo[i].fAzimuth + "  " + mStrElevation +
                    + mGpsInfo[i].fElevation);
                gpsIndex ++;
            }else if(mGpsInfo[i].prn >= 32 && bdIndex < ncount * 2){
                tvSatelite[bdIndex].setText("ID:" + (gpsIndex + 1) + "  " + mStrSnr + mGpsInfo[i].snr
                    + "  " + mStrPrn + mGpsInfo[i].prn + "\n" + mStrAzimuth + "  " +
                    + mGpsInfo[i].fAzimuth + "  " + mStrElevation +
                    + mGpsInfo[i].fElevation);
                bdIndex ++;
            }
        }
        //Modify for passButton clickable when locate success by songguangyu 20140220 start
        if (validsatelite >= 4) {
            mLocatetime ++;
            passButton.setEnabled(true);
        }
        if (validsatelite >= 4 && mLocatetime <= 1) {
            Toast.makeText(this, R.string.gps_located, Toast.LENGTH_SHORT).show();
        }
        //Modify for passButton clickable when locate success by songguangyu 20140220 end
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        m_mgr.removeUpdates(locationListener);
        //m_mgr.removeGpsStatusListener(statusListener);
        m_mgr.removeNmeaListener(mNmeaListener);
        st_timer.cancel();
        /*if (m_isGpsOpen == true) {
            closeGPS();
        }
        mcd.CopyFile("/data/GpsData.txt", Environment.getExternalStorageDirectory()+"/GpsData.txt");*/
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
            Log.d(TAG_GPS, "lvhongshan the onLocationChanged is exced");
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
            Log.d(TAG_GPS, "lvhongshan the onProviderDisabled is exced");
        }

        public void onProviderEnabled(String provider) {
            Log.d(TAG_GPS, "lvhongshan the onProviderEnabled is exced");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG_GPS, "lvhongshan the onStatusChanged is exced");
        }
    };

    private void updateWithNewLocation(Location location) {
    }

    private class GpsInfo {
        int prn;
        int iID;
        private float fAzimuth;
        private float fElevation;
        private float snr;

        public GpsInfo() {
            prn = 0;
            iID = 0;
            fAzimuth = 0;
            fElevation = 0;
            snr = 0;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m_isGpsOpen == true) {
            closeGPS();
        }
        //CommonDrive.copyFile("/data/GpsData.txt", Environment.getExternalStorageDirectory()+"/GpsData.txt");
    }
}
