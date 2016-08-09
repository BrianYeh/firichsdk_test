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

public class MainSysKingICCardActivity extends Activity {

    SerialPort sp;

    int intSerialPortHandle = -1;
    final static int SLEEP_MSEC =1000;

    String strTagUtil = "MainSysKingICCardActivity.";

    String strtextViewSmardCardLRCResult="";
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


    //for smart card test command.
    private String strSmartCardttyUSBPath="/dev/ttyUSB4";

    byte[] btyVersion_msg = new byte[]{(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x03};
    //byte[] btyVersion_msg = new byte[]{(byte)0x41, (byte)0x42, (byte)0x43, (byte)0x44, (byte)0x45};
    byte[] btyActivation_msg = new byte[]{0x02,0x10,0x00,0x00,0x12};
    byte[] btyDeactivation_msg = new byte[]{0x02,0x20,0x00,0x00,0x22};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_king_iccard);
    }
    private void SleepMiniSecond(SerialPort spThread, int minSecond)
    {
        try {
            spThread.sleep(minSecond);
            dump_trace("SLEEP_MSEC="+ minSecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    /*
        try {
            sp.sleep(3*SLEEP_MSEC);
            dump_trace("SLEEP_MSEC="+ 3*SLEEP_MSEC);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        SleepMiniSecond(sp, 1000);

        byte[] btyVersion_msg_received = new byte[256];
        Arrays.fill( btyVersion_msg_received, (byte) 0 );
        int intDataReceivedLength=0;
        intDataReceivedLength = sp.getDataReceivedLength();
        int nRetry=0;
        while (intDataReceivedLength == 0)
        {
            SleepMiniSecond(sp, 1000);
            intDataReceivedLength = sp.getDataReceivedLength();
            nRetry++;
            if (nRetry == 3)
                break;
        }
        if ( intDataReceivedLength> 0) {
            btyVersion_msg_received = Arrays.copyOf(sp.getBytDataReceived(),intDataReceivedLength);
        }

        dump_trace("btyVersion_msg_received = "+ new String(btyVersion_msg_received) );
        smartCardUtil smartCardUtilTest = new smartCardUtil();
        boolean match = smartCardUtilTest.calculateLRC(btyVersion_msg_received, intDataReceivedLength);
        dump_trace("Test PASS or NO(true/false) === "+ match );

        sp.close(intSerialPortHandle);

        final TextView textViewSmardCardLRCResult = (TextView) findViewById(R.id.textViewSmardCardLRCResult);
        if (match) {
            strtextViewSmardCardLRCResult += "PASS. \n" + smartCardUtilTest.getLRCString();
            textViewSmardCardLRCResult.setText(strtextViewSmardCardLRCResult);
        }
        else {
            strtextViewSmardCardLRCResult += "FAIL! \n";
            textViewSmardCardLRCResult.setText(strtextViewSmardCardLRCResult );
        }


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

    public void Test_IC_Card_click(View view){
        final TextView textViewSmardCardLRCResult = (TextView) findViewById(R.id.textViewSmardCardLRCResult);
        strtextViewSmardCardLRCResult += "Test Version: \n";
        smart_card_test(btyVersion_msg);

        strtextViewSmardCardLRCResult += "Test Activation: \n";
        smart_card_test(btyActivation_msg);

        strtextViewSmardCardLRCResult += "Test DeActivation: \n";
        smart_card_test(btyDeactivation_msg);
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

    @Override
    protected void onStart() {
        super.onStart();

        configUtil.Device devObject;
        configUtil configFile = new configUtil();
        configFile.dom4jXMLParser();
        //strSmartCardttyUSBPath
        devObject = configFile.getDevice("SysKingICCardTest");
        if (devObject.Dev != null && !devObject.Dev.isEmpty()) {
            strSmartCardttyUSBPath = devObject.Dev;
        }
        EditText editTextTTYUSBPath = (EditText)findViewById(R.id.editTextTTYUSBPath);
        editTextTTYUSBPath.setText(strSmartCardttyUSBPath);
    }
}
