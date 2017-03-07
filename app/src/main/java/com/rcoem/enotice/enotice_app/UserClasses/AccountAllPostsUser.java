package com.rcoem.enotice.enotice_app.UserClasses;

/**
 * Created by Akshat Shukla on 28-02-2017.
 */

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.rcoem.enotice.enotice_app.BlogModel;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.Utils;
import com.rcoem.enotice.enotice_app.ViewHolderClasses.DocumentNoticeViewHolder;
import com.rcoem.enotice.enotice_app.ViewHolderClasses.ImageNoticeViewHolder;
import com.rcoem.enotice.enotice_app.ViewHolderClasses.TextNoticeViewHolder;

import java.util.Date;

import es.dmoral.toasty.Toasty;


public class AccountAllPostsUser extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabaseValidContent;
    private DatabaseReference mCurrentUser;
    private DatabaseReference currentUserStatus;

    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;

    Query mquery;

    View allView;
    Activity context;

    public AccountAllPostsUser() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        allView = inflater.inflate(R.layout.activity_account_user_all, container, false);

        // Inflate the layout for this fragment
        return allView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();

        mBlogList = (RecyclerView) context.findViewById(R.id.blog_recylView_all_list);

        //FAB Animation, Hide when Scrolling Down, Scroll Up to Show.
        fab = (FloatingActionButton)  context.findViewById(R.id.main_fab);
        mBlogList.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 1) {
                    // Scroll Down
                    if (fab.isShown()) {
                        fab.hide();
                    }
                }
                else if (dy < 0) {
                    // Scroll Up
                    if (!fab.isShown()) {
                        fab.show();
                    }
                }
            }
        });


        //SwipeRefresh Code
        swipeRefreshLayout = (SwipeRefreshLayout) context.findViewById(R.id.swipe_refresh_layout_all);
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        //Code to display notices according to current user department
        //viewNotices method is called here
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    String Str = "ALL";
                    viewNotice(Str);
                }
                else {
                    Toasty.warning(context,"No Notices").show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do nothing
            }
        });
    }

    //Method to view department specific notices in feed

    private void viewNotice(String str) {

        //To show only the content relevant to the specific department.
        mDatabaseValidContent = FirebaseDatabase.getInstance().getReference().child("posts").child(str).child("Approved");

        long currentTime = -1 * new Date().getTime();
        String time = "" + currentTime;

        //To query and view only those messages which have been APPROVED by the authenticator.
        mquery =  mDatabaseValidContent.orderByChild("servertime");

        //Online-Offline Syncing (only strings and not images)
        mDatabaseValidContent.keepSynced(true);

        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(context));
        //  recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        currentUserStatus = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

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
                                TextNoticeViewHolder.populateTextNoticeCard((TextNoticeViewHolder) viewHolder, model, position, Post_Key, context);
                                break;
                            case 2 :
                                ImageNoticeViewHolder.populateImageNoticeCard((ImageNoticeViewHolder) viewHolder, model, position, Post_Key, context);
                                break;
                            case 3 :
                                DocumentNoticeViewHolder.populateDocumentNoticeCard((DocumentNoticeViewHolder) viewHolder, model, position, Post_Key, context);
                                break;
                        }
                    }


                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        switch (viewType) {
                            case Utils.TEXT_NOTICE:
                                View textNotice = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.notice_text_card, parent, false);
                                return new TextNoticeViewHolder(textNotice, Utils.USER_VIEW);
                            case Utils.IMAGE_NOTICE:
                                View imageNotice = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.notice_image_card, parent, false);
                                return new ImageNoticeViewHolder(imageNotice, Utils.USER_VIEW);
                            case Utils.DOCUMENT_NOTICE:
                                View documentNotice = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.notice_document_card, parent, false);
                                return new DocumentNoticeViewHolder(documentNotice, Utils.USER_VIEW);
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
        firebaseRecyclerAdapter.notifyDataSetChanged();

    }

}
