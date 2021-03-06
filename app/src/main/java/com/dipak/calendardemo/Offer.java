package com.dipak.calendardemo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Offer extends AppCompatActivity {

    String messid,offerdate,offer,response;
    Button uploadbtn;
    Date newDate;
    String today;
    ProgressDialog pDialog;
    long oneday = 24*60*60*1000;
    long fpfhour = 5*60*60*1000+30*60*1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        Bundle bundle = getIntent().getExtras();
        messid = bundle.getString("messid");
        final long minDate = bundle.getLong("mindate");
        final long maxDate = bundle.getLong("maxdate");
        setTitle("Upload Offer");


        final EditText offerdesc = (EditText) findViewById(R.id.offerdescription);


        uploadbtn = (Button) findViewById(R.id.button3);

        final EditText edittext= (EditText) findViewById(R.id.Birthday);
        edittext.setKeyListener(null);

        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if(myCalendar.getTimeInMillis()<minDate || myCalendar.getTimeInMillis()>maxDate)
                {
                    Toast.makeText(Offer.this, "Cannot Select", Toast.LENGTH_SHORT).show();

                }
                else {
                    String myFormat = "dd MMMM yy"; //In which you need put here
                    String myFormatforDB = "dd/MM/yy"; //In which you need put here

                    SimpleDateFormat sdf = new SimpleDateFormat(myFormatforDB, Locale.US);
                    offerdate = sdf.format(myCalendar.getTime());
                    edittext.setText(new SimpleDateFormat(myFormat,Locale.US).format(myCalendar.getTime()));
                }
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(Offer.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.setTitle("Select Offer Date");
                dpd.getDatePicker().setMinDate(minDate);
                dpd.getDatePicker().setMaxDate(maxDate);
                dpd.show();
            }
        });

        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                offer = offerdesc.getText().toString();

                AlertDialog.Builder a_b = new AlertDialog.Builder(Offer.this);
                a_b.setMessage("Release Offer : "+offer)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new ReleaseOffer().execute();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = a_b.create();
                alertDialog.setTitle("Confirm");
                alertDialog.show();

            }
        });
    }

    public class ReleaseOffer extends AsyncTask<String , Void ,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Offer.this);
            pDialog.setMessage("Uploading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {

            OutputStream os = null;
            InputStream is = null;
            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://wanidipak56.000webhostapp.com/postOffer.php");
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("messid", messid);
                jsonObject.put("date", offerdate);
                jsonObject.put("offer", offer);

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
                is = conn.getInputStream();;

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    response = sb.toString();
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }


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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("Upload",response);
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Offer.this, MainActivity.class);
            startActivity(intent);

        }

    }
}
