package com.rcoem.enotice.enotice_app.AdminApprovalClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rcoem.enotice.enotice_app.AdminClasses.AccountActivityAdmin;
import com.rcoem.enotice.enotice_app.HighAuthorityClasses.AccountActivityAuthority;
import com.rcoem.enotice.enotice_app.LoginSignUpClasses.MainActivity;
import com.rcoem.enotice.enotice_app.NotificationClasses.EndPoints;
import com.rcoem.enotice.enotice_app.NotificationClasses.MyVolley;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.fullScreenImage;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class TextNoticeApproval extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private DatabaseReference mDataApproved;
    private TextView mPostTitle;
    private TextView mPostDesc;
    private TextView profileName;
    private TextView Date;
    private ImageView circularImageView;

    private Button Approved;
    private Button Rejected;
    private Button Share;
    private Uri mImageUri = null;
    private StorageReference mStoarge;
    private boolean process;
    RelativeLayout ri;
    Toolbar mActionBarToolbar;
    private ProgressDialog progressDialog;
    private String feedback;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_notice_approval);
        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");

        mAuth = FirebaseAuth.getInstance();

        mPostTitle = (TextView) findViewById(R.id.Edit_Title_field1) ;
        mPostDesc = (TextView) findViewById(R.id.Edit_description_field1);
        profileName = (TextView) findViewById(R.id.profileName);
        Date = (TextView) findViewById(R.id.date);
        circularImageView = (ImageView) findViewById(R.id.imageView);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(str);
        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mStoarge = FirebaseStorage.getInstance().getReference();
        mPostDesc.setText(str);

        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String string1 = dataSnapshot.child("level").getValue().toString().trim();

                if (string1.equals("2")) {
                    mDataApproved = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Approved").push();
                }
                else {
                    mDataApproved = FirebaseDatabase.getInstance().getReference().child("posts").child("ALL").child("Approved").push();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!TextNoticeApproval.this.isFinishing()) {
                    if (dataSnapshot.hasChildren()) {
                        mPostTitle.setText(dataSnapshot.child("title").getValue().toString().trim());
                        mPostDesc.setText(dataSnapshot.child("Desc").getValue().toString().trim());
                        profileName.setText(dataSnapshot.child("username").getValue().toString().trim());
                        String date = "on " + dataSnapshot.child("time").getValue().toString().trim();
                        Date.setText(date);
                        String profilePic = dataSnapshot.child("profileImg").getValue().toString().trim();
                        Glide.with(TextNoticeApproval.this).load(profilePic).crossFade().into(circularImageView);
                        //mActionBarToolbar.setTitle(dataSnapshot.child("title").getValue().toString().trim());
                        toolbar.setTitle(dataSnapshot.child("title").getValue().toString().trim());
                    } else {
                        isFinishing();
                        //finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Approved = (Button) findViewById(R.id.Approve_button);
        process = true;
        Approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if (!TextNoticeApproval.this.isFinishing()) {

                            if (process) {

                                new BottomDialog.Builder(TextNoticeApproval.this)
                                        .setTitle("Approve Notice")
                                        .setContent("Approved Notices appear on the News Feed as well as on Notice Boards across your department. Are you sure you want to Approve?")
                                        .setPositiveText("Approve")
                                        .setPositiveBackgroundColorResource(R.color.colorPrimary)
                                        .setCancelable(false)
                                        .setNegativeText("No")
                                        .setPositiveTextColorResource(android.R.color.white)
                                        //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
                                        .onPositive(new BottomDialog.ButtonCallback() {
                                            @Override
                                            public void onClick(BottomDialog dialog) {
                                                mDatabase.child("approved").setValue("true");
                                                process = false;

                                                long serverTime = -1 * new Date().getTime();

                                                Calendar calendar = Calendar.getInstance();
                                                int year = calendar.get(Calendar.YEAR);

                                                int month = calendar.get(Calendar.MONTH) + 1;    //Month in Calendar API start with 0.
                                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                                //  Toast.makeText(AddNoticeActivityAdmin.this,day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                                                final String currentDate = day + "/" + month + "/" + year;

                                                String label = dataSnapshot.child("label").getValue().toString().trim();
                                                String title = dataSnapshot.child("title").getValue().toString().trim();
                                                String desc = dataSnapshot.child("Desc").getValue().toString().trim();
                                                String uid = dataSnapshot.child("UID").getValue().toString().trim();
                                                String message = dataSnapshot.child("username").getValue().toString().trim();
                                                String profileImg = dataSnapshot.child("profileImg").getValue().toString().trim();
                                                String dept = dataSnapshot.child("department").getValue().toString().trim();


                                                mDataApproved.child("type").setValue(1);
                                                mDataApproved.child("label").setValue(label);
                                                mDataApproved.child("title").setValue(title);
                                                mDataApproved.child("Desc").setValue(desc);
                                                mDataApproved.child("UID").setValue(uid);
                                                //Missing email Attribute
                                                mDataApproved.child("username").setValue(message);
                                                mDataApproved.child("profileImg").setValue(profileImg);
                                                //Passing Default Text Image for Web App Viewing
                                                mDataApproved.child("images").setValue("https://firebasestorage.googleapis.com/v0/b/e-notice-board-83d16.appspot.com/o/txt-file-symbol.png?alt=media&token=3a8beb43-561f-4f69-a6ad-58d2683abe81");
                                                mDataApproved.child("time").setValue(currentDate);
                                                mDataApproved.child("servertime").setValue(serverTime);
                                                //Passing Default Text Doc Link for Web App Viewing
                                                mDataApproved.child("link").setValue("gs://e-notice-board-83d16.appspot.com/pdf/debug.txt");
                                                mDataApproved.child("department").setValue(dept);
                                                mDataApproved.child("approved").setValue("true");

                                                departmentPush(title, message, dept);

                                                Toasty.custom(TextNoticeApproval.this, "Notice has been Approved", R.drawable.ok, getResources().getColor(R.color.colorWhite), getResources().getColor(R.color.unblocked), 100, true, true).show();

                                                mDatabase1.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        final String string = dataSnapshot.child("level").getValue().toString().trim();

                                                        if (string.equals("2")) {
                                                            Intent intent = new Intent(TextNoticeApproval.this, AccountActivityAdmin.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Intent intent = new Intent(TextNoticeApproval.this, AccountActivityAuthority.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                            }
                                        }).show();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        Rejected = (Button) findViewById(R.id.Reject_button);
        Rejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if (!TextNoticeApproval.this.isFinishing()) {
                            if (process) {

                                process = false;
                                new BottomDialog.Builder(TextNoticeApproval.this)
                                        .setTitle("Reject Notice")
                                        .setContent("Rejected Notices do not appear on the News Feed as well as on Notice Boards across your department. User who's notice has been rejected is also notified. Are you sure you want to Reject?")
                                        .setPositiveText("Reject")
                                        .setPositiveBackgroundColorResource(R.color.colorPrimary)
                                        .setCancelable(false)
                                        .setNegativeText("No")
                                        .onNegative(new BottomDialog.ButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull BottomDialog bottomDialog) {
                                                process = true;
                                            }
                                        })
                                        .setPositiveTextColorResource(android.R.color.white)
                                        //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
                                        .onPositive(new BottomDialog.ButtonCallback() {
                                            @Override
                                            public void onClick(BottomDialog dialog) {

                                                new MaterialDialog.Builder(TextNoticeApproval.this)
                                                        .title("Feedback")
                                                        .content("Notify reason for rejection")
                                                        .cancelable(true)
                                                        .positiveColor(getResources().getColor(R.color.colorBg))
                                                        .positiveText("Send")
                                                        .negativeText("Dismiss")
                                                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                                                        .input("Reason", "", new MaterialDialog.InputCallback() {
                                                            @Override
                                                            public void onInput(MaterialDialog dialog, CharSequence input) {

                                                                feedback = input.toString();
                                                                String title = dataSnapshot.child("title").getValue().toString().trim();
                                                                String email = dataSnapshot.child("email").getValue().toString().trim();
                                                                sendSinglePush(title, feedback, email);

                                                                Toasty.custom(TextNoticeApproval.this, "Notice has been Rejected", R.drawable.cancel, getResources().getColor(R.color.colorWhite), getResources().getColor(R.color.blocked), 100, true, true).show();
                                                                Intent intent = new Intent(TextNoticeApproval.this, AccountActivityAdmin.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        })
                                                        .dismissListener(new DialogInterface.OnDismissListener() {
                                                            @Override
                                                            public void onDismiss(DialogInterface dialog) {
                                                                process = true;
                                                                Toasty.warning(TextNoticeApproval.this,"Notice Rejection Dismissed", 2500).show();
                                                            }
                                                        })
                                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                mDatabase.child("approved").setValue("false");
                                                                Toasty.custom(TextNoticeApproval.this, "Notice has been Rejected", R.drawable.cancel, getResources().getColor(R.color.colorWhite), getResources().getColor(R.color.blocked), 100, true, true).show();
                                                                Intent intent = new Intent(TextNoticeApproval.this, AccountActivityAdmin.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        })
                                                        .show();

                                            }
                                        }).show();

                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


    }

    //Method to send notification of approved notice to all users in the current Admin's Department
    private void departmentPush(final String title,final String message,final String dept){

        final String email = "dhanajay@gmail.com";
        //progressDialog.setMessage("Sending Dept Push");
        // progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH_DEPT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();

                        Toast.makeText(TextNoticeApproval.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);


                params.put("email", email);
                params.put("dept",dept);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void viewImage(String imageUrl) {
        // Toast.makeText(AdminSinglePost.this,imageUrl, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(TextNoticeApproval.this,fullScreenImage.class);
        intent.putExtra("imageUrl",imageUrl);
        startActivity(intent);
    }

    //Method to send notification to the specific user who's notification has been rejected
    private void sendSinglePush(final String title,final String message,final String email){

        //  progressDialog.setMessage("Sending Push");
        // progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   progressDialog.dismiss();

                        Toast.makeText(TextNoticeApproval.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);


                params.put("email", email);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

}