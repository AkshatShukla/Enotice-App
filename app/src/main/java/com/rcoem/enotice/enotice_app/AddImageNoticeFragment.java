package com.rcoem.enotice.enotice_app;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


/**
 * Created by Akshat Shukla on 12-02-2017.
 */

public class AddImageNoticeFragment extends Fragment  {


    private StorageReference mStoarge;
    private DatabaseReference mData;
    private DatabaseReference mDataUser;
    private DatabaseReference mDataBaseDepartment;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private static final int Gallery_Request = 1;

    private EditText titleText;
    private EditText descText;
    private Button btnCaptureImage;
    private Button btnChooseImage;
    private Button btnSubmit;
    private ImageView imgPreview;
    private static final int CAMERA_REQUEST = 1888;
    private Uri mImageUri = null;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "E Notices";

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    View view;

    Activity context;

    public AddImageNoticeFragment() {
        // Required empty public constructor
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();
        view = inflater.inflate(R.layout.activity_add_image_notice, container, false);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_add_image_notice, container, false);
    }

    public void onStart(){
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mStoarge = FirebaseStorage.getInstance().getReference();
        mCurrentUser = mAuth.getCurrentUser();


        mDataBaseDepartment = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mDataBaseDepartment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mData = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Deptposts");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDataUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        titleText = (EditText) context.findViewById(R.id.titleText);
        descText = (EditText) context.findViewById(R.id.descText);

        btnCaptureImage = (Button) context.findViewById(R.id.btnCapturePicture);
        btnCaptureImage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

               /* //create an Intent object
                Intent intent = new Intent(context, AddNoticeActivityAdmin.class);

                //start the second activity
                startActivity(intent);

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , Gallery_Request);
                */
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }

        });
        btnChooseImage = (Button) context.findViewById(R.id.btnChooseImage);
        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, Gallery_Request);
            }
        });
        btnSubmit = (Button) context.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toasty.info(context,"Submitting").show();
                mDataBaseDepartment = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                mDataBaseDepartment.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String Dept = dataSnapshot.child("department").getValue().toString().trim();
                        startPosting(Dept);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        imgPreview  = (ImageView) context.findViewById(R.id.imagePreview);

    }

    private void startPosting(final String Dept) {
        final String title_value =  titleText.getText().toString().trim();
        final String desc_value = descText.getText().toString().trim();
        final String user_id =  mAuth.getCurrentUser().getUid();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH) + 1;    //Month in Calendar API start with 0.
        day = calendar.get(Calendar.DAY_OF_MONTH);
        //  Toast.makeText(AddNoticeActivityAdmin.this,day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
        final String currentDate = day + "/" + month + "/" + year;
        final long currentLongTime = -1 * new Date().getTime();
        final String currentTime = "" + currentLongTime;


        //mProgress.setMessage("Uploading Notice...");
        final AlertDialog dialog = new SpotsDialog(context, R.style.CustomProgress);
        dialog.show();

        if(!TextUtils.isEmpty(title_value) && !TextUtils.isEmpty(desc_value) && imgPreview.getDrawable() != null){
            //mProgress.show();
            StorageReference filepath = mStoarge.child("Images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost =mData.push();

                    mDataUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("approved").setValue("true");  //No Authentication is Required.
                            newPost.child("removed").setValue(0);        //Not removed initially.
                            newPost.child("title").setValue(title_value);
                            newPost.child("time").setValue(currentDate);
                            newPost.child("servertime").setValue(currentLongTime);
                            newPost.child("Desc").setValue(desc_value);
                            newPost.child("email").setValue(mAuth.getCurrentUser().getEmail());
                            newPost.child("department").setValue(Dept);
                            newPost.child("images").setValue(downloadUrl.toString());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toasty.success(context,"Upload Successfully").show();
                                    }
                                    else
                                    {
                                        Toasty.error(context,"Upload Unsuccessful").show();
                                    }
                                }
                            });


                            //departmentPush(title_value,"Notice by HOD ".concat(dataSnapshot.child("name").getValue().toString()),Dept,downloadUrl.toString());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    //mProgress.dismiss();
                    dialog.dismiss();
                    //startActivity(new Intent(context , AddNoticeTabbed.class));
                }
            });

        }
        else if (imgPreview.getDrawable() == null) {
            Toasty.error(context,"No image to upload").show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imgPreview.setImageBitmap(photo);
        }

        try {
            // When an Image is picked
            if (requestCode == Gallery_Request && resultCode == Activity.RESULT_OK
                    && null != data) {
                Uri mImageUri = data.getData();

                CropImage.activity(mImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getContext(),this);
            }
            // when image is cropped
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Log.d("APP_DEBUG",result.toString());
                if (resultCode == Activity.RESULT_OK) {
                    mImageUri = result.getUri();
                    imgPreview.setImageURI(mImageUri);
                    Log.d("APP_DEBUG",mImageUri.toString());
                    //Bitmap bitmap =  MediaStore.Images.Media.getBitmap(context.getContentResolver(), resultUri);
                    //imgPreview.setImageBitmap(bitmap);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong"+e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

}




