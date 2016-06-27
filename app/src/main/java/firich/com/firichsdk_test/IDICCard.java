package firich.com.firichsdk_test;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.idtechproducts.device.IDT_MiniSmartII;

public class IDICCard extends AppCompatActivity {

    // DTTech IC Card test
    private IDT_MiniSmartII device;
    private String info = "";
    private String detail = "";
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idiccard);


    }

    public void releaseSDK() {
        if (device != null) {
            device.unregisterListen();
            device.release();
        }
    }




}
