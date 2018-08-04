package com.sim.cit.testitem;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.sim.cit.CommonDrive;
import android.widget.Toast;

import com.sim.cit.CITTestHelper;
import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class Keypad6700 extends TestActivity {
	private Button btnPass;
	private List<TextView> keyList;
	private List<String> supportKeyList;
	private Context context;
	private CITTestHelper application;
	private int count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutId = R.layout.keypad6700;
		super.onCreate(savedInstanceState);

		context = getApplicationContext();
		application = (CITTestHelper)getApplication();
		supportKeyList = application.getKeypadList();

		btnPass = super.btnPass;
		keyList = new ArrayList<TextView>();
		keyList.add((TextView)findViewById(R.id.txt_back));
		keyList.add((TextView)findViewById(R.id.txt_home));
		keyList.add((TextView)findViewById(R.id.txt_menu));
		keyList.add((TextView)findViewById(R.id.txt_up));
		keyList.add((TextView)findViewById(R.id.txt_down));
		keyList.add((TextView)findViewById(R.id.txt_left));
		keyList.add((TextView)findViewById(R.id.txt_right));
		keyList.add((Button)findViewById(R.id.txt_ok));
		keyList.add((TextView)findViewById(R.id.txt_answer));
		keyList.add((TextView)findViewById(R.id.txt_hangup));		
		keyList.add((TextView)findViewById(R.id.txt_one));
		keyList.add((TextView)findViewById(R.id.txt_two));
		keyList.add((TextView)findViewById(R.id.txt_three));
		keyList.add((TextView)findViewById(R.id.txt_four));
		keyList.add((TextView)findViewById(R.id.txt_five));
		keyList.add((TextView)findViewById(R.id.txt_six));
		keyList.add((TextView)findViewById(R.id.txt_seven));
		keyList.add((TextView)findViewById(R.id.txt_eight));
		keyList.add((TextView)findViewById(R.id.txt_nine));
		keyList.add((TextView)findViewById(R.id.txt_zero));
		keyList.add((TextView)findViewById(R.id.txt_star));
		keyList.add((TextView)findViewById(R.id.txt_pound));
		keyList.add((TextView)findViewById(R.id.txt_headset));
		keyList.add((TextView)findViewById(R.id.txt_ptt));
		keyList.add((TextView)findViewById(R.id.txt_camera));
		keyList.add((TextView)findViewById(R.id.txt_value_up));
		keyList.add((TextView)findViewById(R.id.txt_value_down));
		keyList.add((TextView)findViewById(R.id.txt_sos));
		
		btnPass.setEnabled(false);
		/*for (int i = 0; i < keyList.size(); i++) {
			TextView view = keyList.get(i);
			if (i < supportKeyList.size()) {
				view.setText(supportKeyList.get(i));
			}else{
				view.setVisibility(View.GONE);
			}
		}*/

		registerReceiver(homeKeyReceiver, new IntentFilter("android.sim.home_key_for_cit"));
                CommonDrive.buttonlightControl(1);
                Toast.makeText(getApplicationContext(), R.string.keypad_lightison, 1000).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(homeKeyReceiver);
                CommonDrive.buttonlightControl(0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			changeColor("back");
			break;
		case KeyEvent.KEYCODE_HOME:
			changeColor("home");
			break;
		case KeyEvent.KEYCODE_MENU:
			changeColor("menu");
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			changeColor("up");
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			changeColor("down");
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			changeColor("left");
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			changeColor("right");
			break;
		case KeyEvent.KEYCODE_CALL:
			changeColor("answer");
			break;
		case KeyEvent.KEYCODE_ENDCALL:
			changeColor("hangup");
			break;
		case KeyEvent.KEYCODE_1:
			changeColor("one");
			break;
		case KeyEvent.KEYCODE_2:
			changeColor("two");
			break;
		case KeyEvent.KEYCODE_3:
			changeColor("three");
			break;
		case KeyEvent.KEYCODE_4:
			changeColor("four");
			break;
		case KeyEvent.KEYCODE_5:
			changeColor("five");
			break;
		case KeyEvent.KEYCODE_6:
			changeColor("six");
			break;
		case KeyEvent.KEYCODE_7:
			changeColor("seven");
			break;
		case KeyEvent.KEYCODE_8:
			changeColor("eight");
			break;
		case KeyEvent.KEYCODE_9:
			changeColor("nine");
			break;
		case KeyEvent.KEYCODE_0:
			changeColor("zero");
			break;
		case KeyEvent.KEYCODE_STAR:
			changeColor("star");
			break;
		case KeyEvent.KEYCODE_POUND:
			changeColor("pound");
			break;
		case KeyEvent.KEYCODE_HEADSETHOOK:
			changeColor("handset");
			break;
		/*case KeyEvent.KEYCODE_PTT:
			changeColor("ptt");
			break;*/
		case KeyEvent.KEYCODE_CAMERA:
			changeColor("camera");
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			changeColor("volUp");
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			changeColor("volDown");
			break;
		//case KeyEvent.KEYCODE_A:
		case 224:
			changeColor("sos");
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_ENTER){
			changeColor("ok");
		}
		return true;
	}

	private void changeColor(String keyName){
		for (int i = 0; i < supportKeyList.size(); i++) {
			if (keyName.equals(supportKeyList.get(i))) {
				TextView view = keyList.get(i);
				if (view.getTag() == null) {
					view.setTag(true);
					count++;
					if (count == supportKeyList.size()) {
						btnPass.setEnabled(true);
					}
				}
				if ((Boolean)view.getTag()) {
					view.setBackgroundResource(R.color.green);
					view.setTag(false);
				}else{
					view.setBackgroundResource(R.color.gray);
					view.setTag(true);
				}
				break;
			}
		}
	}

	private BroadcastReceiver homeKeyReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			changeColor("home");
		}
	};
}
