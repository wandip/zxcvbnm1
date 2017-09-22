package com.dipak.calendardemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.CollapsibleActionView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Registration extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    private EditText messName;
    private EditText ownerName;
    private EditText messAddress;
    private EditText nbCollege;
    /*private EditText guestCharge;
    private EditText monthlyCharge;*/
    private EditText contact;
    /*private EditText lunchOpen;
    private EditText lunchClose;
    private EditText dinnerOpen;
    private EditText dinnerClose;*/
    private Button submit;

    String MessName;
    String OwnerName;
    String MessAddress;
    String NbCollege;
    /*String GuestCharge;
    String MonthlyCharge;
    */String Contact;
    /*String LunchOpen;
    String LunchClose;
    String DinnerOpen;
    String DinnerClose;
    */String messid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        //Get Mess Id from Firebase
        messid = "Mess5";

        // Get Phone Number through intent/firebase
        String PhoneNum = "9876543210";



        messName = (EditText) findViewById(R.id.editText5);
        ownerName = (EditText) findViewById(R.id.editText4);
        messAddress  = (EditText) findViewById(R.id.editText6);
        nbCollege = (EditText) findViewById(R.id.editText8);
         contact = (EditText) findViewById(R.id.editText16);

        submit = (Button) findViewById(R.id.button2);

        contact.setText(PhoneNum);
        contact.setKeyListener(null);

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MessName = messName.getText().toString();
                OwnerName = ownerName.getText().toString();
                MessAddress= messAddress.getText().toString();
                NbCollege = nbCollege.getText().toString();
               /* GuestCharge = guestCharge.getText().toString();
                MonthlyCharge = monthlyCharge.getText().toString();*/
                Contact = contact.getText().toString();
                /*LunchOpen = lunchOpen.getText().toString();
                LunchClose = lunchClose.getText().toString();
                DinnerOpen = dinnerOpen.getText().toString();
                DinnerClose = dinnerClose.getText().toString();*/

                new AddMess().execute();

                /*Log.e("messname",MessName);
                Log.e("oname",OwnerName);
                Log.e("add",MessAddress);
                Log.e("coll", NbCollege);
                Log.e("gcharge",GuestCharge);
                Log.e("mcharge",MonthlyCharge);
                Log.e("cont",Contact);
                Log.e("Lopen",LunchOpen);
                Log.e("Lclos",LunchClose);
                Log.e("Dopen",DinnerOpen);
                Log.e("Dclos",DinnerClose);*/





               /* new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream os = null;
                        InputStream is = null;
                        HttpURLConnection conn = null;


                        try {
                            //constants
                            URL url = new URL("https://wanidipak56.000webhostapp.com/addMess.php");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("messid", messid);
                            jsonObject.put("name", MessName);
                            jsonObject.put("gcharge", GuestCharge);
                            jsonObject.put("lopen", LunchOpen);
                            jsonObject.put("lclose", LunchClose);
                            jsonObject.put("dopen", DinnerOpen);
                            jsonObject.put("dclose", DinnerClose);
                            jsonObject.put("mcharge", MonthlyCharge);
                            jsonObject.put("contact", Contact);
                            jsonObject.put("address", MessAddress);
                            jsonObject.put("nbcollege", NbCollege);
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

                            //String contentAsString = readIt(is,len);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            //clean up
                            try {
                                os.close();
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            conn.disconnect();
                        }
                    }
                }).start();


                Intent intent = new Intent(Registration.this, Verify.class);
                intent.putExtra("messid",messid);
                startActivity(intent);*/

            }
        });

    }

    class AddMess extends AsyncTask<String , Void ,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(Registration.this);
            mProgressDialog.setMessage("Registering...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
                jsonObject.put("name", MessName);
                /*jsonObject.put("gcharge", GuestCharge);
                jsonObject.put("lopen", LunchOpen);
                jsonObject.put("lclose", LunchClose);
                jsonObject.put("dopen", DinnerOpen);
                jsonObject.put("dclose", DinnerClose);
                jsonObject.put("mcharge", MonthlyCharge);*/
                jsonObject.put("contact", Contact);
                jsonObject.put("address", MessAddress);
                jsonObject.put("nbcollege", NbCollege);
                jsonObject.put("owner", OwnerName);



                String message = jsonObject.toString();

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout( 10000 );
                conn.setConnectTimeout( 15000 );
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(message.getBytes().length);

                publishProgress();

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
                    os.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                conn.disconnect();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            Intent intent = new Intent(Registration.this, Verify.class);
            intent.putExtra("messid",messid);
            startActivity(intent);

        }

    }

}
