package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class EditKotitevActivity extends AppCompatActivity {
    private int kateraKotitev;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Kotitve";
    private RequestQueue requestQueue;
    private EditText datumKotitveTe, steviloMrtvihTe, opombeTe, ovcaTe, ovenTe;
    private TextView statusKotitev, steviloMladihTv; // za status - dodajam
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kotitev);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        statusKotitev = (TextView) findViewById(R.id.statusGonitev);

        this.datumKotitveTe = findViewById(R.id.DatumKotitve);
        this.steviloMrtvihTe = findViewById(R.id.SteviloMrtvih);
        this.opombeTe = findViewById(R.id.Opombe);
        this.ovcaTe = findViewById(R.id.OvcaID);
        this.ovenTe = findViewById(R.id.OvenID);
        this.steviloMladihTv = findViewById(R.id.SteviloMladih);

        intent = getIntent();
        kateraKotitev = intent.getIntExtra("ID", 1);

        prikaziKotitev(kateraKotitev);
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
                if (opombe.equals("null"))
                    opombe = "";

                datumKotitveTe.setText(datumKotitve);
                steviloMladihTv.setText(String.valueOf(steviloMladih));
                ovcaTe.setText(ovca);
                ovenTe.setText(oven);
                steviloMrtvihTe.setText(String.valueOf(steviloMrtvih));
                opombeTe.setText(opombe);

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
        Toast.makeText(this, "Pošiljam podatke", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("kotitevID", kateraKotitev);
            jsonBody.put("datumKotitve", datumKotitveTe.getText());
            jsonBody.put("ovcaID", ovcaTe.getText());
            jsonBody.put("ovenID", ovenTe.getText());
            jsonBody.put("steviloMrtvih", Integer.parseInt(String.valueOf(steviloMrtvihTe.getText())));
            jsonBody.put("opombe", opombeTe.getText());

            final String mRequestBody = jsonBody.toString();


            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }


            };

            requestQueue.add(stringRequest);
            Toast.makeText(this, "Kotitev je bila urejena.", Toast.LENGTH_SHORT).show();
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}