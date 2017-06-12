package firich.com.firichsdk_test;

import android.util.Log;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by brianyeh on 2017/4/24.
 */
public class configServer {

    private boolean bDebugOn = true;
    public boolean bHasServerConfig  = false;
    String strTagUtil = "configServer.";
    private String log_server_config_path = "/data/fec_config/log_server_config.xml";
    String targetFolder = "smb://192.168.0.5/sw_rd/Temp/Brian/log/";
    class configSettings{

        /*
        String strID="0";
        String strName="server";
        String strIP="192.168.0.5";
        String strPath="/sw_rd/Temp/Brian/log/";
        String strDomain="workgroup";
        String strUser="rd2";
        String strPassword="firich1446";
        */
        String strID;
        String strName;
        String strIP;
        String strPath;
        String strDomain;
        String strUser;
        String strPassword;
    }

    configSettings configSettingsObj;

    Hashtable<String, configSettings> hashtableConfigSettings;

    public configServer()
    {
        hashtableConfigSettings = new Hashtable<String, configSettings>();
    }

    private void dump_trace( String bytTrace)
    {
        if (bDebugOn)
            Log.d(strTagUtil, bytTrace);
    }

    public Hashtable<String, configSettings>  getHashtableConfigSettings(){
        return hashtableConfigSettings;
    }
    boolean HasConfigServerSetting()
    {
        return  bHasServerConfig;
    }
    public configSettings getConfigSettings(String strKey)
    {
        configSettings configSettingsObj= new configSettings();

        boolean IsExist = false;
        IsExist = hashtableConfigSettings.containsKey(strKey);
        if (IsExist){
            configSettingsObj = hashtableConfigSettings.get(strKey);
            bHasServerConfig = true;
        }
        return  configSettingsObj;
    }
    public void dom4jXMLParser()
    {
        String strBaudRate="";
        int i=0; //  android:id="@+id/linearLayout_test_item_1". index from 1.
        String id="0";
        StringWriter xmlWriter = new StringWriter();
        SAXReader reader = new SAXReader();
        File file = new File(log_server_config_path);

        try {

            Document document = reader.read(file);
            Element root = document.getRootElement();
            List<Element> childElements = root.elements();
            for (Element child : childElements) {
                //已知属性名情况下
                dump_trace("name: " + child.attributeValue("name"));
                configSettingsObj = new configSettings();
                configSettingsObj.strName = child.attributeValue("name");
                configSettingsObj.strIP = child.attributeValue("ip");
                configSettingsObj.strPath = child.attributeValue("path");
                configSettingsObj.strDomain = child.attributeValue("domain");
                configSettingsObj.strUser = child.attributeValue("user");
                configSettingsObj.strPassword = child.attributeValue("password");
                id = Integer.toString(i);
                configSettingsObj.strID = id;
                hashtableConfigSettings.put(id, configSettingsObj); //key is "id"
                i++;
                //Debug
/*
                if (i> 13){
                    break;
                }*/

            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
