package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;

import firich.com.firichsdk.SerialPort;

public class MainRFIDActivity extends Activity {

    SerialPort sp;
    int intSerialPortHandle = -1;
    private Handler mHandler = null; //Brian

    private boolean bDebugOn = true;
    String strTagUtil = "MainRFIDActivity.";

    private String strttyUSBPath="/dev/ttyUSB4";
    private int intBaudRate=9600;


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
    private void SleepMiniSecond(SerialPort spThread, int minSecond)
    {
        try {
            spThread.sleep(minSecond);
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

        setContentView(R.layout.activity_rfid);
        this.mHandler = new Handler(); //Brian:
    }

    @Override
    protected void onStart(){
        super.onStart();
        configUtil.Device devObject;
        configUtil configFile = new configUtil();
        configFile.dom4jXMLParser();
        devObject = configFile.getDevice("RFIDTest");
        if (devObject.Dev != null && !devObject.Dev.isEmpty()) {
            strttyUSBPath = devObject.Dev;
        }
        if (devObject.BaudRate != 0 ) {
            intBaudRate = devObject.BaudRate;
        }
        EditText editTextTTYUSBPath = (EditText)findViewById(R.id.editRFID_usb_path);
        editTextTTYUSBPath.setText(strttyUSBPath);

        final TextView textViewRFID = (TextView) findViewById(R.id.RFID_ReturnMessage);
        textViewRFID.setText("Please put a RFID card!!");
        RFID_Test_Thread_Func();


    }
    public String hex(int n) {
        // call toUpperCase() if that's required
        return String.format("0x%2s", Integer.toHexString(n)).replace(' ', '0');
    }
    private void PostUIUpdateLog(final String strTestResult)
    {
        this.mHandler.post(new Runnable()
        {
            public void run()
            {
                final TextView textViewReturnMessaget = (TextView) findViewById(R.id.RFID_ReturnMessage);
                textViewReturnMessaget.setText(strTestResult);
            }
        });
    }

    private class RFID_Test_Thread extends Thread {
        RFID_Test_Thread() {
        }

        public void run() {
            // compute primes larger than minPrime
            boolean testPASS = false;
            Intent intent = getIntent();

            testPASS = RFID_Test();
            if (testPASS){
                setResult(1, intent);
            }else{
                setResult(0, intent);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finish();
            /*
            do {
                bConnectOK = RFID_Test();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } while (!bConnectOK);
            */
        }
    }

    public void RFID_Test_Thread_Func()
    {
        RFID_Test_Thread RFID_Test_ThreadP = new RFID_Test_Thread();
        RFID_Test_ThreadP.start();
    }
    private boolean RFID_Test()
    {
        int intReturnCode = -1;
        boolean testPASS = false;
        sp = new SerialPort();

        dump_trace("RFID.test.start");
        EditText editText_TTYUSBPath = (EditText)findViewById(R.id.editRFID_usb_path);
        String strEditTextTTYUSBPath = editText_TTYUSBPath.getText().toString();
        final TextView textViewRFID = (TextView) findViewById(R.id.RFID_ReturnMessage);
        //PostUIUpdateLog("Please put a RFID card!!");
        //textViewRFID.setText("Please put a RFID card!!");

        strttyUSBPath = strEditTextTTYUSBPath;
        intSerialPortHandle = sp.open(strttyUSBPath,intBaudRate);

        dump_trace("open NFC port: intSerialPortHandle="+intSerialPortHandle);
//
//        intReturnCode = sp.write(intSerialPortHandle,btyCommand);
// The function returns the number of bytes written to the file.
// A return value of -1 indicates an error, with errno set appropriately.
//        dump_trace("write: intReturnCode="+intReturnCode);
        sp.setListener(splistener);
        //SleepMiniSecond(sp, 1000);

        byte[] btyVersion_msg_received = new byte[256];
        Arrays.fill( btyVersion_msg_received, (byte) 0 );
        int intDataReceivedLength=0;
        String strTestResult="";

        intDataReceivedLength = sp.getDataReceivedLength();
        SleepMiniSecond(sp, 1000);
        intDataReceivedLength = 0;
        int nRetry=0;
        while (intDataReceivedLength == 0)
        {
            SleepMiniSecond(sp, 500);
            intDataReceivedLength = sp.getDataReceivedLength();
            nRetry++;
            if (nRetry == 16)
                break;
        }
        if ( intDataReceivedLength>= 0) {
            btyVersion_msg_received = Arrays.copyOf(sp.getBytDataReceived(),intDataReceivedLength);
        }

        String strRFID="";
        for (int i=0; i< intDataReceivedLength; i++){
            strRFID += " "+hex(btyVersion_msg_received[i]);
        }

        String strResult="";
        if (intDataReceivedLength >0) {
            //textViewRFID.setText("PASS!" + strRFID);
            //strResult = "PASS!" + strRFID;
            strResult = "PASS!";
            testPASS = true;
        }
        else {
            //textViewRFID.setText("FAIL!" + strRFID);
            strResult = "FAIL!" + strRFID;
        }
        PostUIUpdateLog(strResult);
        sp.close(intSerialPortHandle);

        return testPASS;
    }

    public void RFID_Test_click(View view)
    {
        RFID_Test();
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
