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
import java.util.HashMap;
import java.util.Map;

public class EditJagenjcekActivity extends AppCompatActivity {
    private Integer iskanJagenjcek;
    private EditText jagenjcekIDTe, spolTe, opombeTe, stanjeTe;
    private TextView kotitevTv;
    private RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Jagenjcki";
    Intent intent;
    private int kotitevID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_jagenjcek);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        jagenjcekIDTe = (EditText) findViewById(R.id.JagenjcekID);
        spolTe = (EditText) findViewById(R.id.Spol);
        opombeTe = (EditText) findViewById(R.id.Opombe);
        stanjeTe = (EditText) findViewById(R.id.Stanje);
        intent = getIntent();
        iskanJagenjcek = intent.getIntExtra("ID", 1);
        showJagenjcek(iskanJagenjcek);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showJagenjcek(Integer iskanJagenjcek) {
        url += "/" + iskanJagenjcek; // sestavi pravi url
        JsonObjectRequest request = new JsonObjectRequest(url, null, jsonObjectListenerShowJagenjcek, errorListener){
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


    private Response.Listener<JSONObject> jsonObjectListenerShowJagenjcek = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            try {
                String jagenjcek = response.getString("idJagenjcka");
                kotitevID = response.getInt("kotitevID");
                String spol = response.getString("spol");
                String opombe = response.getString("opombe");
                if (opombe.equals("null")) {
                    opombe = "";
                }
                String stanje = response.getString("stanje");
                if (stanje.equals("null")) {
                    stanje = "";
                }
                jagenjcekIDTe.setText(jagenjcek);
                spolTe.setText(spol);
                opombeTe.setText(opombe);
                stanjeTe.setText(stanje);

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
        Toast.makeText(this, "Pošiljam podatke", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("skritIdJagenjcka", iskanJagenjcek);
            jsonBody.put("kotitevID", kotitevID);
            jsonBody.put("idJagenjcka", jagenjcekIDTe.getText());
            jsonBody.put("spol", spolTe.getText());
            jsonBody.put("opombe", opombeTe.getText());
            jsonBody.put("stanje", stanjeTe.getText());

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

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        //statusCreda.setText(responseString); // KAJ GA TLE MEDE?
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

            requestQueue.add(stringRequest);
            Toast.makeText(this, "Jagenjček je bil urejen.", Toast.LENGTH_SHORT).show();
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}