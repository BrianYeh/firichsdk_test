package firich.com.firichsdk_test;

import android.util.Log;

/**
 * Created by brianyeh on 2016/6/7.
 */
public class smartCardUtil {

    private boolean bDebugOn = true;
    String strTagUtil = "smartCardUtil.";
    String strOutputLRC="";
    public String getLRCString()
    {
        return  strOutputLRC;
    }
    public static String hex(int n) {
        // call toUpperCase() if that's required
         String strHex="";
        strHex = String.format("0x%4s", Integer.toHexString(n)).replace(' ', '0');
        strHex = strHex.substring(strHex.length()-2);
        strHex = String.format("0x%2s", strHex);

        return  strHex;
    }

    private void dump_trace( String bytTrace)
    {
        if (bDebugOn)
            Log.d(strTagUtil, bytTrace);
    }

    boolean calculateLRC(byte[] btyVersion_msg_received, int intDataReceivedLength)
        {

            // calulate LRC

            byte[] btyLRC = new byte[]{0x0};
            short nLRC=0;
            int i=0;

            for (i = 0; i < intDataReceivedLength; i++) {
                dump_trace("btyVersion_msg_received hex[" + i + "]= " + hex((int) btyVersion_msg_received[i]));
                //strOutputLRC = strOutputLRC + "Hex[" + i + "]= " + hex((int) btyVersion_msg_received[i])+"\n";
            }

            for (i = 0; i < intDataReceivedLength-1; i++)
            {

                nLRC = (short) (nLRC ^ (short)btyVersion_msg_received[i]);
                dump_trace("TmpLRC = "+ hex(nLRC) );
            }
            dump_trace("LRC = "+ hex(nLRC) );
            //strOutputLRC = strOutputLRC + "LRC = "+ hex(nLRC) +"\n";
            strOutputLRC = "LRC = "+ hex(nLRC) +"\n";

        /*
        if(0 != memcmp(&lrc, &buffer[recv_lenth-1],1))
        printf("Get Version LRC error\n");
        */

            btyLRC[0] = (byte)(nLRC & 0xff);

            dump_trace("btyLRC = "+ btyLRC[0] );

            if (intDataReceivedLength == 0){
                strOutputLRC = strOutputLRC + ("Test FAIL")+"\n";
                return false;
            }
            boolean match = (btyVersion_msg_received[intDataReceivedLength-1] == (byte)btyLRC[0]);
            dump_trace("Test PASS or NO(true/false) === "+ match);
            if (match){
                strOutputLRC = strOutputLRC + ("Test PASS")+"\n";
            }else{
                strOutputLRC = strOutputLRC + ("Test FAIL")+"\n";
            }
            return match;
        }
}