package com.example.farmconnect.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.farmconnect.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class WeatherActivity extends BaseActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 101;

    TextView tvCity, tvTemp, tvDesc, tvAdvice;
    FusedLocationProviderClient fusedLocationClient;
    String API_KEY = "dc3cd735616516427fa6f3177036ec1b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        tvCity   = findViewById(R.id.tvCity);
        tvTemp   = findViewById(R.id.tvTemp);
        tvDesc   = findViewById(R.id.tvDesc);
        tvAdvice = findViewById(R.id.tvAdvice);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        fetchWeather(location.getLatitude(), location.getLongitude());
                    } else {
                        // ✅ TRANSLATED
                        Toast.makeText(this,
                                getString(R.string.gps_turn_on),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fetchWeather(double lat, double lon) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getWeather(lat, lon, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call,
                                   Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse data = response.body();
                    float temperature = data.main.temp;

                    tvCity.setText(data.name);
                    tvTemp.setText(temperature + " °C");
                    tvDesc.setText(data.weather[0].description);

                    // ✅ ALL ADVISORY MESSAGES NOW TRANSLATED
                    if (temperature > 35) {
                        tvAdvice.setText(getString(R.string.advice_very_hot));
                    } else if (temperature > 25) {
                        tvAdvice.setText(getString(R.string.advice_warm));
                    } else if (temperature > 15) {
                        tvAdvice.setText(getString(R.string.advice_cool));
                    } else {
                        tvAdvice.setText(getString(R.string.advice_cold));
                    }
                } else {
                    // ✅ TRANSLATED
                    Toast.makeText(WeatherActivity.this,
                            getString(R.string.weather_error),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // ✅ TRANSLATED
                Toast.makeText(WeatherActivity.this,
                        getString(R.string.weather_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface WeatherService {
        @GET("weather")
        Call<WeatherResponse> getWeather(
                @Query("lat") double lat,
                @Query("lon") double lon,
                @Query("appid") String apiKey,
                @Query("units") String units);
    }

    public static class WeatherResponse {
        public Main main;
        public Weather[] weather;
        public String name;
        public static class Main { public float temp; }
        public static class Weather { public String description; }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                // ✅ TRANSLATED
                Toast.makeText(this,
                        getString(R.string.location_denied),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}