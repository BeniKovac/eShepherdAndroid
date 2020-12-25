package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class EditGonitevActivity extends AppCompatActivity {
    private int kateraGonitev;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Gonitve";
    private String urlOvce = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovce";
    private String urlOvni = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovni";
    private RequestQueue requestQueue;
    private EditText datumGonitveTe, predvidenaKotitevTe, opombeTe, ovcaTe, ovenTe;
    private TextView statusGonitev; // za status - dodajam


    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gonitev);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        statusGonitev = (TextView) findViewById(R.id.statusGonitev);

        this.datumGonitveTe = findViewById(R.id.DatumGonitve);
        this.predvidenaKotitevTe = findViewById(R.id.PredvidenaKotitev);
        this.opombeTe = findViewById(R.id.Opombe);
        this.ovcaTe = findViewById(R.id.OvcaID);
        this.ovenTe = findViewById(R.id.OvenID);

        intent = getIntent();
        kateraGonitev = 2;//intent.getIntExtra("ID", 0);

        showGonitev(kateraGonitev);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };


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

                datumGonitveTe.setText(datumGonitve);
                predvidenaKotitevTe.setText(predvidenaKotitev);
                opombeTe.setText(opombe);
                ovcaTe.setText(ovca);
                ovenTe.setText(oven);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    };



    public void editGonitev(View view) {
        this.statusGonitev.setText("Posting to " + url);
        Toast.makeText(this, "Pošiljam podatke", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("datumGonitve", datumGonitveTe.getText());
            jsonBody.put("ovcaID", ovcaTe.getText());
            jsonBody.put("predvidenaKotitev", predvidenaKotitevTe.getText());
            jsonBody.put("ovenID", ovenTe.getText());
            jsonBody.put("opombe", opombeTe.getText());

            final String mRequestBody = jsonBody.toString();

            statusGonitev.setText(mRequestBody);

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
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        //statusCreda.setText(responseString); // KAJ GA TLE MEDE?
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }

            };

            requestQueue.add(stringRequest);
            Toast.makeText(this, "Gonitev je bila urejena.", Toast.LENGTH_SHORT).show();


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}