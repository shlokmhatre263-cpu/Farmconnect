package com.example.farmconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import com.example.farmconnect.R;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class LoginActivity extends BaseActivity {

    EditText emailEdit, passwordEdit;
    Button loginBtn, googleLoginBtn;
    TextView registerText;

    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        loginBtn = findViewById(R.id.loginBtn);
        googleLoginBtn = findViewById(R.id.googleLoginBtn);
        registerText = findViewById(R.id.registerText);

        // ---------- GOOGLE CONFIG ----------
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleLoginBtn.setOnClickListener(v ->
                googleLauncher.launch(googleSignInClient.getSignInIntent())
        );

        loginBtn.setOnClickListener(v -> loginUser());

        registerText.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    // ================= GOOGLE LOGIN =================
    private final ActivityResultLauncher<Intent> googleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

                if (result.getData() == null) return;

                try {
                    GoogleSignInAccount account =
                            GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                                    .getResult(ApiException.class);

                    if (account == null) return;

                    AuthCredential credential =
                            GoogleAuthProvider.getCredential(account.getIdToken(), null);

                    auth.signInWithCredential(credential)
                            .addOnSuccessListener(authResult -> {

                                FirebaseUser user = auth.getCurrentUser();
                                if (user == null) return;

                                DatabaseReference ref =
                                        FirebaseDatabase.getInstance()
                                                .getReference("Users")
                                                .child(user.getUid());

                                ref.child("name").setValue(user.getDisplayName());
                                ref.child("email").setValue(user.getEmail());

                                // 🔥 CLEAR BACKSTACK PROPERLY
                                Intent intent = new Intent(LoginActivity.this,
                                        LanguageSelectionActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            e.getMessage(),
                                            Toast.LENGTH_SHORT).show());

                } catch (ApiException e) {
                    Toast.makeText(this,
                            "Google sign-in failed",
                            Toast.LENGTH_SHORT).show();
                }
            });

    // ================= EMAIL LOGIN =================
    private void loginUser() {

        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    Intent intent = new Intent(LoginActivity.this,
                            LanguageSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}