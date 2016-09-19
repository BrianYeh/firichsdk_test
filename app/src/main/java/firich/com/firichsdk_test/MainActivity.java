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
    private final int TEST_ITEM_HID = 8;
    private final int TEST_ITEM_THERMAL_PRINTER_D10 = 9;
    private final int TEST_ITEM_RS232_DEVICE = 10;

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
    private final int TEST_ITEM_ETHERNET = 30;
    private final int TEST_ITEM_STORAGE = 31;
    private final int TEST_ITEM_RTC = 32;
    private final int TEST_ITEM_DEVICE_INFO = 33;
    private final int TEST_ITEM_SERIAL_NUMBER = 34;
    private final int TEST_ITEM_SDCARD = 35;
    private final int TEST_ITEM_USB_STORAGE = 36;

    final private int max_test_items= 37;

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
    public void EthernetTest_click(View view)
    {
        int requestCode = TEST_ITEM_ETHERNET;
        String strClass = PACKAGE_NAME+".MainEthernetActivity";
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
        startActivityForResult(intent, requestCode);
    }

    public void SDCard_click(View view)
    {
        int requestCode = TEST_ITEM_SDCARD;
        String strClass = PACKAGE_NAME+".MainSDCardActivity";
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(PACKAGE_NAME, strClass);
        intent.setComponent(cn);
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
    final String Class_ETHERNET = ".MainEthernetActivity";

    //
    private int fec_test_count_index=0;
    //FEC test class oreder : 11 items
    class fec_test_item{
        int fec_test_item_request_code;
        String fec_test_item_class;
        boolean test;
        String name; // test name
        fec_test_item(int fec_test_item_request_code_l, String fec_test_item_class_l)
        {
            fec_test_item_request_code = fec_test_item_request_code_l;
            fec_test_item_class = fec_test_item_class_l;
            test = true;
            name ="";
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
            new fec_test_item(TEST_ITEM_ETHERNET         , Class_ETHERNET), //Brian:
            new fec_test_item(TEST_ITEM_STORAGE      , ACTION_STORAGE),

            new fec_test_item(TEST_ITEM_RTC          , ACTION_RTC),
            new fec_test_item(TEST_ITEM_DEVICE_INFO  , ACTION_DEVICE_INFO),
            new fec_test_item(TEST_ITEM_SERIAL_NUMBER, ACTION_SERIAL_NUMBER), //34

    };

    /*
    private String fec_test_items_class_order[]={Class_SysKingICCard,Class_CashDrawer, Class_IDICCard, Class_FingerPrinter
            ,Class_VFDLCM, Class_NFC, Class_ThermalPrinter, Class_RFID, Class_HID, Class_ThermalPrinterD10, Class_RS232};
    private int fec_test_item_request_code_order[]={TEST_ITEM_SYSKING_IC_CARD, TEST_ITEM_CASH_DRAWER, TEST_ITEM_ID_IC_CARD,
            TEST_ITEM_FINGER_PRINTER, TEST_ITEM_VFD_LCM, TEST_ITEM_NFC,
            TEST_ITEM_THERMAL_PRINTER, TEST_ITEM_RFID, TEST_ITEM_HID,
            TEST_ITEM_THERMAL_PRINTER_D10, TEST_ITEM_RS232_DEVICE};
    */

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
            case TEST_ITEM_ETHERNET:
                txtResult = (TextView) findViewById(R.id.textViewEthernetTestResult);
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
        String testResult="";

        if ((requestCode >= TEST_ITEM_BATTERY) && (requestCode<=TEST_ITEM_SERIAL_NUMBER)){
            if (requestCode ==  TEST_ITEM_ETHERNET)
            {
                if (resultCode == 1) {
                    testResult = "PASS";
                    //txtResult.setText("PASS"); //Activity.RESULT_CANCELED = 0 , so PASS use 1.
                } else if (resultCode == 0) {
                    testResult = "FAIL";
                    //txtResult.setText("FAIL");
                }
            }else {
                resultPASS = (resultCode == Activity.RESULT_OK ? RESULT_PASS : RESULT_FAIL);
                txtResult.setText(resultPASS);
            }
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
        do {

            not_end_test_all = (NextTestItemL != end_test_item);
            if (not_end_test_all) {

                NeedTest = fec_test_items_order[NextTestItemL].test;
                if(NeedTest){
                    break;
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
        for (Enumeration<configItemsUIUtil.configItemUI> e = g_configUIItemsFile.getHashtableConfigUI().elements(); e.hasMoreElements();){
            configItemsUIUtil.configItemUI configItemUIObject = e.nextElement();
            String LLID = "linearLayout_test_item_" + configItemUIObject.id;
            int resID = getResources().getIdentifier(LLID, "id", "firich.com.firichsdk_test");
            layout = (LinearLayout) findViewById(resID);
            int id = Integer.valueOf(configItemUIObject.id);
            id--; // array of fec_test_items_order, start from 0.
            fec_test_items_order[id].name = configItemUIObject.name;
            if (!configItemUIObject.test){
                layout.setVisibility(View.GONE); // don't show that test item.
                fec_test_items_order[id].test = false; // don't need to test for test all function.
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
        setContentView(R.layout.activity_main);

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
        NextTestItem = find_next_test_item(-1);
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
