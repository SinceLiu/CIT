package com.sim.cit;

import java.util.ArrayList;



import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FirstList extends ListActivity {
	private static final int CIT1 = 0;
	private static final int CIT2 = CIT1 + 1;
	private static final int CIT3 = CIT2 + 1;
	
	private Context context;
	private String testTypeStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //屏幕常亮,不修改系统休眠时间
        
        context = getApplicationContext();
        ArrayList<String> mainList = new ArrayList<String>();
      //20121206 add for change the view by lvhongshan start        
        Intent testTypeIntent=getIntent();
        Log.i("lvhongshan",testTypeIntent.getStringExtra(CITTestHelper.EXTRA_KEY_TEST_TYPE));
	    if(CITTestHelper.EXTRA_VALUE_TEST_TYPE_PCB.equals(testTypeIntent.getStringExtra(CITTestHelper.EXTRA_KEY_TEST_TYPE))){
	    	mainList.add(getString(R.string.title_pcba_auto));
            mainList.add(getString(R.string.title_pcba_sensorAuto));
            mainList.add(getString(R.string.title_WBGauto));
	    }else if(CITTestHelper.EXTRA_VALUE_TEST_TYPE_SUBPCB.equals(testTypeIntent.getStringExtra(CITTestHelper.EXTRA_KEY_TEST_TYPE))){
            //mainList.add("B1");
            mainList.add("B1");
            }else{
	    	mainList.add(getString(R.string.title_complete_auto));
			mainList.add(getString(R.string.title_complete_sensorAuto));
			mainList.add(getString(R.string.title_WBGauto));
	    }
		//20121206 add for change the view by lvhongshan end
        
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mainList);
		getListView().setAdapter(adapter);
		
		testTypeStr = getIntent().getStringExtra(CITTestHelper.EXTRA_KEY_TEST_TYPE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(context, SecondList.class);
		i.putExtra(CITTestHelper.EXTRA_KEY_TEST_TYPE, testTypeStr);
		switch (position) {
		case CIT1:
			i.putExtra(CITTestHelper.EXTRA_KEY_TO_TESTLIST, CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT1);
			break;
		case CIT2:
			i.putExtra(CITTestHelper.EXTRA_KEY_TO_TESTLIST, CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT2);
			break;
		case CIT3:
			i.putExtra(CITTestHelper.EXTRA_KEY_TO_TESTLIST, CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT3);
			break;
		default:
			break;
		}
		startActivity(i);
		super.onListItemClick(l, v, position, id);
	}
	
}
