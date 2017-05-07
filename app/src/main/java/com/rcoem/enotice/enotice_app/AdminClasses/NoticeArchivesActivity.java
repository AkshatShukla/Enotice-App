package com.rcoem.enotice.enotice_app.AdminClasses;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.rcoem.enotice.enotice_app.BlogModel;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.Utils;
import com.rcoem.enotice.enotice_app.ViewHolderClasses.DocumentNoticeViewHolder;
import com.rcoem.enotice.enotice_app.ViewHolderClasses.ImageNoticeViewHolder;
import com.rcoem.enotice.enotice_app.ViewHolderClasses.TextNoticeViewHolder;

import java.util.Date;

public class NoticeArchivesActivity extends AppCompatActivity {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private StorageReference mStoarge;
    private FirebaseAuth mAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseValidContent;
    Query mquery;
    private ProgressDialog mProgress;

    private DatabaseReference mDataBaseDepartment;

    private ImageView mBlogThree;

    Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_archives);
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
                }, 1000);
            }
        });

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
        getSupportActionBar().setTitle("Notice Archives");


        mAuth = FirebaseAuth.getInstance();
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("Notice Archives");
        mDataBaseDepartment = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mDataBaseDepartment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    String str = dataSnapshot.child("department").getValue().toString();
                    viewNotices(str);
                } else {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void viewNotices(String dept) {

        //To show only the content relevant to the specific department.
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(dept).child("Pending");


        long currentTime = -1 * new Date().getTime();
        String time = "" + currentTime;


        //To query and view only those messages which are Pending.
        mquery = mDatabase.orderByChild("approved").equalTo("true").limitToLast(30);

        //Online-Offline Syncing (only strings and not images)
        mDatabase.keepSynced(true);

        //BlogList view initialized to view notices in card layout list
        mBlogList = (RecyclerView) findViewById(R.id.blog_recylView_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));


        //Firebase Recycler Adapter inflating multiple view types.
        FirebaseRecyclerAdapter<BlogModel, RecyclerView.ViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<BlogModel, RecyclerView.ViewHolder>(
                        BlogModel.class,
                        R.layout.blog_row,
                        RecyclerView.ViewHolder.class,
                        mquery
                ) {
                    @Override
                    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, BlogModel model, final int position) {

                        final String Post_Key = getRef(position).toString();

                        switch (model.getType()) {
                            case 1:
                                TextNoticeViewHolder.populateTextNoticeCard((TextNoticeViewHolder) viewHolder, model, position, Post_Key, getApplicationContext());
                                break;
                            case 2:
                                ImageNoticeViewHolder.populateImageNoticeCard((ImageNoticeViewHolder) viewHolder, model, position, Post_Key, getApplicationContext());
                                break;
                            case 3:
                                DocumentNoticeViewHolder.populateDocumentNoticeCard((DocumentNoticeViewHolder) viewHolder, model, position, Post_Key, getApplicationContext());
                                break;
                        }
                    }


                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        switch (viewType) {
                            case 1:
                                View textNotice = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.notice_text_card, parent, false);
                                return new TextNoticeViewHolder(textNotice, Utils.ADMIN_APPROVE);
                            case 2:
                                View imageNotice = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.notice_image_card, parent, false);
                                return new ImageNoticeViewHolder(imageNotice, Utils.ADMIN_APPROVE);
                            case 3:
                                View documentNotice = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.notice_document_card, parent, false);
                                return new DocumentNoticeViewHolder(documentNotice, Utils.ADMIN_APPROVE);
                        }
                        return super.onCreateViewHolder(parent, viewType);
                    }

                    @Override
                    public int getItemViewType(int position) {
                        BlogModel model = getItem(position);
                        switch (model.getType()) {
                            case 1:
                                return 1;
                            case 2:
                                return 2;
                            case 3:
                                return 3;
                        }
                        return super.getItemViewType(position);
                    }

                };

        mBlogList.setAdapter(firebaseRecyclerAdapter);

        mAuth = FirebaseAuth.getInstance();

    }

}
