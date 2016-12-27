package com.rcoem.enotice.enotice_app;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AccountActivityAdmin extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

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
    private DatabaseReference mDatabaseDepartment;

    private String department;


    // private  String Dept;
    FloatingActionButton fabplus;
    FloatingActionButton fabaddNotice;
    FloatingActionButton fabaddDocument;
    TextView textaddNotice;
    TextView textaddDocument;

    Animation open;
    Animation close;
    Animation rClock;
    Animation rAntiClock;

    Boolean isOpen = false;

    private int count = 0;

    private Button signOut;

    private int backButtonCount = 0;

    FirebaseAuth mAuth;
    private DatabaseReference mDatabaseValidContent;
    Query mquery;
    static String a;
    FirebaseUser currentUser;

    DrawerLayout di;
    long back_pressed;

    String my_dept = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_admin);


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

        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("posts");
        mAuth = FirebaseAuth.getInstance();
        mDatabaseDepartment = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mDatabaseDepartment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    String dept = dataSnapshot.child("department").getValue().toString().trim();
                    a = dept;
                    viewNotices(dept);
                    my_dept = dept;
                }
                else {
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void viewNotices(String dept) {
        //Toast.makeText(AccountActivityAdmin.this,dept,Toast.LENGTH_LONG).show();
        /*if (dept == "Mech") {
            Snackbar snackbar = Snackbar
                    .make(di, R.string.welcome_mech, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else
        {
            Snackbar snackbar = Snackbar
                    .make(di, R.string.welcome_cse, Snackbar.LENGTH_LONG);
            snackbar.show();
        }*/
        //To show only the content relevant to the specific department.
        mDatabaseValidContent = FirebaseDatabase.getInstance().getReference().child("posts").child(dept).child("Deptposts");

        //To query and view only those messages which have been APPROVED by the authenticator.
        mquery =  mDatabaseValidContent.orderByChild("approved").equalTo("true");


        mBlogList = (RecyclerView) findViewById(R.id.blog_recylView_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));



        FirebaseRecyclerAdapter<BlogModel,BlogViewHolder> firebaseRecyclerAdapter =new
                FirebaseRecyclerAdapter<BlogModel, BlogViewHolder>(

                        BlogModel.class,
                        R.layout.blog_row,
                        BlogViewHolder.class,
                        mquery
                ) {
                    @Override
                    protected void populateViewHolder(BlogViewHolder viewHolder, BlogModel model, final int position) {

                        final String Post_Key = getRef(position).toString();
                        //   Intent intent = getIntent();
                        //   final String str = intent.getStringExtra("location");
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setDesc(model.getDesc());

                        viewHolder.setImage(getApplicationContext(), model.getImages());

                        viewHolder.setDesc(model.getUsername());

                        viewHolder.setTime(model.getTime());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //To code Card-expanding code.



                               // Toast.makeText(AccountActivityAdmin.this,Post_Key,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(AccountActivityAdmin.this,AdminSinglePost.class);
                                intent.putExtra("postkey",Post_Key);
                                startActivity(intent);

                            }
                        });
                    }
                };
        //  mProgress.dismiss();



        di = (DrawerLayout) findViewById(R.id.drawer_layout_admin);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Floating Action Button Functionality
        //Generate New Notice
        /*
        addNotice = (com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.fab_addnotice);
        addNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To add new notice code and shift control to AddNoticeActivityAdmin.
                Intent intent = new Intent(AccountActivityAdmin.this, AddNoticeActivityAdmin.class);
                startActivity(intent);
            }
        });

        addDocument = (com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.fab_adddocument);
        addDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivityAdmin.this,PdfUpload.class);
                startActivity(intent);
            }
        });*/

        fabplus = (FloatingActionButton)findViewById(R.id.main_fab);
        fabaddNotice = (FloatingActionButton) findViewById(R.id.add_notice_fab);
        fabaddDocument = (FloatingActionButton) findViewById(R.id.add_document_fab);

        textaddNotice = (TextView) findViewById(R.id.add_notice_text);
        textaddDocument = (TextView) findViewById(R.id.add_document_text);

        open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        close=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rClock=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        rAntiClock=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);

        fabplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOpen){
                    fabaddNotice.startAnimation(close);
                    fabaddDocument.startAnimation(close);
                    textaddNotice.startAnimation(close);
                    textaddDocument.setAnimation(close);

                    fabplus.startAnimation(rAntiClock);

                    fabaddDocument.setClickable(false);
                    fabaddNotice.setClickable(false);

                    isOpen = false;
                }
                else{
                    fabaddNotice.startAnimation(open);
                    fabaddDocument.startAnimation(open);
                    textaddNotice.startAnimation(open);
                    textaddDocument.setAnimation(open);

                    fabplus.startAnimation(rClock);

                    fabaddDocument.setClickable(true);
                    fabaddDocument.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(),PdfUpload.class);
                            startActivity(intent);
                        }
                    });

                    fabaddNotice.setClickable(true);
                    fabaddNotice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //To add new notice code and shift control to AddNoticeActivityAdmin.
                            Intent intent = new Intent(AccountActivityAdmin.this, AddNoticeActivityAdmin.class);
                            startActivity(intent);
                        }
                    });

                    isOpen = true;
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
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

    }

    //Card Display of Notices

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

        //txtName.setText("Prof. S P Kane");
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

    //Navigation Bar

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {


        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(getApplicationContext(),EditViewProfile.class));
            //  Toast.makeText(this,"work in progress",Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_approval) {
            startActivity(new Intent(AccountActivityAdmin.this,RetriverData.class));
            // Toast.makeText(this,"work in progress",Toast.LENGTH_LONG).show();
        } else if(id == R.id.nav_documents){
            //Toast.makeText(this,"work in progress",Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(),pdfview.class));
        }
        else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Snackbar snackbar = Snackbar
                    .make(di, R.string.sign_out, Snackbar.LENGTH_LONG);
            snackbar.show();
            //Toast.makeText(AccountActivity.this, R.string.sign_out, Toast.LENGTH_LONG).show();
            startActivity(new Intent(AccountActivityAdmin.this, MainActivity.class));
        } else if (id == R.id.nav_about_us) {
            Snackbar snackbar = Snackbar
                    .make(di, "Coded with love by CSE, RCOEM", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else if (id == R.id.ViewUsers) {
            //  Toast.makeText(AccountActivityAdmin.this, R.string.sign_out, Toast.LENGTH_LONG).show();
            startActivity(new Intent(AccountActivityAdmin.this,BlockUserPlanel.class));
        }
        else if (id == R.id.nav_otherDept) {
            //  Toast.makeText(AccountActivityAdmin.this, R.string.sign_out, Toast.LENGTH_LONG).show();
            startActivity(new Intent(AccountActivityAdmin.this,CrossDept.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Back button listener.
     * Will close the application if the back button pressed twice.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    String FilePath = data.getData().getPath();
                    //textFile.setText(FilePath);
                  //  Toast.makeText(this,FilePath.toString(),Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
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




    /* @Override
    public void onRefresh() {
       swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setRefreshing(false);
    }*/



}
