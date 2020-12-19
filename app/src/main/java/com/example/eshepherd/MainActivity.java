package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchOvceDisplay(View view) {
        Intent intent = new Intent(this, OvceDisplayActivity.class);
        startActivity(intent);
    }
}