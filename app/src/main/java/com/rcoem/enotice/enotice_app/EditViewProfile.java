package com.rcoem.enotice.enotice_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
//import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rcoem.enotice.enotice_app.UserClasses.ImageNoticeUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dmax.dialog.SpotsDialog;


public class EditViewProfile extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private StorageReference mStorage;
    private ProgressDialog mprogress;
    int flag = 0;
    private DatabaseReference mDataUser;
    TextView txt_desig,dept_disp;
    private DatabaseReference mDatabase1;
    private boolean zoomOut =  false;
    private FirebaseAuth mAuth;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar111);
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
        getSupportActionBar().setTitle("Profile");


        mAuth = FirebaseAuth.getInstance();
        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users");
        txt_desig = (TextView)findViewById(R.id.des_ok_input);
        dept_disp = (TextView)findViewById(R.id.dept_display);
        mStorage = FirebaseStorage.getInstance().getReference();
        mprogress =  new ProgressDialog(this);
        final AlertDialog dialog1 = new SpotsDialog(this, R.style.CustomProgress);
        //dialog1.show();


        String path  ="photos/"+mAuth.getCurrentUser().getUid().toString();
        try {
            final File localFile ;

            localFile = File.createTempFile("images", "jpg");
            mDatabase1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // txt_desig.setText(dataSnapshot.getValue().toString());
                    // Toast.makeText(getApplicationContext(),dataSnapshot.getValue().toString(),Toast.LENGTH_LONG).show();
                    if(dataSnapshot.hasChildren()) {
                        TextView txt_name = (TextView) findViewById(R.id.name_input);
                        txt_name.setText(dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("name").getValue().toString().trim());
                        String chk = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("level").getValue().toString().trim();
                        dept_disp.setText(dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("department").getValue().toString().trim());
                        if (chk.equalsIgnoreCase("2")) {
                            txt_desig.setText("Head of Dept.");

                        } else if (chk.equalsIgnoreCase("1")) {
                            txt_desig.setText("Assistant professor");

                        }
                        try {

                            ImageView circularImageView = (ImageView) findViewById(R.id.imageView_imagggggggg);
                            String url = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("images").getValue().toString().trim();

                            Glide.with(getApplicationContext()).load(url)
                                    .crossFade()
                                    .thumbnail(0.5f)
                                    .bitmapTransform(new CircleTransform(getApplicationContext()))
                                    .into(circularImageView);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    else {
                        finish();
                    }

                }
                // mprogress.dismiss();

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // System.out.println("The read failed: " + databaseError.getCode());
                    Toast.makeText(getApplicationContext(),databaseError.toString(),Toast.LENGTH_LONG).show();

                }
            });





           // mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users");

/*
          final   ImageView imageView = (ImageView) findViewById(R.id.imageView_imagggggggg);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDatabase1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                        //    String imageUrl = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("images").getValue().toString().trim();

                            String url = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("images").getValue().toString().trim();

                            Intent intent = new Intent(getApplicationContext(), fullScreenImage.class);
                            intent.putExtra("imageUrl", url);
                            startActivity(intent);
                           // Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),databaseError.getMessage().toString(),Toast.LENGTH_LONG).show();


                        }
                    });
                }
            });
*/













            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
               String name = user.getDisplayName();
                Uri photoUrl = user.getPhotoUrl();
                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

                TextView txt_email = (TextView)findViewById(R.id.email_input);
                txt_email.setText(user.getEmail());
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUrl);

                final String user_id = mAuth.getCurrentUser().getUid();

            }

        }catch (Exception e)
        {
           e.printStackTrace();
        }
        Button email_edit = (Button)findViewById(R.id.email_edit);
        email_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.input_diaglog_profile_input, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        //mprogress.setMessage("Updating");
                                      /// mprogress.show();
                                        dialog1.show();

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        user.updateEmail(userInput.getText().toString().trim())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            //   Log.d(TAG, "User email address updated.");
                                                            TextView txt  = (TextView)findViewById(R.id.email_input);
                                                            txt.setText(userInput.getText().toString().trim());
                                                            Toast.makeText(context, "Email updated succcessfully", Toast.LENGTH_SHORT).show();
                                                          dialog1.dismiss();
                                                            //  mprogress.dismiss();
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(context, task.getException().toString(), Toast.LENGTH_LONG).show();
                                                         //   mprogress.dismiss();
dialog1.dismiss();
                                                        }
                                                    }
                                                });



                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();

            }
        });
        Button name_edit = (Button)findViewById(R.id.name_edit);
        name_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.input_diaglog_profile_input, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                       // mprogress.setMessage("Updating");
                                        //mprogress.show();
                                        dialog1.show();
                                        final String user_id = mAuth.getCurrentUser().getUid();


                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(userInput.getText().toString())


                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            //Log.d(TAG, "User profile updated.");
                                                            mDatabase1.child(user_id).child("name").setValue(userInput.getText().toString());
                                                            // mDatabase1.child(user_id).child("image").setValue(downloadUri);
                                                            TextView txt  = (TextView)findViewById(R.id.name_input);
                                                            txt.setText(userInput.getText().toString());
                                                            Toast.makeText(context, "Name updated succcessfully", Toast.LENGTH_SHORT).show();
                                                            //mprogress.dismiss();
                                                        dialog1.dismiss();
                                                        }
                                                    }
                                                });

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri uri = data.getData();
            mAuth = FirebaseAuth.getInstance();
            mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users");
         //   Toast.makeText(getApplicationContext(),uri.getLastPathSegment().toString(),Toast.LENGTH_LONG).show();

            StorageReference mStoarge = FirebaseStorage.getInstance().getReference();



            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            final AlertDialog dialog1 = new SpotsDialog(this, R.style.CustomProgress);
            dialog1.show();

            try {

    StorageReference filepath = mStoarge.child("Images").child(uri.getLastPathSegment().toString());
    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
            mDatabase1.child(mAuth.getCurrentUser().getUid()).child("images").setValue(downloadUrl.toString());

            Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_LONG).show();
           // mprogress.dismiss();
            dialog1.dismiss();

        }
    })

            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    dialog1.dismiss();
                    //  mprogress.dismiss();
                    Toast.makeText(getApplicationContext(), exception.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });


}catch (Exception e){
    //Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
e.printStackTrace();
}

        }


    }
    private void viewImage(String imageUrl) {
        // Toast.makeText(AdminSinglePost.this,imageUrl, Toast.LENGTH_LONG).show();

    }
}