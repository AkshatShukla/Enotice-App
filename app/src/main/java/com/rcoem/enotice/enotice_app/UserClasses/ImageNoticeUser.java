package com.rcoem.enotice.enotice_app.UserClasses;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rcoem.enotice.enotice_app.AdminClasses.ImageNoticeAdmin;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.fullScreenImage;
import com.squareup.picasso.Picasso;

public class ImageNoticeUser extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView mPostTitle;
    private TextView mPostDesc;
    private TextView mUsername;
    private TextView mDate;
    private ImageButton mViewImage;
    private ImageView circularImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_notice_user);

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

        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");

        mPostTitle = (TextView) findViewById(R.id.Post_title_User);
        mPostDesc = (TextView) findViewById(R.id.Post_Desc_User);
        mUsername = (TextView) findViewById(R.id.usernameUser);
        mViewImage = (ImageButton) findViewById(R.id.select_image_ButtonUser);
        mDate = (TextView) findViewById(R.id.date_imageuser);
        circularImageView = (ImageView) findViewById(R.id.imageView);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(str);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    mUsername.setText(dataSnapshot.child("username").getValue().toString().trim());
                    mPostTitle.setText(dataSnapshot.child("title").getValue().toString().trim());
                    mPostDesc.setText(dataSnapshot.child("Desc").getValue().toString().trim());
                    String date = "on " + dataSnapshot.child("time").getValue().toString().trim();
                    mDate.setText(date);
                    String profilePic = dataSnapshot.child("profileImg").getValue().toString().trim();
                    String imageUrl = dataSnapshot.child("images").getValue().toString().trim();
                    //Picasso.with(ImageNoticeUser.this).load(imageUrl).into(mViewImage);

                    final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

                    Glide.with(ImageNoticeUser.this)
                            .load(imageUrl)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .thumbnail(0.5f)
                            .into(mViewImage);

                    Glide.with(ImageNoticeUser.this).load(profilePic).crossFade().into(circularImageView);

                    toolbar.setTitle(dataSnapshot.child("title").getValue().toString().trim());
                } else {
                    finish();
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
                        String imageUrl = dataSnapshot.child("images").getValue().toString().trim();
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
        Intent intent = new Intent(ImageNoticeUser.this, fullScreenImage.class);
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }
}

