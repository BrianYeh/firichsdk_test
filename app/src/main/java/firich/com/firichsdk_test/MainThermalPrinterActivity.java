package firich.com.firichsdk_test;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fujitsu.fitPrint.Library.FitPrintAndroidUsb_v1011.FitPrintAndroidUsb;

import java.util.HashMap;
import java.util.Iterator;

public class MainThermalPrinterActivity extends Activity implements OnClickListener {

    FitPrintAndroidUsb mPrinter = new FitPrintAndroidUsb();

    private UsbManager mUsbManager = null;

    private PendingIntent mPermissionIntent = null ;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private UsbDevice mDevice = null ;
    private Handler mHandler = null; //Brian
    private boolean foundUSBDevice = false;

    private boolean bDebugOn = true;
    String strTagUtil = "MainThermalPrinterActivity.";

    private void dump_trace( String bytTrace)
    {
        if (bDebugOn)
            Log.d(strTagUtil, bytTrace);
    }

    private boolean printDone = false;
    private void Test_Printer_DTP220()
    {
        if (!printDone) {
            Connect_DTP220_PrinterThread ConnectPrinterThreadP = new Connect_DTP220_PrinterThread();
            ConnectPrinterThreadP.start();
            printDone = true;
        }

    }
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(mDevice != null){
                            //call method to set up device communication
                            PostUIUpdateLog("Got Device Printer");

                        }
                        else
                        {
                            PostUIUpdateLog("Device Printer is NULL!!");
                        }
                    }
                    else {
                        //Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }

        }
    };

    UsbManager musbManager = null ;

    private void GetUsbDevice()
    {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String,UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator  = deviceList.values().iterator();

        while(deviceIterator.hasNext()){
            mDevice = deviceIterator.next();
            // Vender/Product
            int nProduct = mDevice.getProductId();
            int nVender = mDevice.getVendorId();

            if (nVender == 0x04C5 &&
                    (nProduct == 0x117A ||
                            nProduct == 0x11CA ||
                            nProduct == 0x126E ))
            {

                mUsbManager.requestPermission(mDevice, mPermissionIntent);
                foundUSBDevice = true;
                return ;
            }
        }
        mDevice = null ;
        //Toast.makeText(this, "Not Find Printer", Toast.LENGTH_LONG).show();
        PostUIUpdateLog("Not Find Printer");
    }

    private void PostUIUpdateLog(final String msg)
    {
        this.mHandler.post(new Runnable()
        {
            public void run()
            {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

            }
        });
    }

    //////////////////////////////////////////////////////////
    // Layout
    private Button mBtnUsbAuthentication = null;
    private Button mBtnUsbConnect = null;
    private Button mBtnUsbDisconnect = null;
    private Button mBtnPrint = null;
    private Button mBtnPrintPageMode = null;
    private Button mBtnGetStatus = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkBuild checkBuildOK = new checkBuild();
        if (!checkBuildOK.checkVersion())
            return;


        setContentView(R.layout.activity_thermal_printer);

        this.mHandler = new Handler(); //Brian:

        mBtnUsbAuthentication = (Button)findViewById(R.id.btnUsbAuthentication);
        mBtnUsbAuthentication.setOnClickListener(this);
        mBtnUsbConnect = (Button)findViewById(R.id.btnUsbConnect);
        mBtnUsbConnect.setOnClickListener(this);
        mBtnUsbDisconnect = (Button)findViewById(R.id.btnUsbDisconnect);
        mBtnUsbDisconnect.setOnClickListener(this);
        mBtnPrint = (Button)findViewById(R.id.btnPrint);
        mBtnPrint.setOnClickListener(this);
        //mBtnPrintPageMode = (Button)findViewById(R.id.btnPageMode);
        //mBtnPrintPageMode.setOnClickListener(this);
        //mBtnGetStatus = (Button)findViewById(R.id.btnGetStatus);
        //mBtnGetStatus.setOnClickListener(this);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        GetUsbDevice();

    }

    /** Brian: Called when the activity has become visible. */
    @Override
    public void onResume()
    {
        super.onResume();
        /*
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
*/
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);


        Test_Printer_DTP220();


    }

    /** Called when another activity is taking focus. */
    @Override
    public void onPause()
    {
        super.onPause();

        unregisterReceiver(mUsbReceiver);

    }
    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        foundUSBDevice = false;
        printDone = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_fit_print_sample_usb, menu);
        return true;
    }

    private final Handler handler = new Handler();
    public int mRtn = 0 ;

    private class Connect_DTP220_PrinterThread extends Thread {
        Connect_DTP220_PrinterThread() {
        }

        public void run() {
            // compute primes larger than minPrime
            boolean bConnectOK = false;
            Intent intent = getIntent();

            int retryTimes = 0;
            do {
                //if (!foundUSBDevice) {
                    GetUsbDevice();
               // }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mRtn = mPrinter.Connect(mUsbManager, mDevice);
                dump_trace("Connect_DTP220_PrinterThread:mRtn=="+ mRtn);
                if (mRtn == 0) {
                    bConnectOK = true;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                retryTimes++;
                if (retryTimes >4)
                    break;
            } while (!bConnectOK);
            if (bConnectOK) {
                Print_Thermal_Printer_DTP_220();
                mPrinter.Disconnect();
                setResult(1, intent); // return code = 1 -> OK
            }else{
                setResult(0, intent); // return code = 0 fail
            }
            finish();
        }
    }

    void Print_Thermal_Printer_DTP_220()
    {
        int nRtn ;
        // Image
        EditText editText = (EditText)findViewById(R.id.editImagePath);
        String strText = editText.getText().toString() ;
        nRtn = mPrinter.PrintImageFile(strText) ;
        nRtn = mPrinter.PaperFeed(64);

        // Image
        EditText editText2 = (EditText)findViewById(R.id.editImagePath2);
        String strText2 = editText2.getText().toString() ;
        nRtn = mPrinter.PrintImageFile(strText2) ;
        nRtn = mPrinter.PaperFeed(64);





        nRtn = mPrinter.SetLocale(8);
        //String strTextReceipt = getResources().getString(R.string.receipt);;
        String strTextReceipt = "10F, No.75, Sec. 1, Sin Tai Wu Rd., Sijhih Dist.\n" +
                "     New Taipei City 221, Taiwan R.O.C.\n" +
                "             Tel +886-2-2698-1446\n" +
                "\n" +
                "  \t     Receipt Sample\n" +
                "\n" +
                " No.123456789        03/04/11 12:50 PM\n" +
                "  -------------------------------------\n" +
                "  GRILLED CHICKEN BREAST\t$18.50\n" +
                "  SIRLOIN STEAK\t\t\t$32.00\n" +
                "  ROAST LAMB\t\t\t$20.00\n" +
                "  SALAD\t\t\t\t$10.00\n" +
                "  COKE\t\t\t\t$ 3.50\n" +
                "  COKE\t\t\t\t$ 3.50\n" +
                "  ICE CREAM\t\t\t$ 5.00\n" +
                "  CHINESE NOODLE\t\t$15.00\n" +
                "  SUKIYAKI\t\t\t$30.00\n" +
                "  SANDWICH\t\t\t$10.00\n" +
                "  PIZZA\t\t\t\t$20.00\n" +
                "  TEA\t\t\t\t$ 3.50\n" +
                "  COFFEE\t\t\t$ 3.50\n" +
                "\n" +
                "  -------------------------------------\n" +
                "\t\tSUBTOTAL\t$174.50\n" +
                "\t\tSALES TAX\t$  8.73\n" +
                "\t\tTOTAL\t\t$183.23\n" +
                "\n" +
                "\t\tCASH\t\t$200.00\n" +
                "\t\tCHANGE DUE\t$ 16.77";
        nRtn = mPrinter.PrintText(strTextReceipt, "SJIS");
        nRtn = mPrinter.PaperFeed(64);

        // Barcode (JAN13)
        editText = (EditText)findViewById(R.id.editBarcode);
        strText = editText.getText().toString();
        nRtn = mPrinter.PrintBarcode(2, strText, 2, 0, 2, 100, 0) ;
        nRtn = mPrinter.PaperFeed(64);

        // Feed and Cut
        nRtn = mPrinter.PaperFeed(64);
        nRtn = mPrinter.CutPaper(0);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int nRtn ;


        if (v == mBtnUsbAuthentication)
        {
            GetUsbDevice() ;
        }
        if (v == mBtnUsbConnect)
        {
            /*
            GetUsbDevice() ;

            mRtn = mPrinter.Connect(mUsbManager, mDevice);
            */
            /*
            Connect_DTP220_PrinterThread ConnectPrinterThreadP = new Connect_DTP220_PrinterThread();
            ConnectPrinterThreadP.start();
            */
            mRtn = mPrinter.Connect(mUsbManager, mDevice);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainThermalPrinterActivity.this,  ErrorValue(mRtn), Toast.LENGTH_SHORT).show();
                }});
        }

        else if (v == mBtnUsbDisconnect)
        {
            mPrinter.Disconnect();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainThermalPrinterActivity.this,  "Success", Toast.LENGTH_SHORT).show();
                }});
        }

        else if (v == mBtnPrint)
        {


            Connect_DTP220_PrinterThread ConnectPrinterThreadP = new Connect_DTP220_PrinterThread();
            ConnectPrinterThreadP.start();


/*
			// Text
			nRtn = mPrinter.SetLocale(8);
			EditText editText = (EditText)findViewById(R.id.editTextText);
			String strText = editText.getText().toString() + '\n';
			nRtn = mPrinter.PrintText(strText , "SJIS");
			nRtn = mPrinter.PaperFeed(64);

			// Image
			editText = (EditText)findViewById(R.id.editImagePath);
			strText = editText.getText().toString() ;
			nRtn = mPrinter.PrintImageFile(strText) ;
			nRtn = mPrinter.PaperFeed(64);

			// Barcode (JAN13)
			editText = (EditText)findViewById(R.id.editBarcode);
			strText = editText.getText().toString();
			nRtn = mPrinter.PrintBarcode(2, strText, 2, 0, 2, 100, 0) ;
			nRtn = mPrinter.PaperFeed(64);

			// QR-Code
			editText = (EditText)findViewById(R.id.editQrCode);
			strText = editText.getText().toString();
			nRtn = mPrinter.PrintQrCode(strText, 0, 6, false, 0);

			// Feed and Cut
			nRtn = mPrinter.PaperFeed(64);
			nRtn = mPrinter.CutPaper(0);
*/
        }
		/*
		else if (v == mBtnPrintPageMode)
    	{
			nRtn = 0;

			nRtn = mPrinter.SetLocale(8);
			nRtn = mPrinter.SetPageMode();
			nRtn = mPrinter.SetPageArea(0, 0, 576, 1200);

		// Rotate 180
			nRtn = mPrinter.SetPageDirection(2);


			nRtn = mPrinter.SetLeftPos(true, 0);
			nRtn = mPrinter.SetTopPos(true, 100);
			nRtn = mPrinter.PrintText("Page Mode Test(Rotate 180)" + '\n' + '\n', "SJIS");
			EditText editText = (EditText)findViewById(R.id.editTextText);
			String strText = editText.getText().toString() + '\n';
			nRtn = mPrinter.PrintText(strText , "SJIS");

			nRtn = mPrinter.SetLeftPos(true, 0);
			nRtn = mPrinter.SetTopPos(false, 530);
			editText = (EditText)findViewById(R.id.editImagePath);
			strText = editText.getText().toString() ;
			nRtn = mPrinter.PrintImageFile(strText) ;

			nRtn = mPrinter.SetLeftPos(true, 0);
			nRtn = mPrinter.SetTopPos(false, 210);
			editText = (EditText)findViewById(R.id.editBarcode);
			strText = editText.getText().toString();
			nRtn = mPrinter.PrintBarcode(2, strText, 2, 0, 2, 100, 0) ;

			nRtn = mPrinter.SetLeftPos(true, 288);
			nRtn = mPrinter.SetTopPos(false, -204);
			editText = (EditText)findViewById(R.id.editQrCode);
			strText = editText.getText().toString();
			nRtn = mPrinter.PrintQrCode(strText, 0, 6, false, 0);

			nRtn = mPrinter.PrintPage();

			nRtn = mPrinter.CancelPage();

			mPrinter.CutPaper(0);

		// Rotate 90
			nRtn = mPrinter.SetPageDirection(3);

			nRtn = mPrinter.PrintText("Page Mode Test(Rotate 90)" + '\n' + '\n', "SJIS");
			editText = (EditText)findViewById(R.id.editTextText);
			strText = editText.getText().toString() + '\n';
			nRtn = mPrinter.PrintText(strText , "SJIS");

			nRtn = mPrinter.SetLeftPos(true, 0);
			nRtn = mPrinter.SetTopPos(false, 265);
			editText = (EditText)findViewById(R.id.editImagePath);
			strText = editText.getText().toString() ;
			nRtn = mPrinter.PrintImageFile(strText) ;

			nRtn = mPrinter.SetLeftPos(true, 0);
			nRtn = mPrinter.SetTopPos(false, 105);
			editText = (EditText)findViewById(R.id.editBarcode);
			strText = editText.getText().toString();
			nRtn = mPrinter.PrintBarcode(2, strText, 2, 0, 2, 100, 0) ;

			nRtn = mPrinter.SetLeftPos(true, 576);
			nRtn = mPrinter.SetTopPos(false, -102);
			editText = (EditText)findViewById(R.id.editQrCode);
			strText = editText.getText().toString();
			nRtn = mPrinter.PrintQrCode(strText, 0, 6, false, 0);

			nRtn = mPrinter.PrintPage();

			nRtn = mPrinter.CancelPage();

			mPrinter.CutPaper(0);

			nRtn = mPrinter.SetStandardMode();

    	}
    	*/
        else if (v == mBtnGetStatus)
        {
            Thread thread= new Thread(new Runnable(){ public void run(){

                // GetStatus
                mRtn = mPrinter.GetPrinterStatus();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainThermalPrinterActivity.this,  StatusValue(mRtn), Toast.LENGTH_SHORT).show();
                    }});

            }});
            thread.start();

        }


    }

    private String StatusValue(int StatusNum)
    {
        String Result = "Online(0)";

        // 200
        if(StatusNum == 200)
        {
            Result = "Offline(200)";
        }
        else if(StatusNum == 202)
        {
            Result = "Paper Near End(202) ";
        }
        else if(StatusNum == 301)
        {
            Result = "Cover Open(301) ";
        }
        else if(StatusNum == 302)
        {
            Result = "Paper End(302) ";
        }
        else if(StatusNum == 303)
        {
            Result = "Head Hot(303) ";
        }
        else if(StatusNum == 304)
        {
            Result = "Paper Layout Error(304) ";
        }
        else if(StatusNum == 305)
        {
            Result = "Cutter Jam(305) ";
        }
        else if(StatusNum == 700)
        {
            Result = "Hard Error(700) ";
        }
        else if(StatusNum == 1500)
        {
            Result = "Communication Error(1500) ";
        }
        else if(StatusNum == -3003)
        {
            Result = "Not Ready Status(-3003) ";
        }

        return Result;

    }

    private String ErrorValue(int ErrorStatus)
    {
        String Result = "Success(0)";

        // -1000
        if(ErrorStatus == -1000)
        {
            Result = "Parameter Error(-1000)";
        }
        else if(ErrorStatus == -1001)
        {
            Result = "Invalid Devices(-1001) ";
        }
        else if(ErrorStatus == -1002)
        {
            Result = "Parameter is Null(-1002) ";
        }
        else if(ErrorStatus == -1003)
        {
            Result = "Illegal data length(-1003) ";
        }
        else if(ErrorStatus == -1004)
        {
            Result = "Encoding undefined(-1004) ";
        }
        else if(ErrorStatus == -1005)
        {
            Result = "Value out of range(-1005) ";
        }
        // -1100
        else if(ErrorStatus == -1100)
        {
            Result = "Illegal characters bar code data(-1100) ";
        }
        else if(ErrorStatus == -1101)
        {
            Result = "Illegal length bar code data(-1101) ";
        }
        // -2000
        else if(ErrorStatus == -2000)
        {
            Result = "Communication Error(-2000) ";
        }
        else if(ErrorStatus == -2001)
        {
            Result = "Connect failure(-2001) ";
        }
        else if(ErrorStatus == -2002)
        {
            Result = "Not connected(-2002) ";
        }
        else if(ErrorStatus == -2003)
        {
            Result = "Time out(-2003) ";
        }
        // -3000
        else if(ErrorStatus == -3000)
        {
            Result = "File access failure(-3000) ";
        }
        else if(ErrorStatus == -3001)
        {
            Result = "File failed to read(-3001) ";
        }
        else if(ErrorStatus == -3002)
        {
            Result = "Failures to receive status(-3002) ";
        }
        else if(ErrorStatus == -3003)
        {
            Result = "Not Ready Status(-3003) ";
        }

        return Result;

    }

    @Override
    protected void onStop()
    {
        //unregisterReceiver(mUsbReceiver); //Brian Add.
        super.onStop();
    }

    private String fectest_config_path = "/data/fec_config/fectest_config.xml";

    @Override
    protected void onStart(){
        super.onStart();
        fectest_config_path  = ((FECApplication) this.getApplication()).getFEC_config_path();

        String strImagePath="/data/fec/X.jpg";
        String strImagePath2="/data/fec/X.jpg";

        configUtil.Device devObject;
        configUtil configFile = new configUtil(fectest_config_path);
        configFile.dom4jXMLParser();
        //strSmartCardttyUSBPath
        devObject = configFile.getDevice("ThermalPrinterTest");
        if (devObject.Path1 != null && !devObject.Path1.isEmpty()) {
            strImagePath = devObject.Path1;
        }
        if (devObject.Path2 != null && !devObject.Path2.isEmpty()) {
            strImagePath2 = devObject.Path2;
        }

        EditText editTextImagePath = (EditText)findViewById(R.id.editImagePath);
        EditText editTextImagePath2 = (EditText)findViewById(R.id.editImagePath2);
        editTextImagePath.setText(strImagePath);
        editTextImagePath2.setText(strImagePath2);

//        Connect_DTP220_PrinterThread ConnectPrinterThreadP = new Connect_DTP220_PrinterThread();
//        ConnectPrinterThreadP.start();
        Test_Printer_DTP220();
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
