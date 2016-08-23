package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;

import firich.com.firichsdk.SerialPort;

public class MainNFCActivity extends Activity {

    SerialPort sp;
    int intSerialPortHandle = -1;

    private boolean bDebugOn = true;
    String strTagUtil = "MainNFCActivity.";
    private Handler mHandler = null; //Brian


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

        setContentView(R.layout.activity_nfc);
        this.mHandler = new Handler(); //Brian:

    }
    private String strNFCttyUSBPath="/dev/ttyUSB0";
    private byte[] btyReturnMessage = new byte[50];
    private final int intCommandLength=9;
    private byte[] gbtyCommand= new byte[intCommandLength];

    //**************************************************************************************************************************************************************
//E1 get firmware version: SOH + "S   0  1  E  1" +          STX + ETX +BCC
//                         01     53 30 31 45 31             02    03   Cal= SOH 到 ETX 每一個 byte 作 XOR後, 再 OR 0X20
    byte[] btyE1GetFirmwareVer = new byte[]{(byte)0x01,
                                            (byte)0x53, (byte)0x30, (byte)0x31, (byte)0x45, (byte)0x31,
                                            (byte)0x02, (byte)0x03, (byte)0x00};
    //**************************************************************************************************************************************************************
//A0 讀取卡片並連續讀取卡號: SOH +  "S   0  1  A  0" +          STX + ETX +BCC
//                         01     53 30 31 41 30             02    03   Cal= SOH 到 ETX 每一個 byte 作 XOR後, 再 OR 0X20
    byte[] btyA0_cmd_read_card_continue_number = new byte[]{(byte)0x01,
                                                            (byte)0x53, (byte)0x30, (byte)0x31, (byte)0x41, (byte)0x30,
                                                            (byte)0x02, (byte)0x03, (byte)0x00, (byte)0x00};
    //*************************************************************************************************************************************************************
//A1 讀取卡片並傳回卡號:     SOH +  "S   0  1  A  1" +          STX + ETX +BCC
//                         01     53 30 31 41 31             02    03   Cal= SOH 到 ETX 每一個 byte 作 XOR後, 再 OR 0X20
    byte[] btyA1_cmd_read_card_number = new byte[]{(byte)0x01,
                                                    (byte)0x53, (byte)0x30, (byte)0x31, (byte)0x41, (byte)0x31,
                                                    (byte)0x02, (byte)0x03, (byte)0x00, (byte)0x00};
    //*************************************************************************************************************************************************************
//A9 讀取卡片並傳回卡號和類型:     SOH +  "S   0  1  A  9" +          STX + ETX +BCC
//                              01     53 30 31 41 39             02    03   Cal= SOH 到 ETX 每一個 byte 作 XOR後, 再 OR 0X20
    byte[] btyA9_cmd_read_card_number_and_type = new byte[]{(byte)0x01,
                                                    (byte)0x53, (byte)0x30, (byte)0x31, (byte)0x41, (byte)0x39,
                                                    (byte)0x02, (byte)0x03, (byte)0x00, (byte)0x00};


    private byte cal_BCC(byte[] btyCommand,int intCommandLength)
    {
        int i=0;
        int bytBCC=btyCommand[0];

        for (i =1 ; i< intCommandLength; i++ )
        {
            bytBCC = (bytBCC)^(btyCommand[i]);
        }
        bytBCC = bytBCC | 0x20;
        return (byte)bytBCC;
    }

    int gintDataReceivedLength=0;

    private int NFC_getReturnLength()
    {
        return gintDataReceivedLength;
    }
    private void NFC_setReturnLength(int length)
    {
        gintDataReceivedLength = length;
    }
    private byte[] NFC_read_return_message()
    {
        int intReturnCode = -1;
        // Open serial port
        sp = new SerialPort();
        intSerialPortHandle = sp.openNFC(strNFCttyUSBPath,9600);
        sp.setListener(splistener);
        SleepMiniSecond(sp, 1000);

        byte[] btyVersion_msg_received = new byte[256];
        Arrays.fill( btyVersion_msg_received, (byte) 0 );
        int intDataReceivedLength=0;
        String strTestResult="";

        intDataReceivedLength = sp.getDataReceivedLength();
        if ( intDataReceivedLength>= 0) {
            btyVersion_msg_received = Arrays.copyOf(sp.getBytDataReceived(),intDataReceivedLength);
        }
        NFC_setReturnLength(intDataReceivedLength);
        return btyVersion_msg_received;
    }
    private byte[] NFC_get_return_value_of_write_cmd(byte[] btyCommand)
    {
        int intReturnCode = -1;
// Open serial port
        sp = new SerialPort();

        dump_trace("NFC.test.start");
        EditText editText_TTYUSBPath = (EditText)findViewById(R.id.editNFC_usb_path);
        String strEditTextTTYUSBPath = editText_TTYUSBPath.getText().toString();

        strNFCttyUSBPath = strEditTextTTYUSBPath;
        intSerialPortHandle = sp.openNFC(strNFCttyUSBPath,9600);

        dump_trace("open NFC port: intSerialPortHandle="+intSerialPortHandle);
// 1. send: Send command
        intReturnCode = sp.write(intSerialPortHandle,btyCommand);
// The function returns the number of bytes written to the file.
// A return value of -1 indicates an error, with errno set appropriately.
        dump_trace("write: intReturnCode="+intReturnCode);
        sp.setListener(splistener);
        SleepMiniSecond(sp, 1000);

        byte[] btyVersion_msg_received = new byte[256];
        Arrays.fill( btyVersion_msg_received, (byte) 0 );
        int intDataReceivedLength=0;
        String strTestResult="";

        intDataReceivedLength = sp.getDataReceivedLength();
        int nRetry=0;
        while (intDataReceivedLength == 0)
        {
            SleepMiniSecond(sp, 1000);
            intDataReceivedLength = sp.getDataReceivedLength();
            nRetry++;
            if (nRetry == 4)
                break;
        }
        if ( intDataReceivedLength>= 0) {
            btyVersion_msg_received = Arrays.copyOf(sp.getBytDataReceived(),intDataReceivedLength);
        }
        NFC_setReturnLength(intDataReceivedLength);
        sp.close(intSerialPortHandle);
        return btyVersion_msg_received;
    }
    private enum NFC_cmd_type {
        E1, A0, A1, A9;
    }
    String strTestResult="";
    private boolean NFC_test_cmd_Func(byte[] btyCommand, String strCommandType)
    {
        boolean testNFCCmdPASS = false;

        NFC_cmd_type  NFC_cmd_type_value =  NFC_cmd_type.valueOf(strCommandType); //


        byte[] btyNFCReturnMessage = new byte[256];
        byte[] btyNFCReturnMessage2 = new byte[256];
        int intNFCReturnMessageLength=0;
        Arrays.fill( btyNFCReturnMessage, (byte) 0 );
        Arrays.fill( btyNFCReturnMessage2, (byte) 0 );
        btyNFCReturnMessage = NFC_get_return_value_of_write_cmd(btyCommand);
        intNFCReturnMessageLength = NFC_getReturnLength();

        int DataLength=0;
        DataLength = intNFCReturnMessageLength -7-2;
        if (DataLength >=0 ) {
            switch (NFC_cmd_type_value) {
                case E1: //E1 get firmware version
                    if ((btyNFCReturnMessage[7] != 0x41) && DataLength > 0)// is not "N"
                    {
                        btyNFCReturnMessage2 = Arrays.copyOfRange(btyNFCReturnMessage, 7, intNFCReturnMessageLength-2 );
                        strTestResult = new String(btyNFCReturnMessage2);
                        strTestResult = "Firmware version:"  + strTestResult;
                        testNFCCmdPASS = true;
                        PostUIUpdateButton(true);

                    } else {
                        strTestResult = " N ; Read fail!!";
                        testNFCCmdPASS = false;
                    }
                    break;
                case A0: //A0 讀取卡片並連續讀取卡號
                    // 會先回應 'Y' 再執行讀取動作
                    if (DataLength > 0)//
                    {
                        if (btyNFCReturnMessage[7] == 0x59)// 'Y'
                        {
                            //SleepMiniSecond(sp, 5000);

                            btyNFCReturnMessage2 = Arrays.copyOfRange(btyNFCReturnMessage, 7, intNFCReturnMessageLength - 2);
                            strTestResult = new String(btyNFCReturnMessage2);
                            strTestResult = "Y : PASS. " + strTestResult;
                        } else {
                            btyNFCReturnMessage2 = Arrays.copyOfRange(btyNFCReturnMessage, 7, intNFCReturnMessageLength - 2);
                            strTestResult = new String(btyNFCReturnMessage2);
                        }
                        testNFCCmdPASS = true;
                    } else {
                        strTestResult = " Read fail!!";
                        testNFCCmdPASS = false;
                    }
                    // Call get version to stop.
                    SleepMiniSecond(sp, 2000);
                    btyE1GetFirmwareVer[intCommandLength - 1] = cal_BCC(btyE1GetFirmwareVer, intCommandLength - 1);
                    NFC_get_return_value_of_write_cmd(btyE1GetFirmwareVer);
                    break;
                case A1: //A1讀取卡片並傳回卡號
                case A9:
                    if ((btyNFCReturnMessage[7] != 0x4E) && DataLength > 0)// is not "N"
                    {
                        btyNFCReturnMessage2 = Arrays.copyOfRange(btyNFCReturnMessage, 7, intNFCReturnMessageLength - 2);
                        String strTestResult2 = new String(btyNFCReturnMessage2);
                        strTestResult += "\nCard number: " + strTestResult2;
                    } else {
                        //strTestResult += "\n N ; Please put a card!!";
                        strTestResult += "\nPlease put a card!!";
                    }
                    break;

            }
        }
        dump_trace("NFC return message = "+ strTestResult );


        /*
        final TextView textViewReturnMessaget = (TextView) findViewById(R.id.NFC_ReturnMessage);
        textViewReturnMessaget.setText("NFC Test Result:"+ strTestResult);
        */
        PostUIUpdateLog(strTestResult);

        return testNFCCmdPASS;

    }

    private void PostUIUpdateButton(final boolean show)
    {
        this.mHandler.post(new Runnable()
        {
            public void run()
            {
                Button btn = (Button) findViewById(R.id.btnA1);
                //btn.setVisibility(View.INVISIBLE);
                if (show)
                    btn.setVisibility(View.VISIBLE);
                else
                    btn.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void PostUIUpdateLog(final String strTestResult)
    {
        this.mHandler.post(new Runnable()
        {
            public void run()
            {
                final TextView textViewReturnMessaget = (TextView) findViewById(R.id.NFC_ReturnMessage);
                textViewReturnMessaget.setText(strTestResult);
            }
        });
    }

    private void NFC_test_cmd(byte[] btyCommand, String strCommandType)
    {
        boolean testNFCCmdPASS = false;
        //testNFCCmdPASS= NFC_test_cmd_Func(btyCommand, strCommandType);
        NFC_test_cmd_Thread NFC_test_cmd_ThreadP = new NFC_test_cmd_Thread(btyCommand, strCommandType);
        NFC_test_cmd_ThreadP.start();
    }

    private class NFC_test_cmd_Thread extends Thread {
        String strCommandType;
        byte[] btyCommand = new byte[256];
        NFC_test_cmd_Thread(byte[] lbtyCommand, String lstrCommandType) {
            Arrays.fill( btyCommand, (byte) 0 );
            btyCommand = Arrays.copyOf(lbtyCommand, 255);
            strCommandType = lstrCommandType;
        }

        public void run() {
            // compute primes larger than minPrime
            boolean bConnectOK = false;
            bConnectOK = NFC_test_cmd_Func(btyCommand,strCommandType );
            /*
            do {
                bConnectOK = NFC_test_cmd_Func(btyCommand,strCommandType );
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

    private void testGetFirmwareVer()
    {
        boolean testPASS = false;
        // E1傳回韌體版本	        01-53-30-31-45-31-02-03-26
        btyE1GetFirmwareVer[intCommandLength-1] = cal_BCC(btyE1GetFirmwareVer, intCommandLength-1 );

        String strCommand="";
        for (int i=0;i <intCommandLength ; i++){
            strCommand += " "+ smartCardUtil.hex(btyE1GetFirmwareVer[i]);
        }
        //dump_trace("NFC E1GetFirmwareVer BCC="+smartCardUtil.hex(btyE1GetFirmwareVer[intCommandLength-1]));
        dump_trace("NFC E1GetFirmwareVer command ="+  strCommand );
        NFC_test_cmd(btyE1GetFirmwareVer, "E1");
    }
    public void NFC_GetFirmwareVer_click(View view){

        testGetFirmwareVer();
    }
    public void NFC_A0_cmd_click(View view)
    {
        // A0 讀取卡片並連續讀取卡號: SOH +  "S   0  1  A  0" +          STX + ETX +BCC
        //                         01     53 30 31 41 30             02    03   Cal= SOH 到 ETX 每一個 byte 作 XOR後, 再 OR 0X20
        // A0讀取卡片並連續讀取卡號	01-53-30-31-41-30-02-03-23
        btyA0_cmd_read_card_continue_number[intCommandLength-1] = cal_BCC(btyA0_cmd_read_card_continue_number, intCommandLength-1 );

        String strCommand="";
        for (int i=0;i <intCommandLength ; i++){
            strCommand += " "+ smartCardUtil.hex(btyA0_cmd_read_card_continue_number[i]);
        }
        //dump_trace("NFC A0 BCC="+smartCardUtil.hex(btyA0_cmd_read_card_continue_number[intCommandLength-1]));
        dump_trace("NFC A0 command ="+  strCommand );
        NFC_test_cmd(btyA0_cmd_read_card_continue_number, "A0");
    }
    public void NFC_A1_cmd_click(View view)
    {
        // A1 讀取卡片並傳回卡號:     SOH +  "S   0  1  A  1" +          STX + ETX +BCC
        //                         01     53 30 31 41 31             02    03   Cal= SOH 到 ETX 每一個 byte 作 XOR後, 再 OR 0X20
        // A1讀取卡片並傳回卡號	01-53-30-31-41-31-02-03-22
        Arrays.fill(  gbtyCommand, (byte) 0 );
        gbtyCommand = Arrays.copyOf(btyA1_cmd_read_card_number, intCommandLength);
        gbtyCommand[intCommandLength-1] = cal_BCC(gbtyCommand, intCommandLength-1 );

        String strCommand="";
        for (int i=0;i <intCommandLength ; i++){
            strCommand += " "+ smartCardUtil.hex(gbtyCommand[i]);
        }
        //dump_trace("NFC A1 BCC="+smartCardUtil.hex(gbtyCommand[intCommandLength-1]));
        dump_trace("NFC A1 command ="+  strCommand );
        NFC_test_cmd(gbtyCommand, "A1");
    }
    public void NFC_A9_cmd_click(View view)
    {
        //A9 讀取卡片並傳回卡號和類型:     SOH +  "S   0  1  A  9" +          STX + ETX +BCC
        //                              01     53 30 31 41 39             02    03   Cal= SOH 到 ETX 每一個 byte 作 XOR後, 再 OR 0X20
        // A9讀取卡片並傳回卡號和類型	01-53-30-31-41-39-02-03-2A
        Arrays.fill(  gbtyCommand, (byte) 0 );
        gbtyCommand = Arrays.copyOf(btyA9_cmd_read_card_number_and_type, intCommandLength);
        gbtyCommand[intCommandLength-1] = cal_BCC(gbtyCommand, intCommandLength-1 );

        String strCommand="";
        for (int i=0;i <intCommandLength ; i++){
            strCommand += " "+ smartCardUtil.hex(gbtyCommand[i]);
        }
        //dump_trace("NFC A1 BCC="+smartCardUtil.hex(gbtyCommand[intCommandLength-1]));
        dump_trace("NFC A9 command ="+  strCommand );
        NFC_test_cmd(gbtyCommand, "A9");
    }

    @Override
    protected void onStart(){
        super.onStart();
        configUtil.Device devObject;
        configUtil configFile = new configUtil();
        configFile.dom4jXMLParser();
        //strSmartCardttyUSBPath
        devObject = configFile.getDevice("NFCTest");
        if (devObject.Dev != null && !devObject.Dev.isEmpty()) {
            strNFCttyUSBPath = devObject.Dev;
        }
        EditText editTextTTYUSBPath = (EditText)findViewById(R.id.editNFC_usb_path);
        editTextTTYUSBPath.setText(strNFCttyUSBPath);
        PostUIUpdateLog("Please wait for getting firmware version..");
        Button btn = (Button) findViewById(R.id.btnA1);
        btn.setVisibility(View.INVISIBLE);
        testGetFirmwareVer();
       // btn.setVisibility(View.VISIBLE);


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
