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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
//import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class EditViewProfile extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private StorageReference mStorage;
    private ProgressDialog mprogress;
    int flag = 0;
    private DatabaseReference mDataUser;
    TextView txt_desig,dept_disp;
    private DatabaseReference mDatabase1;

    private FirebaseAuth mAuth;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users");
        txt_desig = (TextView)findViewById(R.id.des_ok_input);
        dept_disp = (TextView)findViewById(R.id.dept_display);
        mStorage = FirebaseStorage.getInstance().getReference();
        //   Bitmap bm = BitmapFactory.decodeResource(getResources(),
        //    R.drawable.profile_pic);
        final ImageView mImage = (ImageView) findViewById(R.id.imageView);

        mprogress =  new ProgressDialog(this);
        //  mprogress.setMessage("loading");
       final TextView txt_name = (TextView)findViewById(R.id.name_input);

        //// mprogress.show();
        String path  ="photos/"+mAuth.getCurrentUser().getUid().toString();
//mprogress.setMessage("loading");
        // mprogress.show();
        // Create a reference with an initial file path and name
        //StorageReference path = mStorage.child("images/stars.jpg");
        //StorageReference islandRef = mStorage.child(path);
        mprogress.setMessage("loading");
        mprogress.show();
            mDatabase1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String imageUrl = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("images").getValue().toString().trim();
                    Picasso.with(EditViewProfile.this).load(imageUrl).into(mImage);

                    // txt_desig.setText(dataSnapshot.getValue().toString());
                    // Toast.makeText(getApplicationContext(),dataSnapshot.getValue().toString(),Toast.LENGTH_LONG).show();
                    String chk = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("level").getValue().toString().trim();
                    dept_disp.setText(dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("department").getValue().toString().trim());
                    txt_name.setText(dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("name").getValue().toString().trim());
                    if(chk.equalsIgnoreCase("2"))
                    {
                        txt_desig.setText("Head of Dept.");

                    }
                    else
                    if(chk.equalsIgnoreCase("1"))
                    {
                        txt_desig.setText("Assistant professor");

                    }
                       mprogress.dismiss();
                }
                // mprogress.dismiss();

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // System.out.println("The read failed: " + databaseError.getCode());
                    Toast.makeText(getApplicationContext(),databaseError.toString(),Toast.LENGTH_LONG).show();

                }
            });



//mprogress.setMessage("loading");
            //   mprogress.show();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Name, email address, and profile photo Url
                String name = user.getDisplayName();
                Uri photoUrl = user.getPhotoUrl();
                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();


                // final String user_id = mAuth.getCurrentUser().getUid();

                //   Toast.makeText(getApplicationContext(),photoUrl.toString(),Toast.LENGTH_LONG).show();


//                //txt_desig.setText(use);

                TextView txt_email = (TextView)findViewById(R.id.email_input);
                txt_email.setText(user.getEmail());
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUrl);
             //   mImage.setImageBitmap(bitmap);

                //  mprogress.dismiss();
                final String user_id = mAuth.getCurrentUser().getUid();

                //  DatabaseReference okk =   mDatabase1.child(user_id).getRef();
/*
    okk.child("image").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String val = dataSnapshot.getValue().toString();
            Toast.makeText(getApplicationContext(),val,Toast.LENGTH_LONG).show();
            int responseCode = -1;
            try {
                URL url = new URL(val);//"http://192.xx.xx.xx/mypath/img1.jpg
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setDoInput(true);
                con.connect();
                responseCode = con.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    //download
                    InputStream in = con.getInputStream();
                    Toast.makeText(getApplicationContext(),in.toString(),Toast.LENGTH_LONG).show();
                    Bitmap bmp = BitmapFactory.decodeStream(in);
                    ImageView mImage = (ImageView) findViewById(R.id.imageView);
                    mImage.setImageBitmap(bmp);
                    mprogress.dismiss();
                }
                //return myBitmap;
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    });
              //  String reff = okk.substring(okk.lastIndexOf("/")+1,okk.length());
                Toast.makeText(getApplicationContext(),okk.toString(),Toast.LENGTH_LONG).show();
*/

                //   Toast.makeText(getApplicationContext(),photoUrl.toString(),Toast.LENGTH_LONG).show();
                //  Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUrl);                //Set Bitmap to ImageView
                //taskSnapshot.toString();
                // ImageView mImage = (ImageView) findViewById(R.id.imageView);
                // mImage.setImageBitmap(bitmap);
                //  Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_LONG).show();
//mprogress.dismiss();
                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getToken() instead.
                //String uid = user.getUid();
            }
            // mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users");





/*
        Button des_edit = (Button)findViewById(R.id.des_edit);
        des_edit.setOnClickListener(new View.OnClickListener() {
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
                                        TextView txt  = (TextView)findViewById(R.id.des_ok_input);
                                        txt.setText(userInput.getText().toString());
                                        Toast.makeText(context,userInput.getText().toString(),Toast.LENGTH_SHORT).show();                                    }
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
        // set circle bitmap
        //  Toast.makeText(getApplicationContext(),uid,Toast.LENGTH_LONG).show();
*/
///////////////////////////set profile////////////////////////////




        ///////////////////////////set profile////////////////////////////














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
                                        mprogress.setMessage("updating");
                                        mprogress.show();

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
                                                            mprogress.dismiss();
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(context, task.getException().toString(), Toast.LENGTH_LONG).show();
                                                            mprogress.dismiss();

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
        // set circle bitmap

        //  Toast.makeText(getApplicationContext(),uid,Toast.LENGTH_LONG).show();

        Button name_edit = (Button)findViewById(R.id.name_edit);
        name_edit.setOnClickListener(new View.OnClickListener() {
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
                                        mprogress.setMessage("updating");
                                        mprogress.show();
                                        final String user_id = mAuth.getCurrentUser().getUid();


                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(userInput.getText().toString())


                                                .build();
                                     //  final  String user_id = mAuth.getCurrentUser().getUid();

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
                                                            mprogress.dismiss();
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
                // Click action
                //final Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.freeze_button_bubble);

                // Use bounce interpolator with amplitude 0.2 and frequency 20
                //  BounceInterpolator interpolator = new BounceInterpolator(0.5, 30);
                //  myAnim.setInterpolator(interpolator);

                //  fab.startAnimation(myAnim);

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
//
//               startActivity(intent);
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri uri = data.getData();

//String pro_nm = data.getData().toString();
            //   String abc = pro_nm.substring(pro_nm.lastIndexOf("/")+1,pro_nm.length());
            // Toast.makeText(getApplicationContext(),abc,Toast.LENGTH_LONG).show();

            // String pro__name = pro_nm.replace(abc,"profile");
            // Uri uri = Uri.parse(pro__name);
            //Toast.makeText(getApplicationContext(),uri.toString(),Toast. mStorage = FirebaseStorage.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();


            mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users");
            Toast.makeText(getApplicationContext(),uri.getLastPathSegment().toString(),Toast.LENGTH_LONG).show();


            StorageReference mStoarge = FirebaseStorage.getInstance().getReference();

/*
            StorageReference filepath = mStoarge.child("Images").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                  ///  final DatabaseReference newPost =mData.push();
    mDataUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                    // Toast.makeText(StartPosting.this,"uploaded successfully", Toast.LENGTH_LONG).show();
                    mDataUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                               mDatabase1.child(mAuth.getCurrentUser().getUid()).child("image").setValue("");
                               @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                       // Toast.makeText(AddNoticeActivityUser.this,"uploaded successfully", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                       // Toast.makeText(AddNoticeActivityUser.this,"Not successfully Uploaded", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    mProgress.dismiss();
                    startActivity(new Intent(AddNoticeActivityUser.this , AccountActivityUser.class));
                }
            });
*/
            // String path  ="photos/"+;
/*
            StorageReference filepath = mStorage.child(mAuth.getCurrentUser().getUid().toString());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mprogress.dismiss();
                    Toast.makeText(getApplicationContext(),"successfully uploaded",Toast.LENGTH_LONG).show();
                }
            });
            */

            mprogress.setMessage("uploading");
            mprogress.show();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            final String user_id1 = mAuth.getCurrentUser().getUid();
final String dadada=uri.toString();

            mprogress.setMessage("updating");
            mprogress.show();


            StorageReference filepath = mStoarge.child("Images").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mDatabase1.child(user_id1).child("images").setValue(downloadUrl.toString());

                    Toast.makeText(getApplicationContext(),"successfully uploaded",Toast.LENGTH_LONG).show();
                    mprogress.dismiss();

                }
            });





















                    Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();



            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            //Bitmap  circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 500);

            ImageView circularImageView = (ImageView)findViewById(R.id.imageView);
            circularImageView.setImageBitmap(bitmap);

        }


    }
}