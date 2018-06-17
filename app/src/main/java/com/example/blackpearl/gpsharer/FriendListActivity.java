package com.example.blackpearl.gpsharer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    private ListView listView;


    //Database
    DatabaseReference mDatabaseReference;
    FirebaseDatabase mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        listView = findViewById(R.id.friendsListView_id);


        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference("Users");

        getList();
        //setting recycler view


    }

    String id;
    String selectedItem;
    ArrayList<String> list = new ArrayList<>();
    Boolean yesClicked = true;
    Boolean startnavigating = true;
    private boolean yesStop = true;

    private void getList() {

        Bundle b = getIntent().getExtras();
        if (b != null) {
            id = b.getString("id");
            Log.d("FriendListId", "This is a " + id);


        }
        if (id != null) {
            mDatabaseReference.child(id).child("Friends").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot datasnap : dataSnapshot.getChildren()) {

                        String name = (String) datasnap.getKey();
                        list.add(name);
                        Log.d("Username", name);

                    }
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, list);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            selectedItem = (String) parent.getItemAtPosition(position);
                            Intent intent = new Intent(FriendListActivity.this, MapsActivity2.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("islistitemclicked", yesClicked);
                            Log.d("yesClicked", yesClicked.toString());
                            bundle.putString("selecteditem", selectedItem);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Id is null", Toast.LENGTH_SHORT).show();
        }
    }

}
