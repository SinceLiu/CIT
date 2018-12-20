package com.sim.cit.testitem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


import com.sim.cit.CITTestHelper;
import com.sim.cit.R;
import com.sim.cit.TestActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Wlan extends TestActivity implements OnClickListener{
	private WifiManager wm;
	private ProgressDialog mProgressDialog;
	IntentFilter intentFilter;
	private Button discovery_wifipoint_button;

    private boolean is24g = false;
    private boolean is5g = false;
    private TextView text_wifi_24g;
    private TextView text_wifi_5g;
    private static final int START_SCAN_WLAN = 123;
    private static final int GET_SCAN_RESULTS = 124;
    private TextView text_wifi_info;
    private Button passButton;
    private boolean isExit = false;
    private boolean stopScan = false;
    List<ScanResult> scanResults = new ArrayList<ScanResult>();
    CITTestHelper application ;

    //add by hwj20170422
    private ListView mWifiListView;

    private WifiAdapter mAdapter;
    private MyHandler mHandler;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.wifiseting_page;
        super.onCreate(savedInstanceState);
        application = (CITTestHelper) getApplication();
        mHandler = new MyHandler(this);
        passButton = (Button) findViewById(R.id.btn_pass);
        passButton.setEnabled(false);
        wm = (WifiManager)application.getSystemService(Context.WIFI_SERVICE);
        discovery_wifipoint_button=(Button)findViewById(R.id.btn_discovery_wifipoint);
        discovery_wifipoint_button.setOnClickListener(this);
        discovery_wifipoint_button.setText("Search ConnectPoints");
        text_wifi_24g=(TextView) findViewById(R.id.tv_wifi_24g);
        text_wifi_5g=(TextView) findViewById(R.id.tv_wifi_5g);


        //add by hwj20170422
        mWifiListView = (ListView) findViewById(R.id.wifi_list);
        text_wifi_24g.setVisibility(View.GONE);
        text_wifi_5g.setVisibility(View.GONE);

        mAdapter = new WifiAdapter();
        mWifiListView.setAdapter(mAdapter);

        //text_wifi_info=(TextView) findViewById(R.id.tv_wifi_info);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.wait));
        //add for show wifi listView quickly by lxx 20180727
        if(application.getScanResults()!=null && application.getScanResults().size()>0){
            passButton.setEnabled(true);
            scanResults = application.getScanResults();
            sortByLevel(scanResults);
            mAdapter.notifyDataSetChanged();
        }else {
            mProgressDialog.show();
        }
        intentFilter=new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        this.registerReceiver(wifiReceiver,intentFilter);
        Log.i("progress", "onCreate enable="+wm.isWifiEnabled());
        if (wm.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            if (wm.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                Log.i("progress", "WifiManager.WIFI_STATE_DISABLING");
                discovery_wifipoint_button.setText("OPEN WIFI");
            }else {
                wm.setWifiEnabled(true);
                Log.i("progress", "WifiManager.setWifiEnabled(true)");
            }
        }else if(wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
            //wm.startScan();
            if(scanResults==null){
                mHandler.removeMessages(START_SCAN_WLAN);
                mHandler.sendMessage(mHandler.obtainMessage(START_SCAN_WLAN));
            }
        }
    }
    private BroadcastReceiver wifiReceiver=new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                if (wm.isWifiEnabled()) {
                    //wm.startScan();
                    mHandler.removeMessages(START_SCAN_WLAN);
                    mHandler.sendMessage(mHandler.obtainMessage(START_SCAN_WLAN));
                }else {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
                Log.i("progress", "WifiManager.WIFI_STATE_CHANGED_ACTION enable="+wm.isWifiEnabled());
            }
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.endsWith(action)) {
                scanResults=wm.getScanResults();

                //add by hwj20170422
                sortByLevel(scanResults);
                mAdapter.notifyDataSetChanged();

                mHandler.removeMessages(GET_SCAN_RESULTS);
                mHandler.sendMessage(mHandler.obtainMessage(GET_SCAN_RESULTS));
                discovery_wifipoint_button.setText("Search ConnectPoints");
                Log.i("progress", "WifiManager.SCAN_RESULTS_AVAILABLE_ACTION result="+scanResults.size());
            }
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        isExit = false;
    }

	@Override
	protected void onPause() {
        isExit = true;
        mHandler.removeMessages(START_SCAN_WLAN);
		if (mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		super.onPause();
	}


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (wm.isWifiEnabled()) {
//            wm.setWifiEnabled(false);
//            Log.i("progress", "onDestroy_WifiManager.setWifiEnabled(false)");
//        }
        unregisterReceiver(wifiReceiver);
        mAdapter = null;
        mHandler.removeCallbacksAndMessages(null);  //把消息对象从消息队列移除
    }

    private static class MyHandler extends Handler{
        private WeakReference<Wlan> reference;
        public MyHandler(Wlan activity) {
            reference = new WeakReference<Wlan>(activity);//这里传入activity的上下文
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Wlan wlanActivity = reference.get();
            switch (msg.what) {
                case START_SCAN_WLAN:
                    if (wlanActivity.isExit) {
                        break;
                    }
//                    if ((!mProgressDialog.isShowing()) && (!isExit) && (!stopScan)) {
//                        mProgressDialog.show();
//                    }
                    wlanActivity.wm.startScan();
                    sendEmptyMessageDelayed(wlanActivity.START_SCAN_WLAN, 10 * 1000);
                    break;
                case GET_SCAN_RESULTS:
                    if (wlanActivity.isExit) {
                        break;
                    }
                    int size = wlanActivity.scanResults.size();
                    if (size > 0) {
                    /*text_wifi_info.setText(getString(R.string.wifisettins_page)+"\n"+
                    scanResults.get(0).toString()
                    );*/
                    for (ScanResult sc : wlanActivity.scanResults) {
//                        if (sc.is5GHz()) {
                            wlanActivity.is5g = true;
                            wlanActivity.text_wifi_5g.setText(wlanActivity.getString(R.string.wifisettins_5g) + "" + sc.SSID);
//                        }
//                        if (sc.is24GHz()) {
                            wlanActivity.is24g = true;
                            wlanActivity.text_wifi_24g.setText(wlanActivity.getString(R.string.wifisettins_24g) + "" + sc.SSID);
//                        }
                    }
                    Log.i("cit_wifi", "is 5g :[" + wlanActivity.is5g + "] and is 2.4g :[" + wlanActivity.is24g + "]");
                    if (wlanActivity.is5g && wlanActivity.is24g) {
                        wlanActivity.stopScan = true;
                        wlanActivity.passButton.setEnabled(true);
                        if (wlanActivity.mProgressDialog.isShowing()) {
                            wlanActivity.mProgressDialog.dismiss();
                        }
                    }
                }
                break;
                /*Intent i = new Intent();
                i.setClassName("com.android.settings",
                "com.android.settings.wifi.WifiSettings");
                startActivity(i);*/
            }
        }
    };
    @Override
    public void onClick(View v) {
        mProgressDialog.show();
        stopScan = false;
        if (wm.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            wm.setWifiEnabled(true);
        }else {
            //wm.startScan();
            mHandler.removeMessages(START_SCAN_WLAN);
            mHandler.sendMessage(mHandler.obtainMessage(START_SCAN_WLAN));
        }
    }

    private class WifiAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return scanResults.size();
		}

		@Override
		public Object getItem(int position) {
			return scanResults.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.wifi_item, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.wifi_item_name);
                viewHolder.level = (TextView) convertView.findViewById(R.id.tv_wifi_item_level);
                viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.pb_wifi_item_level);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ScanResult result = scanResults.get(position);
            if (result != null) {
                Log.v("Wlan", "position = " + position + "-----wifi name is " + result.SSID);
                viewHolder.name.setText(result.SSID);
                viewHolder.level.setText(result.level + "dB");
                viewHolder.progressBar.setProgress(getProgress(result.level));
            }
            return convertView;
        }

        public class ViewHolder{
            TextView name;
            TextView level;
            ProgressBar progressBar;
        }

    }

    private int getProgress(int level){
        return (int)((level + 100.0)/0.6);
    }

    private void sortByLevel(List<ScanResult> list) {
        int length = list.size();
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                if (Math.abs(list.get(i).level) > Math.abs(list.get(j).level)) {
                    ScanResult temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }
}
