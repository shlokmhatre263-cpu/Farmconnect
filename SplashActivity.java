package com.example.farmconnect.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import com.example.farmconnect.R;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadLocale();
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {

            FirebaseUser user =
                    FirebaseAuth.getInstance().getCurrentUser();

            if (user == null) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }

            finish();

        }, 2000); // 2 seconds
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