package com.example.eshepherd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

public class ShowKotitevActivity extends AppCompatActivity {

    private int iskanaKotitev;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Kotitve";
    private RequestQueue requestQueue;
    private TextView datumKotitveTv, steviloMladihTv, ovcaTv, ovenTv, steviloMrtvihTv, opombeTv;
    BottomNavigationView navigationView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_kotitev);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);
        //BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
        //        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(navView, navController);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.datumKotitveTv = findViewById(R.id.DatumKotitve);
        this.steviloMladihTv = findViewById(R.id.SteviloMladih);
        this.ovcaTv = findViewById(R.id.OvcaID);
        this.ovenTv = findViewById(R.id.OvenID);
        this.steviloMrtvihTv = findViewById(R.id.SteviloMrtvih);
        this.opombeTv = findViewById(R.id.Opombe);

        intent = getIntent();
        if(intent != null) {
            iskanaKotitev = intent.getIntExtra("ID", 0); // treba prenest ovco prek intentov!
        }else{
            iskanaKotitev = savedInstanceState.getInt("ID");
        }
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.list_icon:
                        Intent intent = new Intent(ShowKotitevActivity.this, SpecificKotitevJagenjcki.class);
                        intent.putExtra("SpecificID",Integer.toString(iskanaKotitev));
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
        prikaziKotitev(iskanaKotitev);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void prikaziKotitev(int iskanaKotitev) {
        url += "/" + iskanaKotitev; // sestavi pravi url
        JsonObjectRequest request = new JsonObjectRequest(url, null, jsonObjectListener, errorListener);
        requestQueue.add(request);
    }


    private Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            try {
                String datumKotitve = response.getString("datumKotitve").substring(0,10);
                String ovca = response.getString("ovcaID");
                String oven = response.getString("ovenID");
                int steviloMladih = response.getInt("steviloMladih");
                int steviloMrtvih = response.getInt("steviloMrtvih");
                String opombe = response.getString("opombe");

                datumKotitveTv.setText(datumKotitve);
                steviloMladihTv.setText(String.valueOf(steviloMladih));
                ovcaTv.setText(ovca);
                ovenTv.setText(oven);
                steviloMrtvihTv.setText(String.valueOf(steviloMrtvih));
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

    public void editKotitev(View view) {
        Intent intent = new Intent(this, EditKotitevActivity.class);
        intent.putExtra("ID", iskanaKotitev);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ID", iskanaKotitev);
    }

}