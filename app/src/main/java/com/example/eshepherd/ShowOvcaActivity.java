package com.example.eshepherd;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

public class ShowOvcaActivity extends AppCompatActivity {
    public static ShowOvcaActivity instance;
    private String iskanaOvca;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovce";
    private RequestQueue requestQueue;
    private TextView ovcaIDTv, credaIDTv, datumRojstvaTv, pasmaTv, mamaIDtv,
            oceIDtv, steviloSorojencevTv, stanjeTv, opombeTv, steviloKotitevTv, povprecjeJagenjckovTv;
    BottomNavigationView navigationView;
    Intent intent;
    Context ct;
    boolean Nadaljevanje = false;
    public static final int TEXT_REQUEST = 1;
    private static boolean resume = false;
    JSONArray kotitve;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ct = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ovca);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);

        requestQueue = Volley.newRequestQueue(ct);

        this.ovcaIDTv = findViewById(R.id.OvcaID);
        this.credaIDTv = findViewById(R.id.CredaID);
        this.datumRojstvaTv = findViewById(R.id.DatumRojstva);

        this.pasmaTv = findViewById(R.id.Pasma);
        this.mamaIDtv = findViewById(R.id.mamaID);
        this.oceIDtv = findViewById(R.id.oceID);
        this.steviloSorojencevTv = findViewById(R.id.SteviloSorojencev);
        this.opombeTv = findViewById(R.id.Opombe);
        this.stanjeTv = findViewById(R.id.Stanje);
        this.steviloKotitevTv = findViewById(R.id.SteviloKotitev);
        this.povprecjeJagenjckovTv = findViewById(R.id.PovprecjeJagenjckov);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.gonitev_icon:
                        Intent intent = new Intent(ShowOvcaActivity.this, SpecificOvcaGonitve.class);
                        intent.putExtra("SpecificID",iskanaOvca);
                        startActivity(intent);
                        break;
                    case R.id.kotitev_icon:
                        Intent intent2 = new Intent(ShowOvcaActivity.this, SpecificOvcaKotitve.class);
                        intent2.putExtra("SpecificID",iskanaOvca);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });

        intent = getIntent();
        if(intent != null) {
            iskanaOvca = intent.getStringExtra("ID"); // treba prenest ovco prek intentov!
        }else{
            iskanaOvca = savedInstanceState.getString("ID");
        }
        resume = false;
        showOvca(iskanaOvca);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu_show_ovca, menu);
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
                AlertDialog.Builder(ShowOvcaActivity.this);
        myAlertBuilder.setTitle("Izbrisati želite ovco.");
        myAlertBuilder.setMessage("Ali ste prepričani, da želite izbrisati ovco?");
        myAlertBuilder.setPositiveButton("Da, izbriši ovco", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked OK button.
                        if(kotitve == null || kotitve.length() == 0)
                            deleteOvca();
                        else
                            moveOvca();
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


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showOvca(String iskanaOvca) {
        if (! resume)
            url += "/" + iskanaOvca; // sestavi pravi url
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

    public void moveOvca() {
        Toast.makeText(this, "Premikam ovco", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("ovcaID", ovcaIDTv.getText());
            jsonBody.put("credaID", "0");

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
            Toast.makeText(this, "Ovca je bila skrita.", Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void deleteOvca() {
        Toast.makeText(this, "Brišem ovco", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("ovcaID", ovcaIDTv.getText());

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
            Toast.makeText(this, "Ovca je bila izbrisana.", Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                String ovca = response.getString("ovcaID");
                String creda = response.getString("credaID");
                String datumRojstva = response.getString("datumRojstva");
                kotitve = response.getJSONArray("seznamKotitev");
                if(!datumRojstva.equals("null"))
                    datumRojstva = datumRojstva.substring(0,10);
                else
                    datumRojstva = "/";
                String pasma = response.getString("pasma");
                if (pasma.equals("null"))
                    pasma = "";
                String mama = response.getString("mamaID");
                if (mama.equals("null"))
                    mama = "";
                String oce = response.getString("oceID");
                if (oce.equals("null"))
                    oce = "";
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
                credaIDTv.setText(creda);
                datumRojstvaTv.setText(datumRojstva);
                pasmaTv.setText(pasma);
                mamaIDtv.setText(mama);
                oceIDtv.setText(oce);
                steviloSorojencevTv.setText(String.valueOf(steviloSorojencev));
                stanjeTv.setText(stanje);
                opombeTv.setText(opombe);
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
        Intent intent = new Intent(this, EditOvcaActivity.class);
        intent.putExtra("ID", iskanaOvca);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ID", iskanaOvca);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume = true;
        showOvca(iskanaOvca);
    }


}