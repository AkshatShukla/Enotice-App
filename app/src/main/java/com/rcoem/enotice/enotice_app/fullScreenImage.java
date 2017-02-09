package com.rcoem.enotice.enotice_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class fullScreenImage extends AppCompatActivity {

    private ImageView mViewImage;
    PhotoViewAttacher mAttacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        Intent intent = getIntent();
        final String str = intent.getStringExtra("imageUrl");

        mViewImage = (ImageView) findViewById(R.id.FullScreenImageView);
        Picasso.with(fullScreenImage.this).load(str).into(mViewImage);

        //PhotoViewAttacher handles all the gesture zooming functionality
        mAttacher = new PhotoViewAttacher(mViewImage);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
