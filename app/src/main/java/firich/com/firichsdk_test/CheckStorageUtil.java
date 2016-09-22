package firich.com.firichsdk_test;

import android.os.Build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by brianyeh on 2016/9/20.
 */
public class CheckStorageUtil {
    boolean findStorageFeclogFileFlag = false;

    String path = "/mnt/media_rw/extsd/sd.txt";
    String FECLogString = "FEC Storage test complete.";
    public CheckStorageUtil(String logPath)
    {
        if (logPath != null && !logPath.isEmpty()) {
            path = logPath;
        }
    }
    public boolean isFindStorageFECLogFile()
    {
        return findStorageFeclogFileFlag;
    }
    public boolean checkSDCardStorage()
    {
        // check feclog file exist?
        String strStorage="/mnt/media_rw";
        String strfeclog = "sd.txt";
        String strSDCard_4="extsd";
        String strUDisk4_4 = "udisk";
        String strUSBDisk5_1 = "usbdisk";
        String strDisk=strUDisk4_4;
        String strDiskNum=strSDCard_4;
        boolean findFeclogFile = false;
        File logFile;

        boolean checkFileOK=false;


        String strVersion = Build.DISPLAY;
        boolean contains_android4 = strVersion.contains("4.4.3 2.0.0-rc2.");
        boolean contains_android5 = strVersion.contains("Edelweiss-T 5.1");
        boolean contains_android5_D = strVersion.contains("Edelweiss-D 5.1");

        if (contains_android5|| contains_android5_D ){
            //strDisk = strUSBDisk5_1;
            //strDiskNum = strUSBDisk5_1;
        }

        logFile = new File(path);

        int retry=0;
        do {
            if (logFile.exists()) {
                findFeclogFile = true;
                findStorageFeclogFileFlag = true;
                //check write, read file ok?
                try {
                    //BufferedWriter for performance, true to set append to file flag
                    BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, false)); //false means: not append.
                    buf.write(FECLogString);
                    buf.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String readLogFile="";
                readLogFile = readStorageLog();
                checkFileOK = readLogFile.contains(FECLogString);

                break;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            retry++;
        } while (retry < 3);
        return checkFileOK;
    }

    public String readStorageLog() {
        //Find the directory for the SD Card using the API
//*Don't* hardcode "/sdcard"
       // File sdcard = Environment.getExternalStorageDirectory();

//Get the text file
      //  File file = new File(sdcard,"file.txt");
        File logFile;
        logFile = new File(path);

//Read text from file
        StringBuilder text = new StringBuilder();

        String line="";

        try {
            BufferedReader br = new BufferedReader(new FileReader(logFile));

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }
}
