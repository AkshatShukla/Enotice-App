package com.rcoem.enotice.enotice_app.AuthorityClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
import com.rcoem.enotice.enotice_app.AboutUs;
import com.rcoem.enotice.enotice_app.BlogModel;
import com.rcoem.enotice.enotice_app.CircleTransform;
import com.rcoem.enotice.enotice_app.EditViewProfile;
import com.rcoem.enotice.enotice_app.LoginSignUpClasses.MainActivity;
import com.rcoem.enotice.enotice_app.NoticeCard;
import com.rcoem.enotice.enotice_app.NoticeCardAdapter;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.UserNoticeStatusClasses.UserNoticeStatus;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AccountAdminPanel extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

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
                        R.layout.blog_row,
                        AccountAdminPanel.BlogViewHolder.class,
                        mquery
                ) {
                    @Override
                    protected void populateViewHolder(final AccountAdminPanel.BlogViewHolder viewHolder, final BlogModel model, int position) {

                        final String Post_Key = getRef(position).toString();
                        Intent intent = getIntent();
                        final String str = intent.getStringExtra("location");
                        viewHolder.setTitle(model.getName());
                        if(model.getLevel() == 1){
                            viewHolder.setName("Rights : Assistant Professor");
                        }
                        else if (model.getLevel() == 2){
                            viewHolder.setName("Rights : Head of Department");
                        }
                        else if (model.getLevel() == 99){
                            viewHolder.setName("Rights : Not Assigned");
                        }

                        viewHolder.setTime(model.getTime());
                        viewHolder.setImage(getApplicationContext(), model.getImages());
                        //  viewHolder.setTitle(model.getTitle());
                        //   viewHolder.setDesc(model.getDesc());

                        //    viewHolder.setImage(getApplicationContext(), model.getImages());

                        //    viewHolder.setDesc(model.getUsername());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (model.getLevel() == 99) {
                                    //Toast.makeText(AccountAdminPanel.this,Post_Key,Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(AccountAdminPanel.this, AdminApprove.class);
                                    intent.putExtra("postkey", Post_Key);
                                    startActivity(intent);
                                }
                                else {
                                    Intent intent = new Intent(AccountAdminPanel.this,AdminNotApproved.class);
                                    intent.putExtra("postkey", Post_Key);
                                    startActivity(intent);
                                }


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




        /*
        fabplus = (FloatingActionButton)findViewById(R.id.main_fab);
        fabplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //To add new notice code and shift control to AddNoticeActivityUser.
                startActivity(new Intent(getApplicationContext(),AddNoticeActivityUser.class));
                //To add new notice code
            }
        });
        */

        //  randomListing = new ArrayList<NoticeCard>();
        // adapter = new NoticeCardAdapter(this, randomListing);

        // swipeRefreshLayout.setOnRefreshListener(this);

        /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_user);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        loadNavHeader();

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
            Glide.with(context).load(image)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(context))
                    .into(post_image);

        }

        public void setTime(String time){

            TextView post_Desc = (TextView) mView.findViewById(R.id.card_timestamp);
            post_Desc.setText(time);
        }



    }



    private void loadNavHeader() {
        // name, website
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mCurrentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtName.setText(dataSnapshot.child("name").getValue().toString().trim());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        txtWebsite.setText(mAuth.getCurrentUser().getEmail());

        // loading header background image
        Glide.with(this).load(R.drawable.navmenuheaderbg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(R.drawable.user)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .into(imgProfile);

        // showing dot next to notifications label
        // navigationView.getMenu().getItem(2).setActionView(R.layout.menu_dot);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this,"work in progress",Toast.LENGTH_LONG).show();
            startActivity(new Intent(AccountAdminPanel.this, UserNoticeStatus.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(getApplicationContext(),EditViewProfile.class));
            //  Toast.makeText(this,"work in progress",Toast.LENGTH_LONG).show();
        }  else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Snackbar snackbar = Snackbar
                    .make(di, R.string.sign_out, Snackbar.LENGTH_LONG);
            snackbar.show();
            //Toast.makeText(AccountActivity.this, R.string.sign_out, Toast.LENGTH_LONG).show();
            startActivity(new Intent(AccountAdminPanel.this, MainActivity.class));
        } else if (id == R.id.nav_about_us) {
            startActivity(new Intent(getApplicationContext(),AboutUs.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_user);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Back button listener.
     * Will close the application if the back button pressed twice.
     */
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_user);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (back_pressed + 3000 > System.currentTimeMillis()){
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else{
                back_pressed = System.currentTimeMillis();
                Snackbar snackbar = Snackbar
                        .make(di, R.string.backpress, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }


}
