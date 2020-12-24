package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ShowGonitevActivity extends AppCompatActivity {
    private int iskanaGonitev;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Gonitve";
    private RequestQueue requestQueue;
    private TextView datumGonitveTv,ovcaTv, ovenTv, predvidenaKotitevTv, opombeTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gonitev);
        //BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
        //        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(navView, navController);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.datumGonitveTv = findViewById(R.id.DatumGonitve);
        this.ovcaTv = findViewById(R.id.OvcaID);
        this.ovenTv = findViewById(R.id.OvenID);
        this.predvidenaKotitevTv = findViewById(R.id.PredvidenaKotitev);
        this.opombeTv = findViewById(R.id.Opombe);



        iskanaGonitev = 2; // treba prenest kotitev prek intentov!

        showGonitev(iskanaGonitev);
    }

    public void showGonitev(int iskanaGonitev) {
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

}