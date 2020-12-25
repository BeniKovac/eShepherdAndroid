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
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class EditOvenActivity extends AppCompatActivity {
    private String iskanOven;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovni";
    private RequestQueue requestQueue;
    private TextView ovenIDTv;
    private EditText credaIDTe, datumRojstvaTe, pasmaTe, mamaIDte,
            oceIDte, steviloSorojencevTe, stanjeTe, opombeTe, porekloTe;
    BottomNavigationView navigationView;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_oven);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.ovenIDTv = findViewById(R.id.OvenID);
        this.credaIDTe = findViewById(R.id.CredaID);
        this.datumRojstvaTe = findViewById(R.id.DatumRojstva);
        this.pasmaTe = findViewById(R.id.Pasma);
        this.mamaIDte = findViewById(R.id.mamaID);
        this.oceIDte = findViewById(R.id.oceID);
        this.steviloSorojencevTe = findViewById(R.id.SteviloSorojencev);
        this.opombeTe = findViewById(R.id.Opombe);
        this.stanjeTe = findViewById(R.id.Stanje);
        this.porekloTe = findViewById(R.id.Poreklo);

        intent = getIntent();
        iskanOven = intent.getStringExtra("ID");

        showOven(iskanOven);
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
                credaIDTe.setText(String.valueOf(creda));
                datumRojstvaTe.setText(datumRojstva);
                pasmaTe.setText(pasma);
                mamaIDte.setText(mama);
                oceIDte.setText(oce);
                steviloSorojencevTe.setText(String.valueOf(steviloSorojencev));
                stanjeTe.setText(stanje);
                opombeTe.setText(opombe);
                porekloTe.setText(poreklo);


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
        Toast.makeText(this, "Pošiljam podatke", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ovenID", ovenIDTv.getText());
            jsonBody.put("credaID", credaIDTe);

            //Date wrongFormatDate = (Date) DatumRojstva.getText();
            //SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            jsonBody.put("datumRojstva", datumRojstvaTe.getText());
            //01.01.2020 --> "2020-01-01"
            jsonBody.put("pasma", pasmaTe.getText());
            jsonBody.put("mamaID", mamaIDte);
            jsonBody.put("oceID", oceIDte);
            jsonBody.put("steviloSorojencev", Integer.parseInt(String.valueOf(steviloSorojencevTe.getText())));
            jsonBody.put("stanje", stanjeTe.getText());
            jsonBody.put("opombe", opombeTe.getText());
            jsonBody.put("poreklo", porekloTe.getText());

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
                    // As of f605da3 the following should work
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            // Now you can use any deserializer to make sense of data
                            JSONObject obj = new JSONObject(res);
                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        } catch (JSONException e2) {
                            // returned data is not JSONObject?
                            e2.printStackTrace();
                        }
                    }
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
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }

            };

            requestQueue.add(stringRequest);
            Toast.makeText(this, "Oven je bil dodan.", Toast.LENGTH_SHORT).show();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}