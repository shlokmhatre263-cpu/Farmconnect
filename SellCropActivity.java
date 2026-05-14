package com.example.farmconnect.marketplace;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SellCropActivity extends AppCompatActivity {

    EditText    etCropName, etPricePerKg, etQuantityKg, etLocation, etDescription;
    Spinner     spinnerCropType;
    Button      btnPostListing;
    ProgressBar progressBar;

    static final String[] CROP_TYPES = {
            "Wheat", "Rice", "Vegetables", "Fruits",
            "Pulses", "Cotton", "Sugarcane", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_crop);

        etCropName      = findViewById(R.id.etCropName);
        etPricePerKg    = findViewById(R.id.etPricePerKg);
        etQuantityKg    = findViewById(R.id.etQuantityKg);
        etLocation      = findViewById(R.id.etLocation);
        etDescription   = findViewById(R.id.etDescription);
        spinnerCropType = findViewById(R.id.spinnerCropType);
        btnPostListing  = findViewById(R.id.btnPostListing);
        progressBar     = findViewById(R.id.progressSell);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, CROP_TYPES);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCropType.setAdapter(typeAdapter);

        btnPostListing.setOnClickListener(v -> validateAndPost());
    }

    private void validateAndPost() {
        String cropName    = etCropName.getText().toString().trim();
        String priceStr    = etPricePerKg.getText().toString().trim();
        String quantityStr = etQuantityKg.getText().toString().trim();
        String location    = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String cropType    = spinnerCropType.getSelectedItem().toString();

        if (TextUtils.isEmpty(cropName))    { etCropName.setError("Enter crop name");        return; }
        if (TextUtils.isEmpty(priceStr))    { etPricePerKg.setError("Enter price per kg");   return; }
        if (TextUtils.isEmpty(quantityStr)) { etQuantityKg.setError("Enter quantity in kg"); return; }
        if (TextUtils.isEmpty(location))    { etLocation.setError("Enter your location");    return; }

        double price, quantity;
        try {
            price    = Double.parseDouble(priceStr);
            quantity = Double.parseDouble(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price or quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (price <= 0)    { etPricePerKg.setError("Price must be > 0");    return; }
        if (quantity <= 0) { etQuantityKg.setError("Quantity must be > 0"); return; }

        postListing(cropName, cropType, price, quantity, location, description);
    }

    private void postListing(String cropName, String cropType, double price,
                             double quantity, String location, String description) {
        progressBar.setVisibility(View.VISIBLE);
        btnPostListing.setEnabled(false);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String sellerName = snapshot.child("name").getValue(String.class);
                        if (TextUtils.isEmpty(sellerName)) sellerName = "Farmer";

                        CropListing listing = new CropListing(
                                uid, sellerName, cropName, cropType,
                                price, quantity, location, description);

                        DatabaseReference listingsRef = FirebaseDatabase.getInstance()
                                .getReference("CropListings");
                        String key = listingsRef.push().getKey();
                        if (key == null) { showError("Failed to generate key"); return; }

                        listing.setListingId(key);
                        listingsRef.child(key).setValue(listing)
                                .addOnSuccessListener(unused -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(SellCropActivity.this,
                                            "✅ Listing posted!", Toast.LENGTH_LONG).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> showError(e.getMessage()));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        showError(error.getMessage());
                    }
                });
    }

    private void showError(String msg) {
        progressBar.setVisibility(View.GONE);
        btnPostListing.setEnabled(true);
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
    }
}