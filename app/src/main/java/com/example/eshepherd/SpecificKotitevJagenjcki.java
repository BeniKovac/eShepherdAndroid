package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SpecificKotitevJagenjcki extends AppCompatActivity implements ListAdapterJagenjcki.OnClickListener {
    private RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Jagenjcki";
    RecyclerView recyclerView;
    Context ct;
    int order = 1;
    ArrayList<Integer> dataID;
    ArrayList<String> dataDrugiID;
    ArrayList<String> dataDatum;
    boolean sortByDate = false;
    TextView NapisID;
    ListAdapterJagenjcki listAdapterJagenjcki;
    Intent intent;
    String specificKotitevID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_kotitev_jagenjcki);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //jagenjcki = (TextView) findViewById(R.id.jagenjcki);
        ct = this;
        recyclerView = findViewById(R.id.recycler_view_jagenjcki);
        dataDatum  = new ArrayList<>();
        dataDrugiID = new ArrayList<>();
        dataID = new ArrayList<>();
        NapisID = findViewById(R.id.textView_IDtitle);
        intent = getIntent();
        specificKotitevID = intent.getStringExtra("SpecificID");
        listAdapterJagenjcki = new ListAdapterJagenjcki(ct, dataDrugiID, dataDatum, this);
        prikaziJagenjcki();
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
                    String kotitevID  = object.getString("kotitevID").toString();
                    if(kotitevID.equals("null"))
                        kotitevID = "neznan";
                    if(!kotitevID.equals(specificKotitevID))
                        continue;
                    Integer ID  = object.getInt("skritIdJagenjcka");
                    String drugiID = object.getString("idJagenjcka");
                    String spol  = object.getString("spol");
                    dataID.add(ID);
                    dataDrugiID.add(drugiID);
                    dataDatum.add(kotitevID);
                }catch (JSONException e){
                    e.printStackTrace();
                    return;
                }
            }
            if(sortByDate)
                bubbleSort(dataDatum, dataDrugiID);
            else
                bubbleSort(dataDrugiID, dataDatum);
            recyclerView.setAdapter(listAdapterJagenjcki);
            recyclerView.setLayoutManager(new LinearLayoutManager(ct));
            /*                                                              DISPLAY Z TextView-om
            for(String row : data){
                String currentText = jagenjcki.getText().toString();
                jagenjcki.setText(currentText + "\n\n" + row);
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

    public void bubbleSort(ArrayList<String> s1, ArrayList<String> s2){
        String t;
        int n = s1.size();
        boolean swapped = false;
        int lastswap = 0;
        int urejeniDel = 0;
        for (int j = 0; j < n; j++) {
            int i = n-1;
            while(i > urejeniDel) {
                int test=1;
                String str = s1.get(i);
                String str2 = s1.get(i-1);
                if (compare(s1.get(i),s1.get(i - 1)) * order > 0) {
                    t = s1.get(i);
                    s1.set(i, s1.get(i - 1));
                    s1.set(i - 1, t);           //swapped s1 elements
                    t = s2.get(i);
                    s2.set(i, s2.get(i - 1));
                    s2.set(i - 1, t);           //swapped s2 elements
                    swapped = true;
                    lastswap = i;
                }
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
        if (date.isValidDate(str1)) {
            try {
                Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(str1);
                Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(str2);
                if(date1.after(date2))
                    return 1;
                else if(date1.before(date2))
                    return -1;
                else
                    return 0;
            }catch (ParseException Pe){
                System.out.println(Pe.getMessage());
            }
        }
        int test = 1;
        return Integer.compare(Integer.parseInt(str1), Integer.parseInt(str2));
    }

    public void ChangeDirection(View view) {
        if(view.getId() != NapisID.getId())
            sortByDate = true;
        order *= -1;
        if(sortByDate)
            bubbleSort(dataDatum, dataDrugiID);
        else
            bubbleSort(dataDrugiID, dataDatum);
        recyclerView.setAdapter(listAdapterJagenjcki);
        recyclerView.setLayoutManager(new LinearLayoutManager(ct));
    }

    public void launchAddOvca(View view) {
        Intent intent = new Intent(this, AddOvcaActivity.class);
        startActivity(intent);
    }

    public void launchAddJagenjcek(View view) {
        Intent intent = new Intent(this, AddJagenjcekActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRowClick(int position) {
        Integer id = dataID.get(position);
        Intent intent = new Intent(this, ShowJagenjcekActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }
}