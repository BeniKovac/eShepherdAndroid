package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class ShowGonitevActivity extends AppCompatActivity {
    private int iskanaGonitev;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Gonitve";
    private RequestQueue requestQueue;
    private TextView datumGonitveTv,ovcaTv, ovenTv, predvidenaKotitevTv, opombeTv;
    BottomNavigationView navigationView;
    Intent intent;
    private boolean resume = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gonitev);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.datumGonitveTv = findViewById(R.id.DatumGonitve);
        this.ovcaTv = findViewById(R.id.OvcaID);
        this.ovenTv = findViewById(R.id.OvenID);
        this.predvidenaKotitevTv = findViewById(R.id.PredvidenaKotitev);
        this.opombeTv = findViewById(R.id.Opombe);

        intent = getIntent();
        if(intent != null) {
            iskanaGonitev = intent.getIntExtra("ID", 0); // treba prenest ovco prek intentov!
        }else{
            iskanaGonitev = savedInstanceState.getInt("ID");
        }
        showGonitev(iskanaGonitev, resume);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showGonitev(int iskanaGonitev, boolean resume) {
        if (! resume)
            url += "/" + iskanaGonitev; // sestavi pravi url
        JsonObjectRequest request = new JsonObjectRequest(url, null, jsonObjectListener, errorListener);
        requestQueue.add(request);
    }


    private Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            try {
                String datumGonitve = response.getString("datumGonitve").substring(0,10);
                String predvidenaKotitev = response.getString("predvidenaKotitev").substring(0,10);
                String ovca = response.getString("ovcaID");
                String oven = response.getString("ovenID");
                String opombe = response.getString("opombe");
                if (opombe.equals("null"))
                    opombe = "";
                datumGonitveTv.setText(datumGonitve);
                predvidenaKotitevTv.setText(predvidenaKotitev);
                ovcaTv.setText(ovca);
                ovenTv.setText(oven);
                opombeTv.setText(opombe);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

    public void editGonitev(View view) {
        Intent intent = new Intent(this, EditGonitevActivity.class);
        intent.putExtra("ID", iskanaGonitev);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ID", iskanaGonitev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume = true;
        showGonitev(iskanaGonitev, resume);
    }

}