package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddKotitevActivity extends AppCompatActivity {
    private EditText DatumKotitve, SteviloMrtvih, Opombe;
    private Spinner mamaSpinner, oceSpinner;
    private static String mama, oce;
    private TextView statusKotitev; // za status - dodajam
    private RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Kotitve";
    private String urlOvce = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovce";
    private String urlOvni = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovni";
    private ArrayList<String> mamaList, oceList;
    private int kotitevID;
    Intent intentGetKotitev;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kotitev);

        DatumKotitve = (EditText) findViewById(R.id.DatumKotitve);
        SteviloMrtvih = (EditText) findViewById(R.id.SteviloMrtvih);
        Opombe = (EditText) findViewById(R.id.Opombe);

        statusKotitev = (TextView) findViewById(R.id.statusGonitev);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        mamaSpinner = (Spinner) findViewById(R.id.OvcaID);
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


        oceSpinner = (Spinner) findViewById(R.id.OvenID);
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

        intentGetKotitev = getIntent();
        if (intentGetKotitev != null)
            kotitevID = intentGetKotitev.getIntExtra("ID", 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void addKotitev(View view) {
        this.statusKotitev.setText("Posting to " + url);
        Toast.makeText(this, "Pošiljam podatke", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("datumKotitve", DatumKotitve.getText());
            jsonBody.put("ovcaID", mama);
            jsonBody.put("ovenID", oce);
            if (SteviloMrtvih.getText().length() == 0)
                jsonBody.put("steviloMrtvih", 0);
            else
                jsonBody.put("steviloMrtvih", SteviloMrtvih.getText());
            jsonBody.put("opombe", Opombe.getText());

            final String mRequestBody = jsonBody.toString();

            statusKotitev.setText(mRequestBody);

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
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ApiKey", "SecretKey");
                    params.put("Content-Type", "application/json");
                    return params;
                }

            };

            requestQueue.add(stringRequest);
            Toast.makeText(this, "Kotitev je bila dodana.", Toast.LENGTH_SHORT).show();
            addJagenjcka();
            finish();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addJagenjcka() {
        Intent intent = new Intent(this, AddJagenjcekActivity.class);
        intent.putExtra("kotitevID", kotitevID);
        startActivity(intent);
    }


    public void dodajMame(){
        JsonArrayRequest request = new JsonArrayRequest(urlOvce, jsonArrayListenerMama, errorListener)
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

    public void dodajOcete(){
        JsonArrayRequest request = new JsonArrayRequest(urlOvni, jsonArrayListenerOce, errorListener)
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



    private Response.Listener<JSONArray> jsonArrayListenerMama = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){
            ArrayList<String> data = new ArrayList<>();
            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    String ovca = object.getString("ovcaID");
                    data.add(ovca);

                } catch (JSONException e){
                    e.printStackTrace();
                    return;

                }
            }
            for (String row : data){
                mamaList.add(row);
                Log.d("mamaList", row);
            }
            ArrayAdapter<String> adapterMama = new ArrayAdapter<String>(AddKotitevActivity.this, android.R.layout.simple_spinner_item, mamaList);
            adapterMama.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mamaSpinner.setAdapter(adapterMama);
        }
    };

    private Response.Listener<JSONArray> jsonArrayListenerOce = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){
            ArrayList<String> data = new ArrayList<>();
            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    String ovca = object.getString("ovenID");
                    data.add(ovca);

                } catch (JSONException e){
                    e.printStackTrace();
                    return;

                }
            }
            for (String row : data){
                oceList.add(row);
                Log.d("oceList", row);
            }
            ArrayAdapter<String> adapterOce = new ArrayAdapter<String>(AddKotitevActivity.this, android.R.layout.simple_spinner_item, oceList);
            adapterOce.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            oceSpinner.setAdapter(adapterOce);
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };



}