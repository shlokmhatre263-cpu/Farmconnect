package com.example.farmconnect.community;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmconnect.R;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {

    EditText etComment;
    Button btnSend;
    ListView listView;

    DatabaseReference commentRef;
    ArrayList<String> commentList;
    ArrayAdapter<String> adapter;

    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        etComment = findViewById(R.id.etComment);
        btnSend = findViewById(R.id.btnSend);
        listView = findViewById(R.id.listView);

        postId = getIntent().getStringExtra("postId");

        commentRef = FirebaseDatabase.getInstance()
                .getReference("CommunityPosts")
                .child(postId)
                .child("commentsList");

        commentList = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                commentList);

        listView.setAdapter(adapter);

        loadComments();

        btnSend.setOnClickListener(v -> addComment());
    }

    private void loadComments() {

        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                commentList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String comment = ds.getValue(String.class);
                    if (comment != null)
                        commentList.add(comment);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void addComment() {

        String text = etComment.getText().toString().trim();

        if (TextUtils.isEmpty(text)) return;

        commentRef.push().setValue(text);

        FirebaseDatabase.getInstance()
                .getReference("CommunityPosts")
                .child(postId)
                .child("comments")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        Integer value = currentData.getValue(Integer.class);
                        if (value == null) value = 0;
                        currentData.setValue(value + 1);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError error,
                                           boolean committed,
                                           DataSnapshot currentData) {}
                });

        etComment.setText("");
        Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show();
    }
}