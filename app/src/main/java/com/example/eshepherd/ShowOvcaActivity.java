package com.example.eshepherd;

import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class ShowOvcaActivity extends AppCompatActivity {
    private String iskanaOvca;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovce";
    private RequestQueue requestQueue;
    private TextView ovcaIDTv, credaIDTv, datumRojstvaTv, pasmaTv, mamaIDtv,
            oceIDtv, steviloSorojencevTv, stanjeTv, opombeTv, steviloKotitevTv, povprecjeJagenjckovTv;
    BottomNavigationView navigationView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ovca);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);
        //BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

         */
        requestQueue = Volley.newRequestQueue(getApplicationContext());

            this.ovcaIDTv = findViewById(R.id.OvcaID);
        this.credaIDTv = findViewById(R.id.CredaID);
        this.datumRojstvaTv = findViewById(R.id.DatumRojstva);

        this.pasmaTv = findViewById(R.id.Pasma);
        this.mamaIDtv = findViewById(R.id.mamaID);
        this.oceIDtv = findViewById(R.id.oceID);
        this.steviloSorojencevTv = findViewById(R.id.SteviloSorojencev);
        this.opombeTv = findViewById(R.id.Opombe);
        this.stanjeTv = findViewById(R.id.Stanje);
        this.steviloKotitevTv = findViewById(R.id.SteviloKotitev);
        this.povprecjeJagenjckovTv = findViewById(R.id.PovprecjeJagenjckov);

        intent = getIntent();
        iskanaOvca = intent.getStringExtra("ID"); // treba prenest ovco prek intentov!

        prikaziOvco(iskanaOvca);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void prikaziOvco(String iskanaOvca) {
        url += "/" + iskanaOvca; // sestavi pravi url
        JsonObjectRequest request = new JsonObjectRequest(url, null, jsonObjectListener, errorListener);
        requestQueue.add(request);
    }


    private Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                String ovca = response.getString("ovcaID");
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
                int steviloKotitev = response.getInt("steviloKotitev");
                int povprecjeJagenjckov = response.getInt("povprecjeJagenjckov");

                ovcaIDTv.setText(ovca);
                credaIDTv.setText(creda);
                datumRojstvaTv.setText(datumRojstva);
                pasmaTv.setText(pasma);
                mamaIDtv.setText(mama);
                oceIDtv.setText(oce);
                steviloSorojencevTv.setText(String.valueOf(steviloSorojencev));
                stanjeTv.setText(stanje);
                opombeTv.setText(opombe);
                steviloKotitevTv.setText(String.valueOf(steviloKotitev));
                povprecjeJagenjckovTv.setText(String.valueOf(povprecjeJagenjckov));


                } catch (JSONException e) {
                    e.printStackTrace();
                    return;

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