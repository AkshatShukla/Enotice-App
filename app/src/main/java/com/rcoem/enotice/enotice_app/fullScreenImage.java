package com.rcoem.enotice.enotice_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
        //Toast.makeText(getApplicationContext(), str,Toast.LENGTH_LONG).show();

        mViewImage = (ImageView) findViewById(R.id.FullScreenImageView);
        //Picasso.with(fullScreenImage.this).load(str).into(mViewImage);

        //Glide.with(fullScreenImage.this).load(str).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(mViewImage);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

        Glide.with(fullScreenImage.this)
                .load(str)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(mViewImage);

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
