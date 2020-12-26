package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class ShowJagenjcekActivity extends AppCompatActivity {

    private Integer iskanJagenjcek;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Jagenjcki";
    private RequestQueue requestQueue;
    private TextView jagenjcekIDTv, kotitevIDTv, spolTv;
    BottomNavigationView navigationView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_jagenjcek);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.jagenjcekIDTv = findViewById(R.id.JagenjcekID);
        this.kotitevIDTv = findViewById(R.id.KotitevID);
        this.spolTv = findViewById(R.id.Spol);

        intent = getIntent();
        if(intent != null) {
            iskanJagenjcek = intent.getIntExtra("ID", 0); // treba prenest ovco prek intentov!
        }else{
            iskanJagenjcek = savedInstanceState.getInt("ID");
        }
        showJagenjcek(iskanJagenjcek);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showJagenjcek(Integer iskanJagenjcek) {
        url += "/" + iskanJagenjcek; // sestavi pravi url
        JsonObjectRequest request = new JsonObjectRequest(url, null, jsonObjectListener, errorListener);
        requestQueue.add(request);
    }


    private Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            try {
                String jagenjcek = response.getString("idJagenjcka");
                String kotitev = response.getString("kotitevID");
                String spol = response.getString("spol");

                jagenjcekIDTv.setText(jagenjcek);
                kotitevIDTv.setText(kotitev);
                spolTv.setText(spol);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

    public void editJagenjcek(View view) {
        Intent intent = new Intent(this, EditJagenjcekActivity.class);
        intent.putExtra("ID", iskanJagenjcek);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ID", iskanJagenjcek);
    }

}