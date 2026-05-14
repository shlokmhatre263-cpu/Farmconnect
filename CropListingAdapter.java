package com.example.farmconnect.marketplace;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CropListingAdapter extends RecyclerView.Adapter<CropListingAdapter.ListingViewHolder> {

    private final List<CropListing> listings;
    private final Context           context;
    private final String            currentUserId;

    public CropListingAdapter(List<CropListing> listings, Context context, String currentUserId) {
        this.listings      = listings;
        this.context       = context;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_crop_listing, parent, false);
        return new ListingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        CropListing listing = listings.get(position);

        holder.tvCropName.setText(listing.getCropName());
        holder.tvCropType.setText(listing.getCropType());
        holder.tvPrice.setText("₹ " + String.format("%.2f", listing.getPricePerKg()) + " / kg");
        holder.tvQuantity.setText("Available: " + listing.getQuantityKg() + " kg");
        holder.tvSeller.setText("Seller: " + listing.getSellerName());
        holder.tvLocation.setText("📍 " + listing.getLocation());

        boolean isOwner = listing.getSellerId().equals(currentUserId);

        if (isOwner) {
            holder.btnAction.setText("Delete Listing");
            holder.btnAction.setBackgroundResource(R.drawable.red_button);
            holder.btnAction.setOnClickListener(v -> deleteListing(listing, position));
        } else {
            holder.btnAction.setText("Buy Now");
            holder.btnAction.setBackgroundResource(R.drawable.green_button);
            holder.btnAction.setOnClickListener(v -> openBuyScreen(listing));
        }
    }

    private void deleteListing(CropListing listing, int position) {
        FirebaseDatabase.getInstance()
                .getReference("CropListings")
                .child(listing.getListingId())
                .child("status")
                .setValue("deleted")
                .addOnSuccessListener(unused -> {
                    listings.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, listings.size());
                    Toast.makeText(context, "Listing removed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Delete failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void openBuyScreen(CropListing listing) {
        Intent intent = new Intent(context, BuyCropActivity.class);
        intent.putExtra("listingId",  listing.getListingId());
        intent.putExtra("cropName",   listing.getCropName());
        intent.putExtra("cropType",   listing.getCropType());
        intent.putExtra("pricePerKg", listing.getPricePerKg());
        intent.putExtra("quantityKg", listing.getQuantityKg());
        intent.putExtra("sellerName", listing.getSellerName());
        intent.putExtra("sellerId",   listing.getSellerId());
        intent.putExtra("location",   listing.getLocation());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() { return listings.size(); }

    static class ListingViewHolder extends RecyclerView.ViewHolder {
        TextView tvCropName, tvCropType, tvPrice, tvQuantity, tvSeller, tvLocation;
        Button   btnAction;

        ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCropName = itemView.findViewById(R.id.tvCropName);
            tvCropType = itemView.findViewById(R.id.tvCropType);
            tvPrice    = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSeller   = itemView.findViewById(R.id.tvSeller);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            btnAction  = itemView.findViewById(R.id.btnAction);
        }
    }
}