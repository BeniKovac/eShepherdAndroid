package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddJagenjcekActivity extends AppCompatActivity {
    private Spinner spolSpinner;
    private EditText JagenjcekID, Stanje, Opombe;
    private String spol;
    private int kotitev;
    ArrayList<String> kotitevList;
    private TextView datumRojstvaTv, mamaTv, Kotitev;
    TextView statusJagenjcek;
    RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Jagenjcki";
    private String urlKotitve = "https://eshepherd-dev.azurewebsites.net/api/v1/Kotitve";
    Intent getKotitevIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jagenjcek);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        this.JagenjcekID = (EditText) findViewById(R.id.JagenjcekID);
        this.Stanje = (EditText) findViewById(R.id.Stanje);
        this.Opombe = (EditText) findViewById(R.id.Opombe);
        //this.Kotitev = (TextView) findViewById(R.id.KotitevID);
        this.datumRojstvaTv = (TextView) findViewById(R.id.DatumRojstva);
        this.mamaTv = (TextView) findViewById(R.id.mamaID);
        getKotitevIntent = getIntent(); // dobi pravo kotitevID
        prikaziKotitev(); // prikazi podatke prave kotitve


        spolSpinner = (Spinner) findViewById(R.id.Spol);
        ArrayAdapter<CharSequence> adapterSpol = ArrayAdapter.createFromResource(this, R.array.spol_array, android.R.layout.simple_spinner_item);
        adapterSpol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spolSpinner.setAdapter(adapterSpol);
        spolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spol = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        statusJagenjcek = (TextView) findViewById(R.id.status);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void prikaziKotitev() {
        kotitev = getKotitevIntent.getIntExtra("kotitevID", 7);
        urlKotitve += "/" + kotitev;
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
                mamaTv.setText(mamaID);
                //Kotitev.setText(String.valueOf(kotitev));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };



    public void addJagenjcka(View view) {
        this.statusJagenjcek.setText("Posting to " + url);
        Toast.makeText(this, "Pošiljam podatke", Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("kotitevID", kotitev);
            jsonBody.put("idJagenjcka", JagenjcekID.getText());
            jsonBody.put("spol", spol);
            jsonBody.put("stanje", Stanje.getText());
            jsonBody.put("opombe", Opombe.getText());

            final String mRequestBody = jsonBody.toString();
            Log.d("Body", mRequestBody);

            statusJagenjcek.setText(mRequestBody);

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
            Toast.makeText(this, "Jagenjček je bil prijavljen v sistem Volos..", Toast.LENGTH_SHORT).show();
            addJagenjcka();
            finish();


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void dodajKotitve(){
        JsonArrayRequest request = new JsonArrayRequest(urlKotitve, jsonArrayListenerKotitev, errorListener) {
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



    private Response.Listener<JSONArray> jsonArrayListenerKotitev = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){
            ArrayList<String> data = new ArrayList<>();
            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    String kotitev = object.getString("kotitevID");
                    data.add(kotitev);

                } catch (JSONException e){
                    e.printStackTrace();
                    return;

                }
            }
            for (String row : data){
                kotitevList.add(row);
                Log.d("kotitevList", row);
            }
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

    public void addJagenjcka() {
        Intent intent = new Intent(this, AddJagenjcekActivity.class);
        intent.putExtra("kotitevID", kotitev);
        startActivity(intent);
    }

}
