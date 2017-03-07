package com.rcoem.enotice.enotice_app.UserClasses;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rcoem.enotice.enotice_app.AboutUs;
import com.rcoem.enotice.enotice_app.AddNoticeClasses.AddNoticeTabbed;
import com.rcoem.enotice.enotice_app.CircleTransform;
import com.rcoem.enotice.enotice_app.EditViewProfile;
import com.rcoem.enotice.enotice_app.LoginSignUpClasses.MainActivity;
import com.rcoem.enotice.enotice_app.NoticeCard;
import com.rcoem.enotice.enotice_app.NoticeCardAdapter;
import com.rcoem.enotice.enotice_app.NotificationClasses.ActivitySendPushNotification;
import com.rcoem.enotice.enotice_app.NotificationClasses.EndPoints;
import com.rcoem.enotice.enotice_app.NotificationClasses.MyFirebaseMessagingService;
import com.rcoem.enotice.enotice_app.NotificationClasses.SharedPrefManager;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.UserNoticeStatusClasses.UserNoticeStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountActivityUser extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

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
    private DatabaseReference currentUserStatus;

    FloatingActionButton fabplus;

    FirebaseAuth mAuth;

    private String dept;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.text_notice,
            R.drawable.image_notice
    };

    DrawerLayout di;
    private DatabaseReference mDatabaseValidContent;
    Query mquery;
    long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_user);
        mAuth = FirebaseAuth.getInstance();

        /*
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
                },1000);
            }
        });
        */

        startService(new Intent(this, MyFirebaseMessagingService.class));

        di = (DrawerLayout) findViewById(R.id.drawer_layout_user);

        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        //Code to send user token and details to hosted MySQL server
        //sendTokenToServer method called here
        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check if user exists in the firebase real-time database
                if(dataSnapshot.hasChildren()) {
                    dept = dataSnapshot.child("department").getValue().toString();
                    sendTokenToServer(dept);
                }
                else {
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do nothing
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabplus = (FloatingActionButton)findViewById(R.id.main_fab);

        currentUserStatus = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        fabplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUserStatus.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.child("block").getValue().toString().trim();

                        if (status.equals("No")) {
                            Intent intent = new Intent(AccountActivityUser.this, AddNoticeTabbed.class);
                            startActivity(intent);
                        }
                        else {
                            Snackbar snackbar = Snackbar
                                    .make(di, "You are unauthorized to generate notices. Please contact your HOD.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

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

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //Added for Seamless Floating Action Button Animation Transition between Tabs.
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1:
                        if (!fabplus.isShown()) {
                            fabplus.show();
                        }
                        break;
                    case 2:
                        if (!fabplus.isShown()) {
                            fabplus.show();
                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AccountDeptPostsUser(), "Department Feed");
        adapter.addFragment(new AccountAllPostsUser(), "University Feed");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //Method to get token from firebase server and send token to MySQL server

    private void sendTokenToServer(final String dept) {
        mAuth = FirebaseAuth.getInstance();
        //getting token from shared preferences
        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String email = mAuth.getCurrentUser().getEmail();

        //String Str1 = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("department").toString();

        //Toast.makeText(this, dept, Toast.LENGTH_LONG).show();

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
                            Toast.makeText(AccountActivityUser.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(AccountActivityUser.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("token", token);
                params.put("dept", dept);
                params.put("post","AP");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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

            }
        });
        txtWebsite.setText(mAuth.getCurrentUser().getEmail());

        // loading header background image
        Glide.with(this).load(R.drawable.navmenuheaderbg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
       /* Glide.with(this).load(R.drawable.user)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .into(imgProfile);*/

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

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(getApplicationContext(),EditViewProfile.class));
            //  Toast.makeText(this,"work in progress",Toast.LENGTH_LONG).show();
        } else if(id == R.id.nav_checkstatus){
            //Toast.makeText(this,"work in progress",Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(),UserNoticeStatus.class));
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Snackbar snackbar = Snackbar
                    .make(di, R.string.sign_out, Snackbar.LENGTH_LONG);
            snackbar.show();
            startActivity(new Intent(AccountActivityUser.this, MainActivity.class));
        } else if (id == R.id.nav_about_us) {
            //startActivity(new Intent(getApplicationContext(),ActivitySendPushNotification.class));
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
