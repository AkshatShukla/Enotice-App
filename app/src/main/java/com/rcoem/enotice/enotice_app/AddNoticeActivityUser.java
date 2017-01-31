package com.rcoem.enotice.enotice_app;

/**
 * Created by Akshat Shukla on 29-10-2016.
 */


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AddNoticeActivityUser extends AppCompatActivity {


    private EditText mPostTitle;
    private EditText mPostDesc;
    private ImageButton mSelectImage;
    private Button mSubmitButton;
    private Uri mImageUri = null;
    private static final int Gallery_Request = 1;
    private ProgressDialog mprogress;
    private TextView textUid;
    private StorageReference mStoarge;
    private DatabaseReference mData;
    private DatabaseReference mDataUser;
    private DatabaseReference mDataUser1;
    private DatabaseReference mDataBaseDepartment;
    private DatabaseReference mDatabase;

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;


    private String Department;

    private ProgressDialog mProgress;

    private FirebaseUser mCurrentUser;

    RelativeLayout ri;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice_user);

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
        //  mData = FirebaseDatabase.getInstance().getReference().child("posts");

        mDataUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mDataUser1 = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());


        mPostTitle = (EditText) findViewById(R.id.Edit_Title_field) ;
        mPostDesc = (EditText) findViewById(R.id.Edit_description_field);

        mSubmitButton = (Button) findViewById(R.id.Submit_button);


        mProgress  = new ProgressDialog(this);

        mSelectImage = (ImageButton) findViewById(R.id.select_image_Button);
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , Gallery_Request);

            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataUser1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String block = dataSnapshot.child("block").getValue().toString().trim();
                        if(block.equals("No")){

                            String Dept = dataSnapshot.child("department").getValue().toString().trim();
                            startPosting(Dept);
                        }
                        else{
                            Toast.makeText(AddNoticeActivityUser.this,"Sorry, you are not authorized to generate notices. Contact HOD of your Department.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                // startPosting();

            }
        });
    }
    private void startPosting(final String Dept) {
        final String title_value =  mPostTitle.getText().toString().trim();
        final String desc_value = mPostDesc.getText().toString().trim();
        final String user_id =  mAuth.getCurrentUser().getUid();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH) + 1;   //Month in Calendar API start with 0.
        day = calendar.get(Calendar.DAY_OF_MONTH);
        Toast.makeText(AddNoticeActivityUser.this,day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
        final String currentDate = day + "/" + month + "/" + year;
        mProgress.setMessage("Uploading Notice...");



        if(!TextUtils.isEmpty(title_value) && !TextUtils.isEmpty(desc_value) || mImageUri != null){
            mProgress.show();
            StorageReference filepath = mStoarge.child("Images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost =mData.push();


                    // Toast.makeText(StartPosting.this,"uploaded successfully", Toast.LENGTH_LONG).show();

                    mDataUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("approved").setValue("pending");  //Authentication is Required.
                            newPost.child("removed").setValue(0);         //Not removed initially.
                            newPost.child("title").setValue(title_value);
                            newPost.child("Desc").setValue(desc_value);
                            newPost.child("time").setValue(currentDate);
                            newPost.child("department").setValue(Dept);
                            newPost.child("email").setValue(mAuth.getCurrentUser().getEmail());
                            newPost.child("UID").setValue(mAuth.getCurrentUser().getUid());
                            newPost.child("images").setValue(downloadUrl.toString());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AddNoticeActivityUser.this,"Uploaded Successfully", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(AddNoticeActivityUser.this,"Not Successfully Uploaded", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            departmentPush(title_value,"Notice For Approval sent by ".concat(dataSnapshot.child("name").getValue().toString()),Dept,downloadUrl.toString());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mProgress.dismiss();


                    startActivity(new Intent(AddNoticeActivityUser.this , AccountActivityUser.class));

                }
            });

        }
    }
    private void departmentPush(final String t,final String m,final String dept,final String i){
        final String title = t;
        final String message = m;
        final String image = i;
        final String email = "dhanajay@gmail.com";
        //progressDialog.setMessage("Sending Dept Push");
        // progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH_HOD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();

                        Toast.makeText(AddNoticeActivityUser.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);

                if (!TextUtils.isEmpty(image))
                    params.put("image", image);

                params.put("email", email);
                params.put("dept",dept);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Request && resultCode == RESULT_OK) {
            Uri ImageUri = data.getData();
            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mImageUri = result.getUri();

                mSelectImage.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

