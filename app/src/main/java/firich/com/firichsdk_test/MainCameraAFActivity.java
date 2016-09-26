package firich.com.firichsdk_test;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

//public class MainCameraAFActivity extends AppCompatActivity {
public class MainCameraAFActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        checkBuild checkBuildOK = new checkBuild();
        if (!checkBuildOK.checkVersion())
            return;
        */
        setContentView(R.layout.activity_camera_af);
    }

    public void Camera_AF_Test_click(View view)
    {
        Camera_AF_Test(this);
    }

    int MY_REQUEST_CODE = 1337;

    public void Camera_AF_Test(Context context)
    {

/*
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // request code

        startActivityForResult(cameraIntent, MY_REQUEST_CODE);
        */
        checksCameraPermission();
    }

    public void checksCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("MyApp", "SDK >= 23");
            if (this.checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("MyApp", "Request permission");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_REQUEST_CODE);

                if (! shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showMessageOKCancel("You need to allow camera usage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainCameraAFActivity.this, new String[] {Manifest.permission.CAMERA},
                                            MY_REQUEST_CODE);
                                }
                            });
                }
            }
            else {
                Log.d("MyApp", "Permission granted: taking pic");
               takePicture();
            }
        }
        else {
            Log.d("MyApp", "Android < 6.0");
            takePicture();
        }
    }

    public void takePicture()
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // request code

        startActivityForResult(cameraIntent, MY_REQUEST_CODE);
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1337:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //criarFoto();
                    Log.d("MyApp", "You allow camera usage.");
                } else {
                    Log.d("MyApp", "You did not allow camera usage!");
                    //Toast.makeText(this, "You did not allow camera usage :(", Toast.LENGTH_SHORT).show();
                    //noFotoTaken();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if( requestCode == MY_REQUEST_CODE)
        {
            //  data.getExtras()
            /*
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            Now you have received the bitmap..you can pass that bitmap to other activity
            and play with it in this activity or pass this bitmap to other activity
            and then upload it to server.
            */
        }
        else
        {
            //Toast.makeText(demo.this, "Picture NOt taken", Toast.LENGTH_LONG);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
