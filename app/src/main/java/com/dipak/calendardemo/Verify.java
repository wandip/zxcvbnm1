package com.dipak.calendardemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Verify extends AppCompatActivity {

    SharedPreferences prefs;
    ProgressDialog mProgressDialog;
private Context context;
    String messid,ownername,nbcollege,contact,address,messname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        context = this;
        Bundle bundle = getIntent().getExtras();
        messid = bundle.getString("messid");

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        messname = prefs.getString("messname", "null");
        nbcollege = prefs.getString("nbcollege", "null");
        address = prefs.getString("address", "null");
        contact = prefs.getString("contactnum", "null");
        ownername = prefs.getString("ownername", "null");


        if(messname.equals("null"))
        {
            Toast.makeText(this, "Sorry, Some Error Occured", Toast.LENGTH_SHORT).show();
        }

        final AddMess messedup = new AddMess();
        messedup.execute();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run() {
                if ( messedup.getStatus() == AsyncTask.Status.RUNNING )
                {
                    //messedup.cancel(true);
                    //mProgressDialog.dismiss();
                    Toast.makeText(Verify.this, "Slow Internet :(", Toast.LENGTH_SHORT).show();
                }


            }
        }, Integer.parseInt(context.getString(R.string.timeout)));



      /*  Button b = (Button) findViewById(R.id.verify_button);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*/


    }

    class AddMess extends AsyncTask<String , Void ,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(Verify.this);
            mProgressDialog.setMessage("Verifying...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }
        @Override
        protected String doInBackground(String... strings) {

            OutputStream os = null;
            InputStream is = null;
            HttpURLConnection conn = null;

            try
            {
                //constants
                URL url = new URL("https://wanidipak56.000webhostapp.com/addMess.php");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("messid", messid);
                jsonObject.put("name", messname);
                /*jsonObject.put("gcharge", GuestCharge);
                jsonObject.put("lopen", LunchOpen);
                jsonObject.put("lclose", LunchClose);
                jsonObject.put("dopen", DinnerOpen);
                jsonObject.put("dclose", DinnerClose);
                jsonObject.put("mcharge", MonthlyCharge);*/
                jsonObject.put("contact", contact);
                jsonObject.put("address", address);
                jsonObject.put("nbcollege", nbcollege);
                jsonObject.put("owner", ownername);



                String message = jsonObject.toString();

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout( 10000 );
                conn.setConnectTimeout( 15000 );
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(message.getBytes().length);


                //make some HTTP header nicety
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                //open
                conn.connect();

                //setup send
                os = new BufferedOutputStream(conn.getOutputStream());
                os.write(message.getBytes());
                //clean up
                os.flush();


                //do somehting with response
                is = conn.getInputStream();
                Log.d("Dipak :",is.toString());


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }  finally {
                //clean up
                try {
                    if(os==null || is == null)
                        Toast.makeText(Verify.this, "Could not connect", Toast.LENGTH_SHORT).show();
                    else{
                        is.close();
                        os.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                conn.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();

            Intent intent = new Intent(Verify.this, MainActivity.class);
            intent.putExtra("messid",messid);
            startActivity(intent);
        }

    }
}
