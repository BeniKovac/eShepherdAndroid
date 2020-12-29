package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinnerCheck extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner credaSpinner, mamaSpinner;
    private TextView prikaziCredo, prikaziMamo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner_check);
        prikaziCredo = (TextView) findViewById(R.id.showCreda);
        credaSpinner = (Spinner) findViewById(R.id.CredaID);
        ArrayList<String> credeList = new ArrayList<>();
        credeList.add("0");
        credeList.add("1");
        credeList.add("2");

        ArrayAdapter<String> adapterCreda = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, credeList);
        adapterCreda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        credaSpinner.setOnItemSelectedListener(this);
        credaSpinner.setAdapter(adapterCreda);


        prikaziMamo = (TextView) findViewById(R.id.showMama);
        mamaSpinner = (Spinner) findViewById(R.id.mamaID);
        ArrayList<String> mamaList = new ArrayList<>();
        mamaList.add("a");
        mamaList.add("b");
        mamaList.add("c");

        ArrayAdapter<String> adapterMama = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mamaList);
        adapterMama.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mamaSpinner.setOnItemSelectedListener(this);
        mamaSpinner.setAdapter(adapterMama);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //switch (view.getId()) {
            //case R.id.CredaID:
                prikaziCredo.setText(credaSpinner.getItemAtPosition(position).toString());
              //  break;
            //case R.id.mamaID:
                prikaziMamo.setText(mamaSpinner.getItemAtPosition(position).toString());
              //  break;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}