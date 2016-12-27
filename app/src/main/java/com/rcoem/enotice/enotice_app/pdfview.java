package com.rcoem.enotice.enotice_app;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;


public class pdfview extends AppCompatActivity {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private StorageReference mStoarge;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    //Query mquery;
    private ProgressDialog mprogress;
    String pdf_link ="" ;
    private DatabaseReference mDataBaseDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retriver_data);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                Handler handler = new Handler();
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                    }
                },1000);
            }
        });
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        getSupportActionBar().setTitle(R.string.new_docs);

        mDataBaseDepartment = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mprogress = new ProgressDialog(this);
        mDataBaseDepartment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String str = dataSnapshot.child("department").getValue().toString();
                viewNotices(str);
                Toast.makeText(pdfview.this,str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //   mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");




    }

    private void viewNotices(String str) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(str).child("Document");
        mStoarge = FirebaseStorage.getInstance().getReference();

        mBlogList = (RecyclerView) findViewById(R.id.blog_recylView_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        //  mProgress.setMessage("Uploading Details");
        //   mProgress.show();
        FirebaseRecyclerAdapter<BlogModel,BlogViewHolder> firebaseRecyclerAdapter =new
                FirebaseRecyclerAdapter<BlogModel, BlogViewHolder>(

                        BlogModel.class,
                        R.layout.pdf_row,
                        BlogViewHolder.class,
                        mDatabase
                ) {
                    @Override
                    protected void populateViewHolder(BlogViewHolder viewHolder, BlogModel model, int position) {

                        final String Post_Key = getRef(position).toString();
                        Intent intent = getIntent();
                        final String str = intent.getStringExtra("location");
                        viewHolder.setTitle(model.getTitle());
                        //  viewHolder.setDesc(model.getDesc());

                        //  viewHolder.setImage(getApplicationContext(), model.getImages());

                        viewHolder.setDesc(model.getUsername());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                //Toast.makeText(pdfview.this,Post_Key,Toast.LENGTH_LONG).show();
                                //  Intent intent = new Intent(pdfview.this,PdfDownload.class);
                                // intent.putExtra("postkey",Post_Key);
                                // startActivity(intent);
                                //intent.putExtra("Post_key",str);
                                //  startActivity(intent);
                                mDatabase1 = FirebaseDatabase.getInstance().getReferenceFromUrl(Post_Key);

                                //Toast.makeText(pdfview.this,mDatabase1.toString(),Toast.LENGTH_LONG).show();

                                mDatabase1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        pdf_link=  dataSnapshot.child("link").getValue().toString().trim();


                                        //Toast.makeText(pdfview.this,pdf_link,Toast.LENGTH_LONG).show();
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf_link));
                                        startActivity(browserIntent);
                                        /*
                                        WebView webView=new WebView(pdfview.this);
                                        webView.getSettings().setJavaScriptEnabled(true);
                                        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
                                       // webView.getSettings().setJavaScriptEnabled(true);
                                       // webView.getSettings().setPluginsEnabled(true);
                                        //---you need this to prevent the webview from
                                        // launching another browser when a url
                                        // redirection occurs---
                                        webView.setWebViewClient(new Callback());
                                        Toast.makeText(pdfview.this,pdf_link,Toast.LENGTH_LONG).show();

                                        String pdfURL = pdf_link;
                                        webView.loadUrl(
                                                "http://docs.google.com/gview?embedded=true&url=" + pdfURL);

                                        setContentView(webView);
                                        */

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });



                            }

                            class Callback extends WebViewClient {
                                @Override
                                public boolean shouldOverrideUrlLoading(
                                        WebView view, String url) {
                                    return(false);
                                }

                            }
                        });
                    }
                };
        //  mProgress.dismiss();
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setUsername(String username){

            TextView post_Desc = (TextView) mView.findViewById(R.id.card_prof_name);
            post_Desc.setText(username);
        }
        public void setTitle(String title){

            TextView post_title = (TextView) mView.findViewById(R.id.title_card);
            post_title.setText(title);
        }

        public void setDesc(String Desc){

            TextView post_Desc = (TextView) mView.findViewById(R.id.card_name);
            post_Desc.setText(Desc);
        }

        public void setImage(Context context, String image){

            ImageView post_image = (ImageView) mView.findViewById(R.id.card_thumbnail123);
            Picasso.with(context).load(image).into(post_image);

        }


    }

}