package com.example.farmconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.cardview.widget.CardView;

import com.example.farmconnect.R;
import com.example.farmconnect.community.CommunityActivity;
import com.example.farmconnect.marketplace.MarketplaceActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends BaseActivity {

    // Register Fields
    EditText nameEdit, emailEdit, passwordEdit;
    Button registerBtn;
    ProgressBar progress;

    // Dashboard Fields
    TextView tvGreeting;
    CardView cardWeather, cardDisease, cardCommunity, cardSchemes, cardMarketplace;
    BottomNavigationView bottomNav;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // IF USER LOGGED IN → SHOW DASHBOARD
        if (user != null) {

            setContentView(R.layout.activity_main);

            tvGreeting      = findViewById(R.id.tvGreeting);
            cardWeather     = findViewById(R.id.cardWeather);
            cardDisease     = findViewById(R.id.cardDisease);
            cardCommunity   = findViewById(R.id.cardCommunity);
            cardSchemes     = findViewById(R.id.cardSchemes);
            cardMarketplace = findViewById(R.id.cardMarketplace);
            bottomNav       = findViewById(R.id.bottomNav);

            tvGreeting.setText("Welcome, " + user.getEmail());

            cardWeather.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, WeatherActivity.class)));

            cardDisease.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, DiseaseActivity.class)));

            cardCommunity.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, CommunityActivity.class)));

            cardSchemes.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, SchemesActivity.class)));

            cardMarketplace.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, MarketplaceActivity.class)));

            bottomNav.setOnItemSelectedListener(item -> {

                if (item.getItemId() == R.id.nav_home) {
                    return true;
                }
                if (item.getItemId() == R.id.nav_weather) {
                    startActivity(new Intent(this, WeatherActivity.class));
                    return true;
                }
                if (item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    return true;
                }
                return false;
            });

        } else {
            // IF USER NOT LOGGED IN → SHOW REGISTER SCREEN
            setContentView(R.layout.activity_register);

            nameEdit     = findViewById(R.id.nameEdit);
            emailEdit    = findViewById(R.id.emailEdit);
            passwordEdit = findViewById(R.id.passwordEdit);
            registerBtn  = findViewById(R.id.registerBtn);
            progress     = findViewById(R.id.progressBar);

            registerBtn.setOnClickListener(v -> registerUser());
        }
    }

    private void registerUser() {

        String name     = nameEdit.getText().toString().trim();
        String email    = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameEdit.setError(getString(R.string.name_required));
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError(getString(R.string.email_required));
            return;
        }
        if (password.length() < 6) {
            passwordEdit.setError(getString(R.string.password_length));
            return;
        }

        progress.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        DatabaseReference ref = FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(user.getUid());
                        ref.child("name").setValue(name);
                        ref.child("email").setValue(email);
                    }

                    progress.setVisibility(View.GONE);
                    startActivity(new Intent(MainActivity.this, LanguageSelectionActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}