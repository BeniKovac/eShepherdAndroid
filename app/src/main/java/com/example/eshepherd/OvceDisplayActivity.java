package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class OvceDisplayActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private TextView ovce;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovce";
    RecyclerView recyclerView;
    Context ct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ovce_display);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //ovce = (TextView) findViewById(R.id.ovce);
        ct = this;
        recyclerView = findViewById(R.id.recycler_view_ovce);
        prikaziOvce();
    }

    public void prikaziOvce(){
            JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener);
            requestQueue.add(request);
    }

    Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            ArrayList<String> dataID = new ArrayList<>();
            ArrayList<String> dataDatum = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject object = response.getJSONObject(i);
                    String ID  = object.getString("ovcaID");
                    if(ID.equals("/"))
                        continue;
                    String datumRojstva  = object.getString("datumRojstva");
                    if(!datumRojstva.equals("null"))
                        datumRojstva = datumRojstva.substring(0,10);
                    else
                        datumRojstva = "neznan";
                    dataID.add(ID);
                    dataDatum.add(datumRojstva);
                    ListAdapter listAdapter = new ListAdapter(ct, dataID, dataDatum);
                    recyclerView.setAdapter(listAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ct));
                }catch (JSONException e){
                    e.printStackTrace();
                    return;
                }
            }
            /*                                                              DISPLAY Z TextView-om
            for(String row : data){
                String currentText = ovce.getText().toString();
                ovce.setText(currentText + "\n\n" + row);
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

}