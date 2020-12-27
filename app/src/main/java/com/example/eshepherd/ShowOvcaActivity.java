package com.example.eshepherd;

import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
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
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

public class ShowOvcaActivity extends AppCompatActivity {
    private String iskanaOvca;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovce";
    private RequestQueue requestQueue;
    private TextView ovcaIDTv, credaIDTv, datumRojstvaTv, pasmaTv, mamaIDtv,
            oceIDtv, steviloSorojencevTv, stanjeTv, opombeTv, steviloKotitevTv, povprecjeJagenjckovTv;
    BottomNavigationView navigationView;
    Intent intent;
    public static final int TEXT_REQUEST = 1;
    private static boolean resume = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ovca);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);

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

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.gonitev_icon:
                        Intent intent = new Intent(ShowOvcaActivity.this, SpecificOvcaGonitve.class);
                        intent.putExtra("SpecificID",iskanaOvca);
                        startActivity(intent);
                        break;
                    case R.id.kotitev_icon:
                        Intent intent2 = new Intent(ShowOvcaActivity.this, SpecificOvcaKotitve.class);
                        intent2.putExtra("SpecificID",iskanaOvca);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });

        intent = getIntent();
        if(intent != null) {
            iskanaOvca = intent.getStringExtra("ID"); // treba prenest ovco prek intentov!
        }else{
            iskanaOvca = savedInstanceState.getString("ID");
        }
        resume = false;
        showOvca(iskanaOvca);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showOvca(String iskanaOvca) {
        if (! resume)
            url += "/" + iskanaOvca; // sestavi pravi url
        JsonObjectRequest request = new JsonObjectRequest(url, null, jsonObjectListener, errorListener)
        {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ApiKey", "SecretKey");
                return params;
            }
        };
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

    public void editOvca(View view) {
        Intent intent = new Intent(this, EditOvcaActivity.class);
        intent.putExtra("ID", iskanaOvca);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ID", iskanaOvca);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume = true;
        showOvca(iskanaOvca);
    }


}