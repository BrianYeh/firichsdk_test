package firich.com.firichsdk_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

import firich.com.firichsdk.SerialPort;

public class MainCashDrawerActivity extends AppCompatActivity {

    SerialPort sp;
    int intSerialPortHandle = -1;

    private boolean bDebugOn = true;
    String strTagUtil = "MainCashDrawerActivity.";

    final static int SLEEP_MSEC =1000;
    final int bWirteForGPIO =1;
    final int bReadFOrGPIO =0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_drawer);
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
