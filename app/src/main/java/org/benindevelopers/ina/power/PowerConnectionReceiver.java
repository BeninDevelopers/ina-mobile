package org.benindevelopers.ina.power;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.benindevelopers.ina.webservice.WebService;

/**
 * Created by Shadaï ALI on 27/02/16.
 */
public class PowerConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = "PowerReceiver";
    private boolean isCharging = false;
    private boolean usbCharge = false;
    private boolean acCharge = false;
    private double latitude;
    private double longitude;
    private Context cxt;
    private WebService retrofit;
    private AlertDialog materialDialog;

    @Override
    public void onReceive(Context context, Intent intent) {

        cxt = context;

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if (usbCharge){
            //aucun traitement en cas de charge par usb
            Log.i(TAG,"en charge : usb branché");
            usbCharge = false;
            cxt.startActivity(new Intent(cxt, PowerDialogActivity.class));

        }else if (acCharge) {
            //Si charge par secteur
            // envoie de l'info au serveur
            Log.i(TAG, "ac branché");
//            envoieEtatCourant(true);
            acCharge = false;
        }

    }



}