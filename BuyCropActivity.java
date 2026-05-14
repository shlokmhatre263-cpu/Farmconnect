package com.example.farmconnect.marketplace;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmconnect.R;

public class BuyCropActivity extends AppCompatActivity {

    TextView   tvCropName, tvSellerInfo, tvPricePerKg, tvAvailable, tvTotalPrice;
    EditText   etQuantityToBuy, etDeliveryAddress;
    RadioGroup rgPaymentMethod;
    Button     btnProceedPayment;

    String listingId, cropName, cropType, sellerName, sellerId, location;
    double pricePerKg, availableQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_crop);

        readIntentData();
        bindViews();
        populateSummary();
        setupLiveTotalCalc();

        btnProceedPayment.setOnClickListener(v -> validateAndProceed());
    }

    private void readIntentData() {
        listingId    = getIntent().getStringExtra("listingId");
        cropName     = getIntent().getStringExtra("cropName");
        cropType     = getIntent().getStringExtra("cropType");
        pricePerKg   = getIntent().getDoubleExtra("pricePerKg", 0);
        availableQty = getIntent().getDoubleExtra("quantityKg", 0);
        sellerName   = getIntent().getStringExtra("sellerName");
        sellerId     = getIntent().getStringExtra("sellerId");
        location     = getIntent().getStringExtra("location");
    }

    private void bindViews() {
        tvCropName        = findViewById(R.id.tvBuyCropName);
        tvSellerInfo      = findViewById(R.id.tvBuySellerInfo);
        tvPricePerKg      = findViewById(R.id.tvBuyPricePerKg);
        tvAvailable       = findViewById(R.id.tvBuyAvailable);
        tvTotalPrice      = findViewById(R.id.tvBuyTotal);
        etQuantityToBuy   = findViewById(R.id.etBuyQuantity);
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress);
        rgPaymentMethod   = findViewById(R.id.rgPaymentMethod);
        btnProceedPayment = findViewById(R.id.btnProceedPayment);
    }

    private void populateSummary() {
        tvCropName.setText(cropName + "  (" + cropType + ")");
        tvSellerInfo.setText("Seller: " + sellerName + "   📍 " + location);
        tvPricePerKg.setText("₹ " + String.format("%.2f", pricePerKg) + " per kg");
        tvAvailable.setText("Available: " + availableQty + " kg");
        tvTotalPrice.setText("Total:  ₹ 0.00");
    }

    private void setupLiveTotalCalc() {
        etQuantityToBuy.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                try {
                    double qty   = Double.parseDouble(s.toString());
                    double total = qty * pricePerKg;
                    tvTotalPrice.setText("Total:  ₹ " + String.format("%.2f", total));
                } catch (NumberFormatException e) {
                    tvTotalPrice.setText("Total:  ₹ 0.00");
                }
            }
        });
    }

    private void validateAndProceed() {
        String qtyStr  = etQuantityToBuy.getText().toString().trim();
        String address = etDeliveryAddress.getText().toString().trim();

        if (TextUtils.isEmpty(qtyStr)) { etQuantityToBuy.setError("Enter quantity"); return; }

        double qty;
        try {
            qty = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            etQuantityToBuy.setError("Invalid number");
            return;
        }

        if (qty <= 0)          { etQuantityToBuy.setError("Must be greater than 0");              return; }
        if (qty > availableQty){ etQuantityToBuy.setError("Max available: " + availableQty + " kg"); return; }
        if (TextUtils.isEmpty(address)) { etDeliveryAddress.setError("Enter delivery address");   return; }

        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rb     = findViewById(selectedId);
        String      method = rb.getText().toString();
        double      total  = qty * pricePerKg;

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("listingId",       listingId);
        intent.putExtra("cropName",        cropName);
        intent.putExtra("sellerId",        sellerId);
        intent.putExtra("sellerName",      sellerName);
        intent.putExtra("quantityKg",      qty);
        intent.putExtra("pricePerKg",      pricePerKg);
        intent.putExtra("totalAmount",     total);
        intent.putExtra("paymentMethod",   method);
        intent.putExtra("deliveryAddress", address);
        startActivity(intent);
    }
}