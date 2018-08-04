package com.sim.cit;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
//add for mark test results by songguangyu 20140220 start
import android.widget.SimpleAdapter;
import android.content.Intent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//add for mark test results by songguangyu 20140220 end

public class SecondList extends ListActivity {
	private static final String TAG = "CIT_SecondList";
	private CITTestHelper application;
	private String testTypeStr;
	private String testListStr;
	private boolean hasAutoTest;
	private boolean isStartedBySdCard;
        //add for mark test results by songguangyu 20140220 start
        private ArrayList<HashMap<String, Object>> listItems;
        private SimpleAdapter listItemAdapter;
        //add for mark test results by songguangyu 20140220 end

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
                //add for mark test results by songguangyu 20140220 start
                setContentView(R.layout.test_list);
                //add for mark test results by songguangyu 20140220 end
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        application = (CITTestHelper)getApplication();
        isStartedBySdCard = application.isStartedBySdCard();

        testTypeStr = getIntent().getStringExtra(CITTestHelper.EXTRA_KEY_TEST_TYPE);
        testListStr = getIntent().getStringExtra(CITTestHelper.EXTRA_KEY_TO_TESTLIST);

        if(isStartedBySdCard){
        	application.removeSdCardTest();
		}
        getTestList();
        application.initTestList(hasAutoTest);
        
        //modify for mark test results by songguangyu 20140220 start
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, application.getTitleList());
		getListView().setAdapter(adapter);*/
	}
        //modify for mark test results by songguangyu 20140220 end

        //add for mark test results by songguangyu 20140220 start
        @Override
        protected void onResume() {
            initListView();
            this.setListAdapter(listItemAdapter);
            super.onResume();
        }
        //add for mark test results by songguangyu 20140220 end

        //add for mark test results by songguangyu 20140220 start
	private void initListView() {
            listItems =new ArrayList<HashMap<String, Object>>();
            List<String> titleList = new ArrayList<String>();
            List<TestItem> resultLists =
                    application.getTestResultMaps().get(application.getTestMode());
            titleList = application.getTitleList();
            for( int i = 0 ; i < titleList.size() ; i++) {
                HashMap<String, Object> map =new HashMap<String, Object>();
                map.put("ItemTitle" , titleList.get(i));
                if (hasAutoTest && i == 0) {
                    map.put("ItemImage" , null);
                } else if (hasAutoTest) {
                    switch (resultLists.get(i-1).getTestResult()){
                    case CITTestHelper.TEST_RESULT_PASS:
                        map.put("ItemImage" , R.drawable.test_pass);
                        break;
                    case CITTestHelper.TEST_RESULT_FAIL:
                        map.put("ItemImage" , R.drawable.test_fail);
                        break;
                    default:
                        map.put("ItemImage" , null);
                        break;
                    }
                } else {
                    switch (resultLists.get(i).getTestResult()){
                    case CITTestHelper.TEST_RESULT_PASS:
                        map.put("ItemImage" , R.drawable.test_pass);
                        break;
                    case CITTestHelper.TEST_RESULT_FAIL:
                        map.put("ItemImage" , R.drawable.test_fail);
                        break;
                    default:
                        map.put("ItemImage" , null);
                        break;
                    }
                }
                listItems.add(map);
            }
            listItemAdapter =new SimpleAdapter(this,listItems,
                R.layout.test_list_item,
                new String[] {"ItemTitle","ItemImage"},
                new int[ ] {R.id.ItemTitle, R.id.ItemImage}
                );
        }
        //add for mark test results by songguangyu 20140220 end

	private void getTestList(){
		application.setTestMode(CITTestHelper.NOT_TEST);
		if (testTypeStr == null || testListStr == null
				|| testTypeStr.length() == 0 || testListStr.length() == 0) {
			return;
		}
		if (testTypeStr.equals(CITTestHelper.EXTRA_VALUE_TEST_TYPE_PCB)) {
			if (testListStr.equals(CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT1)) {
				Log.i(TAG, "pcb cit 1");
				hasAutoTest = true;
				application.setTestMode(CITTestHelper.PCB_CIT1);
			}else if(testListStr.equals(CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT2)){
				Log.i(TAG, "pcb cit 2");
				hasAutoTest = true;
				application.setTestMode(CITTestHelper.PCB_CIT2);
			}else if(testListStr.equals(CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT3)){
				Log.i(TAG, "pcb cit 3");
				hasAutoTest = true;
				application.setTestMode(CITTestHelper.PCB_CIT3);
			}else{
				hasAutoTest = false;
			}
        }else if (testTypeStr.equals(CITTestHelper.EXTRA_VALUE_TEST_TYPE_SUBPCB)) {
			if (testListStr.equals(CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT1)) {
				Log.i(TAG, "subpcb cit 1");
				hasAutoTest = true;
				application.setTestMode(CITTestHelper.SUBPCB_CIT1);
			}else if(testListStr.equals(CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT2)){
				Log.i(TAG, "subpcb cit 2");
				hasAutoTest = true;
				application.setTestMode(CITTestHelper.SUBPCB_CIT2);
			}else{
				hasAutoTest = false;
			}
		}else if (testTypeStr.equals(CITTestHelper.EXTRA_VALUE_TEST_TYPE_COMPLETE)) {
			if (testListStr.equals(CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT1)) {
				Log.i(TAG, "complete cit 1");
				hasAutoTest = true;
				application.setTestMode(CITTestHelper.COMPLETE_CIT1);
			}else if(testListStr.equals(CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT2)){
				Log.i(TAG, "complete cit 2");
				hasAutoTest = true;
				application.setTestMode(CITTestHelper.COMPLETE_CIT2);
			}else if(testListStr.equals(CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT3)){
				Log.i(TAG, "complete cit 3");
				hasAutoTest = true;
				application.setTestMode(CITTestHelper.COMPLETE_CIT3);
			}else{
				hasAutoTest = false;
			}
		}else{
			hasAutoTest = false;
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int classNameListIndex = position;
		if (hasAutoTest) {
			classNameListIndex -= 1;
			if (position == 0) {
				startAutoTest();
				return;
			}
		}
		application.startTestActivity(this, classNameListIndex);
		super.onListItemClick(l, v, position, id);
	}
	
	private void startAutoTest(){
		application.setAutoTest(true);
		application.startTestActivity(this, 0);
	}
}
