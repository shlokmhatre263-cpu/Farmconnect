package com.example.farmconnect.marketplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyOrdersActivity extends AppCompatActivity {

    RecyclerView recyclerOrders;
    ProgressBar  progressOrders;
    TextView     tvNoOrders;

    final List<CropOrder> orders = new ArrayList<>();
    OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        progressOrders = findViewById(R.id.progressOrders);
        tvNoOrders     = findViewById(R.id.tvNoOrders);

        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(orders);
        recyclerOrders.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        progressOrders.setVisibility(View.VISIBLE);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance()
                .getReference("CropOrders")
                .orderByChild("buyerId")
                .equalTo(uid)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        orders.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            CropOrder order = ds.getValue(CropOrder.class);
                            if (order != null) {
                                order.setOrderId(ds.getKey());
                                orders.add(0, order);
                            }
                        }
                        progressOrders.setVisibility(View.GONE);
                        tvNoOrders.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        progressOrders.setVisibility(View.GONE);
                        Toast.makeText(MyOrdersActivity.this,
                                "Failed to load orders: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── Inner Adapter ──────────────────────────────────────────────────────
    static class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderVH> {

        final List<CropOrder> list;
        OrderAdapter(List<CropOrder> list) { this.list = list; }

        @NonNull
        @Override
        public OrderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order, parent, false);
            return new OrderVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderVH holder, int position) {
            CropOrder o = list.get(position);

            holder.tvOrderCrop.setText(o.getCropName());
            holder.tvOrderQty.setText(o.getQuantityKg() + " kg  ×  ₹" +
                    String.format("%.2f", o.getPricePerKg()));
            holder.tvOrderTotal.setText("Total: ₹ " + String.format("%.2f", o.getTotalAmount()));
            holder.tvOrderPayment.setText(o.getPaymentMethod() + "  (" + o.getPaymentStatus() + ")");
            holder.tvOrderStatus.setText("Status: " + o.getOrderStatus().toUpperCase());

            if (o.getTransactionId() != null) {
                holder.tvOrderTxn.setText("TXN: " + o.getTransactionId());
                holder.tvOrderTxn.setVisibility(View.VISIBLE);
            } else {
                holder.tvOrderTxn.setVisibility(View.GONE);
            }

            String date = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    .format(new Date(o.getTimestamp()));
            holder.tvOrderDate.setText(date);
        }

        @Override public int getItemCount() { return list.size(); }

        static class OrderVH extends RecyclerView.ViewHolder {
            TextView tvOrderCrop, tvOrderQty, tvOrderTotal,
                    tvOrderPayment, tvOrderStatus, tvOrderTxn, tvOrderDate;

            OrderVH(@NonNull View v) {
                super(v);
                tvOrderCrop    = v.findViewById(R.id.tvOrderCrop);
                tvOrderQty     = v.findViewById(R.id.tvOrderQty);
                tvOrderTotal   = v.findViewById(R.id.tvOrderTotal);
                tvOrderPayment = v.findViewById(R.id.tvOrderPayment);
                tvOrderStatus  = v.findViewById(R.id.tvOrderStatus);
                tvOrderTxn     = v.findViewById(R.id.tvOrderTxn);
                tvOrderDate    = v.findViewById(R.id.tvOrderDate);
            }
        }
    }
}