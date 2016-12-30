package com.rcoem.enotice.enotice_app;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Akshat Shukla on 31-12-2016.
 */

public class EnoticeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Adds Firebase Offline Capabilities to the App.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        //User gets offline thumbnails downloaded and can view synced messages offline too
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false); //True only for Testing purpose
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

    }
}
