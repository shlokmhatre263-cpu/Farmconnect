package com.example.farmconnect.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.farmconnect.R;

public class SchemeDetailActivity extends BaseActivity {

    TextView tvTitle, tvDescription, tvEligibility, tvDocuments, tvHowToApply;
    Button   btnApplyOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme_detail);

        tvTitle       = findViewById(R.id.tvDetailTitle);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvEligibility = findViewById(R.id.tvEligibility);
        tvDocuments   = findViewById(R.id.tvDocuments);
        tvHowToApply  = findViewById(R.id.tvHowToApply);
        btnApplyOnline= findViewById(R.id.btnApplyOnline);

        String title       = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String url         = getIntent().getStringExtra("url");

        tvTitle.setText(title);
        tvDescription.setText(description);

        if (title != null && title.equals(getString(R.string.pm_kisan))) {
            tvEligibility.setText(getString(R.string.eligibility_pm_kisan));
            tvDocuments.setText(getString(R.string.documents_pm_kisan));
            tvHowToApply.setText(getString(R.string.how_to_apply_pm_kisan));

        } else if (title != null && title.equals(getString(R.string.pm_fasal_bima))) {
            tvEligibility.setText(getString(R.string.eligibility_pm_fasal));
            tvDocuments.setText(getString(R.string.documents_pm_fasal));
            tvHowToApply.setText(getString(R.string.how_to_apply_pm_fasal));

        } else if (title != null && title.equals(getString(R.string.pm_sinchai))) {
            tvEligibility.setText(getString(R.string.eligibility_sinchai));
            tvDocuments.setText(getString(R.string.documents_sinchai));
            tvHowToApply.setText(getString(R.string.how_to_apply_sinchai));

        } else if (title != null && title.equals(getString(R.string.kisan_credit_card))) {
            tvEligibility.setText(getString(R.string.eligibility_kcc));
            tvDocuments.setText(getString(R.string.documents_kcc));
            tvHowToApply.setText(getString(R.string.how_to_apply_kcc));

        } else {
            tvEligibility.setText(getString(R.string.visit_agri_office));
            tvDocuments.setText(getString(R.string.carry_id_proof));
            tvHowToApply.setText(getString(R.string.contact_agri_dept));
        }

        // ✅ REAL Apply Online button — opens official government website
        if (url != null && !url.isEmpty()) {
            btnApplyOnline.setVisibility(View.VISIBLE);
            btnApplyOnline.setOnClickListener(v -> {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            });
        } else {
            btnApplyOnline.setVisibility(View.GONE);
        }
    }
}