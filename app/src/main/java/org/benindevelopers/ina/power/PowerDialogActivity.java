package org.benindevelopers.ina.power;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.benindevelopers.ina.R;
import org.benindevelopers.ina.utils.MyUtils;
import org.benindevelopers.ina.webservice.WebService;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PowerDialogActivity extends AppCompatActivity {

    private static final String TAG = "PowerDialogActivity";
    private AlertDialog materialDialog;
    private WebService retrofit;
    private double latitude;
    private double longitude;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_dialog);

        getSupportActionBar().hide();

        // affichage du dialog d'activation de la localisation si necessaire
        boolean isProviderEnabled = SmartLocation.with(this).location().state().locationServicesEnabled();
        if(isProviderEnabled){
            // si localisation actif
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
//        builder.setTitle(R.string.success);
            builder.setMessage(R.string.ask_energie_type)
                    .setPositiveButton(R.string.sbee, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // envoie
                            envoieEtatCourant(true);
                            materialDialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.sources_alternative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            envoieEtatCourant(false);
                            materialDialog.dismiss();
                            finish();
                        }
                    })
                    .setCancelable(false);
            materialDialog = builder.create();

            materialDialog.show();

            // initialisation du loading dialog
            loadingDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        }else {
            // sinon demander activation
            showSettingsAlert();
        }

    }


    /**
     * Methode pour afficher un dialog forcant l'utilisateur à activer la localisation
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        AlertDialog alertDialog = dialogBuilder.create();
        // Setting Dialog Title
        alertDialog.setTitle(R.string.gpsDisabled);
        // Setting Dialog Message
        alertDialog.setMessage(this.getText(R.string.askActivateGPS));
        // On pressing Settings button
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getText(R.string.menu_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }


    /**
     * Methode permettant d'envoyer l'état du courant au serveur
     * et permettant au user de continuer l'usage de l'app
     * @param siCourant
     */
    private void envoieEtatCourant(final boolean siCourant) {
        retrofit = MyUtils.getInstance().getScalarWebServiceManager();
        showLoadingSendingDialog();
        SmartLocation.with(PowerDialogActivity.this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {

                    @Override
                    public void onLocationUpdated(Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        // une fois la localisation réussie
                        // on stop la localisation
                        SmartLocation.with(PowerDialogActivity.this).location().stop();
                        // et on continue le processus
                        Call<String> call = retrofit.renseignerEtatCourant(
                                MyUtils.getPhoneID(PowerDialogActivity.this),
                                siCourant,
                                latitude,
                                longitude
                        );

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                String rep = response.body();
                                Log.i(TAG, "Call: "+latitude+" - "+longitude+" "+MyUtils.getPhoneID(PowerDialogActivity.this));
                                Log.i(TAG, "REP: "+rep);
                                finish();
                                hideLoadingDialog();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d(TAG, "ERREUR: "+t.getMessage());
                                finish();
                                hideLoadingDialog();
                            }
                        });

                    }
                });

    }

    /**
     * Affiche un Progressdialog
     */
    private void showLoadingSendingDialog() {
        loadingDialog.setCancelable(false);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage(getString(R.string.communication_serveur));
        loadingDialog.show();
    }

    /**
     * Cache le ProgressDialog
     */
    private void hideLoadingDialog(){
        loadingDialog.dismiss();
    }

}
