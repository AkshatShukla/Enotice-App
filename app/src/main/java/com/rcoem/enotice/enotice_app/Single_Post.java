package com.rcoem.enotice.enotice_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class Single_Post extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private TextView mPostTitle;
    private TextView mPostDesc;
    private ImageButton mViewImage;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");

        mPostTitle = (TextView) findViewById(R.id.Edit_Title_field1) ;
        mPostDesc = (TextView) findViewById(R.id.Edit_description_field1);
        mViewImage = (ImageButton) findViewById(R.id.select_image_Button1);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(str);
        mStoarge = FirebaseStorage.getInstance().getReference();
        mPostDesc.setText(str);

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


        final AlertDialog.Builder builder1 = new AlertDialog.Builder(Single_Post.this);
        builder1.setMessage("Do yo want to reject and remove this Notice?");
        builder1.setCancelable(true);


        mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChildren()) {
                            mPostTitle.setText(dataSnapshot.child("title").getValue().toString().trim());
                            mPostDesc.setText(dataSnapshot.child("Desc").getValue().toString().trim());
                            String imageUrl = dataSnapshot.child("images").getValue().toString().trim();
                            Picasso.with(Single_Post.this).load(imageUrl).into(mViewImage);
                            //mActionBarToolbar.setTitle(dataSnapshot.child("title").getValue().toString().trim());
                            toolbar.setTitle(dataSnapshot.child("title").getValue().toString().trim());
                        }
                    else {
                            finish();
                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
        });

        mViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            String imageUrl = dataSnapshot.child("images").getValue().toString().trim();
                            viewImage(imageUrl);
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
        });

        Approved = (Button) findViewById(R.id.Approve_button);
        process = true;
        Approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if(process) {

                            new BottomDialog.Builder(Single_Post.this)
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
                                           final DatabaseReference mDataApproved = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Approved").push();
                                            long serverTime = new Date().getTime();

                                            Calendar calendar = Calendar.getInstance();
                                            int year = calendar.get(Calendar.YEAR);

                                            int month = calendar.get(Calendar.MONTH) + 1;    //Month in Calendar API start with 0.
                                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                                            //  Toast.makeText(AddNoticeActivityAdmin.this,day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                                            final String currentDate = day + "/" + month + "/" + year;

                                            String title = dataSnapshot.child("title").getValue().toString().trim();
                                            String message = dataSnapshot.child("username").getValue().toString().trim();
                                            String dept = dataSnapshot.child("department").getValue().toString().trim();
                                            String image = dataSnapshot.child("images").getValue().toString().trim();
                                            String desc = dataSnapshot.child("Desc").getValue().toString().trim();

                                            mDataApproved.child("title").setValue(title);
                                            mDataApproved.child("username").setValue(message);
                                            mDataApproved.child("department").setValue(dept);
                                            mDataApproved.child("images").setValue(image);
                                            mDataApproved.child("servertime").setValue(serverTime);
                                            mDataApproved.child("date").setValue(currentDate);
                                            mDataApproved.child("Desc").setValue(desc);
                                            departmentPush(title,message,dept,image);

                                            Toasty.custom(Single_Post.this, "Notice has been Approved", R.drawable.ok, getResources().getColor(R.color.colorWhite), getResources().getColor(R.color.unblocked), 100, true, true).show();
                                            Intent intent = new Intent(Single_Post.this, AccountActivityAdmin.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).show();

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
                        if(process) {

                            new BottomDialog.Builder(Single_Post.this)
                                    .setTitle("Reject Notice")
                                    .setContent("Rejected Notices do not appear on the News Feed as well as on Notice Boards across your department. User who's notice has been rejected is also notified. Are you sure you want to Reject?")
                                    .setPositiveText("Reject")
                                    .setPositiveBackgroundColorResource(R.color.colorPrimary)
                                    .setCancelable(false)
                                    .setNegativeText("No")
                                    .onNegative(new BottomDialog.ButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull BottomDialog bottomDialog) {

                                        }
                                    })
                                    .setPositiveTextColorResource(android.R.color.white)
                                    //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
                                    .onPositive(new BottomDialog.ButtonCallback() {
                                        @Override
                                        public void onClick(BottomDialog dialog) {
                                            mDatabase.child("approved").setValue("false");
                                            mDatabase.child("removed").setValue(1);

                                            new MaterialDialog.Builder(Single_Post.this)
                                                    .title("Feedback")
                                                    .content("Notify reason for rejection")
                                                    .cancelable(true)
                                                    .positiveColor(getResources().getColor(R.color.colorBg))
                                                    .positiveText("Send")
                                                    .negativeText("Dismiss")
                                                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE)
                                                    .input("Reason", "", new MaterialDialog.InputCallback() {
                                                        @Override
                                                        public void onInput(MaterialDialog dialog, CharSequence input) {

                                                            process = false;
                                                            feedback = input.toString();
                                                            String title = dataSnapshot.child("title").getValue().toString().trim();
                                                            String image = dataSnapshot.child("images").getValue().toString().trim();
                                                            String email = dataSnapshot.child("email").getValue().toString().trim();
                                                            sendSinglePush(title,feedback,image,email);

                                                            Toasty.custom(Single_Post.this, "Notice has been Rejected", R.drawable.cancel, getResources().getColor(R.color.colorWhite), getResources().getColor(R.color.blocked), 100, true, true).show();
                                                            Intent intent = new Intent(Single_Post.this, AccountActivityAdmin.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }).show();

                                        }
                                    }).show();

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        /*
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(Single_Post.this, "Your post is Rejected", Toast.LENGTH_LONG).show();
                Toast.makeText(Single_Post.this, "The notice has been Rejected and Removed", Toast.LENGTH_LONG).show();
               // mDatabase.removeValue();
                Intent intent = new Intent(Single_Post.this, RetriverData.class);
                startActivity(intent);

            }
        });
        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(Single_Post.this, RetriverData.class);
                        startActivity(intent);

                    }
                });
        */

    }

    //Method to send notification of approved notice to all users in the current Admin's Department
    private void departmentPush(final String title,final String message,final String dept,final String image){

        final String email = "dhanajay@gmail.com";
        //progressDialog.setMessage("Sending Dept Push");
       // progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH_DEPT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // progressDialog.dismiss();

                        Toast.makeText(Single_Post.this, response, Toast.LENGTH_LONG).show();
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

                if (!TextUtils.isEmpty(image))
                    params.put("image", image);

                params.put("email", email);
                params.put("dept",dept);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void viewImage(String imageUrl) {
        // Toast.makeText(AdminSinglePost.this,imageUrl, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Single_Post.this,fullScreenImage.class);
        intent.putExtra("imageUrl",imageUrl);
        startActivity(intent);
    }

    //Method to send notification to the specific user who's notification has been rejected
    private void sendSinglePush(final String title,final String message,final String image,final String email){

        //Toast.makeText(Single_Post.this, email, Toast.LENGTH_LONG).show();

        //Toast.makeText(Single_Post.this, email, Toast.LENGTH_LONG).show();
      //  progressDialog.setMessage("Sending Push");
       // progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                     //   progressDialog.dismiss();

                        Toast.makeText(Single_Post.this, response, Toast.LENGTH_LONG).show();
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

                if (!TextUtils.isEmpty(image))
                    params.put("image", image);

                params.put("email", email);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

}