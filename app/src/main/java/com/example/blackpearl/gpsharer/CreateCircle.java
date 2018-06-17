package com.example.blackpearl.gpsharer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateCircle extends AppCompatActivity {

    TextView circleCodeTv;
    Button shareBtn;

    //Database Reference
    DatabaseReference mDatabaseReference;
    FirebaseDatabase mDatabase;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_circle);
        circleCodeTv = findViewById(R.id.userCircleCode_id);
        shareBtn = findViewById(R.id.shareBtn_id);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            id = b.getString("id");

        }
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference("Users");

        fetchCode();
        onClickShare();


    }


    String code;

    private void fetchCode() {
        mDatabaseReference.child(id).child("CircleCode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                code = (String) dataSnapshot.getValue();
                circleCodeTv.setText(code);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void onClickShare() {
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Check out my location on this app: Add me with my CircleCode" + code);
                startActivity(sendIntent);
            }
        });
    }


}
