package org.benindevelopers.ina;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.benindevelopers.ina.fragments.SearchFragment;
import org.benindevelopers.ina.power.PowerConnectionReceiver;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener{


    private PowerConnectionReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // test of power
//        batteryReceiver = new PowerConnectionReceiver();
//        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        registerReceiver(batteryReceiver, filter);

        // affichage du fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new SearchFragment()).commit();

    }

}
