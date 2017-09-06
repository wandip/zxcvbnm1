package com.dipak.calendardemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Upload extends AppCompatActivity {

    String messid,rice,roti,veg1,veg2,veg3,special,special2,other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //Take
        //MessId
        //Lunch/Dinner
        //Day
        //From intent

        messid = "Mess1";





        Button b1 = (Button) findViewById(R.id.uploadbtn);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText temp = (EditText) findViewById(R.id.rice);
                rice = temp.getText().toString();
                temp = (EditText) findViewById(R.id.roti);
                roti = temp.getText().toString();
                temp = (EditText) findViewById(R.id.vegie1);
                veg1 = temp.getText().toString();
                temp = (EditText) findViewById(R.id.vegie2);
                veg2 = temp.getText().toString();
                temp = (EditText) findViewById(R.id.vegie3);
                veg3 = temp.getText().toString();
                temp = (EditText) findViewById(R.id.speical1);
                special = temp.getText().toString();
                temp = (EditText) findViewById(R.id.special2);
                special2 = temp.getText().toString();
                temp = (EditText) findViewById(R.id.other);
                other = temp.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream os = null;
                        InputStream is = null;
                        HttpURLConnection conn = null;


                        Log.e("Dipak",rice);
                        Log.e("Dipak",roti);
                        Log.e("Dipak",veg1);
                        Log.e("Dipak",veg2);
                        Log.e("Dipak",special);
                        Log.e("Dipak",other);



                        try {
                            //constants
                            URL url = new URL("https://wanidipak56.000webhostapp.com/postinsertmenu.php");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("messid", messid);
                            jsonObject.put("rice", rice);
                            jsonObject.put("vegieone", veg1);
                            jsonObject.put("vegietwo", veg2);
                            jsonObject.put("vegiethree", veg3);
                            jsonObject.put("special", special);
                            jsonObject.put("specialextra", special2);
                            jsonObject.put("other", other);


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
            }
        });
    }
}
