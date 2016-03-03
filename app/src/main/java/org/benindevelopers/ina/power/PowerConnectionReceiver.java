package org.benindevelopers.ina.power;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Shadaï ALI on 27/02/16.
 *
 * Modified by Joane SETANGNI
 *      - register this receiver in manisfest for only ACTION_POWER_CONNECTED
 *          and ACTION_POWER_DISCONNECTED actions
 *      - by doing so we always got charging status false because  ACTION_POWER_CONNECTED
 *          is called before ACTION_BATTERY_CHANGED which update the battery status we need.
 *          Thank to ShellDude (http://stackoverflow.com/a/31091887/1993488) for his solution
 *          which is getting the ACTION_BATTERY_CHANGED intent from inside of ower onReceive methode.
 *          We add to that a postDelayed to let some time to ACTION_BATTERY_CHANGED intent to be updated
 *          before calling it.
 */
public class PowerConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = "PowerReceiver";
    private boolean usbCharge = false;
    private boolean acCharge = false;
    private Context cxt;
    private int postDelayTime = 1000 * 3; // en milliseconde

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"onReceive");
        cxt = context;

        Handler myHandler = new Handler();

        // on commence le checking apres un certain nombre de seconde car le BATTERY status n'est
        // pas mise à jour automatiquement apres le ACTION_POWER_CONNECTED.
        // Cela est fait apres  ACTION_BATTERY_CHANGED qui est exécuté apres notre ACTION_POWER_CONNECTED
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = cxt.registerReceiver(null, ifilter);

                int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

//                Log.i(TAG,"chargePlug "+chargePlug);
//                Log.i(TAG,"Type charge "+(usbCharge?"usb":"noUSB")+" "+(acCharge?"ac":"noAc"));

                if (usbCharge){
                    //aucun traitement en cas de charge par usb
                    Log.i(TAG,"en charge : usb branché");
                    usbCharge = false;

                }else if (acCharge) {
                    //Si charge par secteur
                    Log.i(TAG, "ac branché");
                    acCharge = false;
                    Intent lanchActivityIntent = new Intent(cxt, PowerDialogActivity.class);
                    lanchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    cxt.startActivity(lanchActivityIntent);
                }

            }
        }, postDelayTime);


    }



}