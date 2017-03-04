package com.rcoem.enotice.enotice_app.AuthorityClasses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rcoem.enotice.enotice_app.R;
import com.squareup.picasso.Picasso;

public class AdminNotApproved extends AppCompatActivity {
    private TextView mPostTitle;
    private Button HOD;
    private Button AP;
    private TextView profileName;
    private TextView Date;
    private TextView status;
    private String desg;
    private ImageView circularImageView;
    private boolean process;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_not_approved);
        profileName = (TextView) findViewById(R.id.profileName);
        circularImageView = (ImageView) findViewById(R.id.profileImg);
        status = (TextView) findViewById(R.id.textStatus);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(str);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profileName.setText(dataSnapshot.child("name").getValue().toString().trim());
                String imageUrl = dataSnapshot.child("images").getValue().toString().trim();
                String level = dataSnapshot.child("level").getValue().toString().trim();
                if (level.equals("1")) {
                    desg = "AP";
                    status.setText(desg);
                }
                else {
                    desg = "HOD";
                    status.setText(desg);
                }
                Picasso.with(AdminNotApproved.this).load(imageUrl).placeholder(R.drawable.user).into(circularImageView);
                toolbar.setTitle("Access Rights of ".concat(dataSnapshot.child("name").getValue().toString().trim()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        HOD = (Button) findViewById(R.id.Approve_button);
        process = true;
        HOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (process) {
                            mDatabase.child("level").setValue(2);
                            mDatabase.child("DEST").setValue("HOD");
                            process = false;

                            Toast.makeText(AdminNotApproved.this, "HOD", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AdminNotApproved.this, AccountAdminPanel.class);
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

        AP = (Button) findViewById(R.id.Reject_button);
        AP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (process) {
                            mDatabase.child("level").setValue(1);
                            mDatabase.child("DEST").setValue("AP");
                            process = false;

                            Toast.makeText(AdminNotApproved.this, "Assistant Professor", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AdminNotApproved.this, AccountAdminPanel.class);
                            startActivity(intent);
                            finish();
                            ///
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
