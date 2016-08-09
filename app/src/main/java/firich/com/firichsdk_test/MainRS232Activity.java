package firich.com.firichsdk_test;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainRS232Activity extends Activity {


    private String strRS232DeviceName ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rs232);


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
            lookback_result.setText("FAIL");
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

    @Override
    protected void onStart(){
        super.onStart();



        configUtil.Device devObject;
        configUtil configFile = new configUtil();

        configFile.dom4jXMLParser();
        //strSmartCardttyUSBPath
        devObject = configFile.getDevice("RS232Test");
        if (devObject.RS232DeviceName != null && !devObject.RS232DeviceName.isEmpty()) {
            strRS232DeviceName = devObject.RS232DeviceName;
        }

        int deviceCount = 0;
        for( int i=0; i<strRS232DeviceName.length(); i++ ) {
            if( strRS232DeviceName.charAt(i) == '|' ) {
                deviceCount++;
            }
        }
        String[] strRS232DeviceList = strRS232DeviceName.split("\\|");
        if ((strRS232DeviceList == null) || (strRS232DeviceList.length == 0)) {
            return ;
        }
        CreateRS232DeviceTable(strRS232DeviceList, deviceCount+1);
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
