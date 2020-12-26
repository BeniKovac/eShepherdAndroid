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

public class ShowJagenjcekActivity extends AppCompatActivity {

    private Integer iskanJagenjcek, iskanaKotitev;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Jagenjcki";
    private String urlKotitve = "https://eshepherd-dev.azurewebsites.net/api/v1/Kotitve";
    private String mamaID, datumRojstva;
    private RequestQueue requestQueue;
    private TextView jagenjcekIDTv, datumRojstvaTv, mamaIDTv, spolTv;
    BottomNavigationView navigationView;
    Intent intent;
    boolean resume = false;
    static boolean addToUrl = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_jagenjcek);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.jagenjcekIDTv = findViewById(R.id.JagenjcekID);
        this.datumRojstvaTv = findViewById(R.id.DatumRojstva);
        this.spolTv = findViewById(R.id.Spol);
        this.mamaIDTv = findViewById(R.id.mamaID);

        intent = getIntent();
        if(intent != null) {
            iskanJagenjcek = intent.getIntExtra("ID", 0);
        }else{
            iskanJagenjcek = savedInstanceState.getInt("ID");
        }
        addToUrl = true;
        showJagenjcek(iskanJagenjcek, resume);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showJagenjcek(Integer iskanJagenjcek, boolean restart) {
        if (! restart)
            url += "/" + iskanJagenjcek; // sestavi pravi url
        JsonObjectRequest request = new JsonObjectRequest(url, null, jsonObjectListener, errorListener);
        requestQueue.add(request);
    }


    private Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            try {
                String jagenjcek = response.getString("idJagenjcka");
                iskanaKotitev = response.getInt("kotitevID");
                String spol = response.getString("spol");
                prikaziKotitev(iskanaKotitev);
                jagenjcekIDTv.setText(jagenjcek);
                spolTv.setText(spol);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void prikaziKotitev(int iskanaKotitev) {
        if (addToUrl) {
            urlKotitve += "/" + iskanaKotitev; // sestavi pravi url
            addToUrl = false;
        }
        JsonObjectRequest request = new JsonObjectRequest(urlKotitve, null, jsonObjectListenerKotitev, errorListener);
        requestQueue.add(request);
    }

    private Response.Listener<JSONObject> jsonObjectListenerKotitev = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            try {
                String datumRojstva = response.getString("datumKotitve").substring(0,10);
                String mamaID = response.getString("ovcaID");

                datumRojstvaTv.setText(datumRojstva);
                mamaIDTv.setText(mamaID);

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

    public void editJagenjcek(View view) {
        Intent intent = new Intent(this, EditJagenjcekActivity.class);
        intent.putExtra("ID", iskanJagenjcek);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ID", iskanJagenjcek);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume = true;
        showJagenjcek(iskanaKotitev, resume);
    }

}