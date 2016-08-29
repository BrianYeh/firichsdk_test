package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Arrays;

import firich.com.firichsdk.SerialPort;

public class MainLCMActivity extends Activity {
    SerialPort sp;
    int intSerialPortHandle = -1;

    private boolean bDebugOn = true;
    String strTagUtil = "MainLCMActivity.";


    private void dump_trace( String bytTrace)
    {
        if (bDebugOn)
            Log.d(strTagUtil, bytTrace);
    }
    public SerialPort.SerialPortListener splistener = new SerialPort.SerialPortListener() {
        public void onDataReceive(byte[] btyData){
            Log.d("onDataReceive",new String(btyData));
        }
    };

    private String strLCMttyUSBPath="/dev/ttyUSB2";
    byte[] btyInitialize = new byte[]{(byte)0x1b,(byte)0x40};
    byte[] btyClearScreen = new byte[]{0x0c};
    //Send command as 1B 51 41 30 31 32 33 34 35 36 37 38 39 41 42 43 44 45 46 47 48 49 4A 0D :upper string showing
    byte[] btyUpperString = new byte[]{(byte)0x1b, (byte)0x51, (byte)0x41,(byte)0x30,(byte)0x31,(byte)0x32,
            (byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)0x41,(byte)0x42,
            (byte)0x43,(byte)0x44,(byte)0x45,(byte)0x46,(byte)0x47,(byte)0x48,(byte)0x49,(byte)0x4a,(byte)0x0d};

    //byte[] btyLowerString = new byte[]{1B 51 42 4B 4C 4D 4E 4F 50 51 52 53 54 55 56 57 58 59 5A 5B 5C 5D 5F 0D};
    byte[] btyLowerString = hexStringToByteArray("1B51424B4C4D4E4F505152535455565758595A5B5C5D5F0D");

    byte[] btyLowerStringDump = hexStringToByteArray("4B4C4D4E4F505152535455565758595A5B5C5D5F0D00");

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
    private void LCM_test(byte[] btyCommand)
    {
        int intReturnCode = -1;
        String strTestResult="";
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
        //strTestResult = new String(btyLowerStringDump);
        //dump_trace("VFD LCM : btyUpperString = " + strTestResult);
        SleepMiniSecond(1000);

        intReturnCode = sp.write(intSerialPortHandle, btyLowerString);

        byte[] btyVersion_msg_received = new byte[256];
        Arrays.fill( btyVersion_msg_received, (byte) 0 );
        int intDataReceivedLength=0;

        intDataReceivedLength = sp.getDataReceivedLength();
        //Note:  It will not get received message.....so intDataReceivedLength is 0.
        if ( intDataReceivedLength>= 0) {
            btyVersion_msg_received = Arrays.copyOf(sp.getBytDataReceived(),intDataReceivedLength);

        }



        //strTestResult = new String(btyVersion_msg_received);
        // dump_trace("btyVersion_msg_received = "+ strTestResult );

        // dump_trace("Test PASS or NO(true/false) === "+ strTestResult );

        sp.close(intSerialPortHandle);

        //   final TextView textViewLCMTestResult = (TextView) findViewById(R.id.textViewLCMTestResult);

        //   textViewLCMTestResult.setText("VFD/LCM Test Result:"+ strTestResult);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkBuild checkBuildOK = new checkBuild();
        if (!checkBuildOK.checkVersion())
            return;

        setContentView(R.layout.activity_lcm);
    }
    @Override
    protected void onStart(){
        super.onStart();
        configUtil.Device devObject;
        configUtil configFile = new configUtil();
        configFile.dom4jXMLParser();
        //strSmartCardttyUSBPath
        devObject = configFile.getDevice("VFDLCMTest");
        if (devObject.Dev != null && !devObject.Dev.isEmpty()) {
            strLCMttyUSBPath = devObject.Dev;
        }
        EditText editTextTTYUSBPath = (EditText)findViewById(R.id.editTextTTY_LCMUSBPath);
        editTextTTYUSBPath.setText(strLCMttyUSBPath);

    }
    public void LCM_Test_click(View view)
    {
        LCM_test(btyInitialize);
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
