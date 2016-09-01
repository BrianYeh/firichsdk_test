package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import firich.com.firichsdk.SerialPort;
import firich.com.firichsdk.SunComm;

//for IDTech IC Card test
//

//public class MainActivity extends AppCompatActivity {
public class MainActivity extends Activity {
    SerialPort sp;
    SunComm sc;
    int intSerialPortHandle = -1;
    final static int SLEEP_MSEC =1000;
    final int bWirteForGPIO =1;
    final int bReadFOrGPIO =0;
    String strTagUtil = "MainActivity.";
    private String fectest_config_path = "/data/fec_config/fectest_config.xml";


    private boolean bDebugOn = true;

    // Test Package name.
    private final String PACKAGE_NAME = "firich.com.firichsdk_test";
    // Test items
    private final int TEST_ITEM_NFC=0;
    private final int TEST_ITEM_THERMAL_PRINTER=1;
    private final int TEST_ITEM_VFD_LCM=2;
    private final int TEST_ITEM_FINGER_PRINTER = 3;
    private final int TEST_ITEM_ID_IC_CARD = 4; //ID Tech
    private final int TEST_ITEM_CASH_DRAWER = 5;
    private final int TEST_ITEM_SYSKING_IC_CARD = 6; //新精：SYSKING
    private final int TEST_ITEM_RFID = 7;
    private final int TEST_ITEM_HID = 8;
    private final int TEST_ITEM_THERMAL_PRINTER_D10 = 9;
    private final int TEST_ITEM_RS232_DEVICE = 10;

    final private int max_test_items= 34;

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


    /*
    public void open_click(View view) {
        sp = new SerialPort();
        sp.setListener(splistener);
        intSerialPortHandle = sp.open("/dev/ttyUSB4",9600);
    }

    public void close_click(View view) {
        sp.close(intSerialPortHandle);
    }

    public void write_click(View view) {
        int intReturnCode = -1;
        byte[] btyTmp = new byte[2];
        btyTmp[0] = 0x41;
        btyTmp[1] = 0x42;

        intReturnCode = sp.write(intSerialPortHandle, btyTmp);
        //intReturnCode = sp.write(intSerialPortHandle,btyVersion_msg);
        // The function returns the number of bytes written to the file.
        // A return value of -1 indicates an error, with errno set appropriately.
        dump_trace( "intReturnCode="+intReturnCode);
    }

    public void nfc_test_click(View view) {
        sc = new SunComm();
        StringBuffer stbTmp = new StringBuffer();
        sc.CommVersion(1,stbTmp);
        dump_trace("nfc_test_click" + stbTmp.toString());
    }

    public void OpenNFC_click(View view) {
        sc.CommOpen(0,5); //dev/ttyUSB5
    }
    */

    public void FEC_Test_Item(int requestCodeL, String strClassL)
    {
        //int requestCode = TEST_ITEM_SYSKING_IC_CARD;
        int requestCode = requestCodeL;
        if (requestCodeL > 10) { //11-33 =23
            String strClassThunderSoft = strClassL;
            Intent intent = new Intent();
            intent.setAction(strClassThunderSoft);
            startActivityForResult(intent, requestCode);
       }else { //0-10 = 11
            // String strClass = PACKAGE_NAME+".MainSysKingICCardActivity";
            String strClass = PACKAGE_NAME + strClassL;
            Intent intent = new Intent();
            ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
            intent.setComponent(cn);
            startActivityForResult(intent, requestCode);
        }
    }

    public void IC_Card_SysKing_Test_click(View view)
    {

        int requestCode = TEST_ITEM_SYSKING_IC_CARD;
        String strClass = PACKAGE_NAME+".MainSysKingICCardActivity";
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);
    }

    public void CashDrawer_Test_click(View view)
    {

        int requestCode = TEST_ITEM_CASH_DRAWER;
        String strClass = PACKAGE_NAME+".MainCashDrawerActivity";
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);
    }
    public void ID_ICCard_Test_click(View view)
    {
        /*
        Intent intent = new Intent(view.getContext(), IDICCard.class);
        view.getContext().startActivity(intent);
        */
        int requestCode = TEST_ITEM_ID_IC_CARD;
        String strClass = PACKAGE_NAME+".IDICCard"; //ID Tech IC Card test.
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);
    }



    public void FingerPrinter_Test_click(View view)
    {
        /*
        Intent intent = new Intent(MainActivity.this, UareUSampleJava.class);
        startActivity(intent);
        */
        int requestCode = TEST_ITEM_FINGER_PRINTER;
        String strClass = PACKAGE_NAME+".UareUSampleJava"; //Finger printer test.
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);

    }

    public void VFD_LCM_Test_click(View view)
    {

        int requestCode = TEST_ITEM_VFD_LCM;
        String strClass = PACKAGE_NAME+".MainLCMActivity"; //VFD LCM
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);
    }

    public void NFC_Test_click(View view)
    {

        int requestCode = TEST_ITEM_NFC;
        String strClass = PACKAGE_NAME+".MainNFCActivity";
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);
    }
    public void ThermalPrinter_Test_click(View view)
    {
        //com.example.fitprintsampleusb
        /*
        ComponentName cn = new ComponentName("com.example.fitprintsampleusb","com.example.fitprintsampleusb.FitPrintSampleUSB");
           */
        int requestCode = TEST_ITEM_THERMAL_PRINTER;
        String strClass = PACKAGE_NAME+".MainThermalPrinterActivity";
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);
    }
    public void RFID_Test_click(View view)
    {

        int requestCode = TEST_ITEM_RFID;
        String strClass = PACKAGE_NAME+".MainRFIDActivity";
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);
    }

    public void HID_Test_click(View view)
    {

        int requestCode = TEST_ITEM_HID;
        String strClass = PACKAGE_NAME+".MainHIDActivity";
        Intent intent = new Intent();
        intent.putExtra("HID_TYPE", "Keyboard Input Test");
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);
    }
    public void Thermal_Printer_D10_Test_click(View view)
    {

        int requestCode = TEST_ITEM_THERMAL_PRINTER_D10;
        String strClass = PACKAGE_NAME+".MainThermalPrinterD10Activity";
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);
    }
    public void RS232_device_click(View view)
    {

        int requestCode = TEST_ITEM_RS232_DEVICE;
        String strClass = PACKAGE_NAME+".MainRS232Activity";
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);
    }

    private final int TEST_ITEM_BATTERY = 11;
    private final int TEST_ITEM_LCM = 12;
    private final int TEST_ITEM_TP = 13;
    private final int TEST_ITEM_KEYPAN = 14;
    private final int TEST_ITEM_WIFI = 15;
    private final int TEST_ITEM_SENSOR = 16;
    private final int TEST_ITEM_LIGHT = 17;
    private final int TEST_ITEM_GYROSCOPE = 18;
    private final int TEST_ITEM_SPEAKER = 19;
    private final int TEST_ITEM_MICROPHONE = 20;
    private final int TEST_ITEM_HEADPHONE = 21;
    private final int TEST_ITEM_HEADSET = 22;
    private final int TEST_ITEM_CAMERA_BACK = 23;
    private final int TEST_ITEM_CAMERA_FRONT = 24;
    private final int TEST_ITEM_HDMI = 25;
    private final int TEST_ITEM_OTG = 26;
    private final int TEST_ITEM_GPS = 27;
    private final int TEST_ITEM_BT = 28;
    private final int TEST_ITEM_ULAN = 29;
    private final int TEST_ITEM_STORAGE = 30;
    private final int TEST_ITEM_RTC = 31;
    private final int TEST_ITEM_DEVICE_INFO = 32;
    private final int TEST_ITEM_SERIAL_NUMBER = 33;
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

    public void Battery_click(View view)
    {

        /*
        int requestCode = TEST_ITEM_RS232_DEVICE;
        String strClass = THUNDER_SOFT_PACKAGE_NAME+".BatteryActivity";
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);
        */
        int requestCode = TEST_ITEM_BATTERY;
        String strClass = ACTION_BATTERY;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void LCMandBackLight_click(View view)
    {
        int requestCode = TEST_ITEM_LCM;
        String strClass = ACTION_LCM;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void TouchPanel_click(View view)
    {
        int requestCode = TEST_ITEM_TP;
        String strClass = ACTION_TP;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void KeyTest_click(View view)
    {
        int requestCode = TEST_ITEM_KEYPAN;
        String strClass = ACTION_KEYPAND;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void WifiTest_click(View view)
    {
        int requestCode = TEST_ITEM_WIFI;
        String strClass = ACTION_WIFI;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }
    public void ASensorTest_click(View view)
    {
        int requestCode = TEST_ITEM_SENSOR;
        String strClass = ACTION_SENSOR;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void LightSensorTest_click(View view)
    {
        int requestCode = TEST_ITEM_LIGHT;
        String strClass = ACTION_LIGHT;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void GyroscopeSensorTest_click(View view)
    {
        int requestCode = TEST_ITEM_GYROSCOPE;
        String strClass = ACTION_GYROSCOPE;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void SPEAKERTest_click(View view)
    {
        int requestCode = TEST_ITEM_SPEAKER;
        String strClass = ACTION_SPEAKER;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void MICROPHONETest_click(View view)
    {
        int requestCode = TEST_ITEM_MICROPHONE;
        String strClass = ACTION_MICROPHONE;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void HEADPHONETest_click(View view)
    {
        int requestCode = TEST_ITEM_HEADPHONE;
        String strClass = ACTION_HEADPHONE;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void HEADSETTest_click(View view) //AMIC
    {
        int requestCode = TEST_ITEM_HEADSET;
        String strClass = ACTION_HEADSET;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }


    public void CAMERA_BACKTest_click(View view)
    {
        int requestCode = TEST_ITEM_CAMERA_BACK;
        String strClass = ACTION_CAMERA_BACK;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void CAMERA_FRONTTest_click(View view)
    {
        int requestCode = TEST_ITEM_CAMERA_FRONT;
        String strClass = ACTION_CAMERA_FRONT;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void HDMITest_click(View view)
    {
        int requestCode = TEST_ITEM_HDMI;
        String strClass = ACTION_HDMI;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void OTGTest_click(View view)
    {
        int requestCode = TEST_ITEM_OTG;
        String strClass = ACTION_OTG;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }
    public void GPSTest_click(View view)
    {
        int requestCode = TEST_ITEM_GPS;
        String strClass = ACTION_GPS;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void BTTest_click(View view)
    {
        int requestCode = TEST_ITEM_BT;
        String strClass = ACTION_BT;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void ULANTest_click(View view)
    {
        int requestCode = TEST_ITEM_ULAN;
        String strClass = ACTION_ULAN;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }
    public void STORAGETest_click(View view)
    {
        int requestCode = TEST_ITEM_STORAGE;
        String strClass = ACTION_STORAGE;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void RTCTest_click(View view)
    {
        int requestCode = TEST_ITEM_RTC;
        String strClass = ACTION_RTC;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }
    public void DEVICE_INFOTest_click(View view)
    {
        int requestCode = TEST_ITEM_DEVICE_INFO;
        String strClass = ACTION_DEVICE_INFO;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }

    public void SERIAL_NUMBERTest_click(View view)
    {
        int requestCode = TEST_ITEM_SERIAL_NUMBER;
        String strClass = ACTION_SERIAL_NUMBER;
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
    }


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

    //
    private int fec_test_count_index=0;
    //FEC test class oreder : 11 items
    class fec_test_item{
        int fec_test_item_request_code;
        String fec_test_item_class;
        fec_test_item(int fec_test_item_request_code_l, String fec_test_item_class_l)
        {
            fec_test_item_request_code = fec_test_item_request_code_l;
            fec_test_item_class = fec_test_item_class_l;
        }
    }

    private fec_test_item[] fec_test_items_order = new fec_test_item[]{
            new fec_test_item(TEST_ITEM_SYSKING_IC_CARD, Class_SysKingICCard), //0
            new fec_test_item(TEST_ITEM_CASH_DRAWER, Class_CashDrawer),
            new fec_test_item(TEST_ITEM_ID_IC_CARD, Class_IDICCard),
            new fec_test_item(TEST_ITEM_FINGER_PRINTER, Class_FingerPrinter),
            new fec_test_item(TEST_ITEM_VFD_LCM, Class_VFDLCM),

            new fec_test_item(TEST_ITEM_NFC, Class_NFC),
            new fec_test_item(TEST_ITEM_THERMAL_PRINTER, Class_ThermalPrinter),
            new fec_test_item(TEST_ITEM_RFID, Class_RFID),
            new fec_test_item(TEST_ITEM_HID, Class_HID),
            new fec_test_item(TEST_ITEM_THERMAL_PRINTER_D10, Class_ThermalPrinterD10),

            new fec_test_item(TEST_ITEM_RS232_DEVICE, Class_RS232),  //10

            //ThunderSoft test items
            new fec_test_item(TEST_ITEM_BATTERY      , ACTION_BATTERY),  //11
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
            new fec_test_item(TEST_ITEM_STORAGE      , ACTION_STORAGE),

            new fec_test_item(TEST_ITEM_RTC          , ACTION_RTC),
            new fec_test_item(TEST_ITEM_DEVICE_INFO  , ACTION_DEVICE_INFO),
            new fec_test_item(TEST_ITEM_SERIAL_NUMBER, ACTION_SERIAL_NUMBER), //33

    };

    private String fec_test_items_class_order[]={Class_SysKingICCard,Class_CashDrawer, Class_IDICCard, Class_FingerPrinter
            ,Class_VFDLCM, Class_NFC, Class_ThermalPrinter, Class_RFID, Class_HID, Class_ThermalPrinterD10, Class_RS232};
    private int fec_test_item_request_code_order[]={TEST_ITEM_SYSKING_IC_CARD, TEST_ITEM_CASH_DRAWER, TEST_ITEM_ID_IC_CARD,
            TEST_ITEM_FINGER_PRINTER, TEST_ITEM_VFD_LCM, TEST_ITEM_NFC,
            TEST_ITEM_THERMAL_PRINTER, TEST_ITEM_RFID, TEST_ITEM_HID,
            TEST_ITEM_THERMAL_PRINTER_D10, TEST_ITEM_RS232_DEVICE};


    int fec_init_test_item =0;
    int thundersoft_init_test_item=11;
    int initial_test_item = fec_init_test_item;
    int NextTestItem=initial_test_item+1;
    boolean not_end_test_all = true;
    int end_test_item = max_test_items; //stop at ?th ; fec next test items:. 1, 2,3, 4~11; total= 11(fec)+23(thunder soft)=34 items.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        int result=0;
        TextView txtResult =(TextView) findViewById(R.id.textViewNFCTestResult);
        String strMsg = String.format("requestCode=%d, resultCode=%d",requestCode,resultCode);
        dump_trace("onActivityResult="+ strMsg);


        switch (requestCode) // which test item
        {
            case TEST_ITEM_NFC:
                txtResult = (TextView) findViewById(R.id.textViewNFCTestResult);
            break;
            case TEST_ITEM_THERMAL_PRINTER:
                txtResult = (TextView) findViewById(R.id.textViewThermalPrinterTestResult);
            break;
            case TEST_ITEM_VFD_LCM:
                txtResult = (TextView) findViewById(R.id.textViewLCMTestResult);
                break;
            case TEST_ITEM_FINGER_PRINTER:
                txtResult = (TextView) findViewById(R.id.textViewFingerPrintTestResult);
                break;
            case TEST_ITEM_ID_IC_CARD:
                txtResult = (TextView) findViewById(R.id.textViewIDICCardTestResult);
                break;
            case TEST_ITEM_CASH_DRAWER:
                txtResult = (TextView) findViewById(R.id.textViewCashDrawerTestResult);
                break;
            case TEST_ITEM_SYSKING_IC_CARD:
                txtResult = (TextView) findViewById(R.id.textViewICCardSysKingTestResult);
                break;
            case TEST_ITEM_RFID:
                txtResult = (TextView) findViewById(R.id.textViewRFIDTestResult);
                break;
            case TEST_ITEM_HID:
                txtResult = (TextView) findViewById(R.id.textViewHIDTestResult);
                break;
            case TEST_ITEM_THERMAL_PRINTER_D10:
                txtResult = (TextView) findViewById(R.id.textViewThermalPrinterD10TestResult);
                break;
            case TEST_ITEM_RS232_DEVICE:
                txtResult = (TextView) findViewById(R.id.textViewRS232TestResult);
                break;
            case TEST_ITEM_BATTERY:
                txtResult = (TextView) findViewById(R.id.textViewBatteryTestResult);
                break;
            case TEST_ITEM_LCM:
                txtResult = (TextView) findViewById(R.id.textViewLCMandBackLightTestResult);
                break;
            case TEST_ITEM_TP:
                txtResult = (TextView) findViewById(R.id.textViewTouchPanelTestResult);
                break;
            case TEST_ITEM_KEYPAN:
                txtResult = (TextView) findViewById(R.id.textViewKeyTestResult);
                break;
            case TEST_ITEM_WIFI:
                txtResult = (TextView) findViewById(R.id.textViewWifiTestResult);
                break;
            case TEST_ITEM_SENSOR:
                txtResult = (TextView) findViewById(R.id.textViewASensorTestResult);
                break;
            case TEST_ITEM_LIGHT:
                txtResult = (TextView) findViewById(R.id.textViewLightSensorTestResult);
                break;
            case TEST_ITEM_GYROSCOPE:
                txtResult = (TextView) findViewById(R.id.textViewGyroscopeSensorTestResult);
                break;
            case TEST_ITEM_SPEAKER:
                txtResult = (TextView) findViewById(R.id.textViewSPEAKERTestResult);
                break;
            case TEST_ITEM_MICROPHONE:
                txtResult = (TextView) findViewById(R.id.textViewMICROPHONETestResult);
                break;
            case TEST_ITEM_HEADPHONE:
                txtResult = (TextView) findViewById(R.id.textViewHEADPHONETestResult);
                break;
            case TEST_ITEM_HEADSET:
                txtResult = (TextView) findViewById(R.id.textViewHEADSETTestResult);
                break;
            case TEST_ITEM_CAMERA_BACK:
                txtResult = (TextView) findViewById(R.id.textViewCAMERA_BACKTestResult);
                break;
            case TEST_ITEM_CAMERA_FRONT:
                txtResult = (TextView) findViewById(R.id.textViewCAMERA_FRONTTestResult);
                break;
            case TEST_ITEM_HDMI:
                txtResult = (TextView) findViewById(R.id.textViewHDMITestResult);
                break;
            case TEST_ITEM_OTG:
                txtResult = (TextView) findViewById(R.id.textViewOTGTestResult);
                break;
            case TEST_ITEM_GPS:
                txtResult = (TextView) findViewById(R.id.textViewGPSTestResult);
                break;
            case TEST_ITEM_BT:
                txtResult = (TextView) findViewById(R.id.textViewBTTestResult);
                break;

            case TEST_ITEM_ULAN:
                txtResult = (TextView) findViewById(R.id.textViewULANTestResult);
                break;
            case TEST_ITEM_STORAGE:
                txtResult = (TextView) findViewById(R.id.textViewSTORAGETestResult);
                break;
            case TEST_ITEM_RTC:
                txtResult = (TextView) findViewById(R.id.textViewRTCTestResult);
                break;
            case TEST_ITEM_DEVICE_INFO:
                txtResult = (TextView) findViewById(R.id.textViewDEVICE_INFOTestResult);
                break;
            case TEST_ITEM_SERIAL_NUMBER:
                txtResult = (TextView) findViewById(R.id.textViewSERIAL_NUMBERTestResult);
                break;

        }
        String resultPASS="";
        if (requestCode >= TEST_ITEM_BATTERY){
            resultPASS = (resultCode == Activity.RESULT_OK ? RESULT_PASS : RESULT_FAIL);
            txtResult.setText(resultPASS);
        }else {
            if (resultCode == 1) {
                txtResult.setText("PASS"); //Activity.RESULT_CANCELED = 0 , so PASS use 1.
            } else if (resultCode == 0) {
                txtResult.setText("FAIL");
            }
        }

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
                FEC_Test_Item(fec_test_items_order[NextTestItem].fec_test_item_request_code, fec_test_items_order[NextTestItem].fec_test_item_class );
                NextTestItem ++;
            }else {
                // end test
                test_all = false;
            }
        }

    }



    private void DetermineTestItems()
    {
        fectest_config_path  = ((FECApplication) this.getApplication()).getFEC_config_path();

        configUtil.Device devObject;
        configUtil configFile = new configUtil(fectest_config_path);
        configFile.dom4jXMLParser();


        LinearLayout layout;
        layout= (LinearLayout)  findViewById(R.id.LLICCard);
        devObject = configFile.getDevice("SysKingICCardTest");
        if (!devObject.Test) {
            layout.setVisibility(View.GONE);
        }

        layout= (LinearLayout)  findViewById(R.id.LLBattery);
        //layout.setVisibility(View.GONE);
        //layout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkBuild checkBuildOK = new checkBuild();
        if (!checkBuildOK.checkVersion())
            return;
        setContentView(R.layout.activity_main);


        startFECLog();
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
        FEC_Test_Item(fec_test_items_order[initial_test_item].fec_test_item_request_code, fec_test_items_order[initial_test_item].fec_test_item_class );
    }


    public void FEC_Clear_ALL_UI_Test_Result()
    {
        TextView txtResult =(TextView) findViewById(R.id.textViewNFCTestResult);
        for (int i=0; i< max_test_items;i++){
            switch (i) // which test item
            {
                case TEST_ITEM_NFC:
                    txtResult = (TextView) findViewById(R.id.textViewNFCTestResult);
                    break;
                case TEST_ITEM_THERMAL_PRINTER:
                    txtResult = (TextView) findViewById(R.id.textViewThermalPrinterTestResult);
                    break;
                case TEST_ITEM_VFD_LCM:
                    txtResult = (TextView) findViewById(R.id.textViewLCMTestResult);
                    break;
                case TEST_ITEM_FINGER_PRINTER:
                    txtResult = (TextView) findViewById(R.id.textViewFingerPrintTestResult);
                    break;
                case TEST_ITEM_ID_IC_CARD:
                    txtResult = (TextView) findViewById(R.id.textViewIDICCardTestResult);
                    break;
                case TEST_ITEM_CASH_DRAWER:
                    txtResult = (TextView) findViewById(R.id.textViewCashDrawerTestResult);
                    break;
                case TEST_ITEM_SYSKING_IC_CARD:
                    txtResult = (TextView) findViewById(R.id.textViewICCardSysKingTestResult);
                    break;
                case TEST_ITEM_RFID:
                    txtResult = (TextView) findViewById(R.id.textViewRFIDTestResult);
                    break;
                case TEST_ITEM_HID:
                    txtResult = (TextView) findViewById(R.id.textViewHIDTestResult);
                    break;
                case TEST_ITEM_THERMAL_PRINTER_D10:
                    txtResult = (TextView) findViewById(R.id.textViewThermalPrinterD10TestResult);
                    break;
                case TEST_ITEM_RS232_DEVICE:
                    txtResult = (TextView) findViewById(R.id.textViewRS232TestResult);
                    break;
                case TEST_ITEM_BATTERY:
                    txtResult = (TextView) findViewById(R.id.textViewBatteryTestResult);
                    break;
                case TEST_ITEM_LCM:
                    txtResult = (TextView) findViewById(R.id.textViewLCMandBackLightTestResult);
                    break;
                case TEST_ITEM_TP:
                    txtResult = (TextView) findViewById(R.id.textViewTouchPanelTestResult);
                    break;
                case TEST_ITEM_KEYPAN:
                    txtResult = (TextView) findViewById(R.id.textViewKeyTestResult);
                    break;
                case TEST_ITEM_WIFI:
                    txtResult = (TextView) findViewById(R.id.textViewWifiTestResult);
                    break;
                case TEST_ITEM_SENSOR:
                    txtResult = (TextView) findViewById(R.id.textViewASensorTestResult);
                    break;
                case TEST_ITEM_LIGHT:
                    txtResult = (TextView) findViewById(R.id.textViewLightSensorTestResult);
                    break;
                case TEST_ITEM_GYROSCOPE:
                    txtResult = (TextView) findViewById(R.id.textViewGyroscopeSensorTestResult);
                    break;
                case TEST_ITEM_SPEAKER:
                    txtResult = (TextView) findViewById(R.id.textViewSPEAKERTestResult);
                    break;
                case TEST_ITEM_MICROPHONE:
                    txtResult = (TextView) findViewById(R.id.textViewMICROPHONETestResult);
                    break;
                case TEST_ITEM_HEADPHONE:
                    txtResult = (TextView) findViewById(R.id.textViewHEADPHONETestResult);
                    break;
                case TEST_ITEM_HEADSET:
                    txtResult = (TextView) findViewById(R.id.textViewHEADSETTestResult);
                    break;
                case TEST_ITEM_CAMERA_BACK:
                    txtResult = (TextView) findViewById(R.id.textViewCAMERA_BACKTestResult);
                    break;
                case TEST_ITEM_CAMERA_FRONT:
                    txtResult = (TextView) findViewById(R.id.textViewCAMERA_FRONTTestResult);
                    break;
                case TEST_ITEM_HDMI:
                    txtResult = (TextView) findViewById(R.id.textViewHDMITestResult);
                    break;
                case TEST_ITEM_OTG:
                    txtResult = (TextView) findViewById(R.id.textViewOTGTestResult);
                    break;
                case TEST_ITEM_GPS:
                    txtResult = (TextView) findViewById(R.id.textViewGPSTestResult);
                    break;
                case TEST_ITEM_BT:
                    txtResult = (TextView) findViewById(R.id.textViewBTTestResult);
                    break;

                case TEST_ITEM_ULAN:
                    txtResult = (TextView) findViewById(R.id.textViewULANTestResult);
                    break;
                case TEST_ITEM_STORAGE:
                    txtResult = (TextView) findViewById(R.id.textViewSTORAGETestResult);
                    break;
                case TEST_ITEM_RTC:
                    txtResult = (TextView) findViewById(R.id.textViewRTCTestResult);
                    break;
                case TEST_ITEM_DEVICE_INFO:
                    txtResult = (TextView) findViewById(R.id.textViewDEVICE_INFOTestResult);
                    break;
                case TEST_ITEM_SERIAL_NUMBER:
                    txtResult = (TextView) findViewById(R.id.textViewSERIAL_NUMBERTestResult);
                    break;

            }
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


    public  void startFECLog()
    {
        logUtill = new logUtil();
        Date newdate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        String date = format.format(newdate);
        logUtill.appendLog("["+ date +"]"+"[Start test]");
    }
}
