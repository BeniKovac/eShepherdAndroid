package com.example.eshepherd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpecificOvcaKotitve extends AppCompatActivity implements ListAdapterKotitve.OnClickListener {

    private RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Kotitve";
    RecyclerView recyclerView;
    Context ct;
    ArrayList<Integer> dataID;
    ArrayList<String> dataDatum;
    ArrayList<String> dataStMladih;
    ArrayList<String> dataOvcaID;
    ListAdapterKotitve listAdapterkotitve;
    Intent intent;
    String specificOvcaID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_ovca_kotitve);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //kotitve = (TextView) findViewById(R.id.kotitve);
        ct = this;
        recyclerView = findViewById(R.id.recycler_view_kotitve);
        dataID = new ArrayList<>();
        dataStMladih = new ArrayList<>();
        dataDatum = new ArrayList<>();
        dataOvcaID = new ArrayList<>();

        intent = getIntent();
        specificOvcaID = intent.getStringExtra("SpecificID");

        listAdapterkotitve = new ListAdapterKotitve(ct, dataStMladih, dataDatum, this);
        prikaziKotitve();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void prikaziKotitve(){
        JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener)
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

    Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject object = response.getJSONObject(i);
                    String ovcaID = object.getString("ovcaID");
                    if(!ovcaID.equals(specificOvcaID))
                        continue;
                    Integer ID  = object.getInt("kotitevID");

                    if(ID.equals("/"))
                        continue;
                    String datumRojstva  = object.getString("datumKotitve");
                    if(!datumRojstva.equals("null"))
                        datumRojstva = datumRojstva.substring(0,10);
                    else
                        datumRojstva = "neznan";
                    String OvcaID = object.getString("ovcaID");
                    Integer stMladih  = object.getInt("steviloMladih");
                    dataID.add(ID);
                    dataDatum.add(datumRojstva);
                    dataStMladih.add(Integer.toString(stMladih));
                    dataOvcaID.add(OvcaID);
                    recyclerView.setAdapter(listAdapterkotitve);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ct));
                }catch (JSONException e){
                    e.printStackTrace();
                    return;
                }
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

    public void launchAddKotitev(View view) {
        Intent intent = new Intent(this, AddKotitevActivity.class);
        startActivity(intent);
    }
    public void showKotitev(View view) {
        Intent intent = new Intent(this, ShowKotitevActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRowClick(int position) {
        int id = dataID.get(position);
        Intent intent = new Intent(this, ShowKotitevActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }
}
