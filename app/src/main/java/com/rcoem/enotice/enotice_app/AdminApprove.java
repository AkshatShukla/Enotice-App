package com.rcoem.enotice.enotice_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AdminApprove extends AppCompatActivity {
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
        setContentView(R.layout.activity_admin_approve);
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
                            mDatabase.child("level").setValue(2);
                            process = false;

                            Toast.makeText(AdminApprove.this, "HOD", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AdminApprove.this, AccountAdminPanel.class);
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
                            mDatabase.child("level").setValue(1);
                            process = false;

                            Toast.makeText(AdminApprove.this, "Assistant Professor", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AdminApprove.this, AccountAdminPanel.class);
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
}
