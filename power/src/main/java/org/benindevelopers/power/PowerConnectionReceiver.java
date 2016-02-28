package org.benindevelopers.power;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

/**
 * Created by Shadaï ALI on 27/02/16.
 */ public class PowerConnectionReceiver extends BroadcastReceiver {

    private boolean isCharging = false;
    private boolean usbCharge = false;
    private boolean acCharge = false;
    @Override
    public void onReceive(Context context, Intent intent) {

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if (usbCharge){
            //TODO :  Traitement en cas de charge par usb
            Toast.makeText(context,"en charge : usb branché",Toast.LENGTH_SHORT).show();
            usbCharge = false;
        }
        else
        if (acCharge) {
            //TODO :  Traitement en cas de charge par secteur
            Toast.makeText(context, "ac branché", Toast.LENGTH_SHORT).show();
            acCharge = false;
        }

    }
}