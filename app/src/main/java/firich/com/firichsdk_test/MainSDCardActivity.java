package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

//public class MainSDCardActivity extends AppCompatActivity {
public class MainSDCardActivity extends Activity {

    private Handler mHandler = null;

    String strTagUtil = "MainSDCardActivity.";

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
        setContentView(R.layout.activity_sdcard);
        this.mHandler = new Handler(); //Brian:
    }

    private String fectest_config_path = "/data/fec_config/fectest_config.xml";

    String sd_path="/mnt/media_rw/extsd";
    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart() {
        super.onStart();


    fectest_config_path  = ((FECApplication) this.getApplication()).getFEC_config_path();

    configUtil.Device devObject;
    configUtil configFile = new configUtil(fectest_config_path);
    configFile.dom4jXMLParser();
    devObject = configFile.getDevice("SDCard");
    if (devObject.Dev != null && !devObject.Dev.isEmpty()) {
        sd_path = devObject.Dev;
    }
    TextView editText_sd_path = (TextView)findViewById(R.id.textViewSDPathSetting);
        editText_sd_path.setText(sd_path);

    Test_SDCard();
    }

    public void SDCard_Test_click(View view)
    {
        Test_SDCard();
    }

    private void Test_SDCard()
    {
        TextView editText_sd_test_result = (TextView)findViewById(R.id.textViewSDCardResult);
        PostUIUpdateLog(editText_sd_test_result,"Testing...", false );
        SDCardTestThread SDCardTestThreadVar = new SDCardTestThread(sd_path);
        SDCardTestThreadVar.start();
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

    private class SDCardTestThread extends Thread {

        String path = "/mnt/media_rw/extsd/sd.txt";

        SDCardTestThread(String logPath) {
            if (logPath != null && !logPath.isEmpty()) {
                path = logPath +"/" +"sd.txt";
            }
        }

        public void run() {
            boolean testPASS = false;
            Intent intent = getIntent();
            TextView editText_sd_test_result = (TextView)findViewById(R.id.textViewSDCardResult);

            CheckStorageUtil CheckStorageUtilVar= new CheckStorageUtil();
            testPASS = CheckStorageUtilVar.checkSDCardStorage();
            if ( testPASS ){

                PostUIUpdateLog(editText_sd_test_result,"PASS", false );
                setResult(1, intent); // return code = 1 -> OK
            }else{
                setResult(0, intent); // return code = 0 -> Error
                PostUIUpdateLog(editText_sd_test_result,"FAIL", false );
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finish();
        }
    }


}
