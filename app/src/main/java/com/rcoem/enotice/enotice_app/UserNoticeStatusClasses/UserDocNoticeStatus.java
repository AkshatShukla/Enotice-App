package com.rcoem.enotice.enotice_app.UserNoticeStatusClasses;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rcoem.enotice.enotice_app.R;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class UserDocNoticeStatus extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView mPostTitle;
    private TextView mPostDesc;
    private TextView mUsername;
    private TextView status;
    private TextView textStatus;
    private ImageView circularImageView;
    private TextView Date;
    private ImageButton deleteDocUserStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_doc_notice_status);
        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");

        circularImageView = (ImageView) findViewById(R.id.profileViewDocUserStatus);
        mUsername = (TextView) findViewById(R.id.profileNameDocUserStatus);
        Date = (TextView) findViewById(R.id.dateDocUserStatus);
        textStatus = (TextView) findViewById(R.id.textStatusDocUserStatus);
        status = (TextView) findViewById(R.id.statusDocUserStatus);
        mPostTitle = (TextView) findViewById(R.id.noticeTitleDocUserStatus);
        mPostDesc = (TextView) findViewById(R.id.noticeDescDocUserStatus);
        deleteDocUserStatus = (ImageButton) findViewById(R.id.downloadPdfDocUserStatus);

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

                if (dataSnapshot.hasChildren()) {
                    mUsername.setText(dataSnapshot.child("username").getValue().toString().trim());
                    mPostTitle.setText(dataSnapshot.child("title").getValue().toString().trim());
                    mPostDesc.setText(dataSnapshot.child("Desc").getValue().toString().trim());
                    String imageUri = dataSnapshot.child("images").getValue().toString().trim();
                    String url = dataSnapshot.child("profileImg").getValue().toString().trim();
                    Date.setText("on " + dataSnapshot.child("time").getValue().toString().trim());
                    Glide.with(UserDocNoticeStatus.this).load(url).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(circularImageView);
                    toolbar.setTitle(dataSnapshot.child("title").getValue().toString().trim());

                    String statusReport;
                    String m;
                    if (dataSnapshot.child("approved").getValue().toString().trim().equals("true")) {
                        m = "APPROVED and on Notice Board.";
                        statusReport = "Approved";
                    } else {
                        m = "DUE TO BE APPROVED by your Head of Department.";
                        statusReport = "Pending";
                    }
                    status.setText("Your following post is ".concat(m));

                    if (statusReport.equals("Approved")) {
                        textStatus.setText(statusReport);
                        textStatus.setBackgroundColor(getResources().getColor(R.color.unblocked));
                        textStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                    } else if (statusReport.equals("Pending")) {
                        textStatus.setText(statusReport);
                        textStatus.setBackgroundColor(getResources().getColor(R.color.md_amber_800));
                        textStatus.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }

                } else {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        deleteDocUserStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            String downloadLink = dataSnapshot.child("link").getValue().toString().trim();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadLink));
                            startActivity(browserIntent);
                        }
                        else {
                            Toasty.error(UserDocNoticeStatus.this,"File does not exist").show();
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
        Intent intent = new Intent(UserDocNoticeStatus.this, UserNoticeStatus.class);
        startActivity(intent);
    }
}
