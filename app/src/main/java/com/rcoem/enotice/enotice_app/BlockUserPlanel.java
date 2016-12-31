package com.rcoem.enotice.enotice_app;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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

import java.util.List;

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

    DrawerLayout di;

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

                                //Toast.makeText(BlockUserPlanel.this,Post_Key,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(BlockUserPlanel.this,BlockContactsPanel.class);
                                intent.putExtra("postkey",Post_Key);
                                startActivity(intent);
                                finish();


                            }
                        });
                    }
                };
        //  mProgress.dismiss();





        //  randomListing = new ArrayList<NoticeCard>();
        // adapter = new NoticeCardAdapter(this, randomListing);

        // swipeRefreshLayout.setOnRefreshListener(this);











        //    addContent();


        mBlogList.setAdapter(firebaseRecyclerAdapter);

        mAuth = FirebaseAuth.getInstance();

        /*
        signOut = (Button) findViewById(R.id.signOut);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                Snackbar snackbar = Snackbar
                        .make(ri, R.string.sign_out, Snackbar.LENGTH_LONG);
                snackbar.show();
                //Toast.makeText(AccountActivity.this, R.string.sign_out, Toast.LENGTH_LONG).show();
                startActivity(new Intent(AccountActivity.this, MainActivity.class));
            }
        });
        */
    }



     /* @Override
    public void onRefresh() {
       // swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setRefreshing(false);
    }*/

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

