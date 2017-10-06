package com.dipak.calendardemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.messaging.FirebaseMessaging;

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
    int timecheck;
    String timecheckaa;


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

    long mind,maxd,nextmonth;
    String getmessid;
    ProgressDialog pDialog;
    Button LunchButton,DinnerButton,OfferButton;
    boolean status[][];
    SharedPreferences prefs;
    String messname;

    DatabaseHandler dbh;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* Bundle bundle = getIntent().getExtras();
        getmessid = bundle.getString("messid");
*/
        context=this;

        //getmessid = "Mess5";
        dbh = new DatabaseHandler(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        messname = prefs.getString("messname", "null");
        getmessid = prefs.getString("messid", "null");
       /* nbcollege = prefs.getString("nbcollege", "null");
        address = prefs.getString("address", "null");
        contact = prefs.getString("contactnum", "null");
        /*ownername = prefs.getString("ownername", "null");*/

        if(!prefs.getBoolean("firstTime", false)) {
            // run your one time code
            SharedPreferences.Editor editor = prefs.edit();
            Toast.makeText(this, "Welcome! "+prefs.getString("ownername","Owner"), Toast.LENGTH_SHORT).show();
            dbh.addFirst();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }


        FirebaseMessaging.getInstance().subscribeToTopic("owner_notif");

        if(prefs.getString("messname","null").equals("null")){
            Log.i("messn",messname);
            new GetMessName().execute("http://wanidipak56.000webhostapp.com/getMessname.php?messname="+getmessid);
        }
        else
        {
            Log.i("messn",messname);
            setTitle(messname);
        }

        if(inetcheck()) {
            final GetServerDate gsd = new GetServerDate();

            gsd.execute("http://wanidipak56.000webhostapp.com/try.php");

            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    if ( gsd.getStatus() == AsyncTask.Status.RUNNING )
                    {
                        //gsd.cancel(true);
                        //mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }, Integer.parseInt(context.getString(R.string.timeout)));
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

            final GetStatus getStatus = new GetStatus();
            getStatus.execute();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    if ( getStatus.getStatus() == AsyncTask.Status.RUNNING )
                    {
                        //getStatus.cancel(true);
                        //mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Slow Internet :/", Toast.LENGTH_SHORT).show();
                    }


                }
            }, Integer.parseInt(context.getString(R.string.timeout)));

        }
    }

    public class GetMessName extends AsyncTask<String , Void ,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    messname = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClientMessName", messname);
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

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("messname", messname);
            Log.i("gotmessn",messname);

            setTitle(messname);
            editor.commit();
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
            final GetWeekMenu getWeekMenu = new GetWeekMenu();
            getWeekMenu.execute();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    if ( getWeekMenu.getStatus() == AsyncTask.Status.RUNNING )
                    {
                        //getWeekMenu.cancel(true);

                        Toast.makeText(MainActivity.this, "Could not load Menu", Toast.LENGTH_SHORT).show();
                    }


                }
            }, Integer.parseInt(context.getString(R.string.timeout)));

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


        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        Date newDate = null;
        SimpleDateFormat sdfortime = new SimpleDateFormat("hhmm");
        SimpleDateFormat sdfortimeaa = new SimpleDateFormat("aa");

        try {
            if(inetcheck())
            {
                newDate = sdf.parse(server_response);
                timecheck = Integer.parseInt(sdfortime.format(newDate));
                timecheckaa = sdfortimeaa.format(newDate);
            }
            else
            {
                java.util.Calendar c = java.util.Calendar.getInstance();
                newDate = c.getTime();
            }

            Log.v("Setting Date", newDate.toString());
            Log.v("Time", String.valueOf(timecheck));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        mind=(newDate.getTime())-((newDate.getTime())%(oneday)) -fpfhour;
        maxd=(newDate.getTime()+6*oneday)-((newDate.getTime()+6*oneday)%(oneday))-fpfhour;
        nextmonth=(newDate.getTime()+29*oneday)-((newDate.getTime()+29*oneday)%(oneday))-fpfhour;
        final Date minDate = new Date(mind);
        final Date maxDate = new Date(maxd);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE-dd");
        String tod = simpleDateFormat.format(minDate);

        String todaytext = "Today : ";
        SpannableString str = new SpannableString(todaytext + tod);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, todaytext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        today.setText(str);


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
                    int daynum = dateClicked.getDay();

                    if(!(timecheck>=300 && timecheckaa.equals("pm") && dateClicked.equals(minDate))) {
                        LunchButton.setClickable(true);

                        if (status[daynum][0]) {
                            Menu m = dbh.getMenu(dayforDBH, "Lunch");

                            String boldText = "Lunch Set : ";
                            String normalText = m.toString();
                            SpannableString str = new SpannableString(boldText + normalText);
                            str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            lview.setText(str);
                        } else
                        {
                            lview.setText("Lunch Not Set");
                        }

                        LunchButton.setBackground(getResources().getDrawable(R.drawable.lun_din_bg));
                    }
                    else
                    {
                        LunchButton.setClickable(false);
                        DinnerButton.setClickable(false);
                        LunchButton.setBackground(getResources().getDrawable(R.drawable.button_grey));
                        DinnerButton.setBackground(getResources().getDrawable(R.drawable.button_grey));

                        lview.setText("Sorry not available");
                        dview.setText("Sorry not available");

                    }

                    if(!(timecheck>=1100 && timecheckaa.equals("pm") && dateClicked.equals(minDate))) {
                        DinnerButton.setClickable(true);
                        if (status[daynum][1]) {
                            Menu m = dbh.getMenu(dayforDBH, "Dinner");
                            String boldText = "Dinner Set : ";
                            String normalText = m.toString();
                            SpannableString str = new SpannableString(boldText + normalText);
                            str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            dview.setText(str);

                        } else
                            dview.setText("Dinner Not Set");
                        DinnerButton.setBackground(getResources().getDrawable(R.drawable.lun_din_bg));
                    }
                    else {
                        LunchButton.setClickable(false);
                        DinnerButton.setClickable(false);
                        LunchButton.setBackground(getResources().getDrawable(R.drawable.button_grey));
                        DinnerButton.setBackground(getResources().getDrawable(R.drawable.button_grey));

                        lview.setText("Sorry not available");
                        dview.setText("Sorry not available");

                    }
                }
                else
                {
                    LunchButton.setClickable(false);
                    DinnerButton.setClickable(false);
                    LunchButton.setBackground(getResources().getDrawable(R.drawable.button_grey));
                    DinnerButton.setBackground(getResources().getDrawable(R.drawable.button_grey));

                    lview.setText("Sorry not available");
                    dview.setText("Sorry not available");


                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Month.setText(dateFormatForMonth.format(firstDayOfNewMonth));
                mydate=null;
                dayOfTheWeek=null;
            }
        });





        LunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inetcheck()) {
                    Intent intent = new Intent(MainActivity.this, Upload.class);
                    if(dayOfTheWeek==null || mydate==null)
                    {
                        Toast.makeText(MainActivity.this, "Select Date", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        intent.putExtra("messid", getmessid);
                        intent.putExtra("meal", "Lunch");
                        intent.putExtra("day", dayOfTheWeek);
                        intent.putExtra("date", mydate);

                        startActivity(intent);
                    }

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
                    if(dayOfTheWeek==null || mydate==null)
                    {
                        Toast.makeText(MainActivity.this, "Select Date", Toast.LENGTH_SHORT).show();
                    }
                    else{
                    Intent intent = new Intent(MainActivity.this, Upload.class);
                    intent.putExtra("messid", getmessid);
                    intent.putExtra("meal", "Dinner");
                    intent.putExtra("day", dayOfTheWeek);
                    intent.putExtra("date", mydate);

                    startActivity(intent);
                }
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
                    intent.putExtra("mindate", mind);
                    intent.putExtra("maxdate", nextmonth);

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
            String mname = prefs.getString("messname","null");
            if(mname.equals("null"))
            {
                Toast.makeText(MainActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
            }
            else {
                jsonStr = sh.makeServiceCall("http://wanidipak56.000webhostapp.com/getMenu.php?messname="+mname.replace(" ","%20"));
            }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
