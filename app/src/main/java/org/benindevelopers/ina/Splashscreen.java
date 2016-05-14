package org.benindevelopers.ina;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.benindevelopers.ina.utils.MyUtils;
import org.benindevelopers.ina.utils.PhoneRegisterEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by Fabrice THILAW on 22/02/2016.
 */

/**
 *  Modifed by Joane SETANGNI
 *      - GCM registration
 *      - handle GCM registration success and failure events
 *      - User registraion
 */
public class Splashscreen extends AppCompatActivity {

    @Bind(R.id.imageView)
    ImageView imgView;
    private String TAG = "Splashscreen";
    private String gcmId ;
    private long splashDisplayTime = 1300;

//    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        // on inscrit SplashCreen pour recevoir les events de EventBus
        EventBus.getDefault().register(this);

        ButterKnife.bind(this);

        if(MyUtils.getBooleanSharedPref(Splashscreen.this, MyUtils.SHARED_PREF_IS_USER_REGISTERED)){
            // si user deja enregistré alors continuer
            continueAppLoading();
        }else{
            // Inscription au GCM si pas encore fait
            registerPhone();
        }


    }

    /**
     * Méthode pour continuer le chargement de l'app
     */
    private void continueAppLoading() {
        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splashscreen.this, MainActivity.class);
                finish();
                startActivity(intent);

            }
        }, splashDisplayTime);
    }

    @Override
    protected void onStop() {
        // on desinscrit SplashCreen pour recevoir les events de EventBus
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * Méthode pour lancer l'enrégistrement GCM
     */
    private void registerPhone(){
        /**
         * on lance l'enregistrement du téléphone
         * N'oubliez pas d'enregistrer une méthode pour écouter les evenements
         * (grace à l'anotation @Subscribe EventBus) PhoneRegisterEvent
          */

        Intent intent = new Intent(Splashscreen.this, RegistrationIntentService.class);
        startService(intent);
    }

    /**
     * Méthode gérant les event enclenchés lors de l'enregistrement GCM
     * @param event
     */
    @Subscribe
    public void onPhoneRegistered(PhoneRegisterEvent event){
        if(event.isRegistered()){
            // si enregistrement GCM réussie alors enrégistrement user sur le serveur
            Log.i(TAG, "GCM registered");
            continueAppLoading();
        }else{
            // si enregistrement échoue
            Log.i(TAG, "GCM failed");
            errorRegisteringUser();
        }
    }



    /**
     * Méthode permettant de notifier le user de l'échec du registration aupres du serveur INA
     * et lui permettre de réessayer
     */
    private void errorRegisteringUser(){
        Snackbar snackBar = Snackbar.make(imgView, R.string.erreur_serveur, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(R.string.reassayer, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPhone();
            }
        });
        snackBar.show();
    }


}
