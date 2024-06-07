package com.ugb.controlesbasicos20;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class ClassVerifyNet {
    private Activity activity;
    public boolean verifyNet;

    public ClassVerifyNet(Activity activity) {
        this.activity = activity;
    }

    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();
            boolean reachable = (val == 0);
            verifyNet = reachable;
            return reachable;

        } catch (Exception e) {
            Log.d("ClassVerifyNet", "Error al verificar conexion a internet: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
