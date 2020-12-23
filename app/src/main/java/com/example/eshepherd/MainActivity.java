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

    public void launchOvniDisplay(View view) {
        Intent intent = new Intent(this, OvniDisplayActivity.class);
        startActivity(intent);
    }

    public void launchKotitveDisplay(View view) {
        Intent intent = new Intent(this, KotitveDisplayActivity.class);
        startActivity(intent);
    }

    public void launchGonitveDisplay(View view) {
        Intent intent = new Intent(this, GonitveDisplayActivity.class);
        startActivity(intent);
    }

    public void launchCredeDisplay(View view) {
        Intent intent = new Intent(this, CredeDisplayActivity.class);
        startActivity(intent);
    }

    public void launchJagenjckiDisplay(View view) {
        Intent intent = new Intent(this, JagenjckiDisplayActivity.class);
        startActivity(intent);
    }
}