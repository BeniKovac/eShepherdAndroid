package com.example.eshepherd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowOvenActivity extends AppCompatActivity {

    private String iskanOven;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovni";
    private RequestQueue requestQueue;
    private TextView ovenIDTv, credaIDTv, datumRojstvaTv, pasmaTv, mamaIDtv,
            oceIDtv, steviloSorojencevTv, stanjeTv, opombeTv, porekloTv;
    BottomNavigationView navigationView;
    Intent intent;
    private boolean resume = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_oven);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.ovenIDTv = findViewById(R.id.OvenID);
        this.credaIDTv = findViewById(R.id.CredaID);
        this.datumRojstvaTv = findViewById(R.id.DatumRojstva);
        this.pasmaTv = findViewById(R.id.Pasma);
        this.mamaIDtv = findViewById(R.id.mamaID);
        this.oceIDtv = findViewById(R.id.oceID);
        this.steviloSorojencevTv = findViewById(R.id.SteviloSorojencev);
        this.opombeTv = findViewById(R.id.Opombe);
        this.stanjeTv = findViewById(R.id.Stanje);
        this.porekloTv = findViewById(R.id.Poreklo);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.gonitev_icon:
                        Intent intent2 = new Intent(ShowOvenActivity.this, SpecificOvenGonitve.class);
                        intent2.putExtra("SpecificID",iskanOven);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });

        intent = getIntent();
        int test = 1;
        if(intent != null) {
            iskanOven = intent.getStringExtra("ID");
        }else{
            iskanOven = savedInstanceState.getString("ID");
        }
        showOven(iskanOven, resume);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showOven(String iskanOven, boolean resume) {
        if (! resume)
            url += "/" + iskanOven; // sestavi pravi url
        JsonObjectRequest request = new JsonObjectRequest(url, null, jsonObjectListener, errorListener);
        requestQueue.add(request);
    }


    private Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            try {
                String oven = response.getString("ovenID");
                String creda = response.getString("credaID");
                String datumRojstva = response.getString("datumRojstva");
                if(!datumRojstva.equals("null"))
                    datumRojstva = datumRojstva.substring(0,10);
                else
                    datumRojstva = "/";
                String pasma = response.getString("pasma");
                if (pasma.equals("null"))
                    pasma = "";
                String mama = response.getString("mamaID");
                if (mama.equals("null"))
                    mama = "";
                String oce = response.getString("oceID");
                if (oce.equals("null"))
                    oce = "";
                int steviloSorojencev = response.getInt("steviloSorojencev");
                String stanje = response.getString("stanje");
                if (stanje.equals("null"))
                    stanje = "";
                String opombe = response.getString("opombe");
                if (opombe.equals("null"))
                    opombe = "";
                String poreklo = response.getString("poreklo");
                if (poreklo.equals("null"))
                    poreklo = "";

                ovenIDTv.setText(oven);
                credaIDTv.setText(String.valueOf(creda));
                datumRojstvaTv.setText(datumRojstva);
                pasmaTv.setText(pasma);
                mamaIDtv.setText(mama);
                oceIDtv.setText(oce);
                steviloSorojencevTv.setText(String.valueOf(steviloSorojencev));
                stanjeTv.setText(stanje);
                opombeTv.setText(opombe);
                porekloTv.setText(poreklo);


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

    public void editOven(View view) {
        Intent intent = new Intent(this, EditOvenActivity.class);
        intent.putExtra("ID", iskanOven);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ID", iskanOven);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume = true;
        showOven(iskanOven, resume);
    }
}