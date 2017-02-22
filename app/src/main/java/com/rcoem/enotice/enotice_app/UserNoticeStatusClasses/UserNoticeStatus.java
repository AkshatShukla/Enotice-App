package com.rcoem.enotice.enotice_app.UserNoticeStatusClasses;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rcoem.enotice.enotice_app.UserClasses.AccountActivityUser;
import com.rcoem.enotice.enotice_app.BlogModel;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.Utils;
import com.rcoem.enotice.enotice_app.ViewHolderClasses.DocumentNoticeViewHolder;
import com.rcoem.enotice.enotice_app.ViewHolderClasses.ImageNoticeViewHolder;
import com.rcoem.enotice.enotice_app.ViewHolderClasses.TextNoticeViewHolder;

public class UserNoticeStatus extends AppCompatActivity {
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private StorageReference mStoarge;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Query  mquery;
    private ProgressDialog mProgress;

    private DatabaseReference mDataBaseDepartment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notice_status);


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
        getSupportActionBar().setTitle(R.string.check_notice_status);

        mDataBaseDepartment = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mDataBaseDepartment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    String str = dataSnapshot.child("department").getValue().toString();
                    viewNotices(str);
                }
                else {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //   mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
    }

    private void viewNotices(String str) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(str).child("Pending");
        mquery =  mDatabase.orderByChild("UID").equalTo(mAuth.getCurrentUser().getUid());
        mStoarge = FirebaseStorage.getInstance().getReference();

        mBlogList = (RecyclerView) findViewById(R.id.blog_recylView_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        //  mProgress.setMessage("Uploading Details");
        //   mProgress.show();
        //  mProgress.dismiss();

        //Firebase Recycler Adapter inflating multiple view types.
        FirebaseRecyclerAdapter<BlogModel,RecyclerView.ViewHolder> firebaseRecyclerAdapter =new
                FirebaseRecyclerAdapter<BlogModel, RecyclerView.ViewHolder>(
                        BlogModel.class,
                        R.layout.blog_row,
                        RecyclerView.ViewHolder.class,
                        mquery
                ) {
                    @Override
                    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, BlogModel model, final int position) {

                        final String Post_Key = getRef(position).toString();

                        switch(model.getType()){
                            case 1 :
                                TextNoticeViewHolder.populateTextNoticeCard((TextNoticeViewHolder) viewHolder, model, position, Post_Key, getApplicationContext());
                                break;
                            case 2 :
                                ImageNoticeViewHolder.populateImageNoticeCard((ImageNoticeViewHolder) viewHolder, model, position, Post_Key, getApplicationContext());
                                break;
                            case 3 :
                                DocumentNoticeViewHolder.populateDocumentNoticeCard((DocumentNoticeViewHolder) viewHolder, model, position, Post_Key, getApplicationContext());
                                break;
                        }
                    }


                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        switch (viewType) {
                            case Utils.TEXT_NOTICE:
                                View textNotice = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.notice_text_card, parent, false);
                                return new TextNoticeViewHolder(textNotice, Utils.USER_STATUS);
                            case Utils.IMAGE_NOTICE:
                                View imageNotice = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.notice_image_card, parent, false);
                                return new ImageNoticeViewHolder(imageNotice, Utils.USER_STATUS);
                            case Utils.DOCUMENT_NOTICE:
                                View documentNotice = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.notice_document_card, parent, false);
                                return new DocumentNoticeViewHolder(documentNotice, Utils.USER_STATUS);
                        }
                        return super.onCreateViewHolder(parent, viewType);
                    }

                    @Override
                    public int getItemViewType(int position) {
                        BlogModel model = getItem(position);
                        switch (model.getType()) {
                            case Utils.TEXT_NOTICE:
                                return Utils.TEXT_NOTICE;
                            case Utils.IMAGE_NOTICE:
                                return Utils.IMAGE_NOTICE;
                            case Utils.DOCUMENT_NOTICE:
                                return Utils.DOCUMENT_NOTICE;
                        }
                        return super.getItemViewType(position);
                    }

                };




        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserNoticeStatus.this, AccountActivityUser.class);
        startActivity(intent);
        finish();
    }
}