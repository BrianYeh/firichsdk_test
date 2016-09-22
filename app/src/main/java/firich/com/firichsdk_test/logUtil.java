package firich.com.firichsdk_test;

import android.os.Build;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by brianyeh on 2016/8/29.
 */
public class logUtil {

    boolean findFeclogFileFlag = false;
    public void logUtil()
    {

    }
    public boolean isFindUdiskFECLogFile()
    {
        return findFeclogFileFlag;
    }
    public String findUDiskFileName()
    {
        // check feclog file exist?
        String strStorage="/storage/";
        String path = "/storage/udisk/feclog.txt";
        String pathReal = "/storage/udisk/serial_XXXX_XX_XX.txt";
        String strfeclog = "feclog.txt";
        String strFecRealLog = "serial_year_month_date.txt";
        String strUDisk4_4 = "udisk";
        String strUSBDisk5_1 = "usbdisk";
        String strDisk=strUDisk4_4;
        String strDiskNum=strUDisk4_4;
        boolean findFeclogFile = false;
        int diskNumber = 1;
        File logFile;
        File logRealFile;


        String strVersion = Build.DISPLAY;
        boolean contains_android4 = strVersion.contains("4.4.3 2.0.0-rc2.");
        boolean contains_android5 = strVersion.contains("Edelweiss-T 5.1");
        boolean contains_android5_D = strVersion.contains("Edelweiss-D 5.1");

        if (contains_android5|| contains_android5_D ){
            strDisk = strUSBDisk5_1;
            strDiskNum = strUSBDisk5_1;
        }
        path = strStorage + strDiskNum + "/" + strfeclog;
        logFile = new File(path);
        //Initialize your Date however you like it.
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String strYear = Integer.toString(year);
        String strMonth = Integer.toString(month);
        String strDay = Integer.toString(day);
        String strDateTime = strYear + "_" + strMonth + "_" + strDay;
        do {
            if (logFile.exists()) {
                findFeclogFile = true;
                findFeclogFileFlag = true;
                pathReal = strStorage + strDisk + "/" + "FEC_Log_"+Build.SERIAL+"_" + strDateTime+".txt";
                break;
            }
            diskNumber++;
            if (diskNumber > 1) {
                //strDisk = strDiskNum + "_" + Integer.toString(diskNumber);
                strDisk = strDiskNum  + Integer.toString(diskNumber);
            }
            path = strStorage + strDisk + "/" + strfeclog; //ex: "/storage/udisk/feclog.txt"
            logFile = new File(path);
        } while (diskNumber < 9);
        return pathReal;

    }
    public void appendLog(String text) {
        // check feclog file exist?
        String strStorage="/storage/";
        String path = "/storage/udisk/feclog.txt";
        String pathReal = "/storage/udisk/serial_XXXX_XX_XX.txt";
        String strfeclog = "feclog.txt";
        String strFecRealLog = "serial_year_month_date.txt";
        String strUDisk4_4 = "udisk";
        String strUSBDisk5_1 = "usbdisk";
        String strDisk=strUDisk4_4;
        String strDiskNum=strUDisk4_4;
        boolean findFeclogFile = false;
        int diskNumber = 1;
        File logFile;
        File logRealFile;


        String strVersion = Build.DISPLAY;
        boolean contains_android4 = strVersion.contains("4.4.3 2.0.0-rc2.");
        boolean contains_android5 = strVersion.contains("Edelweiss-T 5.1");
        boolean contains_android5_D = strVersion.contains("Edelweiss-D 5.1");

        if (contains_android5|| contains_android5_D ){
            strDisk = strUSBDisk5_1;
            strDiskNum = strUSBDisk5_1;
        }
        path = strStorage + strDiskNum + "/" + strfeclog;
        logFile = new File(path);
        //Initialize your Date however you like it.
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String strYear = Integer.toString(year);
        String strMonth = Integer.toString(month);
        String strDay = Integer.toString(day);
        String strDateTime = strYear + "_" + strMonth + "_" + strDay;
        do {
            if (logFile.exists()) {
                findFeclogFile = true;
                pathReal = strStorage + strDisk + "/" + "FEC_Log_"+Build.SERIAL+"_" + strDateTime+".txt";
                break;
            }
            diskNumber++;
            if (diskNumber > 1) {
                strDisk = strDiskNum + "_" + Integer.toString(diskNumber);
            }
            path = strStorage + strDisk + "/" + strfeclog; //ex: "/storage/udisk/feclog.txt"
            logFile = new File(path);
        } while (diskNumber < 9);

        if (findFeclogFile) {
            logRealFile = new File(pathReal);
            if (!logRealFile.exists()) {
                try {
                    logRealFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logRealFile, true));
                buf.append(text);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
