package com.example.farmconnect.activities;

import android.view.LayoutInflater;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.farmconnect.R;
import java.util.List;

public class SchemeAdapter extends RecyclerView.Adapter<SchemeAdapter.ViewHolder> {

    List<SchemeModel> schemeList;

    public SchemeAdapter(List<SchemeModel> schemeList) {
        this.schemeList = schemeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scheme, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SchemeModel scheme = schemeList.get(position);

        holder.tvTitle.setText(scheme.getTitle());
        holder.tvDescription.setText(scheme.getDescription());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(
                    holder.itemView.getContext(),
                    SchemeDetailActivity.class
            );
            intent.putExtra("title",       scheme.getTitle());
            intent.putExtra("description", scheme.getDescription());
            intent.putExtra("url",         scheme.getUrl());  // ← pass URL
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return schemeList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle       = itemView.findViewById(R.id.tvSchemeTitle);
            tvDescription = itemView.findViewById(R.id.tvSchemeDescription);
        }
    }
}