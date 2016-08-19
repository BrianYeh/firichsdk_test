package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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



    private boolean bDebugOn = true;

    // Test Package name.
    private final String PACKAGE_NAME = "firich.com.firichsdk_test";
    // Test items
    private final int TEST_ITEM_NFC=0;
    private final int TEST_ITEM_THERMAL_PRINTER=1;
    private final int TEST_ITEM_LCM=2;
    private final int TEST_ITEM_FINGER_PRINTER = 3;
    private final int TEST_ITEM_ID_IC_CARD = 4; //ID Tech
    private final int TEST_ITEM_CASH_DRAWER = 5;
    private final int TEST_ITEM_SYSKING_IC_CARD = 6; //新精：SYSKING
    private final int TEST_ITEM_RFID = 7;
    private final int TEST_ITEM_HID = 8;
    private final int TEST_ITEM_THERMAL_PRINTER_D10 = 9;
    private final int TEST_ITEM_RS232_DEVICE = 10;



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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkBuild checkBuildOK = new checkBuild();
        if (!checkBuildOK.checkVersion())
            return;
        setContentView(R.layout.activity_main);


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

    public void LCM_Test_click(View view)
    {

        int requestCode = TEST_ITEM_LCM;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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
            case TEST_ITEM_LCM:
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

        }
        if (resultCode == 1) {
            txtResult.setText("PASS"); //Activity.RESULT_CANCELED = 0 , so PASS use 1.
        }else if (resultCode == 0){
            txtResult.setText("FAIL");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        setTitle(" SN:" + Build.SERIAL);

 //       configUtil configFile = new configUtil();
 //       configFile.dom4jXMLParser();
    }
}
