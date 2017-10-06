package com.dipak.calendardemo;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import static com.dipak.calendardemo.R.drawable.calendar;

public class ExtraDetails extends AppCompatActivity {

    private AutoCompleteTextView nbCollege;
    private EditText guestCharge;
    private EditText monthlyCharge;
    private EditText lunchOpen;
    private EditText lunchClose;
    private EditText dinnerOpen;
    private EditText dinnerClose;


    String messid;
    String GuestCharge;
    String MonthlyCharge;
    String LunchOpen;
    String LunchClose;
    String DinnerOpen;
    String DinnerClose;
    String NbCollege;

    String ownername,contact,address,messname;


    SharedPreferences prefs;
    private Context context;
    ArrayList<String> college_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_details);

        setTitle("Just One Step");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        context = this;

        messid = prefs.getString("messid", "null");
        messname = prefs.getString("messname", "null");
        address = prefs.getString("address", "null");
        contact = prefs.getString("contactnum", "null");
        ownername = prefs.getString("ownername", "null");


        if(messname.equals("null") || messid.equals("null"))
        {
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(ExtraDetails.this,Intro.class);
            //startActivity(intent);
        }
        else
        {
            new GetNBCollege().execute();
        }
        new GetNBCollege().execute();

    }

    class GetNBCollege extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            super.onPreExecute();

            college_list = new ArrayList<>();
            college_list.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall("http://wanidipak56.000webhostapp.com/getNBCollege.php");

            Log.e("IN Registration", "Response from url: " + jsonStr);
            try {
                if (jsonStr != null) {

                    JSONObject Colleges = new JSONObject(jsonStr);

                    JSONArray colleges = Colleges.getJSONArray("NBCollege");

                    for(int i=0;i<colleges.length();i++)
                    {
                        Log.e("college "+i, colleges.getString(i));
                        college_list.add(colleges.getString(i));
                    }

                    Log.e("CollegeReceived",college_list.toString());

                } else {
//                Toast.makeText(mcontext, "Oops,Error Updating Mess Menus", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            setupForm();
        }
    }

    private void setupForm() {
        nbCollege = (AutoCompleteTextView) findViewById(R.id.editText8);
        guestCharge = (EditText) findViewById(R.id.editText10);
        monthlyCharge = (EditText) findViewById(R.id.editText9);
        lunchOpen = (EditText) findViewById(R.id.editText12);
        lunchClose = (EditText) findViewById(R.id.editText11);


        dinnerClose = (EditText) findViewById(R.id.editText13);
        dinnerOpen = (EditText) findViewById(R.id.editText14);

        lunchOpen.setKeyListener(null);
        lunchClose.setKeyListener(null);
        dinnerOpen.setKeyListener(null);
        dinnerClose.setKeyListener(null);



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, college_list);
        nbCollege.setAdapter(adapter);
        nbCollege.setThreshold(1);
        nbCollege.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                nbCollege.showDropDown();
                return false;
            }
        });


        lunchOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ExtraDetails.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        lunchOpen.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");

                mTimePicker.show();

            }
        });


        lunchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ExtraDetails.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        lunchClose.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        dinnerOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ExtraDetails.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        dinnerOpen.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        dinnerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ExtraDetails.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        dinnerClose.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        Button done = (Button) findViewById(R.id.button7);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NbCollege = nbCollege.getText().toString();
                GuestCharge = guestCharge.getText().toString();
                MonthlyCharge = monthlyCharge.getText().toString();
                LunchOpen = lunchOpen.getText().toString();
                LunchClose = lunchClose.getText().toString();
                DinnerOpen = dinnerOpen.getText().toString();
                DinnerClose = dinnerClose.getText().toString();

                int t1,t2,t3,t4;
                t1 = Integer.parseInt(LunchOpen.replace(":",""));
                t2 = Integer.parseInt(LunchClose.replace(":",""));
                t3 = Integer.parseInt(DinnerOpen.replace(":",""));
                t4 = Integer.parseInt(DinnerClose.replace(":",""));

                Log.e("Extr1",String.valueOf(t1));
                Log.e("Extr2",String.valueOf(t2));
                Log.e("Extr3",String.valueOf(t3));
                Log.e("Extr4",String.valueOf(t4));

                if(t1<t2 && t2<t3 && t3<t4)
                {
                    final Edetails messedup = new Edetails();
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
                                Toast.makeText(ExtraDetails.this, "No Internet", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }, Integer.parseInt(context.getString(R.string.timeout)));
                }
                else
                {
                    Toast.makeText(context, "Wrong Time", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    class Edetails extends AsyncTask<String , Void ,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
                jsonObject.put("gcharge", GuestCharge);
                jsonObject.put("lopen", LunchOpen);
                jsonObject.put("lclose", LunchClose);
                jsonObject.put("dopen", DinnerOpen);
                jsonObject.put("dclose", DinnerClose);
                jsonObject.put("mcharge", MonthlyCharge);
                jsonObject.put("contact", contact);
                jsonObject.put("address", address);
                jsonObject.put("nbcollege", NbCollege);
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
                        Toast.makeText(ExtraDetails.this, "Could not connect", Toast.LENGTH_SHORT).show();
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

            Intent intent = new Intent(ExtraDetails.this, Verify.class);
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
