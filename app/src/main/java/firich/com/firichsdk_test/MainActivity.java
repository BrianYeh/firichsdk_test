package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;

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

    //for smart card test command.
    private String strSmartCardttyUSBPath="/dev/ttyUSB4";

    byte[] btyVersion_msg = new byte[]{(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x03};
    //byte[] btyVersion_msg = new byte[]{(byte)0x41, (byte)0x42, (byte)0x43, (byte)0x44, (byte)0x45};
    byte[] btyActivation_msg = new byte[]{0x02,0x10,0x00,0x00,0x12};
    byte[] btyDeactivation_msg = new byte[]{0x02,0x20,0x00,0x00,0x22};

    private boolean bDebugOn = true;



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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
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

    private boolean smart_card_test(byte[] btyCommand)
    {
        int intReturnCode = -1;
        //byte[] btyReceiveData_smart_card;

        // Open serial port
        sp = new SerialPort();

        dump_trace("smart_card_test.start");
        EditText editTextTTYUSBPath = (EditText)findViewById(R.id.editTextTTYUSBPath);
        String strEditTextTTYUSBPath = editTextTTYUSBPath.getText().toString();

        strSmartCardttyUSBPath = strEditTextTTYUSBPath;
        intSerialPortHandle = sp.openSmartCard(strSmartCardttyUSBPath,9600);

        dump_trace("openSmartCard: intSerialPortHandle="+intSerialPortHandle);

        // sendAndRecvMesgUart
        // 1. send: write version msg.
        // 2.    sleep
        // 3. Recev: read version msg.
        // 4. calulate LRC: calulateLRC

        // 1. send: write version msg.
        intReturnCode = sp.write(intSerialPortHandle,btyCommand);
        // The function returns the number of bytes written to the file.
        // A return value of -1 indicates an error, with errno set appropriately.
        dump_trace("write: intReturnCode="+intReturnCode);
        sp.setListener(splistener);


        try {
            sp.sleep(3*SLEEP_MSEC);
            dump_trace("SLEEP_MSEC="+ 3*SLEEP_MSEC);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        byte[] btyVersion_msg_received = new byte[256];
        int intDataReceivedLength=0;
        intDataReceivedLength = sp.getDataReceivedLength();
        if ( intDataReceivedLength> 0) {
            btyVersion_msg_received = Arrays.copyOf(sp.getBytDataReceived(),intDataReceivedLength);
        }

        dump_trace("btyVersion_msg_received = "+ new String(btyVersion_msg_received) );
        smartCardUtil smartCardUtilTest = new smartCardUtil();
        boolean match = smartCardUtilTest.calculateLRC(btyVersion_msg_received, intDataReceivedLength);
        dump_trace("Test PASS or NO(true/false) === "+ match );

        sp.close(intSerialPortHandle);

        final TextView textViewSmardCardLRCResult = (TextView) findViewById(R.id.textViewSmardCardLRCResult);

        textViewSmardCardLRCResult.setText(smartCardUtilTest.getLRCString());


        return match;
    }
    public void SC_Test_Version_click(View view) {


        smart_card_test(btyVersion_msg);


    }
    public void SC_Test_Activation_click(View view) {


        smart_card_test(btyActivation_msg);


    }
    public void SC_Test_Deactivation_click(View view) {


        smart_card_test(btyDeactivation_msg);


    }

    public String SetGPIOInOut(String echoGPIONum, String echoInOut, String strHighORLow)
    {
        int intReturnCode = -1;

        // adb shell "echo 162 > /sys/class/gpio/export"
        // Open serial port
        sp = new SerialPort();

        intSerialPortHandle = sp.openGPIO("/sys/class/gpio/export",bWirteForGPIO);

        intReturnCode = sp.write(intSerialPortHandle,echoGPIONum.getBytes());
        sp.close(intSerialPortHandle);
        dump_trace("echo "+echoGPIONum + " > "+"/sys/class/gpio/export");
        try {
            sp.sleep(1*SLEEP_MSEC);
            dump_trace("SLEEP_MSEC= 1 secs");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // adb shell "echo in > /sys/class/gpio/gpio162/direction
        sp = new SerialPort();
        String strGPIONumDirectionPath="";
        strGPIONumDirectionPath = "/sys/class/gpio/gpio"+echoGPIONum+"/direction";
        dump_trace("echo "+echoInOut + " > "+strGPIONumDirectionPath);
        //intSerialPortHandle = sp.openGPIO("/sys/class/gpio/gpio162/direction",bWirteForGPIO);
        intSerialPortHandle = sp.openGPIO(strGPIONumDirectionPath,bWirteForGPIO);
        intReturnCode = sp.write(intSerialPortHandle,echoInOut.getBytes()); // echo "in" or echo "out"
        sp.close(intSerialPortHandle);

        try {
            sp.sleep(1*SLEEP_MSEC);
            dump_trace("SLEEP_MSEC= 1 secs");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //set the value of GPIO:
        //ex: echo 1 > /sys/class/gpio/gpio393/value
        sp = new SerialPort();
        String strGPIONumValuePath="";
        strGPIONumValuePath = "/sys/class/gpio/gpio"+echoGPIONum+"/value";
        dump_trace("echo "+strHighORLow + " > "+strGPIONumValuePath);
        //intSerialPortHandle = sp.openGPIO("/sys/class/gpio/gpio162/direction",bWirteForGPIO);
        intSerialPortHandle = sp.openGPIO(strGPIONumValuePath,bWirteForGPIO);
        intReturnCode = sp.write(intSerialPortHandle,strHighORLow.getBytes()); // echo 1 or echo 0
        sp.close(intSerialPortHandle);



        return "No Cat value";
    }
    public void CashDrawer_Write(String echoGPIONum)
    {
    /*
我们有新添加接口，节点位置：/sys/kernel/debug/cash_drawer/cashdrawer
              控制GPIO393：write 11   Pull High   , write 10 Pull Low
              控制GPIO346：write 21   Pull High   , write 20 Pull Low
              获取GPIO393状态：read 此节点
              权限都有了，直接open, write,read
              root@cht_cr_mrd:/sys/kernel/debug/cash_drawer # ll
              -rwxrwxrwx root     root            0 2012-01-01 21:32 cashdrawer
              root@cht_cr_mrd:/sys/kernel/debug/cash_drawer #
    */
        int intReturnCode = -1;

        // adb shell "echo 11 > /sys/kernel/debug/cash_drawer/cashdrawer"
        // Open serial port
        sp = new SerialPort();
        String strCashDrawerPath="/sys/kernel/debug/cash_drawer/cashdrawer";
        intSerialPortHandle = sp.openGPIO(strCashDrawerPath,bWirteForGPIO);

        intReturnCode = sp.write(intSerialPortHandle,echoGPIONum.getBytes());
        sp.close(intSerialPortHandle);
        dump_trace("echo "+echoGPIONum + " > "+ strCashDrawerPath);
        try {
            sp.sleep(1*SLEEP_MSEC);
            dump_trace("SLEEP_MSEC= 1 secs");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String CashDrawer_Read()
    {
// adb shell "cat /sys/kernel/debug/cash_drawer/cashdrawer"

        sp = new SerialPort();
        String strGPIONumValuePath = "";
        strGPIONumValuePath = "/sys/kernel/debug/cash_drawer/cashdrawer";

        intSerialPortHandle = sp.openGPIO(strGPIONumValuePath, bReadFOrGPIO);
        sp.setListener(splistener);
        try {
            sp.sleep(2*SLEEP_MSEC);
            dump_trace("SLEEP_MSEC= 2 secs");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] btyVersion_msg_received = new byte[256];
        String strReadValue="";
        int intDataReceivedLength=0;
        intDataReceivedLength = sp.getDataReceivedLength();
        if ( intDataReceivedLength> 0) {
            btyVersion_msg_received = Arrays.copyOf(sp.getBytDataReceived(),intDataReceivedLength);
        }
        else
        {
            btyVersion_msg_received[0] = 0;
        }
       // smartCardUtil smartCardUtilTest = new smartCardUtil();
       // strReadValue = smartCardUtilTest.hex(btyVersion_msg_received[0]);
        strReadValue = Integer.toString(btyVersion_msg_received[0]);

        dump_trace(strGPIONumValuePath+" value  = "+ strReadValue );
        sp.close(intSerialPortHandle);
        return strReadValue;

    }

    public void CashDrawer_Test_346_click(View view) {
        CashDrawer_Write("21");
        CashDrawer_Write("20");
        CashDrawer_Write("21");
        CashDrawer_Write("20");
    }

    public void CashDrawer_Test_read_334_click(View view) {
        String strCashDrawerValue="";
        strCashDrawerValue = CashDrawer_Read();

        final TextView textViewCashDrawerValueResult = (TextView) findViewById(R.id.textViewCashDrawerValue);

        textViewCashDrawerValueResult.setText("Value="+strCashDrawerValue);
    }
    public void CashDrawer_Test_393_click(View view) {


        dump_trace("Read_GPIO_click: Enter");
        String echoGPIONum = "163";
        String echoIn = "in";
        String echoOut = "out";
        String echoInOut="";
        String strSetHighORLow="1";
        String catValue="";
/*
        adb shell "echo 162 > /sys/class/gpio/export"  : write echo 162 to sys/class/gpio/export file
        adb shell "echo in > /sys/class/gpio/gpio162/direction" write echo in to /sys/class/gpio/gpio162/direction
        adb shell "cat /sys/class/gpio/gpio162/value"   read from /sys/class/gpio/gpio162/value
*/
        /*

        adb shell "echo 393 > /sys/class/gpio/export"
        adb shell "echo out > /sys/class/gpio/gpio393/direction"
        adb shell "echo 1 > /sys/class/gpio/gpio393/value"
        pause
        adb shell "echo 393 > /sys/class/gpio/export"
        adb shell "echo out > /sys/class/gpio/gpio393/direction"
        adb shell "echo 0 > /sys/class/gpio/gpio393/value"
        timeout /t 1 /nobreak
        adb shell "echo 393 > /sys/class/gpio/export"
        adb shell "echo out > /sys/class/gpio/gpio393/direction"
        adb shell "echo 1 > /sys/class/gpio/gpio393/value"
         */

        //EditText editTextGPIO = (EditText)findViewById(R.id.editTextGPIO);
        //String strEditTextGPIO = editTextGPIO.getText().toString();

/*
        echoGPIONum = "393";
        echoInOut ="out";
        strSetHighORLow="1";
        SetGPIOInOut(echoGPIONum, echoInOut, strSetHighORLow);
*/
        CashDrawer_Write("11");
        CashDrawer_Write("10");
        CashDrawer_Write("11");
        CashDrawer_Write("10");




    }

    public void ID_ICCard_Test_click(View view)
    {
        Intent intent = new Intent(view.getContext(), IDICCard.class);
     //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
    //            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        view.getContext().startActivity(intent);

    }



    public void FingerPrinter_Test_click(View view)
    {
        Intent intent = new Intent(MainActivity.this, UareUSampleJava.class);
        //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        //            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private String strLCMttyUSBPath="/dev/ttyUSB2";
    byte[] btyInitialize = new byte[]{(byte)0x1b,(byte)0x40};
    byte[] btyClearScreen = new byte[]{0x0c};
    //Send command as 1B 51 41 30 31 32 33 34 35 36 37 38 39 41 42 43 44 45 46 47 48 49 4A 0D :upper string showing
    byte[] btyUpperString = new byte[]{(byte)0x1b, (byte)0x51, (byte)0x41,(byte)0x30,(byte)0x31,(byte)0x32,
            (byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)0x41,(byte)0x42,
            (byte)0x43,(byte)0x44,(byte)0x45,(byte)0x46,(byte)0x47,(byte)0x48,(byte)0x49,(byte)0x4a,(byte)0x0d};

    //byte[] btyLowerString = new byte[]{1B 51 42 4B 4C 4D 4E 4F 50 51 52 53 54 55 56 57 58 59 5A 5B 5C 5D 5F 0D};
    byte[] btyLowerString = hexStringToByteArray("1B51424B4C4D4E4F505152535455565758595A5B5C5D5F0D");



    private void SleepMiniSecond(int minSecond)
    {
        try {
            sp.sleep(minSecond);
            dump_trace("SLEEP_MSEC="+ minSecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void LCM_test(byte[] btyCommand)
    {
        int intReturnCode = -1;
        //byte[] btyReceiveData_smart_card;

        // Open serial port
        sp = new SerialPort();

        dump_trace("LCM.test.start");
        EditText editTextTTY_LCMUSBPath = (EditText)findViewById(R.id.editTextTTY_LCMUSBPath);
        String strEditTextTTY_LCMUSBPath = editTextTTY_LCMUSBPath.getText().toString();

        strLCMttyUSBPath = strEditTextTTY_LCMUSBPath;
        intSerialPortHandle = sp.open(strLCMttyUSBPath,9600);

        dump_trace("open LCM port: intSerialPortHandle="+intSerialPortHandle);

        // LCM display
                /*
                1.         Open serial port by 9600/19200 baud rate
                2.         Send command as 1B 40 to LCM for initialize
                3.         Send command as 0C to LCM for clear screen
                4.         Delay 1 sec
                5.         Send command as 1B 51 41 30 31 32 33 34 35 36 37 38 39 41 42 43 44 45 46 47 48 49 4A 0D to LCM for upper string showing
                6.         Send command as 1B 51 42 4B 4C 4D 4E 4F 50 51 52 53 54 55 56 57 58 59 5A 5B 5C 5D 5F 0D to LCM for lower string showing.
                7.         Close serial port
                */

        // 1. send: Send command as 1B 40 to LCM for initialize
        intReturnCode = sp.write(intSerialPortHandle,btyInitialize);
        // The function returns the number of bytes written to the file.
        // A return value of -1 indicates an error, with errno set appropriately.
        dump_trace("write: intReturnCode="+intReturnCode);
        sp.setListener(splistener);

        SleepMiniSecond(1000);

        //3.         Send command as 0C to LCM for clear screen
        intReturnCode = sp.write(intSerialPortHandle,btyClearScreen);
        SleepMiniSecond(1500);
        intReturnCode = sp.write(intSerialPortHandle,btyUpperString);
        SleepMiniSecond(1000);

        intReturnCode = sp.write(intSerialPortHandle, btyLowerString);

        byte[] btyVersion_msg_received = new byte[256];
        Arrays.fill( btyVersion_msg_received, (byte) 0 );
        int intDataReceivedLength=0;
        String strTestResult="";
        intDataReceivedLength = sp.getDataReceivedLength();
        if ( intDataReceivedLength>= 0) {
        btyVersion_msg_received = Arrays.copyOf(sp.getBytDataReceived(),intDataReceivedLength);
        }



        strTestResult = new String(btyVersion_msg_received);
       // dump_trace("btyVersion_msg_received = "+ strTestResult );

       // dump_trace("Test PASS or NO(true/false) === "+ strTestResult );

        sp.close(intSerialPortHandle);

     //   final TextView textViewLCMTestResult = (TextView) findViewById(R.id.textViewLCMTestResult);

     //   textViewLCMTestResult.setText("VFD/LCM Test Result:"+ strTestResult);

    }

        public void LCM_Test_click(View view)
        {
      LCM_test(btyInitialize);
        }
    public void NFC_Test_click(View view)
    {
        Intent intent = new Intent(view.getContext(), MainNFCActivity.class);

        view.getContext().startActivity(intent);

    }
}
