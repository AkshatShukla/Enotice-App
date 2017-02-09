package com.rcoem.enotice.enotice_app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserStatusViewSinglePost extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView mPostTitle;
    private TextView mPostDesc;
    private TextView mUsername;
    private TextView mMsg;
    private ImageButton mViewImage;
    private Button Approved;
    private Button Rejected;
    private Button Share;
    private Uri mImageUri = null;
    private StorageReference mStoarge;
    private boolean process;

    Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status_view_single_post);
        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);

        mPostTitle = (TextView) findViewById(R.id.Post_title) ;
        mMsg = (TextView) findViewById(R.id.textView3) ;
        mPostDesc = (TextView) findViewById(R.id.Post_Desc);
        mUsername = (TextView) findViewById(R.id.username);
        mViewImage = (ImageButton) findViewById(R.id.select_image_ButtonUser);

        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(str);
        mStoarge = FirebaseStorage.getInstance().getReference();


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    mUsername.setText(dataSnapshot.child("username").getValue().toString().trim());
                    String m;
                    if (dataSnapshot.child("approved").getValue().toString().trim().equals("true")) {
                        m = "Approved and on Notice Board";
                    } else {
                        m = "Pending or Rejected";
                    }
                    mMsg.setText("Your following post is " + m);
                    mPostTitle.setText(dataSnapshot.child("title").getValue().toString().trim());
                    mPostDesc.setText(dataSnapshot.child("Desc").getValue().toString().trim());
                    String imageUrl = dataSnapshot.child("images").getValue().toString().trim();
                    Picasso.with(UserStatusViewSinglePost.this).load(imageUrl).into(mViewImage);
                    mActionBarToolbar.setTitle(dataSnapshot.child("title").getValue().toString().trim());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String imageUrl =  dataSnapshot.child("images").getValue().toString().trim();
                        viewImage(imageUrl);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void viewImage(String imageUrl) {
        // Toast.makeText(AdminSinglePost.this,imageUrl, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(UserStatusViewSinglePost.this,fullScreenImage.class);
        intent.putExtra("imageUrl",imageUrl);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserStatusViewSinglePost.this, UserNoticeStatus.class);
        startActivity(intent);
    }
}
