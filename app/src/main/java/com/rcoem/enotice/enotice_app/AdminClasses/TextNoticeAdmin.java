package com.rcoem.enotice.enotice_app.AdminClasses;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.rcoem.enotice.enotice_app.R;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class TextNoticeAdmin extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView mPostTitle;
    private TextView mPostDesc;
    private TextView mUsername;
    private TextView Date;
    private Button delete;
    private ImageButton mViewImage;
    private Button Approved;
    private ImageView circularImageView;
    private Button Rejected;
    private Button Share;
    private Uri mImageUri = null;
    private StorageReference mStoarge;
    private boolean process;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_notice_admin);
        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");
        mPostTitle = (TextView) findViewById(R.id.Edit_Title_field1) ;
        mPostDesc = (TextView) findViewById(R.id.Edit_description_field1);
        mUsername = (TextView) findViewById(R.id.profileName);
        circularImageView = (ImageView) findViewById(R.id.imageView);
        Date = (TextView) findViewById(R.id.date);
      //  mViewImage = (ImageButton) findViewById(R.id.select_image_ButtonAdmin);
        delete = (Button) findViewById(R.id.button2);

        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(str);
       // mStoarge = FirebaseStorage.getInstance().getReference();
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


        final AlertDialog.Builder builder1 = new AlertDialog.Builder(TextNoticeAdmin.this);
        builder1.setMessage("Do yo want to reject and remove this Notice?");
        builder1.setCancelable(true);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()) {
                    mUsername.setText(dataSnapshot.child("username").getValue().toString().trim());
                    mPostTitle.setText(dataSnapshot.child("title").getValue().toString().trim());
                    mPostDesc.setText(dataSnapshot.child("Desc").getValue().toString().trim());
                    String url = dataSnapshot.child("profileImg").getValue().toString().trim();
                    String date = "on " + dataSnapshot.child("time").getValue().toString().trim();
                    Date.setText(date);
                    Glide.with(TextNoticeAdmin.this).load(url).crossFade().into(circularImageView);
                    toolbar.setTitle(dataSnapshot.child("title").getValue().toString().trim());
                }
                else {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new BottomDialog.Builder(TextNoticeAdmin.this)
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
                                Toasty.success(TextNoticeAdmin.this,"Notice Deleted").show();
                                mDatabase.removeValue();
                                Intent intent = new Intent(TextNoticeAdmin.this, AccountActivityAdmin.class);
                                startActivity(intent);
                            }
                        }).show();

            }
        });


    }
}
