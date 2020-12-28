package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OvceDisplayActivity extends AppCompatActivity implements ListAdapterOvce.OnClickListener {

    private RequestQueue requestQueue;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Ovce";
    RecyclerView recyclerView;
    Context ct;
    int order = 1;
    ArrayList<String> dataID;
    ArrayList<String> dataDatum;
    TextView StevilkaOvceTv;
    TextView DatumRojstvaTv;
    boolean sortByDate = false;
    TextView NapisID;
    ListAdapterOvce listAdapterOvce;
    EditText searchView;
    CharSequence search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ovce_display);

        searchView = findViewById(R.id.editText);
        StevilkaOvceTv = findViewById(R.id.textView_IDtitle);
        DatumRojstvaTv = findViewById(R.id.textView_DatumTitle);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        ct = this;
        recyclerView = findViewById(R.id.recycler_view_ovce);
        dataDatum  = new ArrayList<>();
        dataID = new ArrayList<>();
        NapisID = findViewById(R.id.textView_IDtitle);

        listAdapterOvce = new ListAdapterOvce(ct, dataID, dataDatum, this);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    listAdapterOvce.getFilter().filter(charSequence);
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

    public void prikaziOvce(){
        JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener) {
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
                    String ID  = object.getString("ovcaID");
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
                }catch (JSONException e){
                    e.printStackTrace();
                    return;
                }
            }
            if(sortByDate)
                bubbleSort(dataDatum, dataID);
            else
                bubbleSort(dataID, dataDatum);
            recyclerView.setAdapter(listAdapterOvce);
            recyclerView.setLayoutManager(new LinearLayoutManager(ct));
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
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
        else
            sortByDate = false;
        order *= -1;
        if(!sortByDate && order == 1) {
            StevilkaOvceTv.setText("Številka ovce ▼");
            DatumRojstvaTv.setText("Datum Rojstva ⇄");
        }
        else if(!sortByDate) {
            StevilkaOvceTv.setText("Številka ovce ▲");
            DatumRojstvaTv.setText("Datum Rojstva ⇄");
        }
        else if (sortByDate && order == 1) {
            StevilkaOvceTv.setText("Številka ovce ⇄");
            DatumRojstvaTv.setText("Datum Rojstva ▼");
        }
        else if (sortByDate) {
            StevilkaOvceTv.setText("Številka ovce ⇄");
            DatumRojstvaTv.setText("Datum Rojstva ▲");
        }
        if(sortByDate)
            bubbleSort(dataDatum, dataID);
        else
            bubbleSort(dataID, dataDatum);
        ListAdapterOvce listAdapterOvce = new ListAdapterOvce(ct, dataID, dataDatum, this);
        recyclerView.setAdapter(listAdapterOvce);
        recyclerView.setLayoutManager(new LinearLayoutManager(ct));
    }

    public void launchAddOvca(View view) {
        Intent intent = new Intent(this, AddOvcaActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRowClick(int position) {
        String id = dataID.get(position);
        Intent intent = new Intent(this, ShowOvcaActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listAdapterOvce.Clear();
        prikaziOvce();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ovca_show_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.kotitev_icon:
                Log.d("kliknjena ikona", "za neaktivne ovce");
                showCreda0();

                return true;
            default:
                return false;
        }
    }

    public void showCreda0() {
        Intent intent = new Intent(this, SpecificCredaOvce.class);
        Log.d("creda", "0");
        intent.putExtra("SpecificID", "0");
        startActivity(intent);
    }
}