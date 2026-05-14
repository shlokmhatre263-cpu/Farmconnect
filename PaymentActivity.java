package com.example.farmconnect.marketplace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class PaymentActivity extends AppCompatActivity {

    // Summary
    TextView tvOrderSummary, tvOrderTotal, tvPayMethodLabel;

    // UPI
    LinearLayout layoutUPI;
    TextView     tvUpiId;
    EditText     etUpiPin;
    Button       btnPayUPI;

    // Card
    LinearLayout layoutCard;
    EditText     etCardNumber, etCardExpiry, etCardCvv, etCardName;
    Button       btnPayCard;

    // COD
    LinearLayout layoutCOD;
    Button       btnConfirmCOD;

    // Wallet
    LinearLayout layoutWallet;
    TextView     tvWalletBalance;
    Button       btnPayWallet;

    // Common
    ProgressBar  progressPayment;
    LinearLayout layoutSuccess;
    TextView     tvSuccessOrderId;
    Button       btnGoOrders;

    // Intent data
    String listingId, cropName, sellerId, sellerName, paymentMethod, deliveryAddress;
    double quantityKg, pricePerKg, totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        readIntentData();
        bindViews();
        showOrderSummary();
        showPaymentPanel();
    }

    private void readIntentData() {
        listingId       = getIntent().getStringExtra("listingId");
        cropName        = getIntent().getStringExtra("cropName");
        sellerId        = getIntent().getStringExtra("sellerId");
        sellerName      = getIntent().getStringExtra("sellerName");
        paymentMethod   = getIntent().getStringExtra("paymentMethod");
        deliveryAddress = getIntent().getStringExtra("deliveryAddress");
        quantityKg      = getIntent().getDoubleExtra("quantityKg", 0);
        pricePerKg      = getIntent().getDoubleExtra("pricePerKg", 0);
        totalAmount     = getIntent().getDoubleExtra("totalAmount", 0);
    }

    private void bindViews() {
        tvOrderSummary   = findViewById(R.id.tvOrderSummary);
        tvOrderTotal     = findViewById(R.id.tvOrderTotal);
        tvPayMethodLabel = findViewById(R.id.tvPayMethodLabel);

        layoutUPI = findViewById(R.id.layoutUPI);
        tvUpiId   = findViewById(R.id.tvUpiId);
        etUpiPin  = findViewById(R.id.etUpiPin);
        btnPayUPI = findViewById(R.id.btnPayUPI);

        layoutCard   = findViewById(R.id.layoutCard);
        etCardNumber = findViewById(R.id.etCardNumber);
        etCardExpiry = findViewById(R.id.etCardExpiry);
        etCardCvv    = findViewById(R.id.etCardCvv);
        etCardName   = findViewById(R.id.etCardName);
        btnPayCard   = findViewById(R.id.btnPayCard);

        layoutCOD     = findViewById(R.id.layoutCOD);
        btnConfirmCOD = findViewById(R.id.btnConfirmCOD);

        layoutWallet    = findViewById(R.id.layoutWallet);
        tvWalletBalance = findViewById(R.id.tvWalletBalance);
        btnPayWallet    = findViewById(R.id.btnPayWallet);

        progressPayment  = findViewById(R.id.progressPayment);
        layoutSuccess    = findViewById(R.id.layoutSuccess);
        tvSuccessOrderId = findViewById(R.id.tvSuccessOrderId);
        btnGoOrders      = findViewById(R.id.btnGoOrders);
    }

    @SuppressLint("SetTextI18n")
    private void showOrderSummary() {
        tvOrderSummary.setText(cropName + "  ×  " + quantityKg + " kg");
        tvOrderTotal.setText("₹ " + String.format("%.2f", totalAmount));
        tvPayMethodLabel.setText("Paying via: " + paymentMethod);
    }

    private void showPaymentPanel() {
        layoutUPI.setVisibility(View.GONE);
        layoutCard.setVisibility(View.GONE);
        layoutCOD.setVisibility(View.GONE);
        layoutWallet.setVisibility(View.GONE);

        if (paymentMethod == null) return;

        if (paymentMethod.contains("UPI")) {
            layoutUPI.setVisibility(View.VISIBLE);
            tvUpiId.setText("Pay to:  farmconnect@upi");
            btnPayUPI.setOnClickListener(v -> {
                if (TextUtils.isEmpty(etUpiPin.getText())) {
                    etUpiPin.setError("Enter UPI PIN");
                    return;
                }
                simulatePayment();
            });

        } else if (paymentMethod.contains("Card")) {
            layoutCard.setVisibility(View.VISIBLE);
            btnPayCard.setOnClickListener(v -> {
                if (!validateCardFields()) return;
                simulatePayment();
            });

        } else if (paymentMethod.contains("Cash")) {
            layoutCOD.setVisibility(View.VISIBLE);
            btnConfirmCOD.setOnClickListener(v -> simulatePayment());

        } else if (paymentMethod.contains("Wallet")) {
            layoutWallet.setVisibility(View.VISIBLE);
            tvWalletBalance.setText("FarmWallet Balance:  ₹ 10,000.00");
            btnPayWallet.setOnClickListener(v -> simulatePayment());

        } else {
            simulatePayment();
        }
    }

    private boolean validateCardFields() {
        String num    = etCardNumber.getText().toString().trim();
        String expiry = etCardExpiry.getText().toString().trim();
        String cvv    = etCardCvv.getText().toString().trim();
        String name   = etCardName.getText().toString().trim();

        if (num.length() < 16)       { etCardNumber.setError("Enter valid 16-digit number"); return false; }
        if (expiry.length() < 4)     { etCardExpiry.setError("Enter expiry MM/YY");          return false; }
        if (cvv.length() < 3)        { etCardCvv.setError("Enter 3-digit CVV");              return false; }
        if (TextUtils.isEmpty(name)) { etCardName.setError("Enter card holder name");        return false; }
        return true;
    }

    private void simulatePayment() {
        layoutUPI.setVisibility(View.GONE);
        layoutCard.setVisibility(View.GONE);
        layoutCOD.setVisibility(View.GONE);
        layoutWallet.setVisibility(View.GONE);
        progressPayment.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(this::saveOrderToFirebase, 2500);
    }

    private void saveOrderToFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String buyerName = snapshot.child("name").getValue(String.class);
                        if (TextUtils.isEmpty(buyerName)) {
                            buyerName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        }

                        String txnId = "TXN" + UUID.randomUUID()
                                .toString().replace("-", "")
                                .substring(0, 10).toUpperCase();

                        CropOrder order = new CropOrder(
                                listingId, uid, buyerName,
                                sellerId, sellerName, cropName,
                                quantityKg, pricePerKg,
                                paymentMethod, deliveryAddress);
                        order.setTransactionId(txnId);

                        DatabaseReference ordersRef = FirebaseDatabase.getInstance()
                                .getReference("CropOrders");
                        String key = ordersRef.push().getKey();
                        if (key == null) {
                            showPaymentError("Could not generate order ID");
                            return;
                        }

                        order.setOrderId(key);
                        ordersRef.child(key).setValue(order)
                                .addOnSuccessListener(unused -> {
                                    FirebaseDatabase.getInstance()
                                            .getReference("CropListings")
                                            .child(listingId)
                                            .child("status")
                                            .setValue("sold");
                                    showSuccessScreen(key, txnId);
                                })
                                .addOnFailureListener(e -> showPaymentError(e.getMessage()));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        showPaymentError(error.getMessage());
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void showSuccessScreen(String orderId, String txnId) {
        progressPayment.setVisibility(View.GONE);
        layoutSuccess.setVisibility(View.VISIBLE);
        tvSuccessOrderId.setText("Order ID:       " + orderId + "\nTransaction:  " + txnId);
        btnGoOrders.setOnClickListener(v -> {
            startActivity(new Intent(PaymentActivity.this, MyOrdersActivity.class));
            finish();
        });
    }

    private void showPaymentError(String msg) {
        progressPayment.setVisibility(View.GONE);
        Toast.makeText(this, "Payment failed: " + msg, Toast.LENGTH_LONG).show();
        showPaymentPanel();
    }
}