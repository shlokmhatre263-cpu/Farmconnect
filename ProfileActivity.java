package com.example.farmconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.example.farmconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileActivity extends BaseActivity {

    TextView tvName, tvEmail;
    Button btnLogout;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            tvEmail.setText(user.getEmail());
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(user.getUid());

            ref.child("name").get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String name = snapshot.getValue(String.class);
                    tvName.setText(name);
                }
            }); // We can later improve with real name
        }

        btnLogout.setOnClickListener(v -> {

            mAuth.signOut();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}