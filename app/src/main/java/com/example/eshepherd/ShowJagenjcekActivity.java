package com.example.eshepherd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ShowJagenjcekActivity extends AppCompatActivity {

    private Integer iskanJagenjcek;
    private String url = "https://eshepherd-dev.azurewebsites.net/api/v1/Jagenjcki";
    private RequestQueue requestQueue;
    private TextView jagenjcekIDTv, kotitevIDTv, spolTv;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_jagenjcek);
        //BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
        //        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(navView, navController);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        this.jagenjcekIDTv = findViewById(R.id.JagenjcekID);
        this.kotitevIDTv = findViewById(R.id.KotitevID);
        this.spolTv = findViewById(R.id.Spol);

        intent = getIntent();
        iskanJagenjcek = intent.getIntExtra("ID", 0);

        showJagenjcek(iskanJagenjcek);
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
}