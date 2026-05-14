package com.example.farmconnect.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.farmconnect.R;
import java.util.ArrayList;
import java.util.List;

public class SchemesActivity extends BaseActivity {

    RecyclerView recyclerView;
    SchemeAdapter adapter;
    List<SchemeModel> schemeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schemes);

        recyclerView = findViewById(R.id.recyclerSchemes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        schemeList = new ArrayList<>();

        schemeList.add(new SchemeModel(
                getString(R.string.pm_kisan),
                getString(R.string.pm_kisan_desc),
                "https://pmkisan.gov.in"
        ));
        schemeList.add(new SchemeModel(
                getString(R.string.pm_fasal_bima),
                getString(R.string.pm_fasal_bima_desc),
                "https://pmfby.gov.in"
        ));
        schemeList.add(new SchemeModel(
                getString(R.string.pm_sinchai),
                getString(R.string.pm_sinchai_desc),
                "https://pmksy.gov.in"
        ));
        schemeList.add(new SchemeModel(
                getString(R.string.kisan_credit_card),
                getString(R.string.kisan_credit_card_desc),
                "https://www.nabard.org/content1.aspx?id=572"
        ));

        adapter = new SchemeAdapter(schemeList);
        recyclerView.setAdapter(adapter);
    }
}