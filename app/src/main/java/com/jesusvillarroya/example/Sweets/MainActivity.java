package com.jesusvillarroya.example.Sweets;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
 public static final String EXTRA_MESSAGE="com.jesus.example.sesion3_7";
    EditText et1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1 = (EditText)findViewById(R.id.et1);
    }

    public void logging(View view)
    {
        Intent i = new Intent(this,MapsActivity.class);
        i.putExtra(EXTRA_MESSAGE, et1.getText().toString());
        startActivity(i);

    }

}
