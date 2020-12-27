package com.example.eshepherd;

import androidx.annotation.NonNull;
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
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ShowCredaActivity extends AppCompatActivity {
    private String iskanaCreda;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Crede";
    private RequestQueue requestQueue;
    private TextView credaTv, opombeTv;
    BottomNavigationView navigationView;
    Intent intent;
    private boolean resume = false;
    JSONArray seznamOvac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_creda);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.credaTv = findViewById(R.id.CredaID);
        this.opombeTv = findViewById(R.id.Opombe);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.crede_icon:
                        Intent intent2 = new Intent(ShowCredaActivity.this, SpecificCredaOvce.class);
                        intent2.putExtra("SpecificID",iskanaCreda);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });

        intent = getIntent();
        if(intent != null) {
            iskanaCreda = intent.getStringExtra("ID"); // treba prenest ovco prek intentov!
        }else{
            iskanaCreda = savedInstanceState.getString("ID");
        }
        showCreda(iskanaCreda, resume);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu_show_creda, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:
                if(seznamOvac == null)
                    deleteCreda();
                else
                    Toast.makeText(this,"Creda vsebuje ovce!", Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }
    }

    public void deleteCreda() {
        Toast.makeText(this, "Brišem čredo", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("gonitevID", iskanaCreda);

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
            Toast.makeText(this, "Čreda je bila izbrisana.", Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showCreda(String iskanaCreda, boolean resume) {
        if (! resume)
            url += "/" + iskanaCreda; // sestavi pravi url
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
                String creda = response.getString("credeID");
                String opombe = response.getString("opombe");
                seznamOvac = response.getJSONArray("seznamOvac");
                if (opombe.equals("null"))
                    opombe = "";
                credaTv.setText(creda);
                opombeTv.setText(opombe);

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

    public void editCreda(View view) {
        Intent intent = new Intent(this, EditCredaActivity.class);
        intent.putExtra("ID", iskanaCreda);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ID", iskanaCreda);
    }
    @Override
    protected void onResume() {
        super.onResume();
        resume = true;
        showCreda(iskanaCreda, resume);
    }
}