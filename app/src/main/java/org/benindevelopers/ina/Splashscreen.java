package org.benindevelopers.ina;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.benindevelopers.ina.utils.GCMRegisterEvent;
import org.benindevelopers.ina.utils.MyUtils;
import org.benindevelopers.ina.webservice.WebService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    private int splashDisplayTime = 1000 * 2;

//    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        // on inscrit SplashCreen pour recevoir les events de EventBus
        EventBus.getDefault().register(this);

        ButterKnife.bind(this);

        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(MyUtils.getBooleanSharedPref(Splashscreen.this, MyUtils.SHARED_PREF_IS_USER_REGISTERED)){
                    // si user deja enregistré alors continuer
                    continueAppLoading();
                }else{
                    // Inscription au GCM si pas encore fait
                    registerToGCM();
                }

            }
        }, splashDisplayTime);

    }

    /**
     * Méthode pour continuer le chargement de l'app
     */
    private void continueAppLoading() {
        Intent intent = new Intent(Splashscreen.this, MainActivity.class);
        finish();
        startActivity(intent);
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
    private void registerToGCM(){
        Intent intent = new Intent(Splashscreen.this, RegistrationIntentService.class);
        startService(intent);
    }

    /**
     * Méthode utilisée pour notifier le user de l'échec de l'enregistrement GCM
     * et lui permettre de réeassayer
     */
    private void gcmRegistrationFailed(){
        Snackbar snackBar = Snackbar.make(imgView, R.string.erreur_serveur, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(R.string.reassayer, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        snackBar.show();
    }

    /**
     * Méthode gérant les event enclenchés lors de l'enregistrement GCM
     * @param event
     */
    @Subscribe
    public void onGCMRegistered(GCMRegisterEvent event){
        if(event.isRegistered()){
            // si enregistrement GCM réussie alors enrégistrement user sur le serveur
            gcmId = event.getGcmId();
            Log.i(TAG, "GCM registered");
            registerUser();
        }else{
            // si enregistrement échoue
            Log.i(TAG, "GCM failed");
            gcmRegistrationFailed();
        }
    }

    /**
     * Méthode permettant d'enrégistrer le nouvel utilisateur aupres du serveur INA
     */
    private void registerUser(){
        // Enregistremrent du user
        Call<String> call = MyUtils.getInstance().getScalarWebServiceManager().enregistrerUtilisateur(
                MyUtils.getAccountEmail(this),
                MyUtils.getPhoneID(this),
                gcmId
        );

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String rep = response.body();
                Log.i(TAG, "REGISTER REP: "+rep);
                Log.i(TAG, "Email: "+MyUtils.getAccountEmail(Splashscreen.this));
                Log.i(TAG, "gcmId: "+gcmId);
                if (rep!=null && rep.equals(WebService.REP_OK)){
                    // si enrégistrement réussie
                    MyUtils.setBooleanSharedPref(Splashscreen.this, MyUtils.SHARED_PREF_IS_USER_REGISTERED, true);
                    continueAppLoading();
                }else{
                    errorRegisteringUser();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "Erreur "+t.getMessage());
                errorRegisteringUser();
            }
        });
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
                registerUser();
            }
        });
        snackBar.show();
    }


}
