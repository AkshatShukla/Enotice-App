package com.rcoem.enotice.enotice_app.HighAuthorityClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;


import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Date;
import java.util.List;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rcoem.enotice.enotice_app.AboutUs;
import com.rcoem.enotice.enotice_app.AddNoticeClasses.AddNoticeTabbed;
import com.rcoem.enotice.enotice_app.AdminApprovalClasses.RetriverData;
import com.rcoem.enotice.enotice_app.AdminClasses.BlockUserPlanel;
import com.rcoem.enotice.enotice_app.AdminClasses.CrossDept;
import com.rcoem.enotice.enotice_app.BlogModel;
import com.rcoem.enotice.enotice_app.CircleTransform;
import com.rcoem.enotice.enotice_app.EditViewProfile;
import com.rcoem.enotice.enotice_app.NotificationClasses.EndPoints;
import com.rcoem.enotice.enotice_app.LoginSignUpClasses.MainActivity;
import com.rcoem.enotice.enotice_app.NoticeCard;
import com.rcoem.enotice.enotice_app.NoticeCardAdapter;
import com.rcoem.enotice.enotice_app.NotificationClasses.ActivitySendPushNotification;
import com.rcoem.enotice.enotice_app.NotificationClasses.MyFirebaseMessagingService;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.NotificationClasses.SharedPrefManager;
import com.rcoem.enotice.enotice_app.Utils;
import com.rcoem.enotice.enotice_app.ViewHolderClasses.DocumentNoticeViewHolder;
import com.rcoem.enotice.enotice_app.ViewHolderClasses.ImageNoticeViewHolder;
import com.rcoem.enotice.enotice_app.ViewHolderClasses.TextNoticeViewHolder;
import com.rcoem.enotice.enotice_app.pdfview;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AccountActivityAuthority extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.activity_account_authority);

        //SwipeRefresh Code
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

        startService(new Intent(this, MyFirebaseMessagingService.class));


        di = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("posts");
        mAuth = FirebaseAuth.getInstance();
        mDatabaseDepartment = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        //Code to send user token and details to hosted MySQL server
        //sendTokenToServer method called here
        mDatabaseDepartment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check if user exists in the firebase real-time database
                if (dataSnapshot.hasChildren()) {
                    String dept = dataSnapshot.child("department").getValue().toString();
                    sendTokenToServer(dept);
                } else {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do nothing
            }
        });

        //Code to display notices according to current user department
        //viewNotices method is called here
        mDatabaseDepartment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    String dept = dataSnapshot.child("department").getValue().toString().trim();
                    a = dept;
                    viewNotices(dept);
                    my_dept = dept;
                } else {
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do nothing
            }
        });

    }

    //Method to get token from firebase server and send token to MySQL server

    private void sendTokenToServer(final String dept) {
        mAuth = FirebaseAuth.getInstance();

        //getting token from shared preferences
        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String email = mAuth.getCurrentUser().getEmail();

        if (token == null) {
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }


        /*Following code is to send email, token, dept and post of current user
          to the database with POST method by taking end-points from EndPoints.java
          On response, appropriate toast is caught.
        */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(AccountActivityAuthority.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(AccountActivityAuthority.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("token", token);
                params.put("dept", dept);
                params.put("post", "Authority");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    //Method to view department specific notices in feed

    private void viewNotices(String dept) {

        //To show only the content relevant to the specific department.
        mDatabaseValidContent = FirebaseDatabase.getInstance().getReference().child("posts").child(dept).child("Approved");

        long currentTime = -1 * new Date().getTime();
        String time = "" + currentTime;

        //To query and view only those messages which have been APPROVED by the authenticator.
        mquery = mDatabaseValidContent.orderByChild("servertime");

        //Online-Offline Syncing (only strings and not images)
        mDatabaseValidContent.keepSynced(true);

        //BlogList view initialized to view notices in card layout list
        mBlogList = (RecyclerView) findViewById(R.id.blog_recylView_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Floating Action Button Functionality
        fabplus = (FloatingActionButton) findViewById(R.id.main_fab);

        fabplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivityAuthority.this, AddNoticeTabbedAuthority.class);
                startActivity(intent);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //View of Navigation Bar
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        loadNavHeader();


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
                            case Utils.TEXT_NOTICE:
                                View textNotice = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.notice_text_card, parent, false);
                                return new TextNoticeViewHolder(textNotice, Utils.ADMIN_VIEW);
                            case Utils.IMAGE_NOTICE:
                                View imageNotice = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.notice_image_card, parent, false);
                                return new ImageNoticeViewHolder(imageNotice, Utils.ADMIN_VIEW);
                            case Utils.DOCUMENT_NOTICE:
                                View documentNotice = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.notice_document_card, parent, false);
                                return new DocumentNoticeViewHolder(documentNotice, Utils.ADMIN_VIEW);
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

        mAuth = FirebaseAuth.getInstance();

    }

    //Card Display of Notices

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String username) {

            TextView post_Desc = (TextView) mView.findViewById(R.id.card_prof_name);
            post_Desc.setText(username);
        }

        public void setTitle(String title) {

            TextView post_title = (TextView) mView.findViewById(R.id.title_card);
            post_title.setText(title);
        }

        public void setDesc(String Desc) {

            TextView post_Desc = (TextView) mView.findViewById(R.id.card_name);
            post_Desc.setText(Desc);
        }

        public void setImage(final Context context, final String image) {

            final ImageView post_image = (ImageView) mView.findViewById(R.id.card_thumbnail123);
            //Picasso.with(context).load(image).into(post_image);

            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {
                    //Do Nothing
                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(post_image);
                }
            });

        }

        public void setTime(String time) {

            TextView post_Desc = (TextView) mView.findViewById(R.id.card_timestamp);
            post_Desc.setText(time);
        }


    }


    //Method to load current user details in the Navigation Bar header

    private void loadNavHeader() {
        // name, website
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mCurrentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtName.setText(dataSnapshot.child("name").getValue().toString().trim());

                Glide.with(getApplicationContext()).load(dataSnapshot.child("images").getValue().toString())
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(getApplicationContext()))
                        .into(imgProfile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do nothing
            }
        });

        //Set the current user email-id
        txtWebsite.setText(mAuth.getCurrentUser().getEmail());

        //Loading header background image
        Glide.with(this).load(R.drawable.navmenuheaderbg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);


        //COde to show dot next to a particular notifications label
        //navigationView.getMenu().getItem(2).setActionView(R.layout.menu_dot);
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
            //goto main Notice Feed
            startActivity(new Intent(getApplicationContext(), AccountActivityAuthority.class));

        } else if (id == R.id.nav_profile) {
            //goto Profile Setup activity
            startActivity(new Intent(getApplicationContext(), EditViewProfile.class));

        } else if (id == R.id.nav_approval) {
            //goto Pending Approvals activity
            startActivity(new Intent(AccountActivityAuthority.this, RetriverData.class));

        } else if (id == R.id.nav_logout) {
            //Log out from account
            mAuth.signOut();
            Snackbar snackbar = Snackbar
                    .make(di, R.string.sign_out, Snackbar.LENGTH_LONG);
            snackbar.show();
            startActivity(new Intent(AccountActivityAuthority.this, MainActivity.class));

        } else if (id == R.id.nav_about_us) {
            //startActivity(new Intent(getApplicationContext(), ActivitySendPushNotification.class));
            startActivity(new Intent(getApplicationContext(),AboutUs.class));

        } else if (id == R.id.ViewUsers) {
            //goto Block User Panel activity
            startActivity(new Intent(AccountActivityAuthority.this, BlockUserPanelAuthority.class));

        } else if (id == R.id.nav_otherDept) {
            //goto Cross-Dept Portal activity
            startActivity(new Intent(AccountActivityAuthority.this, CrossDept.class));

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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (back_pressed + 3000 > System.currentTimeMillis()) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                back_pressed = System.currentTimeMillis();

                Snackbar snackbar = Snackbar
                        .make(di, R.string.backpress, Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        }
    }

}
