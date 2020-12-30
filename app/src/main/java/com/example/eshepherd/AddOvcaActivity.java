package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AddOvcaActivity extends AppCompatActivity {
    private EditText OvcaID, DatumRojstva, Pasma, SteviloSorojencev, Stanje, Opombe;

    private Spinner mamaSpinner, oceSpinner, credaSpinner;
    private static String mama, oce, creda;

    private RequestQueue requestQueue;
    private String urlOvce = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovce";
    private String urlOvni = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovni";
    private String urlCrede = "https://eshepherd-dev.azurewebsites.net/api/v1/Crede";
    private ArrayList<String> mamaList, oceList, credeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ovca);

        this.OvcaID = (EditText) findViewById(R.id.OvcaID);
        this.DatumRojstva = (EditText) findViewById(R.id.DatumRojstva);
        this.Pasma = (EditText) findViewById(R.id.Pasma);
        this.SteviloSorojencev = (EditText) findViewById(R.id.SteviloSorojencev);
        this.Stanje = (EditText) findViewById(R.id.Stanje);
        this.Opombe = (EditText) findViewById(R.id.Opombe);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        mamaSpinner = (Spinner) findViewById(R.id.mamaID);
        mamaList = new ArrayList<>();
        dodajMame();
        mamaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mama = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        oceSpinner = (Spinner) findViewById(R.id.oceID);
        oceList = new ArrayList<>();
        dodajOcete();
        oceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oce = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        credaSpinner = (Spinner) findViewById(R.id.CredaID);
        credeList = new ArrayList<>();
        dodajCrede();
        credaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                creda = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void addOvca(View view) {
        Toast.makeText(this, "Pošiljam podatke", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ovcaID", OvcaID.getText());
            jsonBody.put("credaID", "1");//creda);

            jsonBody.put("datumRojstva", DatumRojstva.getText());
            jsonBody.put("pasma", Pasma.getText());
            jsonBody.put("mamaID", "/");//mama);
            jsonBody.put("oceID", "/");//oce);
            if (SteviloSorojencev.getText().length() == 0)
                jsonBody.put("steviloSorojencev", 0);
            else
                jsonBody.put("steviloSorojencev", SteviloSorojencev.getText());
            jsonBody.put("stanje", Stanje.getText());
            jsonBody.put("opombe", Opombe.getText());


            final String mRequestBody = jsonBody.toString();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlOvce, new Response.Listener<String>() {
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
                public Map<String,String> getHeaders() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ApiKey", "SecretKey");
                    params.put("Content-Type", "application/json");
                    return params;
                }

            };

            this.requestQueue.add(stringRequest);
            Toast.makeText(this, "Ovca je bila dodana.", Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void dodajMame() {
        JsonArrayRequest request = new JsonArrayRequest(urlOvce, jsonArrayListenerMama, errorListener) {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ApiKey", "SecretKey");
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(request);
    }

    public void dodajOcete() {
        JsonArrayRequest request = new JsonArrayRequest(urlOvni, jsonArrayListenerOce, errorListener) {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ApiKey", "SecretKey");
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(request);
    }

    public void dodajCrede() {
        JsonArrayRequest request = new JsonArrayRequest(urlCrede, jsonArrayListenerCreda, errorListener) {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ApiKey", "SecretKey");
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(request);
    }


    private Response.Listener<JSONArray> jsonArrayListenerMama = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            ArrayList<String> data = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject object = response.getJSONObject(i);
                    String creda = object.getString("credaID");
                    String ovca = object.getString("ovcaID");
                    if (! creda.equals("0"))
                        data.add(ovca);

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;

                }
            }
            for (String row : data) {
                mamaList.add(row);
                Log.d("mamaList", row);
            }
            ArrayAdapter<String> adapterMama = new ArrayAdapter<String>(AddOvcaActivity.this, android.R.layout.simple_spinner_item, mamaList);
            adapterMama.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mamaSpinner.setAdapter(adapterMama);
        }
    };

    private Response.Listener<JSONArray> jsonArrayListenerOce = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            ArrayList<String> data = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject object = response.getJSONObject(i);
                    String creda = object.getString("credaID");
                    String oven = object.getString("ovenID");
                    if (! creda.equals("0"))
                        data.add(oven);

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;

                }
            }
            for (String row : data) {
                oceList.add(row);
                Log.d("oceList", row);
            }
            ArrayAdapter<String> adapterOce = new ArrayAdapter<String>(AddOvcaActivity.this, android.R.layout.simple_spinner_item, oceList);
            adapterOce.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            oceSpinner.setAdapter(adapterOce);
        }
    };

    private Response.Listener<JSONArray> jsonArrayListenerCreda = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            ArrayList<String> data = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject object = response.getJSONObject(i);
                    String creda = object.getString("credeID");
                    if (! creda.equals("0"))
                        data.add(creda);

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;

                }
            }
            for (String row : data) {

                credeList.add(row);
                Log.d("credaList", row);
            }
            ArrayAdapter<String> adapterCreda = new ArrayAdapter<String>(AddOvcaActivity.this, android.R.layout.simple_spinner_item, credeList);
            adapterCreda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            credaSpinner.setAdapter(adapterCreda);
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };



}




