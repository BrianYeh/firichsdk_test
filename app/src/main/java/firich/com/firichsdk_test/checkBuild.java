package firich.com.firichsdk_test;

import android.os.Build;

/**
 * Created by brianyeh on 2016/8/17.
 */
public class checkBuild {

    public void checkBuild(){

    }
    public boolean checkVersion()
    {
        String mString = "";
/*
        mString+=Build.VERSION.RELEASE;
        mString+= Build.VERSION.INCREMENTAL;
        mString+=Build.VERSION.SDK;
        mString+=  Build.BOARD;
        mString+= Build.BRAND;
        mString+= Build.FINGERPRINT;
        mString+= Build.HOST;
        mString+= Build.ID;
        */
        mString+= Build.DISPLAY;
        boolean contains_android4 = mString.contains("4.4.3 2.0.0-rc2.");
        boolean contains_android5 = mString.contains("Edelweiss-T 5.1");
        /*
        mString.concat("VERSION.RELEASE {" + Build.VERSION.RELEASE + "}");
        mString.concat("\nVERSION.INCREMENTAL {" + Build.VERSION.INCREMENTAL + "}");
        mString.concat("\nVERSION.SDK {" + Build.VERSION.SDK + "}");
        mString.concat("\nBOARD {" + Build.BOARD + "}");
        mString.concat("\nBRAND {" + Build.BRAND + "}");
        mString.concat("\nDEVICE {" + Build.DEVICE + "}");
        mString.concat("\nFINGERPRINT {" + Build.FINGERPRINT + "}");
        mString.concat("\nHOST {" + Build.HOST + "}");
        mString.concat("\nID {" + Build.ID + "}");
        */

        boolean checkPASS = false;
        checkPASS = (contains_android4 || contains_android5);
        checkPASS = (contains_android5);
        //return  checkPASS;
        return true;
    }
}
