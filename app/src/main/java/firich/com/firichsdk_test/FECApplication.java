package firich.com.firichsdk_test;

import android.app.Application;

/**
 * Created by brianyeh on 2016/8/31.
 */
public class FECApplication  extends Application {

    private String FEC_config_path;

    public String getFEC_config_path() {
        return FEC_config_path;
    }

    public void setFEC_config_path(String FEC_config_pathl) {
        this.FEC_config_path = FEC_config_pathl;
    }

}
