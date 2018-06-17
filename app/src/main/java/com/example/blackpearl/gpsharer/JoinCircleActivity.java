package com.example.blackpearl.gpsharer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class JoinCircleActivity extends AppCompatActivity {

    Pinview pinview;
    Button submitBtn;
    static String pinviewHolder;


    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_circle);
        pinview = findViewById(R.id.pinview_id);
        submitBtn = findViewById(R.id.submitBtn_id);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference("Users");

        searchPin();

    }

    String mid;

    private void searchPin() {


        Bundle b = getIntent().getExtras();
        if (b != null) {
            mid = b.getString("id");

        }
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinviewHolder = pinview.getValue();
                Log.d("CodeHolder", pinviewHolder);
                mDatabaseReference.child("CircleCodes").orderByValue().equalTo(pinviewHolder).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot datasnap : dataSnapshot.getChildren()) {

                            String username = datasnap.getKey();
                            if (username != null) {
                                Log.d("Username", username);

                                Map<String, Object> map = new HashMap<>();
                                map.put(username, true);

                                mDatabaseReference.child(mid).child("Friends").updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(JoinCircleActivity.this, "Friend Added", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
