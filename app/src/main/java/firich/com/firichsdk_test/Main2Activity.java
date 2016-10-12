package firich.com.firichsdk_test;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;

import firich.com.firichsdk.SerialPort;
import firich.com.firichsdk.SunComm;

//for IDTech IC Card test
//

//public class MainActivity extends AppCompatActivity {
public class Main2Activity extends Activity {
    SerialPort sp;
    SunComm sc;
    int intSerialPortHandle = -1;
    final static int SLEEP_MSEC =1000;
    final int bWirteForGPIO =1;
    final int bReadFOrGPIO =0;
    String strTagUtil = "MainActivity.";
    private String fectest_config_path = "/data/fec_config/fectest_config.xml";


    private boolean bDebugOn = true;

    // 測試項目順序和 config file 是一個一對一的 mapping.
    // 修改測試順序,
    //  1. 修改 test item definition value. ex: TEST_ITEM_SYSKING_IC_CARD =0 , 是第1個測項
    //  2. 修改 fec_test_items_order 測試順序.
    /*
    1. Modify the test order of : private final int TEST_ITEM_SYSKING_IC_CARD = 0; //新精：SYSKING
    2. Modify the test order of : private fec_test_item[] fec_test_items_order = new fec_test_item[]{

    }
    */
    // Test Package name.
    private final String PACKAGE_NAME = "firich.com.firichsdk_test";
    // Test items
    private final int TEST_ITEM_SYSKING_IC_CARD = 0; //新精：SYSKING
    private final int TEST_ITEM_CASH_DRAWER = 1;
    private final int TEST_ITEM_ID_IC_CARD = 2; //ID Tech
    private final int TEST_ITEM_FINGER_PRINTER = 3;
    private final int TEST_ITEM_VFD_LCM=4;
    private final int TEST_ITEM_NFC=5;
    private final int TEST_ITEM_THERMAL_PRINTER=6;
    private final int TEST_ITEM_RFID = 7;
    private final int TEST_ITEM_HID_MSR = 8;
    private final int TEST_ITEM_HID_IBUTTON = 9;
    private final int TEST_ITEM_HID_2DBARCODE = 10;
    private final int TEST_ITEM_THERMAL_PRINTER_D10 = 11;
    private final int TEST_ITEM_RS232_DEVICE = 12;

    private final int TEST_ITEM_BATTERY = 13;
    private final int TEST_ITEM_LCM = 14; //Display Test
    private final int TEST_ITEM_TP = 15;
    private final int TEST_ITEM_KEYPAN = 16;
    private final int TEST_ITEM_WIFI = 17;
    private final int TEST_ITEM_SENSOR = 18;
    private final int TEST_ITEM_LIGHT = 19;
    private final int TEST_ITEM_GYROSCOPE = 20;
    private final int TEST_ITEM_SPEAKER = 21;
    private final int TEST_ITEM_MICROPHONE = 22;
    private final int TEST_ITEM_HEADPHONE = 23;
    private final int TEST_ITEM_HEADSET = 24;
    private final int TEST_ITEM_CAMERA_BACK = 25;
    private final int TEST_ITEM_CAMERA_FRONT = 26;
    private final int TEST_ITEM_HDMI = 27;
    private final int TEST_ITEM_OTG = 28;
    private final int TEST_ITEM_GPS = 29;
    private final int TEST_ITEM_BT = 30;
    private final int TEST_ITEM_ULAN = 31;
    private final int TEST_ITEM_ETHERNET = 32;
    private final int TEST_ITEM_STORAGE = 33;
    private final int TEST_ITEM_RTC = 34;
    private final int TEST_ITEM_DEVICE_INFO = 35;
    private final int TEST_ITEM_SERIAL_NUMBER = 36;
    private final int TEST_ITEM_SDCARD = 37;
    private final int TEST_ITEM_USB_STORAGE = 38;
    private final int TEST_ITEM_CAMERA_AF=39;

// Test items
    private final int TEST_ITEM_1 = 0; 
    private final int TEST_ITEM_2 = 1;
    private final int TEST_ITEM_3 = 2; //ID Tech
    private final int TEST_ITEM_4 = 3;
    private final int TEST_ITEM_5 = 4;

    private final int TEST_ITEM_6 = 5;
    private final int TEST_ITEM_7 = 6;
    private final int TEST_ITEM_8 = 7; //ID Tech
    private final int TEST_ITEM_9 = 8;
    private final int TEST_ITEM_10 = 9;

    private final int TEST_ITEM_11 = 10;
    private final int TEST_ITEM_12 = 11;
    private final int TEST_ITEM_13 = 12;
    private final int TEST_ITEM_14 = 13;
    private final int TEST_ITEM_15 = 14;

    private final int TEST_ITEM_16 = 15;
    private final int TEST_ITEM_17 = 16;
    private final int TEST_ITEM_18 = 17;
    private final int TEST_ITEM_19 = 18;
    private final int TEST_ITEM_20 = 19;

    private final int TEST_ITEM_21 = 20;
    private final int TEST_ITEM_22 = 21;
    private final int TEST_ITEM_23 = 22;
    private final int TEST_ITEM_24 = 23;
    private final int TEST_ITEM_25 = 24;

    private final int TEST_ITEM_26 = 25;
    private final int TEST_ITEM_27 = 26;
    private final int TEST_ITEM_28 = 27;
    private final int TEST_ITEM_29 = 28;
    private final int TEST_ITEM_30 = 29;

    private final int TEST_ITEM_31 = 30;
    private final int TEST_ITEM_32 = 31;
    private final int TEST_ITEM_33 = 32;
    private final int TEST_ITEM_34 = 33;
    private final int TEST_ITEM_35 = 34;

    private final int TEST_ITEM_36 = 35;
    private final int TEST_ITEM_37 = 36;
    private final int TEST_ITEM_38 = 37;
    private final int TEST_ITEM_39 = 38;
    private final int TEST_ITEM_40 = 39;


    final private int max_test_items= 40;

    private void dump_trace( String bytTrace)
    {
        if (bDebugOn)
            Log.d(strTagUtil, bytTrace);
    }

    public static String hex(int n) {
        // call toUpperCase() if that's required
        return String.format("0x%2s", Integer.toHexString(n)).replace(' ', '0');
    }

    public SerialPort.SerialPortListener splistener = new SerialPort.SerialPortListener() {
        public void onDataReceive(byte[] btyData){
            Log.d("onDataReceive",new String(btyData));
        }
    };

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private void SleepMiniSecond(int minSecond)
    {
        try {
            sp.sleep(minSecond);
            dump_trace("SLEEP_MSEC="+ minSecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void FEC_Test_Item(int requestCodeL, String strClassL)
    {
        String strVersion = Build.DISPLAY;
        boolean contains_android4 = strVersion.contains("4.4.3 2.0.0-rc2.");
        //int requestCode = TEST_ITEM_SYSKING_IC_CARD;
        int requestCode = requestCodeL;
        boolean IsHIDClass = fec_test_items_order[requestCodeL].fec_test_item_class.contains("MainHIDActivity");
       // if ((requestCodeL >=TEST_ITEM_BATTERY) && (requestCodeL <=TEST_ITEM_SERIAL_NUMBER) ){ //11-33 =23
        if (!IsHIDClass){

                if (!contains_android4) {
                    String strClassAction = fec_test_items_order[requestCodeL].fec_test_item_package+fec_test_items_order[requestCodeL].fec_test_item_class;
                    Intent intent = new Intent();
                    intent.setAction(strClassAction);
                    startActivityForResult(intent, requestCode);
                }else if (contains_android4){ // for debug on freescale platform
                    //NextTestItem = TEST_ITEM_SDCARD-1;
                    String strClassAction = fec_test_items_order[requestCodeL].fec_test_item_package+fec_test_items_order[requestCodeL].fec_test_item_class;
                    Intent intent = new Intent();
                    intent.setAction(strClassAction);
                    startActivityForResult(intent, requestCode);
                }
        }else {
            // String strClass = PACKAGE_NAME+".MainSysKingICCardActivity";
            String HIDType="MSR Test";
            String strClass = PACKAGE_NAME + strClassL;
            Intent intent = new Intent();
            ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
            intent.setComponent(cn);
            //TODO:
            /*
            switch (requestCodeL) {
                case TEST_ITEM_HID_MSR:
                    intent.putExtra("HID_TYPE", "MSR Test");
                    break;
                case TEST_ITEM_HID_IBUTTON:
                    intent.putExtra("HID_TYPE", "IButton Test");
                    break;
                case TEST_ITEM_HID_2DBARCODE:
                    intent.putExtra("HID_TYPE", "2D Barcode Test");
                    break;
            }*/
            /*
            if (fec_test_items_order[requestCodeL].name.contains("MSRTest")){
                intent.putExtra("HID_TYPE", fec_test_items_order[requestCodeL].UIDescription);
            }else if (fec_test_items_order[requestCodeL].name.contains("IButtonTest")){
                intent.putExtra("HID_TYPE", "IButton Test");
            }else if(fec_test_items_order[requestCodeL].name.contains("2DBarcodeTest")){
                intent.putExtra("HID_TYPE", "2D Barcode Test");
            }*/
            intent.putExtra("HID_TYPE", fec_test_items_order[requestCodeL].UIDescription);
            startActivityForResult(intent, requestCode);
        }
    }

    String FEC_Test_item_get_class(int requestCodeVar)
    {
        String strClass = fec_test_items_order[requestCodeVar].fec_test_item_package + fec_test_items_order[requestCodeVar].fec_test_item_class;
        return strClass;
    }
    Intent intent_test_item;
    public Intent FEC_Test_item_get_intent( int requestCodeL, String strClass)
    {
        intent_test_item = new Intent();
        if (strClass.contains("MainHIDActivity")) {
            /*
            if (fec_test_items_order[requestCodeL].name.contains("MSRTest")) {
                intent_test_item.putExtra("HID_TYPE", "MSR Test");
            } else if (fec_test_items_order[requestCodeL].name.contains("IButtonTest")) {
                intent_test_item.putExtra("HID_TYPE", "IButton Test");
            } else if (fec_test_items_order[requestCodeL].name.contains("2DBarcodeTest")) {
                intent_test_item.putExtra("HID_TYPE", "2D Barcode Test");
            }*/
            intent_test_item.putExtra("HID_TYPE", fec_test_items_order[requestCodeL].UIDescription);
        }
        intent_test_item.setAction(strClass);
        return  intent_test_item;
    }
    public void FEC_Test_item_click(View view)
    {
        int requestCode = TEST_ITEM_1;
        switch(view.getId())
        {
            case R.id.btnTestItem1:
                requestCode = TEST_ITEM_1;
                break;
            case R.id.btnTestItem2:
                requestCode = TEST_ITEM_2;
                break;
            case R.id.btnTestItem3:
                requestCode = TEST_ITEM_3;
                break;
            case R.id.btnTestItem4:
                requestCode = TEST_ITEM_4;
                break;
            case R.id.btnTestItem5:
                requestCode = TEST_ITEM_5;
                break;
            case R.id.btnTestItem6:
                requestCode = TEST_ITEM_6;
                break;
            case R.id.btnTestItem7:
                requestCode = TEST_ITEM_7;
                break;
            case R.id.btnTestItem8:
                requestCode = TEST_ITEM_8;
                break;
            case R.id.btnTestItem9:
                requestCode = TEST_ITEM_9;
                break;
            case R.id.btnTestItem10:
                requestCode = TEST_ITEM_10;
                break;
            case R.id.btnTestItem11:
                requestCode = TEST_ITEM_11;
                break;
            case R.id.btnTestItem12:
                requestCode = TEST_ITEM_12;
                break;
            case R.id.btnTestItem13:
                requestCode = TEST_ITEM_13;
                break;
            case R.id.btnTestItem14:
                requestCode = TEST_ITEM_14;
                break;
            case R.id.btnTestItem15:
                requestCode = TEST_ITEM_15;
                break;

            case R.id.btnTestItem16:
                requestCode = TEST_ITEM_16;
                break;
            case R.id.btnTestItem17:
                requestCode = TEST_ITEM_17;
                break;
            case R.id.btnTestItem18:
                requestCode = TEST_ITEM_18;
                break;
            case R.id.btnTestItem19:
                requestCode = TEST_ITEM_19;
                break;
            case R.id.btnTestItem20:
                requestCode = TEST_ITEM_20;
                break;

            case R.id.btnTestItem21:
                requestCode = TEST_ITEM_21;
                break;
            case R.id.btnTestItem22:
                requestCode = TEST_ITEM_22;
                break;
            case R.id.btnTestItem23:
                requestCode = TEST_ITEM_23;
                break;
            case R.id.btnTestItem24:
                requestCode = TEST_ITEM_24;
                break;
            case R.id.btnTestItem25:
                requestCode = TEST_ITEM_25;
                break;
            case R.id.btnTestItem26:
                requestCode = TEST_ITEM_26;
                break;
            case R.id.btnTestItem27:
                requestCode = TEST_ITEM_27;
                break;
            case R.id.btnTestItem28:
                requestCode = TEST_ITEM_28;
                break;
            case R.id.btnTestItem29:
                requestCode = TEST_ITEM_29;
                break;
            case R.id.btnTestItem30:
                requestCode = TEST_ITEM_30;
                break;
            case R.id.btnTestItem31:
                requestCode = TEST_ITEM_31;
                break;
            case R.id.btnTestItem32:
                requestCode = TEST_ITEM_32;
                break;
            case R.id.btnTestItem33:
                requestCode = TEST_ITEM_33;
                break;
            case R.id.btnTestItem34:
                requestCode = TEST_ITEM_34;
                break;
            case R.id.btnTestItem35:
                requestCode = TEST_ITEM_35;
                break;

            case R.id.btnTestItem36:
                requestCode = TEST_ITEM_36;
                break;
            case R.id.btnTestItem37:
                requestCode = TEST_ITEM_37;
                break;
            case R.id.btnTestItem38:
                requestCode = TEST_ITEM_38;
                break;
            case R.id.btnTestItem39:
                requestCode = TEST_ITEM_39;
                break;
            case R.id.btnTestItem40:
                requestCode = TEST_ITEM_40;
                break;
        }
        String strClass = FEC_Test_item_get_class(requestCode);
        Intent intent;
        intent = FEC_Test_item_get_intent(requestCode, strClass);
        startActivityForResult(intent, requestCode);
    }


  
    private static final String THUNDER_SOFT_PACKAGE_NAME = "com.thundersoft.factorytools.hardwaretest";

    private final String ACTION_BATTERY = "com.thundersoft.factorytools.hardwaretest.BatteryActivity";
    private final String ACTION_LCM = "com.thundersoft.factorytools.hardwaretest.LCMActivity";
    private final String ACTION_TP = "com.thundersoft.factorytools.hardwaretest.TPActivity";
    private final String ACTION_KEYPAND = "com.thundersoft.factorytools.hardwaretest.KeypadActivity";
    private final String ACTION_WIFI = "com.thundersoft.factorytools.hardwaretest.WifiActivity";
    private final String ACTION_SENSOR = "com.thundersoft.factorytools.hardwaretest.SensorActivity";
    private final String ACTION_LIGHT = "com.thundersoft.factorytools.hardwaretest.LightSensorActivity";
    private final String ACTION_GYROSCOPE = "com.thundersoft.factorytools.hardwaretest.GyroscopeSensorActivity";
    private final String ACTION_SPEAKER = "com.thundersoft.factorytools.hardwaretest.SpeakerActivity";
    private final String ACTION_MICROPHONE = "com.thundersoft.factorytools.hardwaretest.MicrophoneActivity";
    private final String ACTION_HEADPHONE = "com.thundersoft.factorytools.hardwaretest.HeadPhoneActivity";
    private final String ACTION_HEADSET = "com.thundersoft.factorytools.hardwaretest.HeadsetActivity";
    private final String ACTION_CAMERA_BACK = "com.thundersoft.factorytools.hardwaretest.CameraBackActivity";
    private final String ACTION_CAMERA_FRONT = "com.thundersoft.factorytools.hardwaretest.CameraFrontActivity";
    private final String ACTION_HDMI = "com.thundersoft.factorytools.hardwaretest.HDMIActivity";
    private final String ACTION_OTG = "com.thundersoft.factorytools.hardwaretest.OTGActivity";
    private final String ACTION_GPS = "com.thundersoft.factorytools.hardwaretest.GPSActivity";
    private final String ACTION_BT = "com.thundersoft.factorytools.hardwaretest.BTActivity";
    private final String ACTION_ULAN = "com.thundersoft.factorytools.hardwaretest.ULANActivity";
    private final String ACTION_STORAGE = "com.thundersoft.factorytools.hardwaretest.StorageActivity";
    private final String ACTION_RTC = "com.thundersoft.factorytools.hardwaretest.RTCActivity";
    private final String ACTION_DEVICE_INFO = "com.thundersoft.factorytools.hardwaretest.DeviceInfoActivity";
    private final String ACTION_SERIAL_NUMBER = "com.thundersoft.factorytools.hardwaretest.InputSerialNumberActivity";
    private static final String RESULT_PASS = "PASS";
    private static final String RESULT_FAIL = "FAIL";

   



    logUtil logUtill;

    final String Class_SysKingICCard = ".MainSysKingICCardActivity";
    final String Class_CashDrawer = ".MainCashDrawerActivity";
    final String Class_IDICCard   = ".IDICCard";
    final String Class_FingerPrinter = ".UareUSampleJava";
    final String Class_VFDLCM  = ".MainLCMActivity";
    final String Class_NFC     = ".MainNFCActivity";
    final String Class_ThermalPrinter = ".MainThermalPrinterActivity";
    final String Class_RFID = ".MainRFIDActivity";
    final String Class_HID = ".MainHIDActivity";
    final String Class_ThermalPrinterD10 = ".MainThermalPrinterD10Activity";
    final String Class_RS232 = ".MainRS232Activity";
    final String Class_ETHERNET = ".MainEthernetActivity";
    final String Class_SDCard = ".MainSDCardActivity";
    final String Class_USBStorage = ".MainUSBStorageActivity";
    final String Class_CameraAF = ".MainCameraAFActivity";

    //
    private int fec_test_count_index=0;
    //FEC test class oreder : 11 items
    class fec_test_item{
        int fec_test_item_request_code;
        String fec_test_item_class;
        String fec_test_item_package;
        boolean test;
        String name; // test name
        String UIDescription;
        fec_test_item(int fec_test_item_request_code_l, String fec_test_item_class_l)
        {
            fec_test_item_request_code = fec_test_item_request_code_l;
            fec_test_item_class = fec_test_item_class_l;
            fec_test_item_package = PACKAGE_NAME;
            test = true;
            name ="";
            UIDescription="";
        }
    }

    // 測試項目順序和 config file 是一個一對一的 mapping.
    // 如果修改測試順序, test id 也要修改. ex: TEST_ITEM_SYSKING_IC_CARD =0 , 是第1個測項
    private fec_test_item[] fec_test_items_order = new fec_test_item[]{
            new fec_test_item(TEST_ITEM_SYSKING_IC_CARD, Class_SysKingICCard), //0
            new fec_test_item(TEST_ITEM_CASH_DRAWER, Class_CashDrawer),
            new fec_test_item(TEST_ITEM_ID_IC_CARD, Class_IDICCard),
            new fec_test_item(TEST_ITEM_FINGER_PRINTER, Class_FingerPrinter),
            new fec_test_item(TEST_ITEM_VFD_LCM, Class_VFDLCM),

            new fec_test_item(TEST_ITEM_NFC, Class_NFC),
            new fec_test_item(TEST_ITEM_THERMAL_PRINTER, Class_ThermalPrinter),
            new fec_test_item(TEST_ITEM_RFID, Class_RFID),
            new fec_test_item(TEST_ITEM_HID_MSR, Class_HID), //8
            new fec_test_item(TEST_ITEM_HID_IBUTTON, Class_HID),
            new fec_test_item(TEST_ITEM_HID_2DBARCODE, Class_HID),
            new fec_test_item(TEST_ITEM_THERMAL_PRINTER_D10, Class_ThermalPrinterD10),

            new fec_test_item(TEST_ITEM_RS232_DEVICE, Class_RS232),  //12

            //ThunderSoft test items
            new fec_test_item(TEST_ITEM_BATTERY      , ACTION_BATTERY),  //13
            new fec_test_item(TEST_ITEM_LCM          , ACTION_LCM),
            new fec_test_item(TEST_ITEM_TP           , ACTION_TP),
            new fec_test_item(TEST_ITEM_KEYPAN       , ACTION_KEYPAND),
            new fec_test_item(TEST_ITEM_WIFI         , ACTION_WIFI),

            new fec_test_item(TEST_ITEM_SENSOR       , ACTION_SENSOR),
            new fec_test_item(TEST_ITEM_LIGHT        , ACTION_LIGHT),
            new fec_test_item(TEST_ITEM_GYROSCOPE    , ACTION_GYROSCOPE),
            new fec_test_item(TEST_ITEM_SPEAKER      , ACTION_SPEAKER),
            new fec_test_item(TEST_ITEM_MICROPHONE   , ACTION_MICROPHONE),

            new fec_test_item(TEST_ITEM_HEADPHONE    , ACTION_HEADPHONE),
            new fec_test_item(TEST_ITEM_HEADSET      , ACTION_HEADSET),
            new fec_test_item(TEST_ITEM_CAMERA_BACK  , ACTION_CAMERA_BACK),
            new fec_test_item(TEST_ITEM_CAMERA_FRONT , ACTION_CAMERA_FRONT),
            new fec_test_item(TEST_ITEM_HDMI         , ACTION_HDMI),

            new fec_test_item(TEST_ITEM_OTG          , ACTION_OTG),
            new fec_test_item(TEST_ITEM_GPS          , ACTION_GPS),
            new fec_test_item(TEST_ITEM_BT           , ACTION_BT),
            new fec_test_item(TEST_ITEM_ULAN         , ACTION_ULAN),
            new fec_test_item(TEST_ITEM_ETHERNET         , Class_ETHERNET), //Brian added
            new fec_test_item(TEST_ITEM_STORAGE      , ACTION_STORAGE),

            new fec_test_item(TEST_ITEM_RTC          , ACTION_RTC),
            new fec_test_item(TEST_ITEM_DEVICE_INFO  , ACTION_DEVICE_INFO),
            new fec_test_item(TEST_ITEM_SERIAL_NUMBER, ACTION_SERIAL_NUMBER),
            new fec_test_item(TEST_ITEM_SDCARD, Class_SDCard), //Brian Added
            new fec_test_item(TEST_ITEM_USB_STORAGE, Class_USBStorage), //Brian Added
            new fec_test_item(TEST_ITEM_CAMERA_AF, Class_CameraAF) //Brian Added

    };


    int normal_init_item =-1;
    int debug_init_item = 9;//37;
    int fec_init_test_item = normal_init_item;
   // int fec_init_test_item = debug_init_item;
    int initial_test_item = fec_init_test_item;
    int NextTestItem=initial_test_item+1;
    boolean not_end_test_all = true;
    int end_test_item = max_test_items; //stop at ?th ; fec next test items:. 1, 2,3, 4~11; total= 11(fec)+23(thunder soft)=34 items.

    private TextView getTextViewFromRequestCode(int requestCode)
    {
        TextView txtResult =(TextView) findViewById(R.id.textViewNFCTestResult);

        String strNum = Integer.toString(requestCode+1);
        String LLID = "textViewFEC_Test_item_"+strNum+"TestResult";
        int resID = getResources().getIdentifier(LLID, "id", "firich.com.firichsdk_test");
        txtResult = (TextView) findViewById(resID);
/*
        switch (requestCode) // which test item
        {
            case TEST_ITEM_1:
                txtResult = (TextView) findViewById(R.id.textViewFEC_Test_item_1TestResult);
                break;
            case TEST_ITEM_2:
                txtResult = (TextView) findViewById(R.id.textViewFEC_Test_item_2TestResult);
                break;
            case TEST_ITEM_3:
                txtResult = (TextView) findViewById(R.id.textViewFEC_Test_item_3TestResult);
                break;
            case TEST_ITEM_4:
                txtResult = (TextView) findViewById(R.id.textViewFEC_Test_item_4TestResult);
                break;
            case TEST_ITEM_5:
                txtResult = (TextView) findViewById(R.id.textViewFEC_Test_item_5TestResult);
                break;


        }
        */
        return txtResult;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        int result=0;
        TextView txtResult =(TextView) findViewById(R.id.textViewNFCTestResult);
        String strMsg = String.format("requestCode=%d, resultCode=%d",requestCode,resultCode);
        dump_trace("onActivityResult="+ strMsg);

        txtResult = getTextViewFromRequestCode(requestCode);

        String resultPASS="";
        String testResult="";

        boolean IsTHPackage = fec_test_items_order[requestCode].fec_test_item_package.contains(THUNDER_SOFT_PACKAGE_NAME);
        //if ((requestCode >= TEST_ITEM_BATTERY) && (requestCode<=TEST_ITEM_SERIAL_NUMBER)){
        if (IsTHPackage){
                testResult = (resultCode == Activity.RESULT_OK ? RESULT_PASS : RESULT_FAIL);
        }else {

            if (resultCode == 1) {
                testResult = "PASS";
                //txtResult.setText("PASS"); //Activity.RESULT_CANCELED = 0 , so PASS use 1.
            } else if (resultCode == 0) {
                testResult = "FAIL";
                //txtResult.setText("FAIL");
            }

        }
        txtResult.setText(testResult);
        String testName="";
        testName = fec_test_items_order[requestCode].name;
        RecordFECLog("["+ testName +"][Test "+ testResult +"]");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        if (test_all) {
            not_end_test_all = (NextTestItem != end_test_item);
            if (not_end_test_all) {

                //FEC_Test_Item(TEST_ITEM_CASH_DRAWER, Class_CashDrawer);
                if ((NextTestItem >=0) && (NextTestItem < max_test_items)) {
                    FEC_Test_Item(fec_test_items_order[NextTestItem].fec_test_item_request_code, fec_test_items_order[NextTestItem].fec_test_item_class);
                    NextTestItem = find_next_test_item(NextTestItem);
                    if (NextTestItem == -1){
                        test_all = false;
                    }else {
                        dump_trace("NextTestItem=" + NextTestItem + "; test name=" + fec_test_items_order[NextTestItem].name);
                    }
                }
            }else {
                // end test
                test_all = false;
            }
        }

    }

    private int find_next_test_item(int CurrentTestItem)
    {
        int NextTestItemL=0;
        boolean NeedTest = false;
        NextTestItemL = CurrentTestItem+1;
        //Debug on Android 4.4
        String strVersion = Build.DISPLAY;
        boolean contains_android4 = strVersion.contains("4.4.3 2.0.0-rc2.");
        do {

            not_end_test_all = (NextTestItemL != end_test_item);
            if (not_end_test_all) {

                NeedTest = fec_test_items_order[NextTestItemL].test;
                if(NeedTest){
                    if (!contains_android4) {
                        break;
                    }else{
                        boolean IsTSPackage = fec_test_items_order[NextTestItemL].fec_test_item_package.contains(THUNDER_SOFT_PACKAGE_NAME);
                        if (IsTSPackage){
                            NextTestItemL++;// skip thunder soft test item on Freescale platform.
                            NeedTest = false;
                        }else{
                            break;
                        }
                    }
                }else{
                    NextTestItemL++;
                }
            }else{
                return -1; //cannot find next available test item. ex: item 33's test is false.
            }

        }while(!NeedTest);
        return NextTestItemL;
    }



    configItemsUIUtil g_configUIItemsFile;
    configItemsUIUtil.configItemUI configItemUIObject;
    private void DetermineTestItem(int LLID, String Device)
    {
        /*
        configUtil.Device devObject;
        g_configFile.dom4jXMLParser();



        LinearLayout layout;
        layout= (LinearLayout)  findViewById(LLID);
        devObject = g_configFile.getDevice(Device);
        if (!devObject.Test) {
            layout.setVisibility(View.GONE);
        }
        */
    }
    private void DetermineTestItems()
    {
        fectest_config_path  = ((FECApplication) this.getApplication()).getFEC_config_path();
        g_configUIItemsFile = new configItemsUIUtil(fectest_config_path);
        g_configUIItemsFile.dom4jXMLParser();

        /*
        DetermineTestItem(R.id.linearLayout_test_item_1, "SysKingICCardTest" );
        DetermineTestItem(R.id.linearLayout_test_item_2, "CashDrawerTest" );
        */
        // 1. Add ID field(auto-added) when load XML; Give up, because each test item has it's own attribute.
        //STD_configUtil.STD_config STD_configObject;

        LinearLayout layout;
        //for(int i=1; i<=10;i++) {
        int id=0;
        for (Enumeration<configItemsUIUtil.configItemUI> e = g_configUIItemsFile.getHashtableConfigUI().elements(); e.hasMoreElements();){
            configItemsUIUtil.configItemUI configItemUIObject = e.nextElement();
            String LLID = "linearLayout_test_item_" + configItemUIObject.id;
            int resID = getResources().getIdentifier(LLID, "id", "firich.com.firichsdk_test");
            layout = (LinearLayout) findViewById(resID);
            id = Integer.valueOf(configItemUIObject.id);
            id--; // array of fec_test_items_order, start from 0.
            fec_test_items_order[id].name = configItemUIObject.name;
            fec_test_items_order[id].fec_test_item_class = configItemUIObject.className;
            fec_test_items_order[id].fec_test_item_package = configItemUIObject.packageName;
            fec_test_items_order[id].UIDescription = configItemUIObject.UIDescription;
            if (!configItemUIObject.test){
                layout.setVisibility(View.GONE); // don't show that test item.
                fec_test_items_order[id].test = false; // don't need to test for test all function.
            }else {

                int btnTestItemid = id+1;
                String buttonID = "btnTestItem" + Integer.toString(btnTestItemid);;
                int resBtnID = getResources().getIdentifier(buttonID, "id", "firich.com.firichsdk_test");
                Button buttonConfig = (Button) findViewById(resBtnID);
               // buttonConfig.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
                buttonConfig.setText(fec_test_items_order[id].UIDescription);
            }
        }

    }

    private IntentFilter mIntentFilter;
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message paramAnonymousMessage)
        {
            //MainActivity.this.updateDeviceInfo();
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
            if (paramAnonymousIntent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED"))
            {
                int i = paramAnonymousIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1);
                if (i == 12) {
                    //MainActivity.this.mHandler.sendEmptyMessage(i);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkBuild checkBuildOK = new checkBuild();
        if (!checkBuildOK.checkVersion())
            return;
        setContentView(R.layout.activity_main2);

        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");


        g_InternallogUtil = new InternallogUtil();

        RecordFECLog("[Start Test]");
        //[SystemInfo][Info][Wireless MAC: 44:2C:05:34:2D:F9]
        Record_Mac_Address(this);

        Record_BT_Mac_Address(this);
        Record_Ethernet_MAc_Address();

        DetermineTestItems();



        //((FECApplication) this.getApplication()).setFEC_config_path(fectest_config_path);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setTitle(" SN:" + Build.SERIAL);

    }

    private int group1Id = 1;

    int test_all_Id = Menu.FIRST;
    int clear_all_Id = Menu.FIRST +1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(group1Id, test_all_Id, test_all_Id, "Test All");
        menu.add(group1Id, clear_all_Id, clear_all_Id, "Clear All");

        return true;

    }


    public void FEC_Test_All_Start()
    {
        //NextTestItem = find_next_test_item(-1);
        //initial_test_item
        NextTestItem = find_next_test_item(initial_test_item);
        if (NextTestItem == -1){
            return;
        }
        dump_trace("NextTestItem="+ NextTestItem +"; test name="+ fec_test_items_order[NextTestItem].name);
        //FEC_Test_Item(fec_test_items_order[initial_test_item].fec_test_item_request_code, fec_test_items_order[initial_test_item].fec_test_item_class );
        FEC_Test_Item(fec_test_items_order[NextTestItem].fec_test_item_request_code, fec_test_items_order[NextTestItem].fec_test_item_class );
        NextTestItem = find_next_test_item(NextTestItem);
        if (NextTestItem == -1){
            return;
        }
        dump_trace("NextTestItem="+ NextTestItem +"; test name="+ fec_test_items_order[NextTestItem].name);
    }


    public void FEC_Clear_ALL_UI_Test_Result()
    {
        TextView txtResult =(TextView) findViewById(R.id.textViewFEC_Test_item_1TestResult);
        for (int i=0; i< max_test_items;i++){
            txtResult = getTextViewFromRequestCode(i);

            txtResult.setText("?");
        }
    }
    private boolean test_all = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast msg;
        switch (item.getItemId()) {
            case 1:
                // write your code here
                //msg = Toast.makeText(MainActivity.this, "Test All", Toast.LENGTH_LONG);
                //msg.show();
                FEC_Test_All_Start();
                test_all = true;
                return true;

            case 2:
                // write your code here

                // msg = Toast.makeText(MainActivity.this, "Clear All", Toast.LENGTH_LONG);
                // msg.show();
                FEC_Clear_ALL_UI_Test_Result();
                test_all = false;
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
    public  void RecordFECLog(String logString)
    {
        logUtill = new logUtil();
        Date newdate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        String date = format.format(newdate);
        logUtill.appendLog("["+ date +"]"+ logString);
    }
    */
    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();

        g_InternallogUtil.clearInterStorageLog();

        dump_trace("The onDestroy() event");
    }
    InternallogUtil g_InternallogUtil;
    public  void RecordFECLog(String logString)
    {

        Date newdate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        String date = format.format(newdate);
        g_InternallogUtil.appendInternalStorageLog("["+ date +"]"+ logString+"\n");
        String strReadLog = g_InternallogUtil.readInternalStorageLog(1000);
        dump_trace(strReadLog);
    }


    public  void copyto_udisk_click(View view)
    {
        boolean copyToUSBOK=false;
        copyToUSBOK = g_InternallogUtil.copyTOUSBStorage();
        if (copyToUSBOK){
            TextView textViewCopytoudisk = (TextView) findViewById(R.id.textViewcopyto_udisk);
            textViewCopytoudisk.setText("Copy Done.");
        }

    }

    public class InternallogUtil {

        String InternalStoragefileName = "InternalLog.txt";

        public void InternallogUtil()
        {

        }
        public void appendInternalStorageLog(String strLog)
        {
            // Create a file in the Internal Storage

            FileOutputStream outputStream;
            try {
                outputStream =  openFileOutput(InternalStoragefileName, Context.MODE_APPEND);
                outputStream.write(strLog.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        public void clearInterStorageLog()
        {
            String strEmpty="";
            FileOutputStream outputStream;
            try {
                outputStream =  openFileOutput(InternalStoragefileName, Context.MODE_PRIVATE);
                outputStream.write(strEmpty.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean copyTOUSBStorage() {
            try {
                int bytesum = 0;
                int byteread = 0;

                logUtil logUtilUdiskFile = new logUtil();
                String strUdiskFileName = logUtilUdiskFile.findUDiskFileName();
                if (logUtilUdiskFile.isFindUdiskFECLogFile()) {
                    FileInputStream fisInternalStorage = openFileInput(InternalStoragefileName);
                    FileOutputStream fs = new FileOutputStream(strUdiskFileName);
                    byte[] buffer = new byte[1444];
                    //byte[] buffer = new byte[14];
                    while ((byteread = fisInternalStorage.read(buffer)) != -1) {
                        bytesum += byteread;
                        fs.write(buffer, 0, byteread);
                    }
                    fisInternalStorage.close();
                    fs.close();

                    return true;
                }else{
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }


        public String readInternalStorageLog(int length) {
            byte[] buffer = new byte[length];
            int readLength=0;
            String str = new String();
            Arrays.fill(buffer, (byte)0);
            try {
                FileInputStream fis = openFileInput(InternalStoragefileName);
                readLength = fis.read(buffer);
                fis.close();
                str = new String(buffer);
                str = str.substring(0, readLength);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return str;
        }
    }




    private class WIFIUTilThread extends Thread {
        Context context;
        WifiManager wimanager;
        WIFIUTilThread(Context contextL) {
            context = contextL;
            wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
        private boolean enableWifi(Context context, boolean paramBoolean)
        {
            boolean OperateOK = false;
            wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wimanager != null) {
                OperateOK = wimanager.setWifiEnabled(paramBoolean);
            }
            return OperateOK;
        }
        boolean isWifiEnable()
        {
            boolean isWifiEnable = false;
            isWifiEnable = wimanager.isWifiEnabled();
            return isWifiEnable;
        }
        private String getMacAddress(Context context)
        {
            wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String macAddress = wimanager.getConnectionInfo().getMacAddress();
        /*
        if (macAddress == null) {
            macAddress = "Device don't have mac address or wi-fi is disabled";
        }
        */
            return macAddress;
        }
        public void run() {
            // compute primes larger than minPrime
            boolean wifiEnableOK;
            boolean OperateWIFIOK = false;
            int retryTimes=0;
            boolean initialWIFIOnOffStatus = false;
            wifiEnableOK = isWifiEnable();
            initialWIFIOnOffStatus = wifiEnableOK;

            do {
                // bConnectOK = connecD10PrinterFunc();
                if (!OperateWIFIOK) {
                    OperateWIFIOK = enableWifi(context, true);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                wifiEnableOK = isWifiEnable();
                retryTimes++;
            } while (!wifiEnableOK && (retryTimes <5));
            if (wifiEnableOK){
                String MacAddress = getMacAddress(context);
                RecordFECLog("[SystemInfo][Info][Wireless MAC: "+ MacAddress +"]");
            }
            OperateWIFIOK = false;
            retryTimes =0;
            //disable wifi after recording wifi mac address.
            do {
                // bConnectOK = connecD10PrinterFunc();
                if (!OperateWIFIOK) {
                    OperateWIFIOK = enableWifi(context, false);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                wifiEnableOK = isWifiEnable();
                retryTimes++;
            } while (wifiEnableOK && (retryTimes <5));

            //Restore WIFI on\off status
            OperateWIFIOK = false;
            if (initialWIFIOnOffStatus){
                //enable wifi
                do {
                    // bConnectOK = connecD10PrinterFunc();
                    if (!OperateWIFIOK) {
                        OperateWIFIOK = enableWifi(context, true);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    wifiEnableOK = isWifiEnable();
                    retryTimes++;
                } while (!wifiEnableOK && (retryTimes <5));
            }

        }
    }

    private void Record_Mac_Address(Context context)
    {
        WIFIUTilThread WIFIUTilThreadP = new WIFIUTilThread(context);
        WIFIUTilThreadP.start();
    }

    private void Record_BT_Mac_Address(Context context)
    {
        BTUtilThread BTUtilThreadP = new BTUtilThread(context);
        BTUtilThreadP.start();
    }


    private void Record_Ethernet_MAc_Address()
    {
        String MacAddress = getEthernetMacAddress();
        RecordFECLog("[SystemInfo][Info][Ethernet MAC: "+ MacAddress +"]");
    }
    /*
     * Load file content to String
     */
    public static String loadFileAsString(String filePath) throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    /*
     * Get the Ethernet MacAddress
     */
    public String getEthernetMacAddress(){

        String MacAddress="";
        try {
            MacAddress = loadFileAsString("/sys/class/net/eth0/address");
            MacAddress = MacAddress.substring(0, 17);
            return MacAddress;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class BTUtilThread extends Thread {
        Context context;
        private BluetoothAdapter mBluetoothAdapter;
        private BluetoothManager mBluetoothManager;
        BTUtilThread(Context contextL) {
            this.mBluetoothManager = ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE));
            this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        }
        private boolean enableBT()
        {
            boolean adapter_startup_has_begun = false;

            this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
            if (this.mBluetoothAdapter != null) {
                adapter_startup_has_begun = this.mBluetoothAdapter.enable();
            }
            return adapter_startup_has_begun;
        }
        private boolean disableBT()
        {
            boolean adapter_startup_has_begun = false;

            this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
            if (this.mBluetoothAdapter != null) {
                adapter_startup_has_begun = this.mBluetoothAdapter.disable();
            }
            return adapter_startup_has_begun;
        }
        boolean isBTEnable()
        {
            //Possible return values are STATE_OFF, STATE_TURNING_ON, STATE_ON, STATE_TURNING_OFF.
            boolean BTenable = false;
            int BTState = android.bluetooth.BluetoothAdapter.STATE_OFF;
            BTState = mBluetoothAdapter.getState();
            if (BTState == BluetoothAdapter.STATE_ON){
                BTenable =  true;
            }
            return BTenable;
        }
        private String getBTMacAddress()
        {

            String macAddress = mBluetoothAdapter.getAddress();
        /*
        if (macAddress == null) {
            macAddress = "Device don't have mac address or wi-fi is disabled";
        }
        */
            return macAddress;
        }
        public void run() {
            // compute primes larger than minPrime
            boolean BTEnableOK;
            boolean adapter_startup_has_begun = false;
            int retryTimes=0;
            do {
                // bConnectOK = connecD10PrinterFunc();
                if (!adapter_startup_has_begun) {
                    adapter_startup_has_begun = enableBT();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                BTEnableOK = isBTEnable();
                retryTimes++;
            } while (!BTEnableOK && (retryTimes <5));
            if (BTEnableOK){
                String MacAddress = getBTMacAddress();
                MacAddress = MacAddress.toLowerCase();
                RecordFECLog("[SystemInfo][Info][Bluetooth MAC: "+ MacAddress +"]");
            }
            adapter_startup_has_begun = false;
            retryTimes =0;
            //disable wifi after recording wifi mac address.
            do {
                // bConnectOK = connecD10PrinterFunc();
                if (!adapter_startup_has_begun) {
                    adapter_startup_has_begun = disableBT();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                BTEnableOK = isBTEnable();
                retryTimes++;
            } while (BTEnableOK && (retryTimes <5));
        }
    }

}
