package com.dipak.calendardemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM, yyyy");


    CompactCalendarView Calendar;
    TextView Month;
    TextView lview,dview;
    String server_response;
    Event lunch_event,dinner_event;
    JSONObject jObj = null;
    String dayOfTheWeek,mydate,dayforDBH;
    TextView today;
    String json = "";
    long oneday = 24*60*60*1000;
    long fpfhour = 5*60*60*1000+30*60*1000;

    String getmessid;
    ProgressDialog pDialog;
    Button LunchButton,DinnerButton,OfferButton;
    boolean status[][];
    SharedPreferences prefs;

    DatabaseHandler dbh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Bundle bundle = getIntent().getExtras();
        getmessid = bundle.getString("messid");*/

        getmessid = "Mess5";
        dbh = new DatabaseHandler(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(!prefs.getBoolean("firstTime", false)) {
            // run your one time code
            SharedPreferences.Editor editor = prefs.edit();
            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
            dbh.addFirst();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }


        if(inetcheck()) {
            final GetServerDate gsd = new GetServerDate();
            gsd.execute("http://wanidipak56.000webhostapp.com/try.php");

        }
        else
        {
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            setCalendar();
        }

    }


    public class GetServerDate extends AsyncTask<String , Void ,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading Calendar...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", server_response);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            new GetStatus().execute();

        }
    }

    public class GetStatus extends AsyncTask<String , Void ,String> {

        @Override
        protected String doInBackground(String... strings) {

            OutputStream os = null;
            InputStream is = null;
            HttpURLConnection conn = null;
            try {
                //constants
                URL url = new URL("https://wanidipak56.000webhostapp.com/receivestatus.php");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("messid", getmessid);

                String message = jsonObject.toString();

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout( 10000 /*milliseconds*/ );
                conn.setConnectTimeout( 15000 /* milliseconds */ );
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

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    json = sb.toString();


                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

                // try parse the string to a JSON object

                    jObj = new JSONObject(json);

                    try {
                        int success = jObj.getInt("success");

                        if (success == 1) {
                            JSONArray mess = jObj.getJSONArray("messweek");

                            JSONObject c = mess.getJSONObject(0);

                            dbh.setFlagWeekMenu(c);


                            Log.i("setFlagMain",c.toString());

                        } else {
                            Log.d("Dipak: ", "Not found! @ 245 in setCalendar");
                        }

                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }
                //String contentAsString = readIt(is,len);
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

            pDialog.dismiss();
            new GetWeekMenu().execute();

            setCalendar();

        }

    }

    private void setCalendar() {

        today = (TextView) findViewById(R.id.textView7);
        lview = (TextView) findViewById(R.id.textView2);
        dview = (TextView) findViewById(R.id.textView3);

        LunchButton = (Button) findViewById(R.id.button2);
        DinnerButton = (Button) findViewById(R.id.button);
        OfferButton = (Button) findViewById(R.id.button4);

        Calendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        Calendar.setUseThreeLetterAbbreviation(true);


        LunchButton.setBackgroundColor(getResources().getColor(R.color.lightgrey));
        DinnerButton.setBackgroundColor(getResources().getColor(R.color.lightgrey));


        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        Date newDate = null;
        try {
            if(inetcheck())
            {
                newDate = sdf.parse(server_response);
            }
            else
            {
                java.util.Calendar c = java.util.Calendar.getInstance();
                newDate = c.getTime();
            }

            Log.v("Setting Date", newDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final Date minDate = new Date((newDate.getTime())-((newDate.getTime())%(oneday)) -fpfhour);
        final Date maxDate = new Date((newDate.getTime()+6*oneday)-((newDate.getTime()+6*oneday)%(oneday))-fpfhour);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE : dd/MMM");
        String tod = simpleDateFormat.format(minDate);
        today.setText(tod);


        final int dayName = minDate.getDay();

        Log.e("minDate Day",String.valueOf(dayName));

        status = dbh.getFlagWeekMenu();

        int startnumber = dayName;
        for (int i = 0; i < 7; i++)
        {
            long setDate = (newDate.getTime()+i*oneday)-((newDate.getTime()+i*oneday)%(oneday)-fpfhour);
            if(status[startnumber%7][0])
                lunch_event = new Event(Color.GREEN,setDate,"Lunch");
            else
                lunch_event = new Event(Color.LTGRAY,setDate,"NoLunch");

            if(status[startnumber%7][1])
                dinner_event = new Event(Color.GREEN,setDate,"Dinner");
            else
                dinner_event = new Event(Color.LTGRAY,setDate,"NoDinner");

            startnumber++;

            Calendar.addEvent(lunch_event,true);
            Calendar.addEvent(dinner_event,true);
        }


        Calendar.setVisibility(View.VISIBLE);

        Month = (TextView) findViewById(R.id.textview);
        Month.setText(dateFormatForMonth.format(Calendar.getFirstDayOfCurrentMonth()));
        Month.setVisibility(View.VISIBLE);

        Calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                Log.e("Date",dateClicked.toString());
                Log.e("Date",minDate.toString());
                Log.e("Date",maxDate.toString());

                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");

                dayOfTheWeek = sdf.format(dateClicked);

                SimpleDateFormat sdfweek = new SimpleDateFormat("EEE");

                dayforDBH = sdfweek.format(dateClicked).toUpperCase();
                Log.i("day for dbh",dayforDBH);

                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                mydate = df.format(dateClicked);

                if((dateClicked.after(minDate) || dateClicked.equals(minDate)) && (dateClicked.before(maxDate)|| dateClicked.equals(maxDate)))
                {
                    LunchButton.setClickable(true);
                    DinnerButton.setClickable(true);

                    int daynum = dateClicked.getDay();
                    if(status[daynum][0])
                    {
                        Menu m = dbh.getMenu(dayforDBH,"Lunch");
                        lview.setText("Lunch Set : "+m.toString());
                    }
                    else
                        lview.setText("Lunch Not Set");

                    if(status[daynum][1])
                    {
                        Menu m = dbh.getMenu(dayforDBH,"Dinner");
                        dview.setText("Dinner Set : "+m.toString());

                    }
                    else
                        dview.setText("Dinner Not Set");


                    LunchButton.setBackgroundColor(Color.DKGRAY);
                    DinnerButton.setBackgroundColor(Color.DKGRAY);
                }
                else
                {
                    LunchButton.setClickable(false);
                    DinnerButton.setClickable(false);
                    LunchButton.setBackgroundColor(getResources().getColor(R.color.lightgrey));
                    DinnerButton.setBackgroundColor(getResources().getColor(R.color.lightgrey));

                    lview.setText("Sorry not available");
                    dview.setText("Sorry not available");


                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Month.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });




        LunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inetcheck()) {
                    Intent intent = new Intent(MainActivity.this, Upload.class);
                    intent.putExtra("messid", getmessid);
                    intent.putExtra("meal", "Lunch");
                    intent.putExtra("day", dayOfTheWeek);
                    intent.putExtra("date", mydate);

                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Connect to Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        DinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inetcheck()) {
                    Intent intent = new Intent(MainActivity.this, Upload.class);
                    intent.putExtra("messid", getmessid);
                    intent.putExtra("meal", "Dinner");
                    intent.putExtra("day", dayOfTheWeek);
                    intent.putExtra("date", mydate);

                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Connect to Internet", Toast.LENGTH_SHORT).show();

                }
            }
        });

        OfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inetcheck()) {
                    Intent intent = new Intent(MainActivity.this, Offer.class);
                    intent.putExtra("messid", getmessid);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Connect to Internet", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    class GetWeekMenu extends AsyncTask<Void, Void, Void> {

        private String TAG = MainActivity.class.getSimpleName();
        String jsonStr;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpHandler sh = new HttpHandler();
            jsonStr = sh.makeServiceCall("http://wanidipak56.000webhostapp.com/getMenu.php?messname=Anand%20Food%20Xprs");

            Log.e(TAG, "Response from url: " + jsonStr);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            setWeekMenuinSQLITE(jsonStr);
        }
    }

    private void setWeekMenuinSQLITE(String jsonStr) {
        dbh.setWeekMenu(jsonStr);
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
