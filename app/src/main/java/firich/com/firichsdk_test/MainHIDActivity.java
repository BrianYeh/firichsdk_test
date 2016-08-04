package firich.com.firichsdk_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainHIDActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hid);
        setHIDType();

    }

    private void setHIDType() {
        final TextView textView = (TextView) findViewById(R.id.textViewHID);
        Bundle params = getIntent().getExtras();
        if (params != null) {
            String strHIDType = params.getString("HID_TYPE");
            if (strHIDType != null && !strHIDType.isEmpty()) {
                textView.setText(strHIDType);
            } else {
                textView.setText("HID Input Text");
            }
        } else {
            textView.setText("HID Input Text");
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
