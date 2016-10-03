package firich.com.firichsdk_test;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by brianyeh on 2016/9/29.
 */
public class ttyUSBParser {

    private boolean bDebugOn = true;

    String[] ttyUSBPathList ;
    int numberOfttyUSB=0;
    int numberOfUSBCOM=0;
    private void dump_trace( String bytTrace)
    {
        if (bDebugOn)
            Log.d("ttyUSBParser", bytTrace);
    }

    public String[] get_ttyUSBList()
    {
        return ttyUSBPathList;
    }

    String str_ExternalDevice_1_7="/1-7/1-7";
    String str_USBCOM_1_4="/1-4/1-4";
    //tablet:  /1-4/1-4:1.0 ~         /1-4/1-4:1.3
    //desktop: /1-4/1-4.4/1-4.4:1.0 ~ /1-4/1-4.4/1-4.4:1.3

    //only one External Device on testing.
    String get_ttyUSBPath_ExternalDevice_by_contain_1_7()
    {
        String strttyUSB_ExternalDevice="";
        String strExterDevice_ttyUSBX="";
        for (int i=0; i< numberOfttyUSB;i++){
            if (ttyUSBPathList[i].contains(str_ExternalDevice_1_7)){
                strttyUSB_ExternalDevice = ttyUSBPathList[i];
            }
        }
        int startTTYUSB=0;
        startTTYUSB = strttyUSB_ExternalDevice.indexOf("ttyUSB");
        if (startTTYUSB == -1)
            return null;
        strExterDevice_ttyUSBX ="/dev/"+strttyUSB_ExternalDevice.substring(startTTYUSB, startTTYUSB+7);
        return  strExterDevice_ttyUSBX;
    }
    String[] ttyUSBCOM;
    String[] get_USBCOM_by_contain_1_4()
    {
        //TODO:
        int USBCOMIndex=0;
        ttyUSBCOM = new String[4];
        for (int i=0; i< numberOfttyUSB;i++){
            if ( ttyUSBPathList[i].contains(str_USBCOM_1_4) && ttyUSBPathList[i].contains("ttyUSB") ){
                int startTTYUSBCOM=0;
                startTTYUSBCOM = ttyUSBPathList[i].indexOf("ttyUSB");
                ttyUSBCOM[USBCOMIndex] ="/dev/"+ ttyUSBPathList[i].substring(startTTYUSBCOM, startTTYUSBCOM+7);
                dump_trace(ttyUSBCOM[USBCOMIndex] );
                USBCOMIndex++;
                if(USBCOMIndex>=4) {
                    break;
                }
            }
        }
        numberOfUSBCOM = USBCOMIndex;
        return ttyUSBCOM;
    }
    public boolean parse_ttyUSB() {
        //args = new String[]{"cmd", "/c", "dir", "C:\\"};

        String[] command = {"ls", "/sys/class/tty", "-al"};
        StringBuilder cmdReturn = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);


            Process process = processBuilder.start();
            //copy(process.getInputStream(), System.out);
            InputStream inputStream = process.getInputStream();
            int c;
            while ((c = inputStream.read()) != -1) {
                cmdReturn.append((char) c);
            }
            String lsString=cmdReturn.toString();
            dump_trace(lsString);

            String[] strttyUSBPathList = lsString.split("\\r?\\n");

            int numLines = strttyUSBPathList.length;
            //String[] ttyUSBPathList = new String[numLines];
            ttyUSBPathList = new String[numLines];
            int ttyUSB=0;
            for (int i=0; i< numLines;i++){
                if (strttyUSBPathList[i].contains("ttyUSB")){
                    ttyUSBPathList[ttyUSB] = strttyUSBPathList[i];
                    dump_trace(ttyUSBPathList[ttyUSB]);
                    ttyUSB++;
                }
            }
            numberOfttyUSB = ttyUSB;
            int mExitValue = process.waitFor();
            System.out.println(" mExitValue "+mExitValue);
            if(mExitValue==0){
                return true;
            }else{
                return false;
            }
        }catch (InterruptedException ignore)
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
}
