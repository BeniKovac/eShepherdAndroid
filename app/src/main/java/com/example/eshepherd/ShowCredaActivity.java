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

public class ShowCredaActivity extends AppCompatActivity {
    private String iskanaCreda;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Crede";
    private RequestQueue requestQueue;
    private TextView credaTv, opombeTv;
    BottomNavigationView navigationView;
    Intent intent;
    private boolean resume = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_creda);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.credaTv = findViewById(R.id.CredaID);
        this.opombeTv = findViewById(R.id.Opombe);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.crede_icon:
                        Intent intent2 = new Intent(ShowCredaActivity.this, SpecificCredaOvce.class);
                        intent2.putExtra("SpecificID",iskanaCreda);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });

        intent = getIntent();
        if(intent != null) {
            iskanaCreda = intent.getStringExtra("ID"); // treba prenest ovco prek intentov!
        }else{
            iskanaCreda = savedInstanceState.getString("ID");
        }
        showCreda(iskanaCreda, resume);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showCreda(String iskanaCreda, boolean resume) {
        if (! resume)
            url += "/" + iskanaCreda; // sestavi pravi url
        JsonObjectRequest request = new JsonObjectRequest(url, null, jsonObjectListener, errorListener);
        requestQueue.add(request);
    }


    private Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            try {
                String creda = response.getString("credeID");
                String opombe = response.getString("opombe");
                if (opombe.equals("null"))
                    opombe = "";
                credaTv.setText(creda);
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

    public void editCreda(View view) {
        Intent intent = new Intent(this, EditCredaActivity.class);
        intent.putExtra("ID", iskanaCreda);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ID", iskanaCreda);
    }
    @Override
    protected void onResume() {
        super.onResume();
        resume = true;
        showCreda(iskanaCreda, resume);
    }
}