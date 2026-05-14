package com.example.farmconnect.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;



import com.example.farmconnect.R;

import java.util.Locale;

public class LanguageSelectionActivity extends BaseActivity {

    Button btnEnglish, btnMarathi, btnHindi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        btnEnglish = findViewById(R.id.btnEnglish);
        btnMarathi = findViewById(R.id.btnMarathi);
        btnHindi = findViewById(R.id.btnHindi);

        btnEnglish.setOnClickListener(v -> setLanguage("en"));
        btnMarathi.setOnClickListener(v -> setLanguage("mr"));
        btnHindi.setOnClickListener(v -> setLanguage("hi"));
    }

    private void setLanguage(String langCode) {

        SharedPreferences prefs =
                getSharedPreferences("Settings", MODE_PRIVATE);

        prefs.edit().putString("App_Lang", langCode).apply();

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(
                config,
                getResources().getDisplayMetrics()
        );

        // 🔥 GO DIRECTLY TO HOME
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}