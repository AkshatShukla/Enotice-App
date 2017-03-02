package com.rcoem.enotice.enotice_app.AdminClasses;

import android.content.Intent;


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
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.fullScreenImage;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class ImageNoticeAdmin extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView mPostTitle;
    private TextView mPostDesc;
    private TextView mUsername;
    private TextView mDate;
    private Button delete;
    private ImageButton mViewImage;
    private ImageView circularImageView;
 //   private Button Approved;
  //  private Button Rejected;
    private Button Share;
  //  private Uri mImageUri = null;
    private StorageReference mStoarge;
    private boolean process;

    // Toolbar mActionBarToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_notice_admin);
        //mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);


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
        //getSupportActionBar().setTitle("Post");

        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");
        mPostTitle = (TextView) findViewById(R.id.Post_title_Admin);
        mPostDesc = (TextView) findViewById(R.id.Post_Desc_Admin);
        mUsername = (TextView) findViewById(R.id.usernameImage);
        mViewImage = (ImageButton) findViewById(R.id.imageViewAdmin);
        mDate = (TextView) findViewById(R.id.date_imageadmin);
        delete = (Button) findViewById(R.id.deleteButtonImage);
        circularImageView = (ImageView) findViewById(R.id.profileViewImage);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(str);
        mStoarge = FirebaseStorage.getInstance().getReference();
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
                    //Picasso.with(ImageNoticeAdmin.this).load(imageUrl).into(mViewImage);

                    final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

                    Glide.with(ImageNoticeAdmin.this)
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

                    Glide.with(ImageNoticeAdmin.this).load(profilePic).crossFade().into(circularImageView);
                    toolbar.setTitle(dataSnapshot.child("title").getValue().toString().trim());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new BottomDialog.Builder(ImageNoticeAdmin.this)
                        .setTitle("Delete Notice Permanently")
                        .setContent("Are you sure you want to remove this notice completely?")
                        .setPositiveText("Yes")
                        .setPositiveBackgroundColorResource(R.color.colorPrimary)
                        .setCancelable(false)
                        .setNegativeText("No")
                        .setPositiveTextColorResource(android.R.color.white)
                        //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
                        .onPositive(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(BottomDialog dialog) {
                                Toasty.success(ImageNoticeAdmin.this, "Notice Deleted").show();
                                mDatabase.removeValue();
                                Intent intent = new Intent(ImageNoticeAdmin.this, AccountActivityAdmin.class);
                                startActivity(intent);
                            }
                        }).show();

            }
        });
        mViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            String imageUrl = dataSnapshot.child("images").getValue().toString().trim();
                            viewImage(imageUrl);
                        } else {
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

    private void viewImage(String imageUrl) {
        // Toast.makeText(AdminSinglePost.this,imageUrl, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ImageNoticeAdmin.this, fullScreenImage.class);
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }
}

