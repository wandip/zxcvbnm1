package com.dipak.calendardemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

public class Intro extends AppCompatActivity {

    SharedPreferences prefs;
    String messid,messname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        messid = prefs.getString("messid", "null");
        messname = prefs.getString("messname", "null");

        if(!prefs.getBoolean("firstTime", false)) {
            ////has not entered Main Activity yet
        }
        else
        {
            ///has entered MainActivity
            Intent intent = new Intent(Intro.this,EmailPasswordActivity.class);
            startActivity(intent);
        }


        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inetcheck()) {
                    Intent intent = new Intent(Intro.this, Registration.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(Intro.this, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inetcheck()) {
                    Intent intent = new Intent(Intro.this, EmailPasswordActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(Intro.this, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }



    boolean inetcheck()
    {
        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        return connected;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

}
