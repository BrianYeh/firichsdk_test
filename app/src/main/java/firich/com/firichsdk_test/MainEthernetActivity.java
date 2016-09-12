package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class MainEthernetActivity extends Activity {

    private Handler mHandler = null; //Brian

    String strTagUtil = "MainEthernetActivity.";

    String strtextViewSmardCardLRCResult="";
    private boolean bDebugOn = true;
    private void dump_trace( String bytTrace)
    {
        if (bDebugOn)
            Log.d(strTagUtil, bytTrace);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkBuild checkBuildOK = new checkBuild();
        if (!checkBuildOK.checkVersion())
            return;

        setContentView(R.layout.activity_ethernet);


        this.mHandler = new Handler(); //Brian:

    }

    private String fectest_config_path = "/data/fec_config/fectest_config.xml";
    private String devIPaddress="124.108.105.150";
    TextView textViewPingPASS;
    @Override
    protected void onStart(){
        super.onStart();


        fectest_config_path  = ((FECApplication) this.getApplication()).getFEC_config_path();

        configUtil.Device devObject;
        configUtil configFile = new configUtil(fectest_config_path);
        configFile.dom4jXMLParser();
        devObject = configFile.getDevice("Ethernet");
        if (devObject.Dev != null && !devObject.Dev.isEmpty()) {
            devIPaddress = devObject.Dev;
        }
        TextView editTextDevIPaddress = (TextView)findViewById(R.id.textViewDestIPaddress);
        editTextDevIPaddress.setText(devIPaddress);

        textViewPingPASS =  (TextView)findViewById(R.id.textViewPingResult);
        Test_Ethernet();
    }
    public void Ethernet_Test_click(View view)
    {
        Test_Ethernet();
    }

    private void Test_Ethernet()
    {

        PostUIUpdateLog(textViewPingPASS,"Testing...", false );
       // PingTestThread PingTestThreadP = new PingTestThread();
        //.PingTestThreadP.start();
        PingWithWIFICheckedThread PingWithWIFICheckedThreadP = new PingWithWIFICheckedThread(this);
        PingWithWIFICheckedThreadP.start();
    }

    private class PingTestThread extends Thread {
        PingTestThread() {
        }

        public void run() {
            // compute primes larger than minPrime
            boolean pingPASS = false;
            int retryTimes=0;
            do {
                pingPASS = executeCommand();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                retryTimes++;
            } while (!pingPASS && (retryTimes <5));
            String strResult="FAIL!";
            if (pingPASS){
                strResult = "PASS";
            }
            PostUIUpdateLog(textViewPingPASS,strResult, pingPASS );
        }
    }

    private void PostUIUpdateLog(final TextView textViewMsg, final String PostUIMsg, final boolean testPASS)
    {
        this.mHandler.post(new Runnable()
        {
            public void run()
            {
                dump_trace("PostUIMsg="+PostUIMsg);

                //textViewPingPASS.setBackgroundResource(R.color.red);
                if (testPASS){
                    //textViewPingPASS.setBackgroundResource(R.color.green);
                }
                textViewMsg.setText(PostUIMsg);

            }
        });
    }

    private boolean executeCommand(){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            String exStr = "/system/bin/ping -c 1 "+ devIPaddress;
            dump_trace("Exec cmd:" + exStr);
            Process  mIpAddrProcess = runtime.exec(exStr);
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue "+mExitValue);
            if(mExitValue==0){
                return true;
            }else{
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
        return false;
    }


    private class PingWithWIFICheckedThread extends Thread {
        Context context;
        WifiManager wimanager;
        PingWithWIFICheckedThread(Context contextL) {
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
            if (wifiEnableOK)
            {
                //disable wifi
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
            }

            boolean pingPASS = false;
            retryTimes=0;
            do {
                pingPASS = executeCommand();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                retryTimes++;
            } while (!pingPASS && (retryTimes <5));
            String strResult="FAIL!";
            if (pingPASS){
                strResult = "PASS";
            }
            PostUIUpdateLog(textViewPingPASS,strResult, pingPASS );

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
