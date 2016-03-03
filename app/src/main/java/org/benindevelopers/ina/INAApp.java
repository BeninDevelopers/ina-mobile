package org.benindevelopers.ina;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by Joane SETANGNI on 29/02/2016.
 */
public class INAApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
    }
}
