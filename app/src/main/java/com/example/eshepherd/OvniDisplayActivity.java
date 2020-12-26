package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OvniDisplayActivity extends AppCompatActivity implements ListAdapterOvni.OnClickListener {

    private RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovni";
    RecyclerView recyclerView;
    Context ct;
    ArrayList<String> dataID = new ArrayList<>();
    ArrayList<String> dataDatum = new ArrayList<>();
    ListAdapterOvni listAdapterOvni;
    EditText searchView;
    CharSequence search = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ovni_display);

        searchView = findViewById(R.id.editText);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //ovni = (TextView) findViewById(R.id.ovni);
        ct = this;
        recyclerView = findViewById(R.id.recycler_view_ovni);
        listAdapterOvni = new ListAdapterOvni(ct, dataID, dataDatum, this);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                listAdapterOvni.getFilter().filter(charSequence);
                search = charSequence;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void prikaziOvne(){
        JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener);
        requestQueue.add(request);
    }

    Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject object = response.getJSONObject(i);
                    String ID  = object.getString("ovenID");
                    String CredaID  = object.getString("credaID");
                    if(CredaID.equals("0"))
                        continue;
                    String datumRojstva  = object.getString("datumRojstva");
                    if(!datumRojstva.equals("null"))
                        datumRojstva = datumRojstva.substring(0,10);
                    else
                        datumRojstva = "neznan";
                    dataID.add(ID);
                    dataDatum.add(datumRojstva);
                    recyclerView.setAdapter(listAdapterOvni);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ct));
                }catch (JSONException e){
                    e.printStackTrace();
                    return;
                }
            }
            /*                                                              DISPLAY Z TextView-om
            for(String row : data){
                String currentText = ovni.getText().toString();
                ovni.setText(currentText + "\n\n" + row);
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

    public void launchAddOven(View view) {
        Intent intent = new Intent(this, AddOvenActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRowClick(int position) {
        String id = dataID.get(position);
        Intent intent = new Intent(this, ShowOvenActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listAdapterOvni.Clear();
        prikaziOvne();
    }
}