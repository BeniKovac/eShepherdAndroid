package com.example.eshepherd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

public class SpecificOvenGonitve extends AppCompatActivity implements ListAdapterGonitve.OnClickListener {

    private RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Gonitve";
    RecyclerView recyclerView;
    Context ct;
    ArrayList<Integer> dataID = new ArrayList<>();
    ArrayList<String> dataDatum = new ArrayList<>();
    ArrayList<String> predvidenDatum = new ArrayList<>();
    ArrayList<String> dataOvcaID = new ArrayList<>();
    ListAdapterGonitve listAdaptergonitve;
    Intent intent;
    String specificOvenID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_oven_gonitve);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //gonitve = (TextView) findViewById(R.id.gonitve);
        ct = this;
        recyclerView = findViewById(R.id.recycler_view_gonitve);
        listAdaptergonitve = new ListAdapterGonitve(ct, dataOvcaID, dataDatum, predvidenDatum,this);
        intent = getIntent();
        specificOvenID = intent.getStringExtra("SpecificID");
        prikaziGonitve();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void prikaziGonitve(){
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
                    String specificovenID = object.getString("ovenID");
                    if(!specificovenID.equals(specificOvenID))
                        continue;
                    Integer ID  = object.getInt("gonitevID");
                    if(ID == null)
                        continue;
                    String ovenID = object.getString("ovenID");
                    String ovcaID = object.getString("ovcaID");
                    String datumRojstva  = object.getString("datumGonitve");
                    if(!datumRojstva.equals("null"))
                        datumRojstva = datumRojstva.substring(0,10);
                    else
                        datumRojstva = "neznan";
                    String predDatum = object.getString("predvidenaKotitev");
                    if(!predDatum.equals("null"))
                        predDatum = predDatum.substring(0,10);
                    else
                        predDatum = "neznan";
                    dataID.add(ID);
                    dataDatum.add(datumRojstva);
                    predvidenDatum.add(predDatum);
                    dataOvcaID.add(ovcaID);
                    recyclerView.setAdapter(listAdaptergonitve);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ct));
                }catch (JSONException e){
                    e.printStackTrace();
                    return;
                }
            }
            /*                                                              DISPLAY Z TextView-om
            for(String row : data){
                String currentText = gonitve.getText().toString();
                gonitve.setText(currentText + "\n\n" + row);
            }
             */
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

    public void launchAddGonitev(View view) {
        Intent intent = new Intent(this, AddGonitevActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRowClick(int position) {
        int id = dataID.get(position);
        Intent intent = new Intent(this, ShowGonitevActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }
}
