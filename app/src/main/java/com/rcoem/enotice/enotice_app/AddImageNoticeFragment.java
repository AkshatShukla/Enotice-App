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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


/**
 * Created by Akshat Shukla on 12-02-2017.
 */

public class AddImageNoticeFragment extends Fragment  {

    // Camera activity request codes
    // Activity result key for camera
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri mImageUri = null;
    private static final int Gallery_Request = 1;

    private Button btnCaptureImage;
    private Button btnChooseImage;
    private ImageView imgPreview;
    private static final int CAMERA_REQUEST = 1888;
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


        imgPreview  = (ImageView) context.findViewById(R.id.imagePreview);

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
                Uri selectedImage = data.getData();

                CropImage.activity(selectedImage)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getContext(),this);
            }
            // when image is cropped
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Log.d("APP_DEBUG",result.toString());
                if (resultCode == Activity.RESULT_OK) {
                    Uri resultUri = result.getUri();
                    Log.d("APP_DEBUG",resultUri.toString());
                    Bitmap bitmap =  MediaStore.Images.Media.getBitmap(context.getContentResolver(), resultUri);
                    imgPreview.setImageBitmap(bitmap);

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




