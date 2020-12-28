package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

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
import com.android.volley.ServerError;
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
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddOvenActivity extends AppCompatActivity {

    private EditText OvenID, DatumRojstva, Pasma, SteviloSorojencev, Stanje, Opombe,Poreklo;

    private Spinner mamaSpinner, oceSpinner, credaSpinner, CredaID;
    private static String mama;
    private String oce, creda;
    String[] values;
    private ArrayList<String> mamaList, oceList, credeList;



    private RequestQueue requestQueue;
    private String urlOvce = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovce";
    private String urlOvni = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovni";
    private String urlCrede = "https://eshepherd-dev.azurewebsites.net/api/v1/Crede";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_oven);

        OvenID = (EditText) findViewById(R.id.OvenID);
        CredaID = (Spinner) findViewById(R.id.CredaID);
        DatumRojstva = (EditText) findViewById(R.id.DatumRojstva);
        Pasma = (EditText) findViewById(R.id.Pasma);
        SteviloSorojencev = (EditText) findViewById(R.id.SteviloSorojencev);
        Stanje = (EditText) findViewById(R.id.Stanje);
        Opombe = (EditText) findViewById(R.id.Opombe);
        Poreklo = (EditText) findViewById(R.id.Poreklo);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        values = new String[]{"/", "/", "0"}; // mama, oce, creda
        mamaSpinner = (Spinner) findViewById(R.id.mamaID);
        mamaList = new ArrayList<>();
        dodajMame();
        ArrayAdapter<String> adapterMama = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mamaList) ;
        adapterMama.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mamaSpinner.setAdapter(adapterMama);
        mamaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("pred izbranim", "!!!");
                mama = mamaList.get(position);
                values[0] = mama;
                Toast.makeText(getApplicationContext(), mama, Toast.LENGTH_SHORT).show();
                Log.d("mama", mama);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mama ="/";
            }

        });


        oceSpinner = (Spinner) findViewById(R.id.oceID);
        oceList = new ArrayList<>();
        dodajOcete();
        ArrayAdapter<String> adapterOce = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, oceList);
        adapterOce.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        oceSpinner.setAdapter(adapterOce);
        oceSpinner.setSelection(0, true);
        oceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oce = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        credaSpinner = (Spinner) findViewById(R.id.CredaID);
        credeList = new ArrayList<>();
        dodajCrede();
        ArrayAdapter<String> adapterCreda = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, credeList);
        adapterCreda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        credaSpinner.setAdapter(adapterCreda);
        credaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                creda = parent.getItemAtPosition(position).toString();
                ((TextView) view).setTextColor(Color.BLACK);
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

    public void addOven(View view) {
        Toast.makeText(this, "Pošiljam podatke", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ovenID", OvenID.getText());
            jsonBody.put("credaID", "2");

            //Date wrongFormatDate = (Date) DatumRojstva.getText();
            //SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            jsonBody.put("datumRojstva", DatumRojstva.getText());
            //01.01.2020 --> "2020-01-01"
            jsonBody.put("pasma", Pasma.getText());
            jsonBody.put("mamaID", "/");
            jsonBody.put("oceID", "/");
            if (SteviloSorojencev.getText().length() == 0)
                jsonBody.put("steviloSorojencev", 0);
            else
                jsonBody.put("steviloSorojencev", SteviloSorojencev.getText());
            jsonBody.put("stanje", Stanje.getText());
            jsonBody.put("opombe", Opombe.getText());
            jsonBody.put("poreklo", Poreklo.getText());

            final String mRequestBody = jsonBody.toString();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlOvni, new Response.Listener<String>() {
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

            requestQueue.add(stringRequest);
            Toast.makeText(this, "Oven je bil dodan.", Toast.LENGTH_SHORT).show();
            finish();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

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

    public void dodajCrede(){
        JsonArrayRequest request = new JsonArrayRequest(urlCrede, jsonArrayListenerCreda, errorListener)
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
        }
    };

    private Response.Listener<JSONArray> jsonArrayListenerCreda = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){
            ArrayList<String> data = new ArrayList<>();
            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    String creda = object.getString("credeID");
                    data.add(creda);

                } catch (JSONException e){
                    e.printStackTrace();
                    return;

                }
            }
            for (String row : data){
                credeList.add(row);
                Log.d("credaList", row);
            }
        }
    };


    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

}