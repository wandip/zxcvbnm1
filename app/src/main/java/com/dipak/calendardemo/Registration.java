package com.dipak.calendardemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
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

public class Registration extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    private EditText messName;
    private EditText ownerName;
    private EditText messAddress;
    private EditText contact;

    private Button submit;

    String MessName;
    String OwnerName;
    String MessAddress;
    String Contact;

    SharedPreferences prefs;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setTitle("Register");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        context=this;

        setupForm();
    }





    private void setupForm() {


        messName = (EditText) findViewById(R.id.editText5);
        ownerName = (EditText) findViewById(R.id.editText4);
        messAddress  = (EditText) findViewById(R.id.editText6);
        contact = (EditText) findViewById(R.id.editText16);

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) && source.charAt(i)!=' ') { // Accept only letter & digits ; otherwise just return
                        Toast.makeText(context,"Invalid Input",Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }

        };

        messName.setFilters(new InputFilter[] { filter });
        ownerName.setFilters(new InputFilter[] { filter });
        messAddress.setFilters(new InputFilter[] { filter });




        submit = (Button) findViewById(R.id.button2);


        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {





                MessName = messName.getText().toString();
                OwnerName = ownerName.getText().toString();
                MessAddress = messAddress.getText().toString();
                Contact = contact.getText().toString();

                if(valid()) {

                    if(inetcheck()) {

                        final AddMess messedup = new AddMess();
                        messedup.execute();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (messedup.getStatus() == AsyncTask.Status.RUNNING) {
                                    //messedup.cancel(true);
                                    //mProgressDialog.dismiss();
                                    Toast.makeText(Registration.this, "Slow Internet :/", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }, Integer.parseInt(context.getString(R.string.timeout)));
                    }
                    else
                    {
                        Toast.makeText(context, "No Internet!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }

    private boolean valid() {

        boolean isvalid = true;

        if(TextUtils.isEmpty(MessName))
        {
            messName.setError("Empty!");
            isvalid = false;
        }
        if(TextUtils.isEmpty(OwnerName))
        {
            ownerName.setError("Empty!");
            isvalid = false;

        }
        if(TextUtils.isEmpty(MessAddress))
        {
            messAddress.setError("Empty!");
            isvalid = false;
        }
        if(TextUtils.isEmpty(Contact))
        {
            isvalid = false;
            contact.setError("Empty!");
        }

        if(Contact.length()!=10)
        {
            contact.setError("10 digits required!");
            isvalid=false;
        }

        return isvalid;
    }

    private class AddMess extends AsyncTask<String , Void ,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(Registration.this);
            mProgressDialog.setMessage("Registering...");
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
                URL url = new URL("https://wanidipak56.000webhostapp.com/addEnquiry.php");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", MessName);
                jsonObject.put("contact", Contact);
                jsonObject.put("address", MessAddress);
                jsonObject.put("owner", OwnerName);

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


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                //clean up
                try {
                    if(os==null || is == null)
                        Toast.makeText(Registration.this, "Could not connect", Toast.LENGTH_SHORT).show();
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

            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("messname", MessName);
            editor.putString("ownername", OwnerName);
            editor.putString("address", MessAddress);
            editor.putString("contactnum", Contact);

            editor.commit();

            Intent intent = new Intent(Registration.this, Intro.class);
            startActivity(intent);

        }

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


}