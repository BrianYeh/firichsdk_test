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
 * Created by brianyeh on 2016/8/31.
 */
public class STD_configUtil {

    private boolean bDebugOn = true;
    String strTagUtil = "STD_configUtil.";
    /*
        private String SysKingICCard_dev="/dev/ttyUSB4";
        private String LCM_dev="/dev/ttyUSB2";
        private String NFC_dev="/dev/ttyUSB4";
    */
    private String fectest_config_path = "/data/fec_config/STD_config.xml";

    class STD_config{
        public String name;
        public String id;
        public boolean test;
        public String path;
        public String configFile;
    }
    STD_config STD_configObject;

    Hashtable<String, STD_config> hashtableSTDConfig;


    public STD_configUtil()
    {
        hashtableSTDConfig = new Hashtable<String, STD_config>();
    }
    public STD_configUtil(String configPath)
    {
        hashtableSTDConfig = new Hashtable<String, STD_config>();
        fectest_config_path = configPath;
    }
    private void dump_trace( String bytTrace)
    {
        if (bDebugOn)
            Log.d(strTagUtil, bytTrace);
    }
    public STD_config getConfig(String strName)
    {
        STD_config configObject= new STD_config();

        boolean IsExist = false;
        IsExist = hashtableSTDConfig.containsKey(strName);
        if (IsExist){
            configObject = hashtableSTDConfig.get(strName);
        }
        return  configObject;
    }
    public void dom4jXMLParser()
    {
        int i=1;
        String id="1";
        String strBaudRate="";
        StringWriter xmlWriter = new StringWriter();
        SAXReader reader = new SAXReader();
        File file = new File(fectest_config_path);

        try {

            Document document = reader.read(file);
            Element root = document.getRootElement();
            List<Element> childElements = root.elements();
            for (Element child : childElements) {
                //已知属性名情况下
                dump_trace("name: " + child.attributeValue("name"));
                dump_trace("dev: " + child.attributeValue("path"));
                STD_configObject = new STD_config();
                STD_configObject.name = child.attributeValue("name");
                id = Integer.toString(i);
                //STD_configObject.id = child.attributeValue("id");
                STD_configObject.id = id;
                STD_configObject.test = Boolean.parseBoolean(child.attributeValue("test"));
                STD_configObject.path = child.attributeValue("path");
                STD_configObject.configFile = child.attributeValue("configFile");
                //hashtableSTDConfig.put(child.attributeValue("id"), STD_configObject); //key is "id"
                hashtableSTDConfig.put(id, STD_configObject); //key is "id"
                i++;
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}