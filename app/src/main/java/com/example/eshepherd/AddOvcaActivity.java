package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import android.content.Intent;
import android.util.Log;
import android.view.View;
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
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AddOvcaActivity extends AppCompatActivity {
    private EditText OvcaID;
    private EditText CredaID;
    private EditText DatumRojstva;
    private EditText Pasma;
    private EditText mamaID;
    private EditText oceID;
    private EditText SteviloSorojencev;
    private EditText Stanje;
    private EditText Opombe;

    private TextView status; // za status - dodajam

    private RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovce";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ovca);

        OvcaID = (EditText) findViewById(R.id.OvcaID);
        CredaID = (EditText) findViewById(R.id.CredaID);
        DatumRojstva = (EditText) findViewById(R.id.DatumRojstva);
        Pasma = (EditText) findViewById(R.id.Pasma);
        mamaID = (EditText) findViewById(R.id.mamaID);
        oceID = (EditText) findViewById(R.id.oceID);
        SteviloSorojencev = (EditText) findViewById(R.id.SteviloSorojencev);
        Stanje = (EditText) findViewById(R.id.Stanje);
        Opombe = (EditText) findViewById(R.id.Opombe);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        status = (TextView) findViewById(R.id.status);
    }

    public void addOvca(View view) {
        this.status.setText("Posting to " + url);
        Toast.makeText(this, "Pošiljam podatke", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ovcaID", OvcaID.getText());
            jsonBody.put("credaID", CredaID.getText());
            jsonBody.put("datumRojstva", DatumRojstva.getText());
            jsonBody.put("pasma", Pasma.getText());
            jsonBody.put("mamaID", mamaID.getText());
            jsonBody.put("oceID", oceID.getText());
            jsonBody.put("steviloSorojencev", SteviloSorojencev.getText());
            jsonBody.put("stanje", Stanje.getText());
            jsonBody.put("opombe", Opombe.getText());
            //jsonBody.put("seznamKotitev", null);
            //jsonBody.put("seznamGonitev", null);

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
            Toast.makeText(this, "Ovca je bila dodana.", Toast.LENGTH_SHORT).show();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }
}