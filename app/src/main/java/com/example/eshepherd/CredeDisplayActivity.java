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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CredeDisplayActivity  extends AppCompatActivity implements ListAdapterCrede.OnClickListener{

    private RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Crede";
    RecyclerView recyclerView;
    Context ct;
    ArrayList<String> dataID = new ArrayList<>();
    ArrayList<String> dataDatum = new ArrayList<>();
    ListAdapterCrede listAdapterCrede;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crede_display);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //crede = (TextView) findViewById(R.id.crede);
        ct = this;
        recyclerView = findViewById(R.id.recycler_view_crede);
        listAdapterCrede = new ListAdapterCrede(ct, dataID, dataDatum, this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void prikaziCrede(){
        JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener);
        requestQueue.add(request);
    }

    Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject object = response.getJSONObject(i);
                    String ID  = object.getString("credeID");
                    if(ID.equals("0"))
                        continue;
                    String datumRojstva  = object.getString("steviloOvc");
                    if(datumRojstva.equals("null"))
                        datumRojstva = "neznan";
                    dataID.add(ID);
                    dataDatum.add(datumRojstva);
                    recyclerView.setAdapter(listAdapterCrede);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ct));
                }catch (JSONException e){
                    e.printStackTrace();
                    return;
                }
            }
            /*                                                              DISPLAY Z TextView-om
            for(String row : data){
                String currentText = crede.getText().toString();
                crede.setText(currentText + "\n\n" + row);
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

    public void launchAddCreda(View view) {
        Intent intent = new Intent(this, AddCredaActivity.class);
        startActivity(intent);
    }
    @Override
    public void onRowClick(int position) {
        String id = dataID.get(position);
        Intent intent = new Intent(this, ShowCredaActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listAdapterCrede.Clear();
        prikaziCrede();
    }
}
