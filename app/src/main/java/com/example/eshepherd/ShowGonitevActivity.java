package com.example.eshepherd;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ShowGonitevActivity extends AppCompatActivity {
    private int iskanaGonitev;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Gonitve";
    private RequestQueue requestQueue;
    private TextView datumGonitveTv,ovcaTv, ovenTv, predvidenaKotitevTv, opombeTv;
    BottomNavigationView navigationView;
    Intent intent;
    private boolean resume = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gonitev);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.datumGonitveTv = findViewById(R.id.DatumGonitve);
        this.ovcaTv = findViewById(R.id.OvcaID);
        this.ovenTv = findViewById(R.id.OvenID);
        this.predvidenaKotitevTv = findViewById(R.id.PredvidenaKotitev);
        this.opombeTv = findViewById(R.id.Opombe);

        intent = getIntent();
        if(intent != null) {
            iskanaGonitev = intent.getIntExtra("ID", 0); // treba prenest ovco prek intentov!
        }else{
            iskanaGonitev = savedInstanceState.getInt("ID");
        }
        showGonitev(iskanaGonitev, resume);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu_show_gonitev, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:
                onClickShowAlert();
                return true;
            default:
                return false;
        }
    }

    public void onClickShowAlert() {
        AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(ShowGonitevActivity.this);
        myAlertBuilder.setTitle("Izbrisati želite gonitev.");
        myAlertBuilder.setMessage("Ali ste prepričani, da želite izbrisati gonitev?");
        myAlertBuilder.setPositiveButton("Da, izbriši gonitev", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked OK button.
                        deleteGonitev();
                    }
                });
        myAlertBuilder.setNegativeButton("Ne, prekliči", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog.
                    }
                });

        myAlertBuilder.show();
    }

    public void deleteGonitev() {
        Toast.makeText(this, "Brišem gonitev", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("gonitevID", iskanaGonitev);

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
            Toast.makeText(this, "Gonitev je bila izbrisana.", Toast.LENGTH_SHORT).show();
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

    public void showGonitev(int iskanaGonitev, boolean resume) {
        if (! resume)
            url += "/" + iskanaGonitev; // sestavi pravi url
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
                String datumGonitve = response.getString("datumGonitve").substring(0,10);
                String predvidenaKotitev = response.getString("predvidenaKotitev").substring(0,10);
                String ovca = response.getString("ovcaID");
                String oven = response.getString("ovenID");
                String opombe = response.getString("opombe");
                if (opombe.equals("null"))
                    opombe = "";
                datumGonitveTv.setText(datumGonitve);
                predvidenaKotitevTv.setText(predvidenaKotitev);
                ovcaTv.setText(ovca);
                ovenTv.setText(oven);
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

    public void editGonitev(View view) {
        Intent intent = new Intent(this, EditGonitevActivity.class);
        intent.putExtra("ID", iskanaGonitev);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ID", iskanaGonitev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume = true;
        showGonitev(iskanaGonitev, resume);
    }

}