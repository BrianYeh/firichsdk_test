package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
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


        // adb shell "cat /sys/class/gpio/gpio162/value"
        /*
        sp = new SerialPort();
        String strGPIONumValuePath = "";
        strGPIONumValuePath = "/sys/class/gpio/gpio"+echoGPIONum+"/value";
       // intSerialPortHandle = sp.openGPIO("/sys/class/gpio/gpio162/value",bReadFOrGPIO);
        intSerialPortHandle = sp.openGPIO(strGPIONumValuePath, bReadFOrGPIO);

        sp.setListener(splistener);

        try {
            sp.sleep(2*SLEEP_MSEC);
            dump_trace("SLEEP_MSEC= 2 secs");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] btyVersion_msg_received = new byte[256];
        int intDataReceivedLength=0;
        intDataReceivedLength = sp.getDataReceivedLength();
        if ( intDataReceivedLength> 0) {
            btyVersion_msg_received = Arrays.copyOf(sp.getBytDataReceived(),intDataReceivedLength);
        }
        dump_trace("cat /sys/class/gpio/gpio"+echoGPIONum+"/value  = "+ new String(btyVersion_msg_received) );
        sp.close(intSerialPortHandle);
        */
        //clean up GPIO
        // ex: echo 393 > /sys/class/gpio/unexport
        /*
        sp = new SerialPort();
        intSerialPortHandle = sp.openGPIO("/sys/class/gpio/unexport",bWirteForGPIO);
        intReturnCode = sp.write(intSerialPortHandle,echoGPIONum.getBytes());
        sp.close(intSerialPortHandle);
        dump_trace("Clean Up \necho "+echoGPIONum + " > "+"/sys/class/gpio/unexport");
        */
        //return new String(btyVersion_msg_received);
        return "No Cat value";
    }

    //export gpio
    public boolean activationPin(int intPin){



        String command = String.format("echo %d > /sys/class/gpio/export", intPin);
        try {
            //Runtime.getRuntime().exec(new String[] {"su", "-c", command});
            Runtime.getRuntime().exec(new String[] {"/system/bin/sh", "-c", command});
            return true;
        } catch (IOException e) {
            return false;
        }


/*
        Process process = null;
        Process process1 = null;
        DataOutputStream os = null;
        DataInputStream is = null;
        try {
            process = Runtime.getRuntime().exec("/system/xbin/su");
            os = new DataOutputStream(process.getOutputStream());
            is = new DataInputStream(process.getInputStream());
            os.writeBytes("/system/bin/ls" + " \n");  //這裡可以執行具有root 權限的程序了
            os.writeBytes(" exit \n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            //
            dump_trace("Unexpected error - Here is what I know:" + e.getMessage());
            return false;
            //Log.e(TAG, "Unexpected error - Here is what I know:" + e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
                process.destroy();
            } catch (Exception e) {
                return false;
            }
        }// get the root privileges
       return true;
    */
    }


    public void CashDrawer_Test_click(View view) {


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

   //     activationPin(393);

        echoGPIONum = "162";
        echoInOut ="out";
        strSetHighORLow="1";
        SetGPIOInOut(echoGPIONum, echoInOut, strSetHighORLow);
/*
        echoGPIONum = "393";
        echoInOut ="out";
        strSetHighORLow="0";
        SetGPIOInOut(echoGPIONum, echoInOut, strSetHighORLow);

        echoGPIONum = "393";
        echoInOut ="out";
        strSetHighORLow="1";
        SetGPIOInOut(echoGPIONum, echoInOut, strSetHighORLow);
        */

       // echoGPIONum = strEditTextGPIO;

        /*
        catValue = SetGPIOInOut(echoGPIONum, echoInOut, strSetHighORLow);

        final TextView textViewGPIOValue = (TextView) findViewById(R.id.textViewGPIOValue);

        textViewGPIOValue.setText("Value= "+ catValue);
        */

    }

    public void ID_ICCard_Test_click(View view)
    {
        Intent intent = new Intent(view.getContext(), IDICCard.class);
     //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
    //            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        view.getContext().startActivity(intent);

    }






}
