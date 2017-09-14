package com.dipak.calendardemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM - yyyy");
    public CompactCalendarView Calendar;
    TextView Month;
    String server_response;
    Event lunch_event,dinner_event;
    JSONObject jObj = null;
    String dayOfTheWeek,mydate;

    String json = "";
    long oneday = 24*60*60*1000;
    long fpfhour = 5*60*60*1000+30*60*1000;

    String getmessid;
    private ProgressDialog pDialog;
    Button LunchButton,DinnerButton,OfferButton;
    boolean status[][];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*Bundle bundle = getIntent().getExtras();
        getmessid = bundle.getString("messid");*/

        getmessid = "Mess5";

        final GetServerDate gsd = new GetServerDate();
        gsd.execute("http://wanidipak56.000webhostapp.com/try.php");


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
                try {
                    jObj = new JSONObject(json);
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
            setCalendar();

        }

    }

    private void setCalendar() {

        LunchButton = (Button) findViewById(R.id.button2);
        DinnerButton = (Button) findViewById(R.id.button);
        OfferButton = (Button) findViewById(R.id.button4);

        Calendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        LunchButton.setBackgroundColor(Color.LTGRAY);
        DinnerButton.setBackgroundColor(Color.LTGRAY);
        final TextView lview = (TextView) findViewById(R.id.textView2);
        final TextView dview = (TextView) findViewById(R.id.textView3);


        Calendar.setUseThreeLetterAbbreviation(true);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        Date newDate = null;
        try {
            newDate = sdf.parse(server_response);
            Log.v("Setting Date", newDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final Date minDate = new Date((newDate.getTime())-((newDate.getTime())%(oneday)) -fpfhour);
        final Date maxDate = new Date((newDate.getTime()+6*oneday)-((newDate.getTime()+6*oneday)%(oneday))-fpfhour);

        final int dayName = minDate.getDay();

        Log.e("minDate Day",String.valueOf(dayName));


        try {
            int success = jObj.getInt("success");
            status = new boolean[8][2];

            if (success == 1) {
                JSONArray mess = jObj.getJSONArray("messweek");

                JSONObject c = mess.getJSONObject(0);
                if(c.getString("lunsun").equals("1"))
                    status[0][0]=true;
                if(c.getString("lunmon").equals("1"))
                    status[1][0]=true;
                if(c.getString("luntue").equals("1"))
                    status[2][0]=true;
                if(c.getString("lunwed").equals("1"))
                    status[3][0]=true;
                if(c.getString("lunthu").equals("1"))
                    status[4][0]=true;
                if(c.getString("lunfri").equals("1"))
                    status[5][0]=true;
                if(c.getString("lunsat").equals("1"))
                    status[6][0]=true;

                if(c.getString("dinsun").equals("1"))
                    status[0][1]=true;
                if(c.getString("dinmon").equals("1"))
                    status[1][1]=true;
                if(c.getString("dintue").equals("1"))
                    status[2][1]=true;
                if(c.getString("dinwed").equals("1"))
                    status[3][1]=true;
                if(c.getString("dinthu").equals("1"))
                    status[4][1]=true;
                if(c.getString("dinfri").equals("1"))
                    status[5][1]=true;
                if(c.getString("dinsat").equals("1"))
                    status[6][1]=true;

            } else {
                Log.d("Dipak: ", "Not found! @ 245 in setCalendar");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        Month = (TextView) findViewById(R.id.textView);
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

                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                mydate = df.format(dateClicked);

                if((dateClicked.after(minDate) || dateClicked.equals(minDate)) && (dateClicked.before(maxDate)|| dateClicked.equals(maxDate)))
                {
                    LunchButton.setClickable(true);
                    DinnerButton.setClickable(true);

                    int daynum = dateClicked.getDay();
                    if(status[daynum][0])
                        lview.setText("Lunch Set");
                    else
                        lview.setText("Lunch Not Set");

                    if(status[daynum][1])
                        dview.setText("Dinner Set");
                    else
                        dview.setText("Dinner Not Set");


                    LunchButton.setBackgroundColor(Color.DKGRAY);
                    DinnerButton.setBackgroundColor(Color.DKGRAY);
                }
                else
                {
                    LunchButton.setClickable(false);
                    DinnerButton.setClickable(false);
                    LunchButton.setBackgroundColor(Color.LTGRAY);
                    DinnerButton.setBackgroundColor(Color.LTGRAY);

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
                Intent intent = new Intent(MainActivity.this, Upload.class);
                intent.putExtra("messid",getmessid);
                intent.putExtra("meal","Lunch");
                intent.putExtra("day",dayOfTheWeek);
                intent.putExtra("date",mydate);

                startActivity(intent);
            }
        });

        DinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Upload.class);
                intent.putExtra("messid",getmessid);
                intent.putExtra("meal","Dinner");
                intent.putExtra("day",dayOfTheWeek);
                intent.putExtra("date",mydate);

                startActivity(intent);
            }
        });

        OfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Offer.class);
                intent.putExtra("messid",getmessid);
                startActivity(intent);
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
}
