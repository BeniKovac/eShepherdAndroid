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
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class AddGonitevActivity extends AppCompatActivity {
    private EditText DatumGonitve, OvcaID, OvenID, Opombe;

    private TextView statusGonitev; // za status - dodajam

    private RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Gonitve";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gonitev);
        url = url.replaceAll(" ", "%20");

        OvcaID = (EditText) findViewById(R.id.OvcaID);
        OvenID = (EditText) findViewById(R.id.OvenID);
        DatumGonitve = (EditText) findViewById(R.id.DatumGonitve);

        Opombe = (EditText) findViewById(R.id.Opombe);
        statusGonitev = (TextView) findViewById(R.id.statusGonitev);

        requestQueue = Volley.newRequestQueue(getApplicationContext());


    }

    public void addGonitev(View view) {
        this.statusGonitev.setText("Posting to " + url);
        Toast.makeText(this, "Pošiljam podatke", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("datumGonitve", DatumGonitve.getText());
            jsonBody.put("ovcaID", OvcaID.getText());
            jsonBody.put("ovenID", OvenID.getText());
            jsonBody.put("opombe", Opombe.getText());

            final String mRequestBody = jsonBody.toString();

            statusGonitev.setText(mRequestBody);

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
                        //statusCreda.setText(responseString); // KAJ GA TLE MEDE?
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }

            };

            requestQueue.add(stringRequest);
            Toast.makeText(this, "Gonitev je bila dodana.", Toast.LENGTH_SHORT).show();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}