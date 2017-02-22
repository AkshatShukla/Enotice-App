package com.rcoem.enotice.enotice_app.AdminClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rcoem.enotice.enotice_app.BlogModel;
import com.rcoem.enotice.enotice_app.CircleTransform;
import com.rcoem.enotice.enotice_app.NoticeCard;
import com.rcoem.enotice.enotice_app.NoticeCardAdapter;
import com.rcoem.enotice.enotice_app.R;
import com.rm.rmswitch.RMSwitch;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class BlockUserPlanel extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoticeCardAdapter adapter;
    private List<NoticeCard> randomListing;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NavigationView navigationView;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private View navHeader;
    private Snackbar snackbar;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase1;
    private DatabaseReference mCurrentUser;

    FloatingActionButton fabplus;


    private int count = 0;

    private Button signOut;

    private int backButtonCount = 0;

    FirebaseAuth mAuth;

    BottomSheetBehavior bottomSheetBehavior ;

    BottomSheetDialog bottomSheetDialog ;

    private TextView mPostTitle;
    private DatabaseReference mDatabase;
    DrawerLayout di;

    private ImageView circularImageView;

    private RMSwitch mSwitch;
    private boolean process;
    private String val;


    Toolbar mActionBarToolbar;
    private DatabaseReference mDatabaseValidContent;

    Query mquery;
    long back_pressed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_user_planel);

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

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
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


        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Str = dataSnapshot.child("department").getValue().toString();
                viewNotice(Str);
                getSupportActionBar().setTitle("All Users in ".concat(Str));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void viewNotice(String str) {
        mDatabaseValidContent = FirebaseDatabase.getInstance().getReference().child("Users");
        String sup = str.substring(0,3).trim();
        if(sup.equals("Mec")){
            sup = sup +"h";
        }
        mquery =  mDatabaseValidContent.orderByChild("department").equalTo(sup);


        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.RelativeLayoutSheet));

        mBlogList = (RecyclerView) findViewById(R.id.blog_recylView_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        //  mProgress.setMessage("Uploading Details");
        //   mProgress.show();
        FirebaseRecyclerAdapter<BlogModel,BlogViewHolder> firebaseRecyclerAdapter =new
                FirebaseRecyclerAdapter<BlogModel, BlogViewHolder>(

                        BlogModel.class,
                        R.layout.block_view,
                        BlogViewHolder.class,
                        mquery
                ) {
                    @Override
                    protected void populateViewHolder(BlogViewHolder viewHolder, BlogModel model, int position) {

                        final String Post_Key = getRef(position).toString();
                        Intent intent = getIntent();
                        final String str = intent.getStringExtra("location");

                        viewHolder.setName(model.getName());
                        viewHolder.setImage(getApplicationContext(), model.getImages());
                        viewHolder.setDesg(model.getDEST());
                        viewHolder.setBlockStatus(model.getBlock());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(BlockUserPlanel.this);

                                View view1 = getLayoutInflater().inflate(R.layout.content_bottom_sheet, null);

                                view1.setVisibility(View.VISIBLE);
                                mPostTitle = (TextView) view1.findViewById(R.id.user_name1);

                                circularImageView = (ImageView) view1.findViewById(R.id.imageView);

                                mSwitch = (RMSwitch) view1.findViewById(R.id.toggleBtn);

                                bottomSheetDialog.setContentView(view1);

                                mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(Post_Key);

                                mDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        mPostTitle.setText(dataSnapshot.child("name").getValue().toString().trim());
                                        String url = dataSnapshot.child("images").getValue().toString().trim();
                                        Picasso.with(BlockUserPlanel.this).load(url).noFade().into(circularImageView);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                process = true;
                                mSwitch.setChecked(false);
                                mDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                            val = dataSnapshot.child("block").getValue().toString().trim();
                                            if (val.equals("No")) {
                                                mSwitch.setChecked(false);
                                            }
                                            else {
                                                mSwitch.setChecked(true);
                                            }
                                        }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                // Add a Switch state observer
                                mSwitch.addSwitchObserver(new RMSwitch.RMSwitchObserver() {
                                    @Override
                                    public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {

                                        if (!isChecked) {
                                            mDatabase.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(process) {
                                                        mDatabase.child("block").setValue("No");
                                                        process = false;
                                                        Toasty.custom(BlockUserPlanel.this, "User Has been Unblocked", R.drawable.ok, getResources().getColor(R.color.colorWhite), getResources().getColor(R.color.unblocked), 100, true, true).show();
                                                        //Toast.makeText(BlockUserPlanel.this, "User Has been Unblocked", Toast.LENGTH_LONG).show();

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                        else {
                                            mDatabase.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(process) {
                                                        mDatabase.child("block").setValue("Yes");
                                                        mSwitch.setChecked(true);
                                                        process = false;
                                                        Toasty.custom(BlockUserPlanel.this, "User Has been Blocked", R.drawable.cancel, getResources().getColor(R.color.colorWhite), getResources().getColor(R.color.blocked), 100, true, true).show();
                                                        //Toast.makeText(BlockUserPlanel.this, "User Has been Blocked", Toast.LENGTH_LONG).show();

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                });

                                bottomSheetDialog.show();
                                //Toast.makeText(BlockUserPlanel.this,Post_Key,Toast.LENGTH_LONG).show();
                                //Intent intent = new Intent(BlockUserPlanel.this,BlockContactsPanel.class);
                                //intent.putExtra("postkey",Post_Key);
                                //startActivity(intent);
                                //finish();


                            }
                        });
                    }
                };

        mBlogList.setAdapter(firebaseRecyclerAdapter);

        mAuth = FirebaseAuth.getInstance();

    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage(Context context, String image){

            ImageView post_image = (ImageView) mView.findViewById(R.id.prof_pic_block_view);
            Glide.with(context).load(image)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(context))
                    .into(post_image);

        }

        public void setName(String name){

            TextView post_Desc = (TextView) mView.findViewById(R.id.name_block_view);
            post_Desc.setText(name);
        }

        public void setDesg(String Desg){

            String desg;

            TextView post_Desc = (TextView) mView.findViewById(R.id.designation_block_view);
            if(Desg.equals("AP")){
                desg = "Assistant Professor";
            }
            else{
                desg = "Head of Department";
            }
            post_Desc.setText(desg);
        }

        public void setBlockStatus(String status){

            LinearLayout li_status = (LinearLayout)mView.findViewById(R.id.li_block_status);

            if(status.equals("No")){
                li_status.setBackgroundResource(R.drawable.unblock_circle);

            }
            else{
                li_status.setBackgroundResource(R.drawable.blocked_circle);
            }
        }

    }




}

