package com.example.farmconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;



import com.example.farmconnect.R;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class RegisterActivity extends BaseActivity {

    EditText nameEdit, emailEdit, passwordEdit;
    Button registerBtn;
    FirebaseAuth auth;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        nameEdit = findViewById(R.id.nameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        registerBtn = findViewById(R.id.registerBtn);
        progress = findViewById(R.id.progressBar);

        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = nameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
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
                                .getReference("Users");

                        ref.child(user.getUid()).child("name").setValue(name);
                        ref.child(user.getUid()).child("email").setValue(email);
                    }

                    progress.setVisibility(View.GONE);

                    startActivity(new Intent(RegisterActivity.this, LanguageSelectionActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}