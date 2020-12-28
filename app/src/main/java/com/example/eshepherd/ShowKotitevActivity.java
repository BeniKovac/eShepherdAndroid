package com.example.eshepherd;

import androidx.annotation.NonNull;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ShowKotitevActivity extends AppCompatActivity {

    private int iskanaKotitev;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Kotitve";
    private RequestQueue requestQueue;
    private TextView datumKotitveTv, steviloMladihTv, ovcaTv, ovenTv, steviloMrtvihTv, opombeTv;
    BottomNavigationView navigationView;
    Intent intent;
    private int steviloMladihForDelete;
    private boolean resume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_kotitev);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.datumKotitveTv = findViewById(R.id.DatumKotitve);
        this.steviloMladihTv = findViewById(R.id.SteviloMladih);
        this.ovcaTv = findViewById(R.id.OvcaID);
        this.ovenTv = findViewById(R.id.OvenID);
        this.steviloMrtvihTv = findViewById(R.id.SteviloMrtvih);
        this.opombeTv = findViewById(R.id.Opombe);

        intent = getIntent();
        if(intent != null) {
            iskanaKotitev = intent.getIntExtra("ID", 0);
        }
        else
            {
            iskanaKotitev = savedInstanceState.getInt("ID");
        }
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.list_icon:
                        Intent intent = new Intent(ShowKotitevActivity.this, SpecificKotitevJagenjcki.class);
                        intent.putExtra("SpecificID",Integer.toString(iskanaKotitev));
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
        prikaziKotitev(iskanaKotitev, resume);
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
                onClickShowAlert();
                return true;
            default:
                return false;
        }
    }

    public void onClickShowAlert() {
        AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(ShowKotitevActivity.this);
        myAlertBuilder.setTitle("Izbrisati želite kotitev.");
        myAlertBuilder.setMessage("Ali ste prepričani, da želite izbrisati kotitev?");
        myAlertBuilder.setPositiveButton("Da, izbriši kotitev", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked OK button.
                        if (steviloMladihForDelete == 0)
                            deleteKotitev();
                        else
                            Toast.makeText(getApplicationContext(), "Kotitev ne more biti izbrisana, ker že ima jagenjčke", Toast.LENGTH_SHORT).show();
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

    public void deleteKotitev() {
        Toast.makeText(this, "Brišem kotitev", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("kotitevID", iskanaKotitev);

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
                public Map<String,String> getHeaders() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ApiKey", "SecretKey");
                    return params;
                }


            };

            requestQueue.add(stringRequest);
            Toast.makeText(this, "Kotitev je bila izbrisana.", Toast.LENGTH_SHORT).show();
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void prikaziKotitev(int iskanaKotitev, boolean resume) {
        if (! resume)
            url += "/" + iskanaKotitev; // sestavi pravi url
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
                String datumKotitve = response.getString("datumKotitve").substring(0,10);
                String ovca = response.getString("ovcaID");
                String oven = response.getString("ovenID");
                int steviloMladih = response.getInt("steviloMladih");
                int steviloMrtvih = response.getInt("steviloMrtvih");
                String opombe = response.getString("opombe");
                if (opombe.equals("null"))
                    opombe = "";

                datumKotitveTv.setText(datumKotitve);
                steviloMladihForDelete = steviloMladih;
                steviloMladihTv.setText(String.valueOf(steviloMladih));
                ovcaTv.setText(ovca);
                ovenTv.setText(oven);
                steviloMrtvihTv.setText(String.valueOf(steviloMrtvih));
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

    public void editKotitev(View view) {
        Intent intent = new Intent(this, EditKotitevActivity.class);
        intent.putExtra("ID", iskanaKotitev);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ID", iskanaKotitev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume = true;
        prikaziKotitev(iskanaKotitev, resume);
    }

    public void addJagenjcka(View view) {
        Intent intent = new Intent(this, AddJagenjcekActivity.class);
        intent.putExtra("kotitevID", iskanaKotitev);
        startActivity(intent);
    }
}