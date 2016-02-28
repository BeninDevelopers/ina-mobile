package org.benindevelopers.ina;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.benindevelopers.power.PowerConnectionReceiver;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Shadaï ALI 28/02/2016
 */
public class SearchActivity extends AppCompatActivity {
    private PowerConnectionReceiver batteryReceiver;
    private MaterialDialog materialDialog;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // affichage du dialog pour demander l'état du courant
        dyPower();
    }

    private void dyPower(){
        //just for git
        materialDialog = new MaterialDialog(SearchActivity.this);
        materialDialog.setMessage("Avez vous le courant dans votre zone?");
        materialDialog.setPositiveButton("Oui", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.setNegativeButton("Non", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
materialDialog.setCanceledOnTouchOutside(false);
        materialDialog.show();
    }
}
