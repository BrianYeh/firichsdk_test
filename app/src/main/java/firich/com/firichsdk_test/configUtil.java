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
 * Created by brianyeh on 2016/7/28.
 */
public class configUtil {

    private boolean bDebugOn = true;
    String strTagUtil = "configUtil.";
/*
    private String SysKingICCard_dev="/dev/ttyUSB4";
    private String LCM_dev="/dev/ttyUSB2";
    private String NFC_dev="/dev/ttyUSB4";
*/
    class Device{
        public String Name;
        public String Dev;
        public String Path1;
        public String Path2;
        public int BaudRate;
    }
    Device DevObject;
    Hashtable<String, Device> hashtableConfig;


    public configUtil()
    {
        hashtableConfig = new Hashtable<String, Device>();
    }
    private void dump_trace( String bytTrace)
    {
        if (bDebugOn)
            Log.d(strTagUtil, bytTrace);
    }
    public Device getDevice(String strName)
    {
        Device devObject= new Device();

        boolean IsExist = false;
        IsExist = hashtableConfig.containsKey(strName);
        if (IsExist){
            devObject = hashtableConfig.get(strName);
        }
        return  devObject;
    }
    public void dom4jXMLParser()
    {
        String strBaudRate="";
        StringWriter xmlWriter = new StringWriter();
        SAXReader reader = new SAXReader();
        File file = new File("/data/fectest_config.xml");

        try {

            Document document = reader.read(file);
            Element root = document.getRootElement();
            List<Element> childElements = root.elements();
            for (Element child : childElements) {
                //已知属性名情况下
                dump_trace("name: " + child.attributeValue("name"));
                dump_trace("dev: " + child.attributeValue("dev"));
                DevObject = new Device();
                if ("ThermalPrinterTest".equals(child.attributeValue("name"))){
                    DevObject.Name = child.attributeValue("name");
                    DevObject.Path1 = child.attributeValue("path1");
                    DevObject.Path2 = child.attributeValue("path2");
                    hashtableConfig.put(child.attributeValue("name"), DevObject);
                }
                else{
                    DevObject.Name = child.attributeValue("name");
                    DevObject.Dev = child.attributeValue("dev");
                    strBaudRate = child.attributeValue("baud_rate");
                    if ( strBaudRate!= null  && !strBaudRate.isEmpty()) {
                        DevObject.BaudRate = Integer.valueOf(strBaudRate);
                    }
                    hashtableConfig.put(child.attributeValue("name"), DevObject);
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
