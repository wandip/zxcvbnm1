package com.dipak.calendardemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;

public class Upload extends AppCompatActivity {

    String messid,rice,roti,veg1,veg2,veg3,special,special2,other;
    String meal, dayname,getdate;
    EditText temp;
    ProgressDialog pDialog;
    String response;
    DatabaseHandler databaseHandler;
    private RadioButton mOneRadioButton;
    private RadioButton mTwoRadioButton;
    private RadioButton mThreeRadioButton;
    private RadioButton mFourRadioButton;
    private RadioButton mFiveRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload);

        Bundle bundle = getIntent().getExtras();
        messid = bundle.getString("messid");
        meal = bundle.getString("meal");
        dayname = bundle.getString("day");
        getdate =bundle.getString("date");

        setTitle(meal+", "+getdate+" : "+dayname);

        databaseHandler = new DatabaseHandler(this);

        Menu m = databaseHandler.getMenu(dayname.substring(0,3).toUpperCase(),meal);
        Log.i("MenuUpload",m.toString());

        if(m!=null)
        {

            temp = (EditText) findViewById(R.id.rice);
            if(!m.getRice().equals("null"))
            {
                temp.setText(m.getRice());
            }
            Log.i("MenuUploadRice",m.getRice());

            temp = (EditText) findViewById(R.id.roti);
            if(!m.getRice().equals("null"))
            {
                temp.setText(m.getRoti());
            }
            Log.i("MenuUploadRoti",m.getRoti());
            temp = (EditText) findViewById(R.id.vegie1);
            if(!m.getRice().equals("null"))
            {
                temp.setText(m.getVeg1());
            }
            Log.i("MenuUploadVeg1",m.getVeg1());
            temp = (EditText) findViewById(R.id.vegie2);
            if(!m.getRice().equals("null"))
            {
                temp.setText(m.getVeg2());
            }
            Log.i("MenuUploadVeg2",m.getVeg2());
            temp = (EditText) findViewById(R.id.vegie3);
            if(!m.getRice().equals("null"))
            {
                temp.setText(m.getVeg3());
            }
            Log.i("MenuUploadVeg3",m.getVeg3());
            temp = (EditText) findViewById(R.id.special);
            if(!m.getRice().equals("null"))
            {
                temp.setText(m.getSpecial());
            }
            Log.i("MenuUploadSpe",m.getSpecial());
            temp = (EditText) findViewById(R.id.special2);
            if(!m.getRice().equals("null"))
            {
                temp.setText(m.getSpecial2());
            }
            Log.i("MenuUploadSpe2",m.getSpecial2());
            temp = (EditText) findViewById(R.id.other);
            if(!m.getRice().equals("null"))
            {
                temp.setText(m.getOther());
            }
            Log.i("MenuUploadOthe",m.getOther());

        }

        RadioGroup rg = (RadioGroup) findViewById(R.id.radio_group);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {

                RadioButton b = (RadioButton) findViewById(checkedId);
                temp = (EditText) findViewById(R.id.roti);
                temp.setText(b.getText());
            }
        });




        mOneRadioButton = (RadioButton) findViewById(R.id.one_radio_btn);
        mTwoRadioButton = (RadioButton) findViewById(R.id.two_radio_btn);
        mThreeRadioButton = (RadioButton) findViewById(R.id.three_radio_btn);
        mFourRadioButton = (RadioButton) findViewById(R.id.four_radio_btn);
        mFiveRadioButton = (RadioButton) findViewById(R.id.two_radio_btn_1);

        final AutoCompleteTextView vegie1 =(AutoCompleteTextView) findViewById(R.id.vegie1);
        final AutoCompleteTextView vegie2 =(AutoCompleteTextView) findViewById(R.id.vegie2);
        final AutoCompleteTextView vegie3 =(AutoCompleteTextView) findViewById(R.id.vegie3);

        final AutoCompleteTextView spec =(AutoCompleteTextView) findViewById(R.id.special);
        final AutoCompleteTextView spec2 =(AutoCompleteTextView) findViewById(R.id.special2);


       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, databaseHandler.getAllVegies());


        textView.setAdapter(adapter);
        textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.showDropDown();
            }
        });*/



        Log.e("InUpload",databaseHandler.getAllSpecials().toString());
        Log.e("InUpload",databaseHandler.getAllSpecials().toString());



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, databaseHandler.getAllVegies());
        vegie1.setAdapter(adapter);
        vegie1.setThreshold(1);
        vegie1.setDropDownHeight(250);
        vegie1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                vegie1.showDropDown();
                return false;
            }
        });

        vegie2.setAdapter(adapter);
        vegie2.setThreshold(1);
        vegie2.setDropDownHeight(250);
        vegie2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                vegie2.showDropDown();
                return false;
            }
        });


        vegie3.setAdapter(adapter);
        vegie3.setThreshold(1);
        vegie3.setDropDownHeight(250);
        vegie3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                vegie3.showDropDown();
                return false;
            }
        });



        ArrayAdapter<String> adapter_spe = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, databaseHandler.getAllSpecials());
        spec.setAdapter(adapter_spe);
        spec.setThreshold(1);
        spec.setDropDownHeight(20);
        spec.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spec.showDropDown();
                return false;
            }
        });

        spec2.setAdapter(adapter_spe);
        spec2.setThreshold(1);
        spec2.setDropDownHeight(250);
        spec2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spec2.showDropDown();
                return false;
            }
        });


        mOneRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOneRadioButton.setChecked(true);
                mTwoRadioButton.setChecked(false);
                mThreeRadioButton.setChecked(false);
                mFourRadioButton.setChecked(false);
                mFiveRadioButton.setChecked(false);
                temp = (EditText) findViewById(R.id.rice);
                temp.setText(mOneRadioButton.getText());

            }
        });
        mTwoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOneRadioButton.setChecked(false);
                mTwoRadioButton.setChecked(true);
                mThreeRadioButton.setChecked(false);
                mFourRadioButton.setChecked(false);
                mFiveRadioButton.setChecked(false);
                temp = (EditText) findViewById(R.id.rice);
                temp.setText(mTwoRadioButton.getText());

            }
        });
        mThreeRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOneRadioButton.setChecked(false);
                mTwoRadioButton.setChecked(false);
                mThreeRadioButton.setChecked(true);
                mFourRadioButton.setChecked(false);
                mFiveRadioButton.setChecked(false);
                temp = (EditText) findViewById(R.id.rice);
                temp.setText(mThreeRadioButton.getText());
            }
        });
        mFourRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOneRadioButton.setChecked(false);
                mTwoRadioButton.setChecked(false);
                mThreeRadioButton.setChecked(false);
                mFourRadioButton.setChecked(true);
                mFiveRadioButton.setChecked(false);
                temp = (EditText) findViewById(R.id.rice);
                temp.setText(mFourRadioButton.getText());
            }
        });

        mFiveRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOneRadioButton.setChecked(false);
                mTwoRadioButton.setChecked(false);
                mThreeRadioButton.setChecked(false);
                mFourRadioButton.setChecked(false);
                mFiveRadioButton.setChecked(true);
                temp = (EditText) findViewById(R.id.rice);
                temp.setText(mFiveRadioButton.getText());
            }
        });





        //RadioGroup riceradio = (RadioGroup) findViewById(R.id.radGroup1);


/*        riceradio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {

                RadioButton b = (RadioButton) findViewById(checkedId);
                temp = (EditText) findViewById(R.id.rice);
                temp.setText(b.getText());
            }
        });
*/

        Button b1 = (Button) findViewById(R.id.uploadbtn);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                temp = (EditText) findViewById(R.id.rice);
                rice = temp.getText().toString();
                temp = (EditText) findViewById(R.id.roti);
                roti = temp.getText().toString();
                temp = (EditText) findViewById(R.id.vegie1);
                veg1 = temp.getText().toString();
                temp = (EditText) findViewById(R.id.vegie2);
                veg2 = temp.getText().toString();
                temp = (EditText) findViewById(R.id.vegie3);
                veg3 = temp.getText().toString();
                temp = (EditText) findViewById(R.id.special);
                special = temp.getText().toString();
                temp = (EditText) findViewById(R.id.special2);
                special2 = temp.getText().toString();
                temp = (EditText) findViewById(R.id.other);
                other = temp.getText().toString();

                if(veg1.length()>1)
                    databaseHandler.addVegie(veg1);
                if(veg2.length()>1)
                    databaseHandler.addVegie(veg2);
                if(veg3.length()>1)
                    databaseHandler.addVegie(veg3);

                if(special.length()>1)
                    databaseHandler.addSpecial(special);
                if(special2.length()>1)
                    databaseHandler.addSpecial(special2);



                AlertDialog.Builder a_b = new AlertDialog.Builder(Upload.this);
                a_b.setMessage("Upload Menu ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new PostMenu().execute();
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


    public class PostMenu extends AsyncTask<String , Void ,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Upload.this);
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
                URL url = new URL("https://wanidipak56.000webhostapp.com/postinsertmenu.php");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("messid", messid);
                jsonObject.put("rice", rice);
                jsonObject.put("roti", roti);
                jsonObject.put("vegieone", veg1);
                jsonObject.put("vegietwo", veg2);
                jsonObject.put("vegiethree", veg3);
                jsonObject.put("special", special);
                jsonObject.put("specialextra", special2);
                jsonObject.put("other", other);
                jsonObject.put("dayname", dayname);
                jsonObject.put("meal", meal);


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
            Toast.makeText(Upload.this,"Successful",Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Upload.this, MainActivity.class);
            startActivity(intent);

        }

    }
}


/*switch(checkedId)
                {
                    case R.id.radio1:
                        RadioButton b = (RadioButton) findViewById(checkedId);
                        break;
                    case R.id.radio2:
                        // TODO Something
                        break;
                    case R.id.radio3:
                        // TODO Something
                        break;
                }*/