package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

//public class MainUSBStorageActivity extends AppCompatActivity {
public class MainUSBStorageActivity extends Activity {

    /* "USB1;/mnt/media_rw/udisk|USB2;/mnt/media_rw/udisk_2|USB3;/mnt/media_rw/udisk_3|USB4;/mnt/media_rw/udisk_4|USB5;/mnt/media_rw/udisk_5|USB6;/mnt/media_rw/udisk_6|USB7;/mnt/media_rw/udisk_7|USB8;/mnt/media_rw/udisk_8"  */
    private String strUSBStorageDeviceName_android4_4 ="/storage/udisk|/storage/udisk_2|/storage/udisk_3|/storage/udisk_4|/storage/udisk_5|/storage/udisk_6|/storage/udisk_7|/storage/udisk_8";
    private String strUSBStorageDeviceName_android5_1 ="/storage/usbdisk|/storage/usbdisk2|/storage/usbdisk3|/storage/usbdisk4|/storage/usbdisk5|/storage/usbdisk6|/storage/usbdisk7|/storage/usbdisk8";

    private String strUSBStorageDeviceName =strUSBStorageDeviceName_android4_4;
    String[] strUSBStorageDeviceList;


    textViewResultUtil ltextViewResultIDs; //1, 2, 3, 4
    int deviceCount = 0;
    private Handler mHandler = null; //Brian
////////////////////////////////////////////////////////////////////////////////


    private boolean bDebugOn = true;
    String strTagUtil = "MainUSBStorageActivity.";

    private void dump_trace( String bytTrace)
    {
        if (bDebugOn)
            Log.d(strTagUtil, bytTrace);
    }

    private class textViewResultUtil{
        int[] textViewResultID= new int[20];
        public void setTextViewResultIDs(int deviceCount){

            //textViewResultID =  new int[deviceCount];
            for (int i=0; i < deviceCount; i++){
                textViewResultID[i] = generateViewId();
            }
        }
        public int getTextViewResultID(int ResultIndex){
            int ResultID=-1;
            if (ResultIndex < 20)
                ResultID = textViewResultID[ResultIndex];
            return ResultID;
        }
        private final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

        /**
         * Generate a value suitable for use in @link setId(int)}.
         * This value will not collide with ID values generated at build time by aapt for R.id.
         *
         * @return a generated ID value
         */
        public int generateViewId() {
            for (;;) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        }
    }

    private void CreateUSBStorageDeviceTable(String[] strUSBStorageDeviceList, int deviceCount)
    {
        Resources resource = (Resources) getBaseContext().getResources();
        ColorStateList colorWhile = (ColorStateList) resource.getColorStateList(R.color.white);
        ColorStateList colorBlack = (ColorStateList) resource.getColorStateList(R.color.black);
        TableLayout device_list_table = (TableLayout) findViewById(R.id.device_list_table);
        //device_list_table.setStretchAllColumns(true);
        TableLayout.LayoutParams row_layout = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams view_layout_device = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 5);
        TableRow.LayoutParams view_layout_result = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 5);


        ltextViewResultIDs = new textViewResultUtil();
        ltextViewResultIDs.setTextViewResultIDs(deviceCount);

        for (int i=0; i< deviceCount; i++) {
            TableRow tr = new TableRow(MainUSBStorageActivity.this);
            tr.setLayoutParams(row_layout);
            tr.setBackgroundColor(resource.getColor(R.color.green));
            tr.setGravity(Gravity.CENTER_HORIZONTAL);

           // String strUSBStorageDevice = strUSBStorageDeviceList[i];
            String strUSBStorageDevice = "USB "+ String.valueOf(i+1);
            if ((strUSBStorageDevice == null) || (strUSBStorageDevice.length() == 0))
                break;
            TextView device = new TextView(MainUSBStorageActivity.this);
            device.setText(strUSBStorageDevice);
            device.setTextColor(colorBlack);
            device.setBackgroundResource(R.color.white);
            device.setPadding(2, 2, 2, 2);
            device.setTextSize(16);
            view_layout_device.setMargins(2, 2, 2, 2);
            device.setLayoutParams(view_layout_device);

            TextView lookback_result = new TextView(MainUSBStorageActivity.this);
            lookback_result.setId(ltextViewResultIDs.getTextViewResultID(i));
            lookback_result.setText("??");
            lookback_result.setTextColor((ColorStateList) resource.getColorStateList(R.color.red));
            lookback_result.setBackgroundResource(R.color.white);
            lookback_result.setPadding(2, 2, 2, 2);
            lookback_result.setTextSize(16);
            view_layout_result.setMargins(2, 2, 2, 2);
            lookback_result.setLayoutParams(view_layout_result);

            tr.addView(device);
            tr.addView(lookback_result);
            device_list_table.addView(tr);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkBuild checkBuildOK = new checkBuild();
        if (!checkBuildOK.checkVersion())
            return;
        setContentView(R.layout.activity_usbstorage);
        this.mHandler = new Handler(); //Brian:
        InitUSBStorageTable();

    }

    private String fectest_config_path = "/data/fec_config/fectest_config.xml";

    @Override
    protected void onStart(){
        super.onStart();

        startUSBStorage_Test();
    }
    public void InitUSBStorageTable()
    {

        fectest_config_path  = ((FECApplication) this.getApplication()).getFEC_config_path();

        String strVersion = Build.DISPLAY;
        boolean contains_android4 = strVersion.contains("4.4.3 2.0.0-rc2.");
        boolean contains_android5 = strVersion.contains("Edelweiss-T 5.1");
        boolean contains_android5_D = strVersion.contains("Edelweiss-D 5.1");

        configUtil.Device devObject;
        configUtil configFile = new configUtil(fectest_config_path);

        configFile.dom4jXMLParser();
        devObject = configFile.getDevice("USBStorage");
        deviceCount = devObject.numOfUSB;
        if (devObject.numOfUSB ==0) {
            deviceCount =8;
        }

        if (devObject.Android4_USBList != null && !devObject.Android4_USBList.isEmpty()) {
            strUSBStorageDeviceName = devObject.Android4_USBList;
        }
        if (! contains_android4){
            if (devObject.Android5_USBList != null && !devObject.Android5_USBList.isEmpty()) {
                strUSBStorageDeviceName = devObject.Android5_USBList;
            }
        }
        strUSBStorageDeviceList = strUSBStorageDeviceName.split("\\|");
        if ((strUSBStorageDeviceList == null) || (strUSBStorageDeviceList.length == 0)) {
            return ;
        }
        // deviceCount = 8; //usbdisk ~ usbdisk8
        CreateUSBStorageDeviceTable(strUSBStorageDeviceList, deviceCount);
    }
    private class USBStorageTestThread extends Thread {
        String[] lstrUSBPath;
        textViewResultUtil ltextViewResultIDs;
        int ldeviceCount;
        USBStorageTestThread(String[] strUSBPath, textViewResultUtil textViewResultIDs, int deviceCount) {
            lstrUSBPath = strUSBPath;
            ltextViewResultIDs = textViewResultIDs;
            ldeviceCount = deviceCount;
        }

        public void run() {
            Intent intent = getIntent();
            boolean testPASS = false;
            boolean[] testResult = new boolean[ldeviceCount];
            for (int i=1; i<= ldeviceCount; i++) {
                testResult[i-1] = USBStorage_Test(lstrUSBPath,ltextViewResultIDs.getTextViewResultID(i-1), i, ldeviceCount);
            }

            int i=0;
            testPASS = testResult[i];
            do {
                testPASS = (testPASS && testResult[i]);
                dump_trace("USB test result:"+testPASS);
                i++;
            } while(i< ldeviceCount);

            if (testPASS){
                setResult(1, intent);
            }else{
                setResult(0, intent);
            }
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finish();
        }
    }

    private void PostUIUpdateLog(final String msg, final  boolean testPASS, final int testDeviceTextViewID)
    {
        this.mHandler.post(new Runnable()
        {
            public void run()
            {
                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                final TextView textViewResultDeviceID = (TextView) findViewById(testDeviceTextViewID);
                dump_trace("PostUIUpdateLog:testPASS="+testPASS);
                if (testPASS)
                    textViewResultDeviceID.setText("PASS");
                else
                    textViewResultDeviceID.setText("FAIL");

            }
        });
    }


    //private boolean USBStorage_Test(String strUSBPath, int testDeviceTextViewID, int USBIndex, int deviceCount) {
    private boolean USBStorage_Test(String[] strUSBPaths, int testDeviceTextViewID, int USBIndex, int deviceCount) {

        boolean testResult=false;
        int intDataReceivedLength=1;
        //TODO:

        CheckUSBStorageUtil CheckUSBStorageUtilVar = new CheckUSBStorageUtil(strUSBPaths, deviceCount);
        testResult = CheckUSBStorageUtilVar.checkUSBStorage(USBIndex);
        PostUIUpdateLog("", testResult, testDeviceTextViewID);
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return testResult;

    }

    public void startUSBStorage_Test()
    {
        USBStorageTestThread USBStorageTestThreadP = new USBStorageTestThread(strUSBStorageDeviceList, ltextViewResultIDs, deviceCount);
        USBStorageTestThreadP.start();
    }
    public void USBStorage_Test_click(View view){
        //USBStorageTestThread USBStorageTestThreadP;

        startUSBStorage_Test();

    }

    public void cmdReturnPASS_Click(View view) {
        Intent intent = getIntent();
        setResult(1, intent); // return code = 1 -> OK
        finish();
    }

    public void cmdReturnFAIL_Click(View view) {
        Intent intent = getIntent();
        setResult(0, intent); // return code = 0 -> Error
        finish();
    }
}
