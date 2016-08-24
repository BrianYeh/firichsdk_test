package firich.com.firichsdk_test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.seikoinstruments.sdk.thermalprinter.PrinterException;
import com.seikoinstruments.sdk.thermalprinter.PrinterManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;


//import com.seikoinstruments.sdk.thermalprinter.sample.FileListDialog.onFileListDialogListener;


/**
 * Main Activity
 *
 */
public class MainThermalPrinterD10Activity extends Activity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_SELECT_DEVICE = 2;
    private static final int REQUEST_SETTING_PROPERTY = 3;
    private static final String BLUETOOTH_DEVICE_ADDRESS = "address";

    // Dialog ID
    /** connect */
    private static final int DIALOG_SELECT_PRINTER_MODEL = 1;
    /** sendText */
    private static final int DIALOG_INPUT_TEXT = 2;
    /** sendBinary */
    private static final int DIALOG_INPUT_BINARY = 3;
    /** getPrinterResponse */
    private static final int DIALOG_SELECT_PRINTER_RESPONSE = 4;
    /** registerLogo */
    private static final int DIALOG_REGISTER_LOGO_ID = 5;
    /** registerLogo */
    private static final int DIALOG_REGISTER_LOGO_ID2 = 6;
    /** unregisterLogo */
    private static final int DIALOG_UNREGISTER_LOGO_ID = 7;
    /** unregisterLogo */
    private static final int DIALOG_UNREGISTER_LOGO_ID2 = 8;
    /** registerStyleSheet */
    private static final int DIALOG_REGISTER_STYLE_SHEET_NO = 9;
    /** unregisterStyleSheet */
    private static final int DIALOG_UNREGISTER_STYLE_SHEET_NO = 10;
    /** Finish Application */
    private static final int DIALOG_FINISH_APP = 99;
    /** Bluetooth no support */
    private static final int DIALOG_BLUETOOTH_NO_SUPPORT = 101;

    /** PrinterManager -- SII Printer's SDK */
    private PrinterManager mPrinterManager;

    /** Window rotation */
    private ArrayList<PrinterManager> mSaveList;

    /** Select port */
    private boolean mSelectBluetooth = false;

    /** Select file */
    private File mSelectFile = null;

    private Handler mHandler = null; //Brian



    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        checkBuild checkBuildOK = new checkBuild();
        if (!checkBuildOK.checkVersion())
            return;


        setContentView(R.layout.activity_sii_d10_thermal_printer);

        //final EditText edtDeviceAddress = (EditText)findViewById(R.id.edittext_device_address);

        // Window rotation
        mPrinterManager = new PrinterManager();
        this.mHandler = new Handler(); //Brian:
        /*
        mSaveList = (ArrayList<PrinterManager>)getLastNonConfigurationInstance();
        if(mSaveList != null) {
            mPrinterManager = mSaveList.get(0);
            if(mPrinterManager.getPortType() == PrinterManager.PRINTER_TYPE_BLUETOOTH) {
                edtDeviceAddress.setEnabled(true);
            } else {
                edtDeviceAddress.setEnabled(true);
            }
        } else {
            mPrinterManager = new PrinterManager();
            SampleApplication application = (SampleApplication)this.getApplication();
            application.setPrinterManager(mPrinterManager);
        }*/

        /*
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)findViewById(checkedId);
                String text = radioButton.getText().toString();
                if(text.equals(getText(R.string.bluetooth).toString())) {
                    mSelectBluetooth = true;
                    edtDeviceAddress.setEnabled(true);
                } else {
                    mSelectBluetooth = false;
                    edtDeviceAddress.setEnabled(false);
                }
            }
        });
        */

        /*
        // [List]Button
        Button btnDeviceList = (Button)findViewById(R.id.button_device_list);
        if(btnDeviceList != null) {
            btnDeviceList.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(MainThermalPrinterD10Activity.this, BluetoothDeviceActivity.class);
                    startActivityForResult(intent, REQUEST_SELECT_DEVICE);
                }
            });
        }
        */

        // [connect]Button
        Button btnConnect = (Button)findViewById(R.id.button_connect);
        if(btnConnect != null) {
            btnConnect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!isFinishing()) {
                        //Brian:showDialog(DIALOG_SELECT_PRINTER_MODEL);
                        connectD10Printer();//Brian:
                    }
                }
            });
        }

        // [disconnect]Button
        Button btnDisconnect = (Button)findViewById(R.id.button_disconnect);
        if(btnDisconnect != null) {
            btnDisconnect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    writeLog("disconnect", true);

                    int ret = 0;
                    String msg;
                    try {
                        mPrinterManager.disconnect();
                        msg = "disconnect OK.";
                    } catch(PrinterException e) {
                        ret = e.getErrorCode();
                        msg = "disconnect NG.[" + ret + "]";
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    writeLog("disconnect", false, ret, "");
                }
            });
        }
/*
        // [sendText]Button
        Button btnSendText = (Button)findViewById(R.id.button_send_text);
        if(btnSendText != null) {
            btnSendText.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!isFinishing()) {
                        showDialog(DIALOG_INPUT_TEXT);
                    }
                }
            });
        }

        // [sendBinary]Button
        Button btnSendBinary = (Button)findViewById(R.id.button_send_binary);
        if(btnSendBinary != null) {
            btnSendBinary.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!isFinishing()) {
                        showDialog(DIALOG_INPUT_BINARY);
                    }
                }
            });
        }


        // [sendDataFile]Button
        Button btnSendDataFile = (Button)findViewById(R.id.button_send_data_file);
        if(btnSendDataFile != null) {
            btnSendDataFile.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!isFinishing()) {
                        FileListDialog dialog = new FileListDialog(MainThermalPrinterD10Activity.this);
                        dialog.setDirectorySelect(false);
                        dialog.setOnFileListDialogListener(new onFileListDialogListener() {
                            public void onClickFileList(File file) {
                                if(file == null){
                                    //not select
                                }else{
                                    writeLog("sendDataFile", true);

                                    int ret = 0;
                                    String msg;
                                    try {
                                        mPrinterManager.sendDataFile(file.getAbsolutePath());
                                        msg = "sendDataFile OK.";
                                    } catch(PrinterException e) {
                                        ret = e.getErrorCode();
                                        msg = "sendDataFile NG.[" + ret + "]";
                                    }
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                                    writeLog("sendDataFile", false, ret, "");
                                }
                            }
                        });
                        dialog.show("/", "Select file.");
                    }
                }
            });
        }

        // [abort]Button
        Button btnAbort = (Button)findViewById(R.id.button_abort);
        if(btnAbort != null) {
            btnAbort.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    writeLog("abort", true);

                    int ret = 0;
                    String msg;
                    try {
                        mPrinterManager.abort();
                        msg = "abort OK.";
                    } catch(PrinterException e) {
                        ret = e.getErrorCode();
                        msg = "abort NG.[" + ret + "]";
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    writeLog("abort", false, ret, "");
                }
            });
        }


        // [getStatus]Button
        Button btnGetStatus = (Button)findViewById(R.id.button_get_status);
        if(btnGetStatus != null) {
            btnGetStatus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    writeLog("getStatus", true);

                    int[] buf = new int[1];
                    int ret = 0;
                    String msg;
                    try {
                        mPrinterManager.getStatus(buf);
                        msg = "getStatus OK. Status:0x" + String.format("%08X", buf[0]);
                    } catch(PrinterException e) {
                        ret = e.getErrorCode();
                        msg = "getStatus NG.[" + ret + "]";
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    writeLog("getStatus", false, ret, "Status:0x" + String.format("%08X", buf[0]));
                }
            });
        }

        // [getPrinterResponse]Button
        Button btnGetPrinterResponse = (Button)findViewById(R.id.button_get_printer_response);
        if(btnGetPrinterResponse != null) {
            btnGetPrinterResponse.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!isFinishing()) {
                        showDialog(DIALOG_SELECT_PRINTER_RESPONSE);
                    }
                }
            });
        }
*/
        /*
        // [registerLogo]Button
        Button btnRegisterLogo = (Button)findViewById(R.id.button_register_logo);
        if(btnRegisterLogo != null) {
            btnRegisterLogo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!isFinishing()) {
                        FileListDialog dialog = new FileListDialog(MainThermalPrinterD10Activity.this);
                        dialog.setDirectorySelect(false);
                        dialog.setOnFileListDialogListener(new onFileListDialogListener() {
                            public void onClickFileList(File file) {
                                if(file == null){
                                    //not select
                                }else{
                                    mSelectFile = file;
                                    if (!isFinishing()) {
                                        if(mPrinterManager.getPrinterModel() == PrinterManager.PRINTER_MODEL_RP_E10
                                        || mPrinterManager.getPrinterModel() == PrinterManager.PRINTER_MODEL_RP_D10) {
                                            showDialog(DIALOG_REGISTER_LOGO_ID2);
                                        } else {
                                            showDialog(DIALOG_REGISTER_LOGO_ID);
                                        }
                                    }
                                }
                            }
                        });
                        dialog.show("/", "Select file.");
                    }
                }
            });
        }

        // [unregisterLogo]Button
        Button btnUnregisterLogo = (Button)findViewById(R.id.button_unregister_logo);
        if(btnUnregisterLogo != null) {
            btnUnregisterLogo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!isFinishing()) {
                        if(mPrinterManager.getPrinterModel() == PrinterManager.PRINTER_MODEL_RP_E10
                        || mPrinterManager.getPrinterModel() == PrinterManager.PRINTER_MODEL_RP_D10) {
                            showDialog(DIALOG_UNREGISTER_LOGO_ID2);
                        } else {
                            showDialog(DIALOG_UNREGISTER_LOGO_ID);
                        }
                    }
                }
            });
        }

        // [registerStyleSheet]Button
        Button btnRegisterStyleSheet = (Button)findViewById(R.id.button_register_style_sheet);
        if(btnRegisterStyleSheet != null) {
            btnRegisterStyleSheet.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!isFinishing()) {
                        FileListDialog dialog = new FileListDialog(MainThermalPrinterD10Activity.this);
                        dialog.setDirectorySelect(false);
                        dialog.setOnFileListDialogListener(new onFileListDialogListener() {
                            public void onClickFileList(File file) {
                                if(file == null){
                                    //not select
                                }else{
                                    mSelectFile = file;
                                    if (!isFinishing()) {
                                        showDialog(DIALOG_REGISTER_STYLE_SHEET_NO);
                                    }
                                }
                            }
                        });
                        dialog.show("/", "Select file.");
                    }
                }
            });
        }

        // [unregisterStyleSheet]Button
        Button btnUnregisterStyleSheet = (Button)findViewById(R.id.button_unregister_style_sheet);
        if(btnUnregisterStyleSheet != null) {
            btnUnregisterStyleSheet.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!isFinishing()) {
                        showDialog(DIALOG_UNREGISTER_STYLE_SHEET_NO);
                    }
                }
            });
        }

        // [resetPrinter]Button
        Button btnResetPrinter = (Button)findViewById(R.id.button_reset_printer);
        if(btnResetPrinter != null) {
            btnResetPrinter.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    writeLog("resetPrinter", true);

                    int ret = 0;
                    String msg;
                    try {
                        mPrinterManager.resetPrinter();
                        msg = "resetPrinter OK.";
                    } catch(PrinterException e) {
                        ret = e.getErrorCode();
                        msg = "resetPrinter NG.[" + ret + "]";
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    writeLog("resetPrinter", false, ret, "");
                }
            });
        }
*/
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isInit = pref.getBoolean("init", false);
        if(!isInit) {
            Editor editor = pref.edit();
            if(Locale.JAPAN.equals(Locale.getDefault())) {
                editor.putString(getText(R.string.key_international_character).toString(), "8");
                editor.putString(getText(R.string.key_code_page).toString(), "1");
            } else {
                editor.putString(getText(R.string.key_international_character).toString(), "0");
                editor.putString(getText(R.string.key_code_page).toString(), "16");
            }
            editor.putBoolean("init", true);
            editor.commit();
        }

    }

    private String strImagePath="/data/fec/1.jpg";
    private String strImagePath2="/data/fec/2.jpg";
    private String strReceipt="/data/fec/ReceiptXX.txt";
    @Override
    public void onStart() {
        super.onStart();

        //checkBluetooth();

        configUtil.Device devObject;
        configUtil configFile = new configUtil();
        configFile.dom4jXMLParser();
        devObject = configFile.getDevice("ThermalPrinterTestD10");
        if (devObject.Path1 != null && !devObject.Path1.isEmpty()) {
            strImagePath = devObject.Path1;
        }
        if (devObject.Path2 != null && !devObject.Path2.isEmpty()) {
            strImagePath2 = devObject.Path2;
        }
        if (devObject.Path3 != null && !devObject.Path3.isEmpty()) {
            strReceipt = devObject.Path3;
        }

        EditText editTextImagePath = (EditText)findViewById(R.id.editImagePath);
        EditText editTextImagePath2 = (EditText)findViewById(R.id.editImagePath2);
        editTextImagePath.setText(strImagePath);
        editTextImagePath2.setText(strImagePath2);

        connectD10Printer(); //connect and print
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onRestart() {
        super.onRestart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

/*
    public void finish() {
 //Brian:       showDialog(DIALOG_FINISH_APP);
    }
*/

    private void finishApp() {
        try {
            mPrinterManager.disconnect();
        } catch (PrinterException e) {
        }

        super.finish();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
/* Brian:
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
*/
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
/* Brian:
        switch (item.getItemId()) {
            case R.id.item_settings:
                Intent intent = new Intent(MainThermalPrinterD10Activity.this, SettingsActivity.class);
                startActivityForResult(intent, REQUEST_SETTING_PROPERTY);
                return true;
        }
*/
        return false;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            /*
            case REQUEST_SELECT_DEVICE:
                if(resultCode == RESULT_OK) {
                   // EditText edtDeviceAddress = (EditText)findViewById(R.id.edittext_device_address);
                    //edtDeviceAddress.setText(data.getStringExtra(BLUETOOTH_DEVICE_ADDRESS));
                }
                break;
            */
            case REQUEST_SETTING_PROPERTY:
                setProperty();
                break;

            case REQUEST_ENABLE_BLUETOOTH:
                if(resultCode == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), R.string.enabled_bluetooth, Toast.LENGTH_LONG);
                }
                break;
        }
    }


    public Object onRetainNonConfigurationInstance() {
        mSaveList = new ArrayList<PrinterManager>(1);
        mSaveList.add(mPrinterManager);

        return mSaveList;
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = super.onCreateDialog(id);

        switch(id) {
            case DIALOG_SELECT_PRINTER_MODEL:
                dialog = createDialogSelectPrinterModel();
                break;

            case DIALOG_INPUT_TEXT:
                dialog = createDialogInputText();
                break;

            case DIALOG_INPUT_BINARY:
                dialog = createDialogInputBinary();
                break;

            case DIALOG_SELECT_PRINTER_RESPONSE:
                dialog = createDialogSelectPrinterResponse();
                break;

            case DIALOG_REGISTER_LOGO_ID:
                dialog = createDialogRegisterLogoID();
                break;



            case DIALOG_FINISH_APP:
                dialog = createDialogConfirmFinishApp();
                break;



        }

        return dialog;
    }

    private void PostUIUpdateLog(final String msg, final  int ret)
    {
        this.mHandler.post(new Runnable()
        {
            public void run()
            {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                writeLog("connect", false, ret, msg);
            }
        });
    }

    private class ConnectPrinterThread extends Thread {
        ConnectPrinterThread() {
        }

        public void run() {
            // compute primes larger than minPrime
            boolean bConnectOK = false;
            Intent intent = getIntent();
            int retryTimes=0;
            do {
                bConnectOK = connecD10PrinterFunc();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                retryTimes++;
            } while (!bConnectOK && (retryTimes <10));
            if (bConnectOK){
                Thermal_Printer_D10_Print();
                setResult(1, intent); // return code = 1 -> OK
            }else{
                setResult(0, intent); // return code = 0 fail
            }
            finish();
        }
    }
    private boolean  connecD10PrinterFunc()
    {
        int ret = 0;
        String msg="";
        boolean bConnectOK = true;
        try {
            setProperty();

            //Brian:
            /*
             public static final int PRINTER_MODEL_DEFAULT = 284;
              public static final int PRINTER_MODEL_DPU_S245 = 284;
              public static final int PRINTER_MODEL_DPU_S445 = 281;
              public static final int PRINTER_MODEL_RP_D10 = 295;
              public static final int PRINTER_MODEL_RP_E10 = 291;
             */
            mPrinterManager.connect(PrinterManager.PRINTER_MODEL_RP_D10, MainThermalPrinterD10Activity.this);
            msg = "connect OK.";
            bConnectOK = true;
        } catch (PrinterException e) {
            ret = e.getErrorCode();
            if (ret == -11) {
                msg = "Already opened port!. Please make sure the printer is ready and print it.";
            } else {
                //msg = "connect NG.[" + ret + "]";
                msg = "Please wait for connecting..";
                bConnectOK = false;
            }
        }
        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        //writeLog("connect", false, ret, "");
        PostUIUpdateLog(msg, ret);

        return bConnectOK;
    }
//Brian:
    private void connectD10Printer()
    {
        int ret = 0;
        String msg="";
        boolean bConnectOK = true;

        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        ConnectPrinterThread ConnectPrinterThreadP = new ConnectPrinterThread();
        ConnectPrinterThreadP.start();
        //writeLog("connect", false, ret, "");
    }

    private Dialog createDialogSelectPrinterModel() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.dialog_connect_title);
        alertDialog.setItems(R.array.printer_model_list, new DialogInterface.OnClickListener() {
            private String[] printerModels = getResources().getStringArray(R.array.printer_model_values_list);
            public void onClick(DialogInterface dialog, int which) {

                int model = Integer.parseInt(printerModels[which]);
                //Brian: EditText edtDeviceAddress = (EditText)findViewById(R.id.edittext_device_address);
                //writeLog("connect", true, 0, edtDeviceAddress.getText().toString());

                int ret = 0;
                String msg;
                try {
                    setProperty();
                    //Brian: mark
                    if (false) {
                        if (mSelectBluetooth) {
                            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainThermalPrinterD10Activity.this);
                            boolean secure = pref.getBoolean(getString(R.string.key_secure_connection), true);
                            //mPrinterManager.connect(model, edtDeviceAddress.getText().toString(), secure);
                        } else {
                            mPrinterManager.connect(model, MainThermalPrinterD10Activity.this);
                        }
                    }
                    //Brian:
                    mPrinterManager.connect(model, MainThermalPrinterD10Activity.this);
                    msg = "connect OK.";
                } catch(PrinterException e) {
                    ret = e.getErrorCode();
                    msg = "connect NG.[" + ret + "]";
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                writeLog("connect", false, ret, "");
            }
        });

        return alertDialog.create();
    }


    private Dialog createDialogInputText() {
        final EditText editView = new EditText(MainThermalPrinterD10Activity.this);
        editView.setInputType(InputType.TYPE_CLASS_TEXT);
        InputFilter[] inputFilter = new InputFilter[1];
        inputFilter[0] = new InputFilter.LengthFilter(256);
        editView.setFilters(inputFilter);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setTitle(R.string.dialog_send_text_title);
        alertDialog.setView(editView);
        alertDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editView.getText().toString();
                        writeLog("sendText", true, 0, text);

                        int ret = 0;
                        String msg;
                        try {
                            mPrinterManager.sendText(text);
                            msg = "sendText OK.";
                        } catch(PrinterException e) {
                            ret = e.getErrorCode();
                            msg = "sendText NG.[" + ret + "]";
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                        writeLog("sendText", false, ret, "");

                        editView.setText("");
                    }
                });
        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return alertDialog.create();
    }


    private Dialog createDialogInputBinary() {
        final EditText editView = new EditText(MainThermalPrinterD10Activity.this);
        editView.setInputType(InputType.TYPE_CLASS_TEXT);
        InputFilter[] inputFilter = new InputFilter[2];
        inputFilter[0] = new BinaryFilter();
        inputFilter[1] = new InputFilter.LengthFilter(256);
        editView.setFilters(inputFilter);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setTitle(R.string.dialog_send_binary_title);
        alertDialog.setView(editView);
        alertDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        byte[] binary = asByteArray(editView.getText().toString());
                        writeLog("sendBinary", true, 0, editView.getText().toString());

                        int ret = 0;
                        String msg;
                        try {
                            mPrinterManager.sendBinary(binary);
                            msg = "sendBinary OK.";
                        } catch(PrinterException e) {
                            ret = e.getErrorCode();
                            msg = "sendBinary NG.[" + ret + "]";
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                        writeLog("sendBinary", false, ret, "");

                        editView.setText("");
                    }
                });
        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return alertDialog.create();
    }


    private Dialog createDialogSelectPrinterResponse() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.dialog_get_printer_response_title);
        alertDialog.setItems(R.array.printer_response_list, new DialogInterface.OnClickListener() {
            private String[] printerResponses = getResources().getStringArray(R.array.printer_response_values_list);
            public void onClick(DialogInterface dialog, int which) {
                writeLog("getPrinterResponse", true);

                int id = Integer.parseInt(printerResponses[which]);
                if(id == PrinterManager.PRINTER_RESPONSE_KEY_CODE) {
                    ArrayList<String> buf = new ArrayList<String>();

                    int ret = 0;
                    String msg;
                    StringBuffer keyCode = new StringBuffer(256);
                    try {
                        mPrinterManager.getPrinterResponse(id, buf);
                        int size = buf.size();
                        for(int index = 0; index < size; index++) {
                            if(index != 0) {
                                keyCode.append(",");
                            }
                            keyCode.append(buf.get(index));
                        }

                        msg = "getPrinterResponse OK. Key:" + keyCode.toString();
                    } catch(PrinterException e) {
                        ret = e.getErrorCode();
                        msg = "getPrinterResponse NG.[" + ret + "]";
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    writeLog("getPrinterResponse", false, ret, "Key:" + keyCode.toString());

                } else {
                    int[] buf = new int[1];
                    if(id == 0) {
                        buf[0] = 0x0F;
                    }
                    int ret = 0;
                    String msg;
                    try {
                        mPrinterManager.getPrinterResponse(id, buf);
                        msg = "getPrinterResponse OK. Response:0x" + String.format("%08X", buf[0]);
                    } catch(PrinterException e) {
                        ret = e.getErrorCode();
                        msg = "getPrinterResponse NG.[" + ret + "]";
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    writeLog("getPrinterResponse", false, ret, "Response:0x" + String.format("%08X", buf[0]));
                }

            }
        });

        return alertDialog.create();
    }


    private Dialog createDialogRegisterLogoID() {
        final EditText editView = new EditText(MainThermalPrinterD10Activity.this);
        editView.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] inputFilter = new InputFilter[1];
        inputFilter[0] = new InputFilter.LengthFilter(4);
        editView.setFilters(inputFilter);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setTitle(R.string.dialog_register_logo_title);
        alertDialog.setView(editView);
        alertDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int logoID = StringUtil.getInt(editView.getText().toString(), 0);
                        writeLog("registerLogo", true, 0, editView.getText().toString());

                        int ret = 0;
                        String msg;
                        try {
                            mPrinterManager.registerLogo(mSelectFile.getAbsolutePath(), logoID);
                            msg = "registerLogo OK.";
                        } catch(PrinterException e) {
                            ret = e.getErrorCode();
                            msg = "registerLogo NG.[" + ret + "]";
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                        writeLog("registerLogo", false, ret, "");

                        editView.setText("");
                    }
                });
        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return alertDialog.create();
    }

/*
    private Dialog createDialogRegisterLogoID2() {
        final EditText editView = new EditText(MainThermalPrinterD10Activity.this);
        editView.setInputType(InputType.TYPE_CLASS_TEXT);
        InputFilter[] inputFilter = new InputFilter[1];
        inputFilter[0] = new InputFilter.LengthFilter(2);
        editView.setFilters(inputFilter);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setTitle(R.string.dialog_register_logo_title);
        alertDialog.setView(editView);
        alertDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String logoID = editView.getText().toString();
                        writeLog("registerLogo", true, 0, editView.getText().toString());

                        int ret = 0;
                        String msg;
                        try {
                            mPrinterManager.registerLogo(mSelectFile.getAbsolutePath(), logoID);
                            msg = "registerLogo OK.";
                        } catch(PrinterException e) {
                            ret = e.getErrorCode();
                            msg = "registerLogo NG.[" + ret + "]";
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                        writeLog("registerLogo", false, ret, "");

                        editView.setText("");
                    }
                });
        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return alertDialog.create();
    }


    private Dialog createDialogUnregisterLogoID() {
        final EditText editView = new EditText(MainThermalPrinterD10Activity.this);
        editView.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] inputFilter = new InputFilter[1];
        inputFilter[0] = new InputFilter.LengthFilter(4);
        editView.setFilters(inputFilter);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setTitle(R.string.dialog_unregister_logo_title);
        alertDialog.setView(editView);
        alertDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int logoID = StringUtil.getInt(editView.getText().toString(), 0);
                        writeLog("unregisterLogo", true, 0, editView.getText().toString());

                        int ret = 0;
                        String msg;
                        try {
                            mPrinterManager.unregisterLogo(logoID);
                            msg = "unregisterLogo OK.";
                        } catch(PrinterException e) {
                            ret = e.getErrorCode();
                            msg = "unregisterLogo NG.[" + ret + "]";
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                        writeLog("unregisterLogo", false, ret, "");

                        editView.setText("");
                    }
                });
        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return alertDialog.create();
    }


    private Dialog createDialogUnregisterLogoID2() {
        final EditText editView = new EditText(MainThermalPrinterD10Activity.this);
        editView.setInputType(InputType.TYPE_CLASS_TEXT);
        InputFilter[] inputFilter = new InputFilter[1];
        inputFilter[0] = new InputFilter.LengthFilter(2);
        editView.setFilters(inputFilter);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setTitle(R.string.dialog_unregister_logo_title);
        alertDialog.setView(editView);
        alertDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String logoID = editView.getText().toString();
                        writeLog("unregisterLogo", true, 0, editView.getText().toString());

                        int ret = 0;
                        String msg;
                        try {
                            mPrinterManager.unregisterLogo(logoID);
                            msg = "unregisterLogo OK.";
                        } catch(PrinterException e) {
                            ret = e.getErrorCode();
                            msg = "unregisterLogo NG.[" + ret + "]";
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                        writeLog("unregisterLogo", false, ret, "");

                        editView.setText("");
                    }
                });
        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return alertDialog.create();
    }


    private Dialog createDialogRegisterStyleSheetNo() {
        final EditText editView = new EditText(MainThermalPrinterD10Activity.this);
        editView.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] inputFilter = new InputFilter[1];
        inputFilter[0] = new InputFilter.LengthFilter(1);
        editView.setFilters(inputFilter);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setTitle(R.string.dialog_register_style_sheet_title);
        alertDialog.setView(editView);
        alertDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int styleSeetNo = StringUtil.getInt(editView.getText().toString(), 0);
                        writeLog("registerStyleSheet", true, 0, editView.getText().toString());

                        int ret = 0;
                        String msg;
                        try {
                            mPrinterManager.registerStyleSheet(mSelectFile.getAbsolutePath(), styleSeetNo);
                            msg = "registerStyleSheet OK.";
                        } catch(PrinterException e) {
                            ret = e.getErrorCode();
                            msg = "registerStyleSheet NG.[" + ret + "]";
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                        writeLog("registerStyleSheet", false, ret, "");

                        editView.setText("");
                    }
                });
        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return alertDialog.create();
    }


    private Dialog createDialogUnregisterStyleSheetNo() {
        final EditText editView = new EditText(MainThermalPrinterD10Activity.this);
        editView.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] inputFilter = new InputFilter[1];
        inputFilter[0] = new InputFilter.LengthFilter(1);
        editView.setFilters(inputFilter);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setTitle(R.string.dialog_unregister_style_sheet_title);
        alertDialog.setView(editView);
        alertDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int styleSeetNo = StringUtil.getInt(editView.getText().toString(), 0);
                        writeLog("unregisterStyleSheet", true, 0, editView.getText().toString());

                        int ret = 0;
                        String msg;
                        try {
                            mPrinterManager.unregisterStyleSheet(styleSeetNo);
                            msg = "unregisterStyleSheet OK.";
                        } catch(PrinterException e) {
                            ret = e.getErrorCode();
                            msg = "unregisterStyleSheet NG.[" + ret + "]";
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                        writeLog("unregisterStyleSheet", false, ret, "");

                        editView.setText("");
                    }
                });
        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return alertDialog.create();
    }
*/

    private Dialog createDialogConfirmFinishApp() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(android.R.drawable.ic_dialog_info);
        alertDialog.setTitle(R.string.dialog_finish_app_title);
        alertDialog.setMessage(R.string.dialog_finish_app_message);
        alertDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finishApp();
                    }
                });
        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return alertDialog.create();
    }

/*
    private Dialog createDialogBluetoothNoSupport() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.bluetooth);
        alertDialog.setMessage(R.string.bluetooth_not_supported);
        alertDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        return alertDialog.create();
    }

*/
    class BinaryFilter implements InputFilter {
        public CharSequence filter(CharSequence source, int start, int end,
                                Spanned dest, int dstart, int dend) {

            if(source.toString().matches("^[a-fA-F0-9]+$") ){
                return source;
            }else{
                return "";
            }
        }
    }


    private byte[] asByteArray(String hex) {
        byte[] bytes = new byte[hex.length() / 2];

        try {
            for(int index = 0; index < bytes.length; index++) {
                String byteStr = hex.substring(index * 2, (index + 1) * 2);
                bytes[index] = (byte)Integer.parseInt(byteStr, 16);
            }
        } catch(IndexOutOfBoundsException e) {
        } catch(NumberFormatException e) {
        }

        return bytes;
    }


    private void checkBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            showDialog(DIALOG_BLUETOOTH_NO_SUPPORT);
            return;
        }

        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }


    private void setProperty() {
        if(mPrinterManager != null) {
            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            int sendTimeout = StringUtil.getInt(pref.getString(getString(R.string.key_send_timeout), "100000"));
            mPrinterManager.setSendTimeout(sendTimeout);

            int receiveTimeout = StringUtil.getInt(pref.getString(getString(R.string.key_receive_timeout), "100000"));
            mPrinterManager.setReceiveTimeout(receiveTimeout);

            int internationalCharacter;
            int codePage;
            if(Locale.JAPAN.equals(Locale.getDefault())) {
                internationalCharacter = StringUtil.getInt(pref.getString(getString(R.string.key_international_character), "8"));
                codePage = StringUtil.getInt(pref.getString(getString(R.string.key_code_page), "1"));
            } else {
                internationalCharacter = StringUtil.getInt(pref.getString(getString(R.string.key_international_character), "0"));
                codePage = StringUtil.getInt(pref.getString(getString(R.string.key_code_page), "16"));
            }
            mPrinterManager.setInternationalCharacter(internationalCharacter);
            mPrinterManager.setCodePage(codePage);
        }
    }

    private void writeLog(String command, boolean start) {
        writeLog(command, start, 0, "");
    }


    private void writeLog(String command, boolean start, int returnCode, String msg) {
        EditText edtLog = (EditText)findViewById(R.id.edittext_log);
        if(edtLog != null) {
            StringBuffer buf = new StringBuffer(128);
            buf.append("[")
                .append(StringUtil.getDateString("yyyy/MM/dd HH:mm:ss.SSS"))
                .append("]");

            buf.append(" ").append(command).append("()");
            if(start) {
                buf.append(" IN");
            } else {
                buf.append(" OUT");
                if(returnCode != 0) {
                    buf.append(" Result:").append(returnCode);
                }
            }

            if(!StringUtil.isEmpty(msg)) {
                buf.append(" ").append(msg);
            }

            buf.append("\n");

            edtLog.append(buf.toString());
        }
    }


    public void Thermal_Printer_D10_Print()
    {
        String strForBinaryData = "0A0A0A";
        String strCut = "1D560048";
        byte[] bytForBinaryData = asByteArray(strForBinaryData);
        byte[] bytCut = asByteArray(strCut);
        byte[] btyLineFeed= new byte[]{(byte)0x0A};
        //Image1

        EditText editText = (EditText)findViewById(R.id.editImagePath);
        String strText = editText.getText().toString() ;
        int ret = 0;
        String msg;

        try {
            mPrinterManager.sendDataFile(strText);
            msg = "sendDataFile OK.";
        } catch(PrinterException e) {
            ret = e.getErrorCode();
            msg = "sendDataFile NG.[" + ret + "]";
        }

        //Image2
        EditText editText2 = (EditText)findViewById(R.id.editImagePath2);
        String strText2 = editText2.getText().toString() ;
        try {
            mPrinterManager.sendDataFile(strText2);
            mPrinterManager.sendBinary(bytForBinaryData);
            msg = "sendDataFile OK.";
        } catch(PrinterException e) {
            ret = e.getErrorCode();
            msg = "sendDataFile NG.[" + ret + "]";
        }

        //Receipt.txt
        try {
//            mPrinterManager.sendDataFile("/data/fec/Receipt.txt");
            mPrinterManager.sendDataFile(strReceipt);

            mPrinterManager.sendBinary(btyLineFeed);//text line feed
            mPrinterManager.sendBinary(bytForBinaryData);
            mPrinterManager.sendBinary(bytCut);
            mPrinterManager.disconnect();
            msg = "sendDataFile OK.";
        } catch(PrinterException e) {
            ret = e.getErrorCode();
            msg = "sendDataFile NG.[" + ret + "]";
        }

    }
    public void Thermal_Printer_D10_Test_Click(View view) 
    {
        //Thermal_Printer_D10_Print();
        connectD10Printer();
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
