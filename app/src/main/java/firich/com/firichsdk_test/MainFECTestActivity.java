package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

//public class MainFECTestActivity extends AppCompatActivity {
public class MainFECTestActivity extends Activity {

    // Test items
    private final int FEC_TEST_ALL=0;

    private final String PACKAGE_NAME = "firich.com.firichsdk_test";
    private String fectest_config_path = "/data/fec_config/STD_config.xml";

    STD_configUtil.STD_config STD_configObject;
    STD_configUtil configFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fectest);

        /*
        Test Camera AF Test

        String strVersion = Build.DISPLAY;
        boolean contains_android4 = strVersion.contains("4.4.3 2.0.0-rc2.");
        boolean contains_android5 = strVersion.contains("Edelweiss");

        if ((!contains_android4) && (!contains_android5)){
            fectest_config_path = "/sdcard/data/fec_config/STD_config.xml";
        }
        */
        //STD_configUtil.STD_config STD_configObject;
        configFile = new STD_configUtil(fectest_config_path);
        configFile.dom4jXMLParser();


        LinearLayout layout;
        String strNum="1";
        for(int i=1; i<=10;i++) {
            strNum = Integer.toString(i);
            String LLID = "linearLayout_test_" + strNum;
            int resID = getResources().getIdentifier(LLID, "id", "firich.com.firichsdk_test");
            layout = (LinearLayout) findViewById(resID);
            STD_configObject = configFile.getConfig(strNum);
            if (!STD_configObject.test) {
                layout.setVisibility(View.GONE);
            }else {

                String buttonID = "btnTest" + strNum;
                int resBtnID = getResources().getIdentifier(buttonID, "id", "firich.com.firichsdk_test");
                Button buttonConfig = (Button) findViewById(resBtnID);
                buttonConfig.setText(STD_configObject.name);
            }
        }

        TextView TextViewConfig = (TextView)findViewById(R.id.textSTD_config);
        TextViewConfig.setText("STD_config.xml is OK!");

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private String getFEC_config_file_name(STD_configUtil.STD_config STD_configObjectl)
    {
        String fectest_config_path = "/data/fec_config/fectest_config.xml";
        if (STD_configObject.path != null && !STD_configObject.path.isEmpty()) {
            if (STD_configObject.configFile != null && !STD_configObject.configFile.isEmpty()) {
                fectest_config_path = STD_configObject.path + STD_configObject.configFile;
            }
        }
        return fectest_config_path;
    }
    public void FEC_Test_1_click(View view)
    {
        /*
        int requestCode = FEC_TEST_ALL;
        String strClass = PACKAGE_NAME+".MainActivity";
        Intent intent = new Intent();
        intent.setAction(strClass);
        startActivityForResult(intent, requestCode);
*/
        String fectest_config_file_name = "/data/fec_config/fectest_config.xml";
        STD_configObject = configFile.getConfig("1");
        fectest_config_file_name = getFEC_config_file_name(STD_configObject);

        ((FECApplication) this.getApplication()).setFEC_config_path(fectest_config_file_name);

        Intent intent = new Intent(MainFECTestActivity.this, MainActivity.class);
        //intent.putExtra("config_file",fectest_config_path );
        int requestCode = FEC_TEST_ALL;
        startActivityForResult(intent, requestCode);
    }

    public void FEC_Test_2_click(View view)
    {

        Intent intent = new Intent(MainFECTestActivity.this, MainActivity.class);
        int requestCode = FEC_TEST_ALL;
        //startActivityForResult(intent, requestCode);
    }

    public void FEC_Test_3_click(View view)
    {

        Intent intent = new Intent(MainFECTestActivity.this, MainActivity.class);
        int requestCode = FEC_TEST_ALL;
        //startActivityForResult(intent, requestCode);
    }

    public void FEC_Test_4_click(View view)
    {

        Intent intent = new Intent(MainFECTestActivity.this, MainActivity.class);
        int requestCode = FEC_TEST_ALL;
        //startActivityForResult(intent, requestCode);
    }
    public void FEC_Test_5_click(View view)
    {

        Intent intent = new Intent(MainFECTestActivity.this, MainActivity.class);
        int requestCode = FEC_TEST_ALL;
        //startActivityForResult(intent, requestCode);
    }
    public void FEC_Test_6_click(View view)
    {

        Intent intent = new Intent(MainFECTestActivity.this, MainActivity.class);
        int requestCode = FEC_TEST_ALL;
        //startActivityForResult(intent, requestCode);
    }
    public void FEC_Test_7_click(View view)
    {

        Intent intent = new Intent(MainFECTestActivity.this, MainActivity.class);
        int requestCode = FEC_TEST_ALL;
        //startActivityForResult(intent, requestCode);
    }
    public void FEC_Test_8_click(View view)
    {

        Intent intent = new Intent(MainFECTestActivity.this, MainActivity.class);
        int requestCode = FEC_TEST_ALL;
        //startActivityForResult(intent, requestCode);
    }
    public void FEC_Test_9_click(View view)
    {

        Intent intent = new Intent(MainFECTestActivity.this, MainActivity.class);
        int requestCode = FEC_TEST_ALL;
        //startActivityForResult(intent, requestCode);
    }
    public void FEC_Test_10_click(View view)
    {

        Intent intent = new Intent(MainFECTestActivity.this, MainActivity.class);
        int requestCode = FEC_TEST_ALL;
        //startActivityForResult(intent, requestCode);
    }

}
