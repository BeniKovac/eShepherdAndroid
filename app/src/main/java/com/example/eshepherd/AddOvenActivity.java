package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class AddOvenActivity extends AppCompatActivity {

    private EditText OvenID;
    private EditText CredaID;
    private EditText DatumRojstva;
    private EditText Pasma;
    private EditText mamaID;
    private EditText oceID;
    private EditText SteviloSorojencev;
    private EditText Stanje;
    private EditText Opombe;
    private EditText Poreklo;


    private TextView status; // za status - dodajam

    private RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovni";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_oven);

        OvenID = (EditText) findViewById(R.id.OvenID);
        CredaID = (EditText) findViewById(R.id.CredaID);
        DatumRojstva = (EditText) findViewById(R.id.DatumRojstva);
        Pasma = (EditText) findViewById(R.id.Pasma);
        mamaID = (EditText) findViewById(R.id.mamaID);
        oceID = (EditText) findViewById(R.id.oceID);
        SteviloSorojencev = (EditText) findViewById(R.id.SteviloSorojencev);
        Stanje = (EditText) findViewById(R.id.Stanje);
        Opombe = (EditText) findViewById(R.id.Opombe);
        Poreklo = (EditText) findViewById(R.id.Poreklo);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        status = (TextView) findViewById(R.id.status);

        url = url.replaceAll(" ", "%20");
    }

    public void addOven(View view) {
        this.status.setText("Posting to " + url);
        Toast.makeText(this, "Pošiljam podatke", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ovenID", OvenID.getText());
            jsonBody.put("credaID", CredaID.getText());
            jsonBody.put("datumRojstva", DatumRojstva.getText());
            jsonBody.put("pasma", Pasma.getText());
            jsonBody.put("mamaID", mamaID.getText());
            jsonBody.put("oceID", oceID.getText());
            jsonBody.put("steviloSorojencev", SteviloSorojencev.getText());
            jsonBody.put("stanje", Stanje.getText());
            jsonBody.put("opombe", Opombe.getText());
            jsonBody.put("poreklo", Poreklo.getText());

            final String mRequestBody = jsonBody.toString();

            status.setText(mRequestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                        status.setText(responseString);
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