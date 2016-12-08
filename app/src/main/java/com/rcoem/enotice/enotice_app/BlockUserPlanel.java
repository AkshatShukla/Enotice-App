package com.rcoem.enotice.enotice_app;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
    private DatabaseReference mDatabaseValidContent;
    Query mquery;
    long back_pressed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_admin_panel);
        mAuth = FirebaseAuth.getInstance();

        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);


        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Str = dataSnapshot.child("department").getValue().toString();
                viewNotice(Str);

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
        FirebaseRecyclerAdapter<BlogModel,AccountAdminPanel.BlogViewHolder> firebaseRecyclerAdapter =new
                FirebaseRecyclerAdapter<BlogModel, AccountAdminPanel.BlogViewHolder>(

                        BlogModel.class,
                        R.layout.block_view,
                        AccountAdminPanel.BlogViewHolder.class,
                        mquery
                ) {
                    @Override
                    protected void populateViewHolder(AccountAdminPanel.BlogViewHolder viewHolder, BlogModel model, int position) {

                        final String Post_Key = getRef(position).toString();
                        Intent intent = getIntent();
                        final String str = intent.getStringExtra("location");
                        viewHolder.setTitle(model.getName());
                      //  viewHolder.setName(model.getName());

                        //  viewHolder.setTitle(model.getTitle());
                        //   viewHolder.setDesc(model.getDesc());

                        //    viewHolder.setImage(getApplicationContext(), model.getImages());

                        //    viewHolder.setDesc(model.getUsername());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Toast.makeText(BlockUserPlanel.this,Post_Key,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(BlockUserPlanel.this,BlockContactsPanel.class);
                                intent.putExtra("postkey",Post_Key);
                                startActivity(intent);


                            }
                        });
                    }
                };
        //  mProgress.dismiss();



        di = (DrawerLayout) findViewById(R.id.drawer_layout_user);

        //  recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        fabplus = (FloatingActionButton)findViewById(R.id.main_fab);
        fabplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //To add new notice code and shift control to AddNoticeActivityUser.
                startActivity(new Intent(getApplicationContext(),AddNoticeActivityUser.class));
                //To add new notice code
            }
        });

        //  randomListing = new ArrayList<NoticeCard>();
        // adapter = new NoticeCardAdapter(this, randomListing);

        // swipeRefreshLayout.setOnRefreshListener(this);

        /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
*/








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
        public void setName(String name){

            TextView post_Desc = (TextView) mView.findViewById(R.id.card_name);
            post_Desc.setText(name);
        }

        public void setImage(Context context, String image){

            ImageView post_image = (ImageView) mView.findViewById(R.id.card_thumbnail123);
            Picasso.with(context).load(image).into(post_image);

        }



    }




}

