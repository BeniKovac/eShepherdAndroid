package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ShowJagenjcekActivity extends AppCompatActivity {

    private Integer iskanJagenjcek, iskanaKotitev;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Jagenjcki";
    private String urlKotitve = "https://eshepherd-dev.azurewebsites.net/api/v1/Kotitve";
    private String mamaID, datumRojstva;
    private RequestQueue requestQueue;
    private TextView jagenjcekIDTv, datumRojstvaTv, mamaIDTv, spolTv, opombeTv, stanjeTv;
    BottomNavigationView navigationView;
    Intent intent;
    boolean resume = false;
    static boolean addToUrl = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_jagenjcek);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.jagenjcekIDTv = findViewById(R.id.JagenjcekID);
        this.datumRojstvaTv = findViewById(R.id.DatumRojstva);
        this.spolTv = findViewById(R.id.Spol);
        this.mamaIDTv = findViewById(R.id.mamaID);
        this.opombeTv = findViewById(R.id.Opombe);
        this.stanjeTv = findViewById(R.id.Stanje);

        intent = getIntent();
        if(intent != null) {
            iskanJagenjcek = intent.getIntExtra("ID", 0);
        }else{
            iskanJagenjcek = savedInstanceState.getInt("ID");
        }
        addToUrl = true;
        showJagenjcek(iskanJagenjcek, resume);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu_show_oven, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:
                //if (steviloMladihTv.getText().length() == 0)
                deleteJagenjcek();
                //        else
                //          Toast.makeText(getApplicationContext(), "Kotitev ne more biti izbrisana, ker že ima jagenjčke", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    public void deleteJagenjcek() {
        Toast.makeText(this, "Brišem jagenjčka", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("skritIdJagenjcka", iskanJagenjcek);

            final String mRequestBody = jsonBody.toString();


            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ApiKey", "SecretKey");
                    return params;
                }
            };

            requestQueue.add(stringRequest);
            Toast.makeText(this, "Jagenjček je bil izbrisan.", Toast.LENGTH_SHORT).show();
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


        public void showJagenjcek(Integer iskanJagenjcek, boolean restart) {
        if (! restart)
            url += "/" + iskanJagenjcek; // sestavi pravi url
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
                String jagenjcek = response.getString("idJagenjcka");
                iskanaKotitev = response.getInt("kotitevID");
                String spol = response.getString("spol");
                prikaziKotitev(iskanaKotitev);

                String opombe = response.getString("opombe");
                if (opombe.equals("null")) {
                    opombe = "";
                }
                String stanje = response.getString("stanje");
                if (stanje.equals("null")) {
                    stanje = "";
                }

                opombeTv.setText(opombe);
                stanjeTv.setText(stanje);
                jagenjcekIDTv.setText(jagenjcek);
                spolTv.setText(spol);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void prikaziKotitev(int iskanaKotitev) {
        if (addToUrl) {
            urlKotitve += "/" + iskanaKotitev; // sestavi pravi url
            addToUrl = false;
        }
        JsonObjectRequest request = new JsonObjectRequest(urlKotitve, null, jsonObjectListenerKotitev, errorListener)
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

    private Response.Listener<JSONObject> jsonObjectListenerKotitev = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            try {
                String datumRojstva = response.getString("datumKotitve").substring(0,10);
                String mamaID = response.getString("ovcaID");

                datumRojstvaTv.setText(datumRojstva);
                mamaIDTv.setText(mamaID);

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
        Intent intent = new Intent(this, EditJagenjcekActivity.class);
        intent.putExtra("ID", iskanJagenjcek);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ID", iskanJagenjcek);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume = true;
        showJagenjcek(iskanaKotitev, resume);
    }

}