package firich.com.firichsdk_test;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.idtechproducts.device.APDUResponseStruct;
import com.idtechproducts.device.Common;
import com.idtechproducts.device.ErrorCode;
import com.idtechproducts.device.IDTEMVData;
import com.idtechproducts.device.IDTMSRData;
import com.idtechproducts.device.IDT_MiniSmartII;
import com.idtechproducts.device.OnReceiverListener;
import com.idtechproducts.device.ResDataStruct;
import com.idtechproducts.device.StructConfigParameters;

import java.util.Set;

//public class IDICCard extends AppCompatActivity {
public class IDICCard extends Activity implements OnReceiverListener {

    // declaring the instance of the MiniSmartII;
    private IDT_MiniSmartII myDevice = null;
    private TextView connectStatusTextView;
    private TextView textLog;
    private TextView lcdLog;
    private Button btnGetFirmware;
    private Button btnStartEMV;
    private Button btnCancelEMV;
    private Handler handler = new Handler();
    private boolean isReaderConnected = false;
    private String info = "";
    private String detail = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_idiccard);
        handler = new Handler();
        //btnStartEMV = (Button)findViewById(R.id.btn_StartEMV);
        //btnCancelEMV = (Button)findViewById(R.id.btn_CancelEMV);
        btnGetFirmware = (Button)findViewById(R.id.btn_getFirmware);
        textLog = (TextView)findViewById(R.id.textLog);
        lcdLog = (TextView)findViewById(R.id.lcdLog);
        connectStatusTextView = (TextView)findViewById(R.id.status_text);
        if(myDevice!=null){
            myDevice.unregisterListen();
            myDevice.release();
            myDevice = null;
        }
        myDevice = new IDT_MiniSmartII(this,this);
        myDevice.registerListen();


        btnGetFirmware.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                info = "Getting Firmware\n";
                detail = "";
                handler.post(doUpdateStatus);
                StringBuilder sb = new StringBuilder();
                int ret = myDevice.device_getFirmwareVersion(sb);
                if (ret == ErrorCode.SUCCESS) {
                    info += "Firmware Version: " + sb.toString();
                    detail = "";
                    handler.post(doUpdateStatus);
                }
                else {
                    info += "GetFirmwareVersion: Failed\n";
                    info += "Status: "+ myDevice.device_getResponseCodeString(ret)+"";
                    detail = "";
                    handler.post(doUpdateStatus);
                }
            }
        });
		/*
		btnStartEMV.setOnClickListener(new Button.OnClickListener(){
	        public void onClick(View v) {;
	        detail = "";
	        info = "Starting EMV Transaction\n";
        	handler.post(doUpdateStatus);
	        	IDT_MiniSmartII.emv_allowFallback(true);
				myDevice.emv_startTransaction(1.00, 0.00, 0, 30, null, false);
	        }
	    });

		btnCancelEMV.setOnClickListener(new Button.OnClickListener(){
	        public void onClick(View v) {
	        	detail = "";
		        info = "Canceling EMV Transaction\n";
	        	handler.post(doUpdateStatus);
	        	ResDataStruct resData = new ResDataStruct();
				myDevice.emv_cancelEMVTransaction(resData);
	        }
	    });
	    */


    }

    public void ICC_PowerOn_click(View view){
        int ret;
        ResDataStruct resData = new ResDataStruct();

        ret = myDevice.icc_powerOnICC(resData);
        if (ret == ErrorCode.SUCCESS) {
            if (resData.resData != null)
                info = "Power On ICC: Successful <" + Common.base16Encode(resData.resData)+">";
            else
                info = "Power On ICC: Successful";
            detail = "";
            handler.post(doUpdateStatus);
        }
        else {
            info = "Power On ICC: Failed\n";
            info += "Status: "+myDevice.device_getResponseCodeString(ret)+"";
            detail = "";
            handler.post(doUpdateStatus);
        }
    }



    private Dialog dlgSendCAPDU;
    private Dialog dlgSendCmd;
    private EditText edtCAPDU;
    private EditText edtCmd;
    public void ICC_Exchange_APDU_Plaintext(View view)
    {
        dlgSendCAPDU = new Dialog(this);
        dlgSendCAPDU.setTitle("Please Enter C-APDU");
        dlgSendCAPDU.setCancelable(false);
        dlgSendCAPDU.setContentView(R.layout.apdu_dialog);
        Button btnSendCAPDU = (Button) dlgSendCAPDU.findViewById(R.id.btnSendCAPDU);
        Button btnCancelCAPDU = (Button) dlgSendCAPDU.findViewById(R.id.btnCancelAPDU);
        edtCAPDU = (EditText) dlgSendCAPDU.findViewById(R.id.edtCAPDU);
        btnSendCAPDU.setOnClickListener(sendCAPDUOnClick);
        btnCancelCAPDU.setOnClickListener(cancelCAPDUOnClick);
        dlgSendCAPDU.show();
    }

    private View.OnClickListener sendCAPDUOnClick = new View.OnClickListener(){
        public void onClick(View v){
            String strData = edtCAPDU.getText().toString();
            APDUResponseStruct apduRes = new APDUResponseStruct();
            int ret;
            if (strData.length()<=0) {
                Toast.makeText(getApplicationContext(), "Command could not be sent", Toast.LENGTH_SHORT).show();
                return;
            }

            byte[] apduPlaintext = Common.getBytesFromHexString(strData);
            if (apduPlaintext == null) {
                Toast.makeText(getApplicationContext(), "Invalid APDU, please input hex data", Toast.LENGTH_SHORT).show();
                return;
            }
            dlgSendCAPDU.dismiss();

            ret = myDevice.icc_exchangeAPDU(apduPlaintext, apduRes);
            if (ret == ErrorCode.SUCCESS){
                info = "C-APDU: <" + strData +">";
                detail = "APDU Result: <" + Common.base16Encode(apduRes.response)+">";
            }
            else {
                info = "Exchange APDU Plaintext Failed\n";
                info += "Status: "+myDevice.device_getResponseCodeString(ret)+"";
                detail = "";
            }
            handler.post(doUpdateStatus);
        }
    };

    private View.OnClickListener cancelCAPDUOnClick = new View.OnClickListener(){
        public void onClick(View v){
            dlgSendCAPDU.cancel();
        }

    };


    @Override
    public void ICCNotifyInfo(byte[] arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void LoadXMLConfigFailureInfo(int arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void autoConfigCompleted(StructConfigParameters arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void autoConfigProgress(int arg0) {
        // TODO Auto-generated method stub

    }

    private Runnable doUpdateLabel = new Runnable()
    {
        public void run()
        {
            if(!isReaderConnected){
                connectStatusTextView.setText("MINISMARTII DISCONNECTED");
            }
            else{
                connectStatusTextView.setText("MINISMARTII CONNECTED");
            }
        }
    };
    @Override
    public void deviceConnected() {
        isReaderConnected = true;
        handler.post(doUpdateLabel);
    }

    @Override
    public void deviceDisconnected() {
        isReaderConnected = false;
        handler.post(doUpdateLabel);
    }


    @Override
    public void emvTransactionData(IDTEMVData emvData) {

        detail += Common.emvErrorCodes(emvData.result);
        detail += "\r\n";
        if (emvData.result == IDTEMVData.START_TRANS_SUCCESS)
            detail += "Start transaction response:\r\n";
        else if (emvData.result == IDTEMVData.GO_ONLINE)
            detail += "\r\nAuthentication response:\r\n";
        else
            detail += "\r\nComplete Transaction response:\r\n";
        if (!emvData.unencryptedTags.isEmpty())
        {
            detail += "Unencrypted Tags:\r\n";
            Set<String> keys = emvData.unencryptedTags.keySet();
            for(String key: keys){
                detail += key + ": ";
                byte[] data = emvData.unencryptedTags.get(key);
                detail += Common.getHexStringFromBytes(data) + "\r\n";
            }
        }
        if (!emvData.maskedTags.isEmpty())
        {
            detail += "Masked Tags:\r\n";
            Set<String> keys = emvData.maskedTags.keySet();
            for(String key: keys){
                detail += key + ": ";
                byte[] data = emvData.maskedTags.get(key);
                detail += Common.getHexStringFromBytes(data) + "\r\n";
            }
        }
        if (!emvData.encryptedTags.isEmpty())
        {
            detail += "Encrypted Tags:\r\n";
            Set<String> keys = emvData.encryptedTags.keySet();
            for(String key: keys){
                detail += key + ": ";
                byte[] data = emvData.encryptedTags.get(key);
                detail += Common.getHexStringFromBytes(data) + "\r\n";
            }
        }
        handler.post(doUpdateStatus);
        if (emvData.result == IDTEMVData.GO_ONLINE){
            //Auto Complete
            byte[] response = new byte[]{0x30,0x30};
            myDevice.emv_completeTransaction(false, response, null, null,null);
        }
        else if (emvData.result == IDTEMVData.START_TRANS_SUCCESS){
            //Auto Authenticate
            myDevice.emv_authenticateTransaction(null);
        }

    }

    public void lcdDisplay(int mode, String[] lines, int timeout) {

        if (mode == 0x01) //Menu Display
        {
            //automatically select 1st application
            myDevice.emv_lcdControlResponse((byte)mode, (byte)0x01);
        }
        else if (mode == 0x08) //Language Menu Display
        {
            //automatically select first language
            myDevice.emv_lcdControlResponse((byte)mode, (byte)0x01);
        }
        else{
            ResDataStruct toData = new ResDataStruct();
            info = lines[0];
            handler.post(doUpdateStatus);
        }
    }


    @Override
    public void msgAudioVolumeAjustFailed() {
        // TODO Auto-generated method stub

    }

    @Override
    public void msgRKICompleted(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void msgToConnectDevice() {
        // TODO Auto-generated method stub

    }

    private Runnable doUpdateStatus = new Runnable()
    {
        public void run()
        {
            lcdLog.setText(info);
            textLog.setText(detail);
        }
    };
    @Override
    public void swipeMSRData(IDTMSRData card) {


    }

    @Override
    public void timeout(int arg0) {
        // TODO Auto-generated method stub

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