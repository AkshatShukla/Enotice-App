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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserRejectSinglePost extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView mPostTitle;
    private TextView mPostDesc;
    private TextView mUsername;
    private Button delete;
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
        setContentView(R.layout.activity_user_reject_single_post);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");
        mPostTitle = (TextView) findViewById(R.id.Post_title_Admin) ;
        mPostDesc = (TextView) findViewById(R.id.Post_Desc_Admin);
        mUsername = (TextView) findViewById(R.id.usernameAdmin);
        mViewImage = (ImageButton) findViewById(R.id.select_image_ButtonAdmin);
        delete = (Button) findViewById(R.id.button2);

        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(str);
        mStoarge = FirebaseStorage.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()
                        ) {
                    mUsername.setText(dataSnapshot.child("username").getValue().toString().trim());
                    mPostTitle.setText(dataSnapshot.child("title").getValue().toString().trim());
                    mPostDesc.setText(dataSnapshot.child("Desc").getValue().toString().trim());
                    String imageUrl = dataSnapshot.child("images").getValue().toString().trim();
                    Picasso.with(UserRejectSinglePost.this).load(imageUrl).into(mViewImage);
                    mActionBarToolbar.setTitle(dataSnapshot.child("title").getValue().toString().trim());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserRejectSinglePost.this, "User Notice Removed", Toast.LENGTH_LONG).show();
                mDatabase.removeValue();
                Intent intent = new Intent(UserRejectSinglePost.this, AccountActivityUser.class);
                startActivity(intent);
            }
        });
        mViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()) {
                            String imageUrl = dataSnapshot.child("images").getValue().toString().trim();
                            viewImage(imageUrl);
                        }
                        else{
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
        Intent intent = new Intent(UserRejectSinglePost.this,fullScreenImage.class);
        intent.putExtra("imageUrl",imageUrl);
        startActivity(intent);
    }
}
