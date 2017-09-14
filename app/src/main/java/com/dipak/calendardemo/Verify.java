package com.dipak.calendardemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Verify extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        Bundle bundle = getIntent().getExtras();
        final String getmessid = bundle.getString("messid");
        final String messid = getmessid.substring(0,4).toLowerCase();

        Button b = (Button) findViewById(R.id.verify_button);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ///Check for the entered input here
                Log.e("messid",messid);
                EditText getcode = (EditText) findViewById(R.id.code_text);

                final String code = getcode.getText().toString().toLowerCase();
                Log.e("code",code);

                if(code.equals(messid))
                {
                    Toast.makeText(Verify.this, "Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Verify.this, MainActivity.class);
                    intent.putExtra("messid",getmessid);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(Verify.this, "Enter valid Code!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
