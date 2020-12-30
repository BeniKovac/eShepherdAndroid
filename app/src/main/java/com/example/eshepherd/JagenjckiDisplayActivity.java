package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JagenjckiDisplayActivity extends AppCompatActivity implements ListAdapterJagenjcki.OnClickListener {
    private RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Jagenjcki";
    RecyclerView recyclerView;
    TextView StevilkaJagenjckaTv;
    TextView SpolTv;
    Context ct;
    int order = 1;
    ArrayList<Integer> dataID, kotitevID;
    ArrayList<String> dataDrugiID;
    ArrayList<String> spolArray;
    boolean sortByDate = false;
    TextView NapisID;
    ListAdapterJagenjcki listAdapterJagenjcki;
    EditText searchView;
    CharSequence search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jagenjcki_display);

        StevilkaJagenjckaTv = findViewById(R.id.textView_IDtitle);
        SpolTv = findViewById(R.id.textView_SpolTitle);

        searchView = findViewById(R.id.editText);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //jagenjcki = (TextView) findViewById(R.id.jagenjcki);
        ct = this;
        recyclerView = findViewById(R.id.recycler_view_jagenjcki);
        spolArray  = new ArrayList<>();
        dataDrugiID = new ArrayList<>();
        dataID = new ArrayList<>();
        kotitevID = new ArrayList<>();
        NapisID = findViewById(R.id.textView_IDtitle);

        listAdapterJagenjcki = new ListAdapterJagenjcki(ct, dataDrugiID, spolArray, this);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                listAdapterJagenjcki.getFilter().filter(charSequence);
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

    public void prikaziJagenjcki(){
        JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener)
        {
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

    Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject object = response.getJSONObject(i);
                    Integer ID  = object.getInt("skritIdJagenjcka");
                    String drugiID = object.getString("idJagenjcka");
                    String spol  = object.getString("spol");
                    int kotitev = object.getInt("kotitevID"); // vse kotitevID
                    dataID.add(ID);
                    dataDrugiID.add(drugiID);
                    spolArray.add(spol);
                    kotitevID.add(kotitev);
                }catch (JSONException e){
                    e.printStackTrace();
                    return;
                }
            }
            if(sortByDate)
                bubbleSort(spolArray, dataDrugiID, dataID);
            else
                bubbleSort(dataDrugiID, spolArray, dataID);
            recyclerView.setAdapter(listAdapterJagenjcki);
            recyclerView.setLayoutManager(new LinearLayoutManager(ct));
        }
    };



    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

    public void bubbleSort(ArrayList<String> s1, ArrayList<String> s2, ArrayList<Integer> s3){
        String t;
        Integer tInt;
        System.out.println(s1.toString());
        System.out.println(s3.toString());
        int n = s1.size();
        boolean swapped = false;
        int lastswap = 0;
        int urejeniDel = 0;
        for (int j = 0; j < n; j++) {
            int i = n-1;
            while(i > urejeniDel) {
                if (compare(s1.get(i),s1.get(i - 1)) * order > 0) {
                    t = s1.get(i);
                    s1.set(i, s1.get(i - 1));
                    s1.set(i - 1, t);           //swapped s1 elements
                    t = s2.get(i);
                    s2.set(i, s2.get(i - 1));
                    s2.set(i - 1, t);       //swapped s2 elements
                    tInt = s3.get(i);
                    s3.set(i, s3.get(i - 1));
                    s3.set(i - 1, tInt);       //swapped s3 elements
                    swapped = true;
                    lastswap = i;
                }
                System.out.println(s1.toString());
                System.out.println(s3.toString());
                i--;

            }
            if(!swapped)
                break;
            urejeniDel = lastswap;
        }
    }

    public int compare(String str1, String str2){
        if(str1.equals("neznan"))
            return -1;
        if(str2.equals("neznan"))
            return 1;
        if (str1.equals("m") || str1.equals("ž")){
            if(str1.equals("m") && str2.equals("ž"))
                return 1;
            else
                return -1;
        }
        int test = 1;
        return Integer.compare(Integer.parseInt(str1), Integer.parseInt(str2));
    }

    public void ChangeDirection(View view) {
        order *= -1;
        if(view.getId() != NapisID.getId())
            sortByDate = true;
        else
            sortByDate = false;
        if(!sortByDate && order == 1) {
            StevilkaJagenjckaTv.setText("Številka jagenjcka ▼");
            SpolTv.setText("Spol ⇄");
        }
        else if(!sortByDate) {
            StevilkaJagenjckaTv.setText("Številka jagenjcka ▲");
            SpolTv.setText("Spol ⇄");
        }
        else if (sortByDate && order == 1) {
            StevilkaJagenjckaTv.setText("Številka jagenjcka ⇄");
            SpolTv.setText("Spol ▼");
        }
        else if (sortByDate) {
            StevilkaJagenjckaTv.setText("Številka jagenjcka ⇄");
            SpolTv.setText("Spol ▲");
        }
        if(sortByDate)
            bubbleSort(spolArray, dataDrugiID, dataID);
        else
            bubbleSort(dataDrugiID, spolArray, dataID);

        ListAdapterJagenjcki listAdapterJagenjcki = new ListAdapterJagenjcki(ct, dataDrugiID, spolArray, this);
        recyclerView.setAdapter(listAdapterJagenjcki);
        recyclerView.setLayoutManager(new LinearLayoutManager(ct));
    }

    public void launchAddJagenjcek(View view) {
        int max = 1;
        for (int i = 0; i < kotitevID.size(); i++) {
            if (kotitevID.get(i) > max)
                max = kotitevID.get(i);
        }
        Intent intent = new Intent(this, AddJagenjcekActivity.class);
        intent.putExtra("kotitevID", max); // avtomatsko dodaj zadnji kotitvi
        startActivity(intent);
    }

    @Override
    public void onRowClick(int position) {
        Integer id = dataID.get(position);
        Intent intent = new Intent(this, ShowJagenjcekActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listAdapterJagenjcki.Clear();
        dataID.clear();
        prikaziJagenjcki();
    }
}