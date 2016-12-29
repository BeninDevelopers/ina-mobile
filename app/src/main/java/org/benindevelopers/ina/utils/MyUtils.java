package org.benindevelopers.ina.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import org.benindevelopers.ina.webservice.WebService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Joane SETANGNI on 11/01/2016.
 */
public class MyUtils {
    public static final String SHARED_PREF_IS_USER_REGISTERED = "is_user_registered";
    public static final String SHARED_PREF_IS_USER_GCM_REGISTERED = "is_user_gcm_registered";
    private static MyUtils ourInstance = new MyUtils();
    private static final String TAG = "MyUtils";

    public static MyUtils getInstance() {
        return ourInstance;
    }

    private MyUtils() {}


    public boolean isNetworkConnectionAvailable(Context cxt) {
        ConnectivityManager cm = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }


    // methode permettant de mofifier un sharedpreference
    public static Boolean getBooleanSharedPref(Context cxt, String prefName){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(cxt);
        return SP.getBoolean(prefName, false);
    }
    // methode permettant de mofifier un sharedpreference
    public static void setBooleanSharedPref(Context cxt, String prefName, boolean value){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(cxt);
        SP.edit().putBoolean(prefName, value).commit();
    }


    public static String getAccountEmail(Context context){
        Account[] accountList = AccountManager.get(context).getAccountsByType("com.google");
        if(accountList != null && accountList.length >0) {
            return accountList[0].name;
        }else{
            return "";
        }
    }

    public WebService getScalarWebServiceManager(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebService.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(WebService.class);

    }

    public WebService getGsonWebServiceManager(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WebService.class);

    }

    /**
     * Recupere l'identificateur du telephone
     *
     * @param appContext
     * @return l'ID du telephone
     */
    /*public static String getPhoneID(Context appContext) {
        TelephonyManager tManager = (TelephonyManager) appContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tManager.getDeviceId();
    }*/
    public static String getPhoneID(Context appContext) {

        return "";
    }
}
