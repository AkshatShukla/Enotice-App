package com.rcoem.enotice.enotice_app;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Single_Post extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private TextView mPostTitle;
    private TextView mPostDesc;
    private ImageButton mViewImage;
    private Button Approved;
    private Button Rejected;
    private Button Share;
    private Uri mImageUri = null;
    private StorageReference mStoarge;
    private boolean process;
    RelativeLayout ri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");

        mPostTitle = (TextView) findViewById(R.id.Edit_Title_field1) ;
        mPostDesc = (TextView) findViewById(R.id.Edit_description_field1);
        mViewImage = (ImageButton) findViewById(R.id.select_image_Button1);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(str);
        mStoarge = FirebaseStorage.getInstance().getReference();
        mPostDesc.setText(str);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPostTitle.setText(dataSnapshot.child("title").getValue().toString().trim());
                mPostDesc.setText(dataSnapshot.child("Desc").getValue().toString().trim());

                String imageUrl =  dataSnapshot.child("images").getValue().toString().trim();
                Picasso.with(Single_Post.this).load(imageUrl).into(mViewImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //To view image fullscreen
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

        Approved = (Button) findViewById(R.id.Approve_button);
        process = true;
        Approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(process) {
                            mDatabase.child("approved").setValue("true");
                            process = false;

                            Toast.makeText(Single_Post.this, "Your post is approved", Toast.LENGTH_LONG).show();
                            //mDatabase.child(str).removeValue();
                            Intent intent = new Intent(Single_Post.this, AccountActivityAdmin.class);
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
                            mDatabase.child("approved").setValue("false");
                            process = false;

                            Toast.makeText(Single_Post.this, "Your post is Rejected", Toast.LENGTH_LONG).show();
                            //mDatabase.child(str).removeValue();
                            Intent intent = new Intent(Single_Post.this, AccountActivityAdmin.class);
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

    private void viewImage(String imageUrl) {
        // Toast.makeText(AdminSinglePost.this,imageUrl, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Single_Post.this,fullScreenImage.class);
        intent.putExtra("imageUrl",imageUrl);
        startActivity(intent);
    }
}
