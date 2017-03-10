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

public class UserImageRejectStatus extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView mPostTitle;
    private TextView mPostDesc;
    private TextView mUsername;
    private TextView status;
    private TextView textStatus;
    private ImageView circularImageView;
    private TextView Date;
    private ImageButton mViewImage;
    private Button deleteImageReject;
    private StorageReference mStoarge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_image_reject_status);
        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");

        circularImageView = (ImageView) findViewById(R.id.profileViewImageReject);
        mUsername = (TextView) findViewById(R.id.profileNameImageReject);
        Date = (TextView) findViewById(R.id.dateImageReject);
        textStatus = (TextView) findViewById(R.id.textStatusImageReject);
        mViewImage = (ImageButton) findViewById(R.id.select_image_ButtonAdmin);
        status = (TextView) findViewById(R.id.statusImageReject);
        mPostTitle = (TextView) findViewById(R.id.noticeTitleImageReject);
        mPostDesc = (TextView) findViewById(R.id.noticeDescImageReject);
        deleteImageReject = (Button) findViewById(R.id.deleteImageReject);

        mStoarge = FirebaseStorage.getInstance().getReference();

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

                if (!UserImageRejectStatus.this.isFinishing()) {
                    if (dataSnapshot.hasChildren()) {
                        mUsername.setText(dataSnapshot.child("username").getValue().toString().trim());
                        mPostTitle.setText(dataSnapshot.child("title").getValue().toString().trim());
                        mPostDesc.setText(dataSnapshot.child("Desc").getValue().toString().trim());
                        String imageUri = dataSnapshot.child("images").getValue().toString().trim();
                        String url = dataSnapshot.child("profileImg").getValue().toString().trim();
                        Date.setText("on " + dataSnapshot.child("time").getValue().toString().trim());
                        Glide.with(UserImageRejectStatus.this).load(url).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(circularImageView);
                        Glide.with(UserImageRejectStatus.this).load(imageUri).diskCacheStrategy(DiskCacheStrategy.ALL).into(mViewImage);
                        toolbar.setTitle(dataSnapshot.child("title").getValue().toString().trim());

                        String m;
                        m = "REJECTED by your Head of Department.";
                        status.setText("Your following post has been ".concat(m));

                        textStatus.setText("Rejected");
                        textStatus.setBackgroundColor(getResources().getColor(R.color.blocked));
                        textStatus.setTextColor(getResources().getColor(R.color.colorWhite));

                    } else {
                        isFinishing();
                        //finish();
                    }
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

        deleteImageReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!UserImageRejectStatus.this.isFinishing()) {
                    new BottomDialog.Builder(UserImageRejectStatus.this)
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
                                    Toasty.success(UserImageRejectStatus.this, "Notice Deleted").show();
                                    mDatabase.removeValue();
                                    Intent intent = new Intent(UserImageRejectStatus.this, UserNoticeStatus.class);
                                    startActivity(intent);
                                }
                            }).show();
                }

            }
        });


    }

    private void viewImage(String imageUrl) {
        // Toast.makeText(AdminSinglePost.this,imageUrl, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(UserImageRejectStatus.this, fullScreenImage.class);
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserImageRejectStatus.this, UserNoticeStatus.class);
        startActivity(intent);
    }
}


