
package android.example.healthasty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  {
    RelativeLayout relativeLayout;
    LinearLayout linearLayout;
    ProgressBar progressBar;
    TextInputEditText text;
    TextView text1,text2,text3;
    ImageView image1,image2,image3;
    RecyclerView recyclerView;
    private ArrayList<WeatherModel> weatherModel;
    private WeatherAdapter adapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityNam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //This code is used to hide status bar and action bar from screen and make visible activity on whole screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        relativeLayout = findViewById(R.id.relative_layout);
        linearLayout = findViewById(R.id.linear_layout);
        progressBar = findViewById(R.id.main_progress);
        text = findViewById(R.id.input_city);
        text1 = findViewById(R.id.city_name);
        text2 = findViewById(R.id.temperature);
        text3 = findViewById(R.id.condition);
        image1 = findViewById(R.id.temp_image);
        image2 = findViewById(R.id.search_image);
        image3 = findViewById(R.id.black_shade);
        recyclerView = findViewById(R.id.recycler_view);
        weatherModel = new ArrayList<>();
        adapter = new WeatherAdapter(this,weatherModel);
        recyclerView.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION
            },PERMISSION_CODE);

        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        cityNam = getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(cityNam);

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = text.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Enter city name",Toast.LENGTH_SHORT).show();
                }
                else{
                    text1.setText(cityNam);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "Permissions granted...", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MainActivity.this, "Please allowed the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            for(Address addr : addresses){
                if(addr != null){
                    String city = addr.getLocality();
                    if(city != null && !city.equals("")){
                        cityName = city;

                    }
                    else{
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this,"City not found",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return cityName;
    }
    private void getWeatherInfo(String cityName){
        String url = "http://api.weatherapi.com/v1/forecast.json?key=64f7637aa6d547e388973422231903&q=" +cityName+ "&days=1&aqi=yes&alerts=yes";
        text1.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
            /**
             * Called when a response is received.
             *
             * @param response
             */
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                weatherModel.clear();
                try {
                    String temp = response.getJSONObject("current").getString("temp_c");
                    text2.setText(temp + "℃");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    text3.setText(condition);
                    String conditionImage = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionImage)).into(image1);

                    if (isDay == 1) {
                        //Morning background
                        Picasso.get().load(R.drawable.day_image).into(image3);
                    }
                    //Night background
                    else {
                        Picasso.get().load(R.drawable.night_image).into(image3);
                    }
                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");
                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");

                        weatherModel.add(new WeatherModel(time, temper, img, wind));

                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }


        },new Response.ErrorListener(){

            /**
             * Callback method that an error has been occurred with the provided error code and optional
             * user-readable message.
             *
             * @param error
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                Log.d("TAG",error.toString());
                Log.d("Error.Response", error.toString());

                error.printStackTrace();
            }
        });


           /* @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                weatherModel.clear();
                try {
                    Toast.makeText(MainActivity.this, "This is not error", Toast.LENGTH_SHORT).show();
                    String temp = response.getJSONObject("current").getString("temp_c");
                    text2.setText(temp+"℃");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    text3.setText(condition);
                    String conditionImage = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    Picasso.get().load("http:".concat(conditionImage)).into(image1);

                    if(isDay==1){
                        //Morning background
                        Picasso.get().load("https://marketplace.canva.com/MADGyXh6h4g/4/thumbnail_large/canva-beautiful-blue-sky-background-MADGyXh6h4g.jpg").into(image3);
                    }
                        //Night background
                    else{
                        Picasso.get().load("http://wallsdesk.com/wp-content/uploads/2016/11/Night-Sky-Moon-HD-Background.jpg").into(image3);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");
                    for(int i=0; i<hourArray.length(); i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");

                        weatherModel.add(new WeatherModel(time,temper,img,wind));

                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {;
                    e.printStackTrace();
                }
            }

            }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Enter valid city name",Toast.LENGTH_SHORT).show();
            }
        });*/
        requestQueue.add(jsonObjectRequest);
    }
}