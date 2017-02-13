package com.rcoem.enotice.enotice_app;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * Created by Akshat Shukla on 12-02-2017.
 */

public class AddImageNoticeFragment extends Fragment  {

    // Camera activity request codes
    // Activity result key for camera
    static final int REQUEST_TAKE_PHOTO = 11111;
    private Uri mImageUri = null;
    private static final int Gallery_Request = 1;

    private Button btnCapturePicture;
    private ImageView imgPreview;
    private Uri fileUri; // file url to store image/video

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "E Notices";

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
        Button bt = (Button)context.findViewById(R.id.btnCapturePicture);
        bt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

               /* //create an Intent object
                Intent intent = new Intent(context, AddNoticeActivityAdmin.class);

                //start the second activity
                startActivity(intent); */

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , Gallery_Request);
            }

        });

        imgPreview  = (ImageView) context.findViewById(R.id.imagePreview);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Request && resultCode == RESULT_OK) {
            Uri ImageUri = data.getData();
            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(context);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mImageUri = result.getUri();

                imgPreview.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}




