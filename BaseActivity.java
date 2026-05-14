package com.example.farmconnect.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        super.onCreate(savedInstanceState);
    }

    private void loadLocale() {

        SharedPreferences prefs =
                getSharedPreferences("Settings", MODE_PRIVATE);

        String language = prefs.getString("App_Lang", "en");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(
                config,
                getResources().getDisplayMetrics()
        );
    }
}