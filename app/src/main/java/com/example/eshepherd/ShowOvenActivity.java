package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_oven);
        //BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
        //        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(navView, navController);
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

        intent = getIntent();
        iskanOven = intent.getStringExtra("ID"); // treba prenest ovco prek intentov!

        showOven(iskanOven);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showOven(String iskanOven) {
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
                String pasma = response.getString("pasma");
                String mama = response.getString("mamaID");
                String oce = response.getString("oceID");
                int steviloSorojencev = response.getInt("steviloSorojencev");
                String stanje = response.getString("stanje");
                String opombe = response.getString("opombe");
                String poreklo = response.getString("poreklo");

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
}