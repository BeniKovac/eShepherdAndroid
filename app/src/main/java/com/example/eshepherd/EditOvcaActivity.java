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
import java.util.HashMap;
import java.util.Map;

public class EditOvcaActivity extends AppCompatActivity {
    private String iskanaOvca;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovce";
    private RequestQueue requestQueue;
    private TextView ovcaIDTv, steviloKotitevTv, povprecjeJagenjckovTv;
    private EditText credaIDTe, datumRojstvaTe, pasmaTe, mamaIDte,
            oceIDte, steviloSorojencevTe, stanjeTe, opombeTe;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ovca);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.ovcaIDTv = findViewById(R.id.OvcaID);
        this.credaIDTe = findViewById(R.id.CredaID);
        this.datumRojstvaTe = findViewById(R.id.DatumRojstva);
        this.pasmaTe = findViewById(R.id.Pasma);
        this.mamaIDte = findViewById(R.id.mamaID);
        this.oceIDte = findViewById(R.id.oceID);
        this.steviloSorojencevTe = findViewById(R.id.SteviloSorojencev);
        this.opombeTe = findViewById(R.id.Opombe);
        this.stanjeTe = findViewById(R.id.Stanje);

        this.steviloKotitevTv = findViewById(R.id.SteviloKotitev);
        this.povprecjeJagenjckovTv = findViewById(R.id.PovprecjeJagenjckov);

        intent = getIntent();
        iskanaOvca = intent.getStringExtra("ID");

        showOvca(iskanaOvca);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showOvca(String iskanaOvca) {
        url += "/" + iskanaOvca; // sestavi pravi url
        JsonObjectRequest request = new JsonObjectRequest(url, null, jsonObjectListener, errorListener){@Override
        public Map<String,String> getHeaders() throws AuthFailureError
        {
            Map<String, String> params = new HashMap<String, String>();
            params.put("ApiKey", "SecretKey");
            return params;
        }};
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
                if (datumRojstva.equals("null"))
                    datumRojstva = "";
                String pasma = response.getString("pasma");
                if (pasma.equals("null"))
                    pasma = "";
                String mama = response.getString("mamaID");
                if (mama.equals("null"))
                    mama = "/";
                String oce = response.getString("oceID");
                if (oce.equals("null"))
                    oce = "/";
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
                credaIDTe.setText(creda);
                datumRojstvaTe.setText(datumRojstva);
                pasmaTe.setText(pasma);
                mamaIDte.setText(mama);
                oceIDte.setText(oce);
                steviloSorojencevTe.setText(String.valueOf(steviloSorojencev));
                stanjeTe.setText(stanje);
                opombeTe.setText(opombe);
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
        Toast.makeText(this, "Pošiljam podatke", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("ovcaID", ovcaIDTv.getText());
            jsonBody.put("credaID", credaIDTe.getText());
            jsonBody.put("datumRojstva", datumRojstvaTe.getText());
            jsonBody.put("pasma", pasmaTe.getText());
            jsonBody.put("mamaID", mamaIDte.getText());
            jsonBody.put("oceID", oceIDte.getText());
            jsonBody.put("steviloSorojencev", Integer.parseInt(String.valueOf(steviloSorojencevTe.getText())));
            jsonBody.put("steviloKotitev", Integer.parseInt(String.valueOf(steviloKotitevTv.getText())));
            jsonBody.put("povprecjeJagenjckov", Integer.parseInt(String.valueOf(povprecjeJagenjckovTv.getText())));
            jsonBody.put("stanje", stanjeTe.getText());
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
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ApiKey", "SecretKey");
                    return params;
                }

            };

            this.requestQueue.add(stringRequest);
            Toast.makeText(this, "Ovca je bila urejena.", Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}