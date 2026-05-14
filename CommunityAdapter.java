package com.example.farmconnect.community;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.R;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private Context context;
    private List<CommunityPost> postList;
    private List<String> postIds;
    private DatabaseReference databaseReference;

    public CommunityAdapter(Context context) {
        this.context = context;
        postList  = new ArrayList<>();
        postIds   = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("CommunityPosts");
    }

    public void listenForPosts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                postList.clear();
                postIds.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CommunityPost post = ds.getValue(CommunityPost.class);
                    if (post != null) {
                        postList.add(0, post);
                        postIds.add(0, ds.getKey());
                    }
                }
                notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommunityPost post   = postList.get(position);
        String        postId = postIds.get(position);

        holder.tvUser.setText(post.userName);
        holder.tvMessage.setText(post.message);
        holder.tvDate.setText(post.date);

        if (post.imageBase64 != null && !post.imageBase64.isEmpty()) {
            holder.imagePost.setVisibility(View.VISIBLE);
            byte[] decodedBytes = Base64.decode(post.imageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    decodedBytes, 0, decodedBytes.length);
            holder.imagePost.setImageBitmap(bitmap);
        } else {
            holder.imagePost.setVisibility(View.GONE);
        }

        // ✅ Like count — TRANSLATED
        holder.btnLike.setText(context.getString(R.string.like_count, post.likes));
        holder.btnLike.setOnClickListener(v ->
                databaseReference.child(postId).child("likes")
                        .setValue(post.likes + 1));

        // ✅ Comment count — TRANSLATED
        holder.btnComment.setText(context.getString(R.string.comment_count, post.comments));
        holder.btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", postId);
            context.startActivity(intent);
        });

        // ✅ Delete — TRANSLATED
        holder.btnDelete.setOnClickListener(v ->
                databaseReference.child(postId).removeValue()
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(context,
                                        context.getString(R.string.post_deleted),
                                        Toast.LENGTH_SHORT).show()));
    }

    @Override
    public int getItemCount() { return postList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView  tvUser, tvMessage, tvDate;
        ImageView imagePost, btnDelete;
        Button    btnLike, btnComment;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUser     = itemView.findViewById(R.id.tvUser);
            tvMessage  = itemView.findViewById(R.id.tvMessage);
            tvDate     = itemView.findViewById(R.id.tvDate);
            imagePost  = itemView.findViewById(R.id.imagePost);
            btnLike    = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnDelete  = itemView.findViewById(R.id.btnDelete);
        }
    }
}