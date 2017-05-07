package com.rcoem.enotice.enotice_app.AddNoticeClasses;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.javiersantos.bottomdialogs.BottomDialog;
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
import com.rcoem.enotice.enotice_app.NotificationClasses.EndPoints;
import com.rcoem.enotice.enotice_app.NotificationClasses.MyVolley;
import com.rcoem.enotice.enotice_app.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;


/**
 * Created by Akshat Shukla on 12-02-2017.
 */

public class AddImageNoticeFragment extends Fragment  {


    private StorageReference mStoarge;
    private DatabaseReference mData;
    private DatabaseReference mDataUser;
    private DatabaseReference mDatabase1;
    private DatabaseReference mDataBaseDepartment;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private static final int Gallery_Request = 1;

    private EditText titleText;
    private EditText descText;
    private String title_value;
    private String desc_value;
    private Button btnCaptureImage;
    private Button btnChooseImage;
    private Button btnSubmit;
    private ImageView imgPreview;
    private static final int CAMERA_REQUEST = 0;
    private Uri mImageUri = null;
    private Uri downloadUrl;
    private File file;

    private Spinner spinnerImage;
    private String noticeType;
    private String strdept;
    private ImageView circularImageView;

    private String [] typeArray =
            {       "For Students",
                    "For Teachers",
                    "Urgent",
                    "Normal",
                    "Assignment",
                    "Time Table"};

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "E Notices";
    private String Approved;

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    View imageView;

    Activity context;

    public AddImageNoticeFragment() {
        // Required empty public constructor
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strdept = getActivity().getIntent().getStringExtra("postkey");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();
        imageView = inflater.inflate(R.layout.activity_add_image_notice, container, false);


        // Inflate the layout for this fragment
        return imageView;
    }

    public void onStart(){
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mStoarge = FirebaseStorage.getInstance().getReference();
        mCurrentUser = mAuth.getCurrentUser();

        spinnerImage = (Spinner) context.findViewById(R.id.spinnerImage);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, typeArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerImage.setAdapter(adapter);
        spinnerImage.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {

                        int position = spinnerImage.getSelectedItemPosition();

                        noticeType = typeArray[+position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }

                }
        );

        mDataBaseDepartment = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mDataUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        titleText = (EditText) context.findViewById(R.id.titleImage);
        descText = (EditText) context.findViewById(R.id.descImage);


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
                file = new File(Environment.getExternalStorageDirectory()+File.separator + System.currentTimeMillis() + "image.jpg");
                Uri photoUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
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

        btnSubmit = (Button) context.findViewById(R.id.btnSubmitImage);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title_value =  titleText.getText().toString().trim();
                desc_value = descText.getText().toString().trim();

                mDataBaseDepartment = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                mDataBaseDepartment.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String Dept = dataSnapshot.child("department").getValue().toString().trim();

                        if(!TextUtils.isEmpty(title_value) && !TextUtils.isEmpty(desc_value) && imgPreview.getDrawable() != null) {
                            startPosting(Dept);
                        }
                        else if (imgPreview.getDrawable() == null) {
                            Toasty.warning(context,"No image to upload").show();
                        }
                        else if (TextUtils.isEmpty(title_value)) {
                            Toasty.warning(context,"Please Enter Title").show();
                        }
                        else if (TextUtils.isEmpty(desc_value)) {
                            Toasty.warning(context,"Please Enter Description").show();
                        }
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

        if (!context.isFinishing()) {
            new BottomDialog.Builder(context)
                    .setTitle("Upload (" + noticeType + ") Image Notice")
                    .setContent("Are you sure you want to submit it as your notice?")
                    .setPositiveText("Approve")
                    .setPositiveBackgroundColorResource(R.color.colorPrimary)
                    .setCancelable(false)
                    .setNegativeText("No")
                    .setPositiveTextColorResource(android.R.color.white)
                    //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
                    .onPositive(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(BottomDialog dialog) {

                            final String user_id = mAuth.getCurrentUser().getUid();
                            calendar = Calendar.getInstance();
                            year = calendar.get(Calendar.YEAR);

                            month = calendar.get(Calendar.MONTH) + 1;    //Month in Calendar API start with 0.
                            day = calendar.get(Calendar.DAY_OF_MONTH);
                            //  Toast.makeText(AddNoticeActivityAdmin.this,day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                            final String currentDate = day + "/" + month + "/" + year;
                            final long currentLongTime = -1 * new Date().getTime();
                            final String currentTime = "" + currentLongTime;


                            //mProgress.setMessage("Uploading Notice...");
                            final AlertDialog dialog1 = new SpotsDialog(context, R.style.CustomProgress);
                            dialog1.show();

                            //mProgress.show();
                            StorageReference filepath = mStoarge.child("Images").child(mImageUri.getLastPathSegment());
                            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    downloadUrl = taskSnapshot.getDownloadUrl();


                                    mDataUser.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            final String lvlCheck = dataSnapshot.child("level").getValue().toString().trim();

                                            if (lvlCheck.equals("1")) {
                                                mData = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Pending");
                                                Approved = "pending";
                                            } else if (lvlCheck.equals("2")) {
                                                if (strdept == null) {
                                                    mData = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Approved");
                                                    mDatabase1 = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Pending").push();
                                                    Approved = "true";

                                                    //For Archival Activity

                                                    mDatabase1.child("type").setValue(2);
                                                    mDatabase1.child("label").setValue(noticeType);
                                                    mDatabase1.child("title").setValue(titleText);
                                                    mDatabase1.child("Desc").setValue(descText);
                                                    mDatabase1.child("UID").setValue(mAuth.getCurrentUser().getUid());
                                                    mDatabase1.child("email").setValue(mAuth.getCurrentUser().getEmail());
                                                    mDatabase1.child("username").setValue(dataSnapshot.child("name").getValue());
                                                    mDatabase1.child("profileImg").setValue(dataSnapshot.child("images").getValue());
                                                    //Passing Default Text Image for Web App Viewing
                                                    mDatabase1.child("images").setValue("https://firebasestorage.googleapis.com/v0/b/e-notice-board-83d16.appspot.com/o/txt-file-symbol.png?alt=media&token=3a8beb43-561f-4f69-a6ad-58d2683abe81");
                                                    mDatabase1.child("time").setValue(currentDate);
                                                    mDatabase1.child("servertime").setValue(currentLongTime);
                                                    //Default Link
                                                    mDatabase1.child("link").setValue("gs://e-notice-board-83d16.appspot.com/pdf/debug.txt");
                                                    mDatabase1.child("department").setValue(dataSnapshot.child("department").getValue().toString().trim());
                                                    mDatabase1.child("approved").setValue(Approved);
                                                } else {
                                                    mData = FirebaseDatabase.getInstance().getReference().child("posts").child(strdept).child("Pending");
                                                    Approved = "pending";
                                                }
                                            }

                                            final DatabaseReference newPost = mData.push();

                                            newPost.child("type").setValue(2);
                                            newPost.child("label").setValue(noticeType);
                                            newPost.child("title").setValue(title_value);
                                            newPost.child("Desc").setValue(desc_value);
                                            newPost.child("UID").setValue(mAuth.getCurrentUser().getUid());
                                            newPost.child("email").setValue(mAuth.getCurrentUser().getEmail());
                                            newPost.child("username").setValue(dataSnapshot.child("name").getValue());
                                            newPost.child("profileImg").setValue(dataSnapshot.child("images").getValue());
                                            newPost.child("images").setValue(downloadUrl.toString());
                                            newPost.child("time").setValue(currentDate);
                                            newPost.child("servertime").setValue(currentLongTime);
                                            //Default link
                                            newPost.child("link").setValue("gs://e-notice-board-83d16.appspot.com/pdf/debug.txt");
                                            newPost.child("department").setValue(Dept);
                                            newPost.child("approved").setValue(Approved).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toasty.success(context, "Upload Successful").show();
                                                    } else {
                                                        Toasty.error(context, "Upload Unsuccessful").show();
                                                    }
                                                }
                                            });

                                            if (lvlCheck.equals("2")) {
                                                departmentPushDept(title_value, "Notice by HOD ".concat(dataSnapshot.child("name").getValue().toString()), Dept, downloadUrl.toString());
                                            } else if (lvlCheck.equals("1")) {
                                                departmentPushHOD(title_value, "Pending Notice Approval sent by ".concat(dataSnapshot.child("name").getValue().toString()), Dept, downloadUrl.toString());
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toasty.error(context, "Connection Error").show();
                                        }
                                    });

                                    Toasty.success(context, "Successfully Posted").show();
                                    dialog1.dismiss();
                                    startActivity(new Intent(context, AddNoticeTabbed.class));
                                    getActivity().finish();
                                }
                            });

                        }
                    }).show();

        }
    }

    private void departmentPushDept(final String t,final String m,final String dept,final String i){
        final String title = t;
        final String message = m;
        final String image = i;
        final String email = "dhanajay@gmail.com";
        //progressDialog.setMessage("Sending Dept Push");
        // progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH_DEPT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();

                        //Toast.makeText(AddNoticeActivityAdmin.this, response, Toast.LENGTH_LONG).show();
                        //Toasty.custom(getActivity().getBaseContext(), "Department Teachers will be notified of your Notice", R.mipmap.ic_launcher, getResources().getColor(R.color.colorWhite), getResources().getColor(R.color.colorBg), 100, false, true).show();
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

        MyVolley.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void departmentPushHOD(final String t,final String m,final String dept,final String i){
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

                        //Toast.makeText(AddNoticeActivityUser.this, response, Toast.LENGTH_LONG).show();
                        //Toasty.custom(getActivity().getBaseContext(), "HOD will be notified of your Notice", R.mipmap.ic_launcher, getResources().getColor(R.color.colorWhite), getResources().getColor(R.color.colorBg), 100, false, true).show();
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

        MyVolley.getInstance(context).addToRequestQueue(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            /*
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imgPreview.setImageBitmap(photo);
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP


            mImageUri = getImageUri(context, photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(mImageUri));
            */

            mImageUri = Uri.fromFile(file);
            Log.d("APP_DEBUG",mImageUri.toString());
            imgPreview.setImageURI(mImageUri);
            Toast.makeText(context,mImageUri.toString(),Toast.LENGTH_LONG).show();

            //use imageUri here to access the image

            /*
            // Describe the columns you'd like to have returned. Selecting from the Thumbnails location gives you both the Thumbnail Image ID, as well as the original image ID
            String[] projection = {
                    MediaStore.Images.Thumbnails._ID,  // The columns we want
                    MediaStore.Images.Thumbnails.IMAGE_ID,
                    MediaStore.Images.Thumbnails.KIND,
                    MediaStore.Images.Thumbnails.DATA};
            String selection = MediaStore.Images.Thumbnails.KIND + "="  + // Select only mini's
                    MediaStore.Images.Thumbnails.MINI_KIND;

            String sort = MediaStore.Images.Thumbnails._ID + " DESC";

            //At the moment, this is a bit of a hack, as I'm returning ALL images, and just taking the latest one. There is a better way to narrow this down I think with a WHERE clause which is currently the selection variable
            Cursor myCursor = context.managedQuery(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, selection, null, sort);

            long imageId = 0l;
            long thumbnailImageId = 0l;
            String thumbnailPath = "";

            try{
                myCursor.moveToFirst();
                imageId = myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID));
                thumbnailImageId = myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID));
                thumbnailPath = myCursor.getString(myCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
            }
            finally{myCursor.close();}

            //Create new Cursor to obtain the file Path for the large image

            String[] largeFileProjection = {
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA
            };

            String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
            myCursor = context.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, largeFileProjection, null, null, largeFileSort);
            String largeImagePath = "";

            try{
                myCursor.moveToFirst();

            //This will actually give you the file path location of the image.
                largeImagePath = myCursor.getString(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
            }
            finally{myCursor.close();}
            // These are the two URI's you'll be interested in. They give you a handle to the actual images
            mImageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(imageId));
            Uri uriThumbnailImage = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, String.valueOf(thumbnailImageId));
            imgPreview.setImageURI(uriThumbnailImage);
            // I've left out the remaining code, as all I do is assign the URI's to my own objects anyways...
            */
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

}




