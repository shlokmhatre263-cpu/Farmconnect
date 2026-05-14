package com.example.farmconnect.marketplace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MarketplaceActivity extends AppCompatActivity {

    TabLayout            tabLayout;
    RecyclerView         recyclerView;
    FloatingActionButton fabSell;
    ProgressBar          progressBar;
    TextView             tvEmpty;
    Spinner              spinnerFilter;

    CropListingAdapter      adapter;
    final List<CropListing> allListings       = new ArrayList<>();
    final List<CropListing> displayedListings = new ArrayList<>();

    String currentUserId;
    int    currentTab = 0;

    // ✅ Crop type keys — used for Firebase matching (never change these)
    static final String[] CROP_TYPE_KEYS = {
            "all_types", "Wheat", "Rice", "Vegetables",
            "Fruits", "Pulses", "Cotton", "Sugarcane", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        initViews();
        setupTabs();
        setupFilterSpinner();
        loadListingsFromFirebase();

        fabSell.setOnClickListener(v ->
                startActivity(new Intent(this, SellCropActivity.class)));
    }

    private void initViews() {
        tabLayout     = findViewById(R.id.tabLayout);
        recyclerView  = findViewById(R.id.recyclerMarketplace);
        fabSell       = findViewById(R.id.fabSell);
        progressBar   = findViewById(R.id.progressMarket);
        tvEmpty       = findViewById(R.id.tvMarketEmpty);
        spinnerFilter = findViewById(R.id.spinnerCropFilter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CropListingAdapter(displayedListings, this, currentUserId);
        recyclerView.setAdapter(adapter);
    }

    private void setupTabs() {
        // ✅ Tab names are now translated using getString()
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_browse)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_my_listings)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_my_orders)));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                if (currentTab == 2) {
                    startActivity(new Intent(MarketplaceActivity.this, MyOrdersActivity.class));
                    tabLayout.selectTab(tabLayout.getTabAt(0));
                    currentTab = 0;
                    return;
                }
                applyFilter(spinnerFilter.getSelectedItemPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupFilterSpinner() {
        // ✅ Spinner labels are now translated using getString()
        String[] cropTypeLabels = {
                getString(R.string.crop_filter_all),
                getString(R.string.crop_filter_wheat),
                getString(R.string.crop_filter_rice),
                getString(R.string.crop_filter_vegetables),
                getString(R.string.crop_filter_fruits),
                getString(R.string.crop_filter_pulses),
                getString(R.string.crop_filter_cotton),
                getString(R.string.crop_filter_sugarcane),
                getString(R.string.crop_filter_other)
        };

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, cropTypeLabels);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                applyFilter(pos);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void loadListingsFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance()
                .getReference("CropListings")
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        allListings.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            CropListing listing = ds.getValue(CropListing.class);
                            if (listing != null) {
                                listing.setListingId(ds.getKey());
                                if ("available".equals(listing.getStatus())) {
                                    allListings.add(0, listing);
                                }
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        applyFilter(spinnerFilter.getSelectedItemPosition());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MarketplaceActivity.this,
                                "Failed to load listings", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void applyFilter(int filterIndex) {
        displayedListings.clear();
        for (CropListing listing : allListings) {
            boolean tabMatch = (currentTab == 0) ||
                    listing.getSellerId().equals(currentUserId);

            // ✅ Filter uses CROP_TYPE_KEYS (English) for Firebase matching
            // Index 0 = All Types = show everything
            boolean typeMatch = (filterIndex == 0) ||
                    listing.getCropType().equals(CROP_TYPE_KEYS[filterIndex]);

            if (tabMatch && typeMatch) displayedListings.add(listing);
        }
        tvEmpty.setVisibility(displayedListings.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadListingsFromFirebase();
    }
}