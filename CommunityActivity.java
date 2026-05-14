package com.example.farmconnect.community;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import android.graphics.Bitmap;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.provider.MediaStore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommunityActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 200;

    EditText etMessage;
    Button btnPost, btnImage;
    ImageView imagePreview;
    RecyclerView recyclerView;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    CommunityAdapter adapter;
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        etMessage    = findViewById(R.id.etMessage);
        btnPost      = findViewById(R.id.btnPost);
        btnImage     = findViewById(R.id.btnImage);
        imagePreview = findViewById(R.id.imagePreview);
        recyclerView = findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference("CommunityPosts");
        storageReference  = FirebaseStorage.getInstance().getReference("CommunityImages");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommunityAdapter(this);
        recyclerView.setAdapter(adapter);

        btnPost.setOnClickListener(v -> postMessage());
        btnImage.setOnClickListener(v -> openGallery());

        adapter.listenForPosts();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imagePreview.setVisibility(View.VISIBLE);
            imagePreview.setImageURI(imageUri);
        }
    }

    private void postMessage() {
        String message = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            // ✅ TRANSLATED
            Toast.makeText(this, getString(R.string.write_something_first),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userName = (user != null && user.getEmail() != null)
                ? user.getEmail().split("@")[0] : "Farmer";

        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm",
                Locale.getDefault()).format(new Date());
        String imageBase64 = "";

        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                imageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            } catch (Exception e) {
                // ✅ TRANSLATED
                Toast.makeText(this, getString(R.string.image_error),
                        Toast.LENGTH_SHORT).show();
            }
        }

        CommunityPost post = new CommunityPost(
                userName, message, date, imageBase64, 0, 0);

        databaseReference.push().setValue(post)
                .addOnSuccessListener(aVoid -> resetFields());
    }

    private void resetFields() {
        etMessage.setText("");
        imageUri = null;
        imagePreview.setVisibility(View.GONE);
        // ✅ TRANSLATED
        Toast.makeText(this, getString(R.string.post_success),
                Toast.LENGTH_SHORT).show();
    }
}