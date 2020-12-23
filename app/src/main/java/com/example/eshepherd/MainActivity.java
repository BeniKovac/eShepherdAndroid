package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.eshepherd.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchAddingActivity(View view){
        Intent intent = new Intent(MainActivity.this, AddingActivity.class);
        startActivity(intent);
    }

    public void launchEntityDisplaysActivity(View view) {
        Intent intent = new Intent(MainActivity.this,EntityDisplaysActivity.class);
        startActivity(intent);
    }

    public void addOvcaActivity(View view) {
        Intent intent = new Intent(this, AddOvcaActivity.class);
        String message = "Dodaj ovco v seznam";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
    public void addOvenActivity(View view) {
        Intent intent = new Intent(this, AddOvenActivity.class);
        String message = "Dodaj ovna v seznam";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void addKotitevActivity(View view) {
        Intent intent = new Intent(this, AddKotitevActivity.class);
        String message = "Dodaj kotitev v seznam";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void addGonitevActivity(View view) {
        Intent intent = new Intent(this, AddGonitevActivity.class);
        String message = "Dodaj gonitev v seznam";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void addCredaActivity(View view) {
        Intent intent = new Intent(this, AddCredaActivity.class);
        String message = "Dodaj čredo v seznam";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void addJagenjcekActivity(View view) {
        Intent intent = new Intent(this, AddJagenjcekActivity.class);
        String message = "Dodaj v jagenjčka seznam";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}