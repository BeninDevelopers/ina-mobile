/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.benindevelopers.ina;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.benindevelopers.ina.utils.PhoneRegisterEvent;
import org.benindevelopers.ina.utils.MyUtils;
import org.benindevelopers.ina.webservice.WebService;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;


public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private static String register_id="";
    private Context cxt;
    private String gcmId = "";

    public RegistrationIntentService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cxt = getApplicationContext();

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            gcmId = instanceID.getToken(getString(R.string.gcm_reg_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
//            Toast.makeText(this,token,Toast.LENGTH_LONG).show();
            Log.e(TAG, "GCM Registration Token: " + gcmId);

            // TODO: Implement this method to send any registration to your app's servers.
            onGCMRegisterResponse();
//            register_id=token;

            // Subscribe to topic channels

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
            onGCMRegisterResponse();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        //Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


    /**
     * Méthode gérant les event enclenchés lors de l'enregistrement GCM
     */
    public void onGCMRegisterResponse(){
        if(gcmId != null){
            Log.i(TAG, "GCM registered");
        }else{
            /**
             * Désormais on permet à l'utilisateur de continuer le processus si l'enregistrement GCM ne
             * marche pas. Ainsi nous pouvons réessayer le registerGCM plus tard
             */
            Log.i(TAG, "GCM failed");

//            /**
//             * Méthode utilisée pour notifier le user de l'échec de l'enregistrement GCM
//             * et lui permettre de réeassayer
//             */
//        Snackbar snackBar = Snackbar.make(imgView, R.string.erreur_serveur, Snackbar.LENGTH_INDEFINITE);
//        snackBar.setAction(R.string.reassayer, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                registerUser();
//            }
//        });
//        snackBar.show();
        }
        registerUser();
    }

    /**
     * Méthode permettant d'enrégistrer le nouvel utilisateur aupres du serveur INA
     */
    private void registerUser(){
        // Enregistremrent du user
        Call<String> call = MyUtils.getInstance().getScalarWebServiceManager().enregistrerUtilisateur(
                MyUtils.getAccountEmail(cxt),
                MyUtils.getPhoneID(cxt),
                gcmId
        );

        try {
            Response<String> response = call.execute();
            String rep = response.body();
            if (rep!=null && rep.equals(WebService.REP_OK)){
                // si enrégistrement réussie
                MyUtils.setBooleanSharedPref(cxt, MyUtils.SHARED_PREF_IS_USER_REGISTERED, true);
                EventBus.getDefault().post(new PhoneRegisterEvent(true));
            }else{
                EventBus.getDefault().post(new PhoneRegisterEvent(false));
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Erreur "+e.getMessage());
            EventBus.getDefault().post(new PhoneRegisterEvent(false));
        }
    }

}