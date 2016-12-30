package com.rcoem.enotice.enotice_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

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

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);


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
                        String imageUrl =  dataSnapshot.child("images").getValue().toString().trim();
                        viewImage(imageUrl);
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
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(process) {
                            mDatabase.child("approved").setValue("true");
                            process = false;
                            String title = dataSnapshot.child("title").getValue().toString().trim();
                            String message = dataSnapshot.child("username").getValue().toString().trim();
                            String dept = dataSnapshot.child("department").getValue().toString().trim();
                            String image = dataSnapshot.child("images").getValue().toString().trim();
                            departmentPush(title,message,dept,image);

                            Toast.makeText(Single_Post.this, "The notice has been Approved", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Single_Post.this, AccountActivityAdmin.class);
                            startActivity(intent);
                            finish();
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
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(process) {
                            mDatabase.child("approved").setValue("false");
                            mDatabase.child("removed").setValue(1);
                            process = false;
                            String title = dataSnapshot.child("title").getValue().toString().trim();
                            String message ="Your Notice has been Rejected";
                            String image = dataSnapshot.child("images").getValue().toString().trim();
                            String email = dataSnapshot.child("email").getValue().toString().trim();
                           // Toast.makeText(Single_Post.this, email, Toast.LENGTH_LONG).show();
                            sendSinglePush(title,message,image,email);
                            //Toast.makeText(Single_Post.this, "The notice has been Rejected", Toast.LENGTH_LONG).show();


                            //AlertDialog alert11 = builder1.create();
                            //alert11.show();
                            //Intent intent = new Intent(Single_Post.this, AccountActivityAdmin.class);
                            // startActivity(intent);
                            finish();
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