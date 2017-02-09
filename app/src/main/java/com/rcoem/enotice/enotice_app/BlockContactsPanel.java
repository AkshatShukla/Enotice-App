package com.rcoem.enotice.enotice_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BlockContactsPanel extends AppCompatActivity {

    private TextView mPostTitle;
    private Button Approved;
    private Button Rejected;
    private boolean process;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_contacts_panel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Block/Unblock User");

        mPostTitle = (TextView) findViewById(R.id.username);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(str);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPostTitle.setText(dataSnapshot.child("name").getValue().toString().trim());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Approved = (Button) findViewById(R.id.Approve_button);
        process = true;
        Approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(process) {
                            mDatabase.child("block").setValue("Yes");
                            process = false;

                            Toast.makeText(BlockContactsPanel.this, "User Has been Blocked", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(BlockContactsPanel.this, BlockUserPlanel.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        Rejected = (Button) findViewById(R.id.Reject_button);
        Rejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(process) {
                            mDatabase.child("block").setValue("No");
                            process = false;

                            Toast.makeText(BlockContactsPanel.this, "User Has been Unblocked", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(BlockContactsPanel.this, BlockUserPlanel.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BlockContactsPanel.this, BlockUserPlanel.class);
        startActivity(intent);
    }
}

