package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import firich.com.firichsdk.SerialPort;

public class MainRS232Activity extends Activity {


    private String strRS232DeviceName ="";

    String[] strRS232DeviceList; // /dev/ttyUSB0|/dev/ttyUSB1|/dev/ttyUSB2|/dev/ttyUSB3
    textViewResultUtil ltextViewResultIDs; //1, 2, 3, 4
    int deviceCount = 0;

    private String strTestString="testStringtestString";
    private Handler mHandler = null; //Brian
////////////////////////////////////////////////////////////////////////////////
    SerialPort sp;
    int intSerialPortHandle = -1;

    private boolean bDebugOn = true;
    String strTagUtil = "MainRS232Activity.";
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

    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkBuild checkBuildOK = new checkBuild();
        if (!checkBuildOK.checkVersion())
            return;

        setContentView(R.layout.activity_rs232);
        this.mHandler = new Handler(); //Brian:

    }


    private class textViewResultUtil{
        int[] textViewResultID= new int[20];
        public void setTextViewResultIDs(int deviceCount){

            //textViewResultID =  new int[deviceCount];
            for (int i=0; i < deviceCount; i++){
                textViewResultID[i] = generateViewId();
            }
        }
        public int getTextViewResultID(int ResultIndex){
            int ResultID=-1;
            if (ResultIndex < 20)
                ResultID = textViewResultID[ResultIndex];
            return ResultID;
        }
        private final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

        /**
         * Generate a value suitable for use in @link setId(int)}.
         * This value will not collide with ID values generated at build time by aapt for R.id.
         *
         * @return a generated ID value
         */
        public int generateViewId() {
            for (;;) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        }
    }

    private void CreateRS232DeviceTable(String[] strRS232DeviceList, int deviceCount)
    {
        Resources resource = (Resources) getBaseContext().getResources();
        ColorStateList colorWhile = (ColorStateList) resource.getColorStateList(R.color.white);
        ColorStateList colorBlack = (ColorStateList) resource.getColorStateList(R.color.black);
        TableLayout device_list_table = (TableLayout) findViewById(R.id.device_list_table);
        //device_list_table.setStretchAllColumns(true);
        TableLayout.LayoutParams row_layout = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams view_layout_device = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 5);
        TableRow.LayoutParams view_layout_result = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 5);


        ltextViewResultIDs = new textViewResultUtil();
        ltextViewResultIDs.setTextViewResultIDs(deviceCount);

        for (int i=0; i< deviceCount; i++) {
            TableRow tr = new TableRow(MainRS232Activity.this);
            tr.setLayoutParams(row_layout);
            tr.setBackgroundColor(resource.getColor(R.color.green));
            tr.setGravity(Gravity.CENTER_HORIZONTAL);

            String strRS232Device = strRS232DeviceList[i];
            if ((strRS232Device == null) || (strRS232Device.length() == 0))
                break;
            TextView device = new TextView(MainRS232Activity.this);
            device.setText(strRS232Device);
            device.setTextColor(colorBlack);
            device.setBackgroundResource(R.color.white);
            device.setPadding(2, 2, 2, 2);
            device.setTextSize(16);
            view_layout_device.setMargins(2, 2, 2, 2);
            device.setLayoutParams(view_layout_device);

            TextView lookback_result = new TextView(MainRS232Activity.this);
            lookback_result.setId(ltextViewResultIDs.getTextViewResultID(i));
            lookback_result.setText("??");
            lookback_result.setTextColor((ColorStateList) resource.getColorStateList(R.color.red));
            lookback_result.setBackgroundResource(R.color.white);
            lookback_result.setPadding(2, 2, 2, 2);
            lookback_result.setTextSize(16);
            view_layout_result.setMargins(2, 2, 2, 2);
            lookback_result.setLayoutParams(view_layout_result);

            tr.addView(device);
            tr.addView(lookback_result);
            device_list_table.addView(tr);
        }

    }


    private String fectest_config_path = "/data/fec_config/fectest_config.xml";

    @Override
    protected void onStart(){
        super.onStart();

        fectest_config_path  = ((FECApplication) this.getApplication()).getFEC_config_path();

        configUtil.Device devObject;
        configUtil configFile = new configUtil(fectest_config_path);

        configFile.dom4jXMLParser();
        //strSmartCardttyUSBPath
        devObject = configFile.getDevice("RS232Test");
        if (devObject.RS232DeviceName != null && !devObject.RS232DeviceName.isEmpty()) {
            strRS232DeviceName = devObject.RS232DeviceName;
        }

        int numberOfSeparator =0;
        for( int i=0; i<strRS232DeviceName.length(); i++ ) {
            if( strRS232DeviceName.charAt(i) == '|' ) {
                numberOfSeparator++;
            }
        }
        deviceCount = numberOfSeparator+1;
        strRS232DeviceList = strRS232DeviceName.split("\\|");
        if ((strRS232DeviceList == null) || (strRS232DeviceList.length == 0)) {
            return ;
        }
        CreateRS232DeviceTable(strRS232DeviceList, deviceCount);
        startRS232_Loopback_Test();
    }

    private class DeviceLoopbackTestThread extends Thread {
        String[] lstrttyUSBPath;
        textViewResultUtil ltextViewResultIDs;
        int ldeviceCount;
        DeviceLoopbackTestThread(String[] strttyUSBPath, textViewResultUtil textViewResultIDs, int deviceCount) {
            lstrttyUSBPath = strttyUSBPath;
            ltextViewResultIDs = textViewResultIDs;
            ldeviceCount = deviceCount;
        }

        public void run() {
            Intent intent = getIntent();
            boolean testPASS = false;
            for (int i=0; i< ldeviceCount; i++) {
                testPASS = RS232_Test(lstrttyUSBPath[i], ltextViewResultIDs.getTextViewResultID(i));
            }
            if (testPASS){
                setResult(1, intent);
            }else{
                setResult(0, intent);
            }
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finish();
        }
    }

    private void PostUIUpdateLog(final String msg, final  int intDataReceivedLength, final int testDeviceTextViewID)
    {
        this.mHandler.post(new Runnable()
        {
            public void run()
            {
                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                final TextView textViewResultDeviceID = (TextView) findViewById(testDeviceTextViewID);
                dump_trace("PostUIUpdateLog:intDataReceivedLength="+intDataReceivedLength);
                if (intDataReceivedLength >0)
                    textViewResultDeviceID.setText("PASS");
                else
                    textViewResultDeviceID.setText("FAIL");

            }
        });
    }

    private boolean RS232_Test(String strttyUSBPath, int testDeviceTextViewID) {
        int intReturnCode = -1;
        // Open serial port
        sp = new SerialPort();

        dump_trace("RS232.test.start");
        dump_trace("strttyUSBPath:"+strttyUSBPath);

        intSerialPortHandle = sp.open(strttyUSBPath,intBaudRate);

        dump_trace("open NFC port: intSerialPortHandle="+intSerialPortHandle);

        byte[] btyVersion_msg_received = new byte[256];
        Arrays.fill( btyVersion_msg_received, (byte) 0 );
        int intDataReceivedLength=0;
        String strTestResult="";

        intReturnCode = sp.write(intSerialPortHandle,strTestString.getBytes());
        dump_trace("write: intReturnCode="+intReturnCode);
        sp.setListener(splistener);
        //SleepMiniSecond(sp, 1000);

        intDataReceivedLength = sp.getDataReceivedLength();
        int nRetry=0;
        while (intDataReceivedLength == 0)
        {
            SleepMiniSecond(sp, 1000);
            intDataReceivedLength = sp.getDataReceivedLength();
            nRetry++;
            if (nRetry == 2)
                break;
        }
        if ( intDataReceivedLength>= 0) {
            btyVersion_msg_received = Arrays.copyOf(sp.getBytDataReceived(),intDataReceivedLength);
        }

        PostUIUpdateLog("", intDataReceivedLength, testDeviceTextViewID);


        sp.close(intSerialPortHandle);
        sp = null;
        boolean testResult = false;
        testResult = (intDataReceivedLength > 0 )? true: false;
        return testResult;

    }

    public void startRS232_Loopback_Test()
    {
        DeviceLoopbackTestThread DeviceLoopbackTestThreadP = new DeviceLoopbackTestThread(strRS232DeviceList, ltextViewResultIDs, deviceCount);
        DeviceLoopbackTestThreadP.start();
    }
    public void RS232_Loopback_Test_click(View view){
        //DeviceLoopbackTestThread DeviceLoopbackTestThreadP;

        startRS232_Loopback_Test();

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
