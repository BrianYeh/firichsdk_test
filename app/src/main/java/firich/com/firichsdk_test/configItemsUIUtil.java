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
 * Created by brianyeh on 2016/9/2.
 */
public class configItemsUIUtil {
    private boolean bDebugOn = true;
    String strTagUtil = "configItemsUIUtil.";
    /*
        private String SysKingICCard_dev="/dev/ttyUSB4";
        private String LCM_dev="/dev/ttyUSB2";
        private String NFC_dev="/dev/ttyUSB4";
    */
    private String fectest_config_path = "/data/fec_config/fectest_config.xml";

    class configItemUI{
        public String name;
        public String id;
        public boolean test;
    }
    configItemUI configItemUIObj;

    Hashtable<String, configItemUI> hashtableConfigUI;


    public configItemsUIUtil()
    {
        hashtableConfigUI = new Hashtable<String, configItemUI>();
    }
    public configItemsUIUtil(String configPath)
    {
        hashtableConfigUI = new Hashtable<String, configItemUI>();
        fectest_config_path = configPath;
    }
    private void dump_trace( String bytTrace)
    {
        if (bDebugOn)
            Log.d(strTagUtil, bytTrace);
    }
    public Hashtable<String, configItemUI>  getHashtableConfigUI(){
        return hashtableConfigUI;
    }
    public configItemUI getConfigItemUI(String strName)
    {
        configItemUI configUIObject= new configItemUI();

        boolean IsExist = false;
        IsExist = hashtableConfigUI.containsKey(strName);
        if (IsExist){
            configUIObject = hashtableConfigUI.get(strName);
        }
        return  configUIObject;
    }
    public void dom4jXMLParser()
    {
        String strBaudRate="";
        int i=1; //  android:id="@+id/linearLayout_test_item_1". index from 1.
        String id="1";
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
                configItemUIObj = new configItemUI();
                configItemUIObj.name = child.attributeValue("name");
                id = Integer.toString(i);
                configItemUIObj.id = id;
                configItemUIObj.test = Boolean.parseBoolean(child.attributeValue("test"));
                hashtableConfigUI.put(id, configItemUIObj); //key is "id"
                i++;

            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
