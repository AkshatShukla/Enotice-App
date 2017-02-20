package com.rcoem.enotice.enotice_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.style.TtsSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class PdfUpload extends AppCompatActivity {
    private File root;
    private ArrayList<File> fileList = new ArrayList<File>();

    private ArrayList<String> fileList12 = new ArrayList<String>();

    private LinearLayout view;
    private ProgressDialog mProgress;
    private DatabaseReference mDataUser;
    private StorageReference mStoarge;
    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase;

    private String TitleText;
    private String DescText;
    private String noticeType;
    private String Approved;

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_upload);
        Intent intent = getIntent();
        TitleText = intent.getStringExtra("title_value");
        DescText = intent.getStringExtra("desc_value");
        noticeType = intent.getStringExtra("noticeType");

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
        getSupportActionBar().setTitle("Select a Document");

        view = (LinearLayout) findViewById(R.id.view);
        mProgress  = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String string = dataSnapshot.child("department").getValue().toString().trim();
                startPosting(string);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void startPosting(String string) {
        //mData = FirebaseDatabase.getInstance().getReference().child("posts").child(string).child("Document");
        mDataUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        //getting SDcard root path
        root = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath());

        final ArrayList<File> arr =   getfile(root);
        //   final ArrayList<String> arr1 =   getfile1(root);

        ListView lv = (ListView) findViewById(R.id.listViewAnimals);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                fileList12 );

        lv.setAdapter(arrayAdapter);

        mStoarge = FirebaseStorage.getInstance().getReference();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            // argument position gives the index of item which is clicked
            public void onItemClick(AdapterView<?> arg0, View v, final int position, long arg3)
            {

                new BottomDialog.Builder(PdfUpload.this)
                        .setTitle("Upload Document ("+noticeType+")")
                        .setContent("Upload the document and submit the notice. Are you sure you want to upload this document as your notice?")
                        .setPositiveText("Approve")
                        .setPositiveBackgroundColorResource(R.color.colorPrimary)
                        .setCancelable(false)
                        .setNegativeText("No")
                        .setPositiveTextColorResource(android.R.color.white)
                        //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
                        .onPositive(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(BottomDialog dialog) {
                                String selectedmovie = arr.get(position).toString();
                                final String okk = selectedmovie.substring(selectedmovie.lastIndexOf("/")+1,selectedmovie.length());
                                Toast.makeText(getApplicationContext(), okk,   Toast.LENGTH_LONG).show();
                                Uri mImageUri = Uri.fromFile(new File(selectedmovie));

                                calendar = Calendar.getInstance();
                                year = calendar.get(Calendar.YEAR);

                                month = calendar.get(Calendar.MONTH) + 1;    //Month in Calendar API start with 0.
                                day = calendar.get(Calendar.DAY_OF_MONTH);
                                //  Toast.makeText(AddNoticeActivityAdmin.this,day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                                final String currentDate = day + "/" + month + "/" + year;
                                final long currentLongTime = -1 * new Date().getTime();
                                final String currentTime = "" + currentLongTime;

                                final AlertDialog dialog1 = new SpotsDialog(PdfUpload.this, R.style.CustomProgress);
                                dialog1.show();


                                StorageReference filepath = mStoarge.child("pdf").child(mImageUri.getLastPathSegment());
                                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();


                                        mDataUser.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(final DataSnapshot dataSnapshot) {

                                                final String Dept = dataSnapshot.child("department").getValue().toString().trim();
                                                final String lvlCheck = dataSnapshot.child("level").getValue().toString().trim();

                                                if (lvlCheck.equals("1")) {
                                                    mData = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Pending");
                                                    Approved = "false";
                                                }
                                                else if (lvlCheck.equals("2")){
                                                    mData = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Approved");
                                                    Approved = "true";
                                                }

                                                final DatabaseReference newPost = mData.push();

                                                newPost.child("type").setValue(3);
                                                newPost.child("label").setValue(noticeType);
                                                newPost.child("title").setValue(TitleText);
                                                newPost.child("Desc").setValue(DescText);
                                                newPost.child("UID").setValue(mAuth.getCurrentUser().getUid());
                                                newPost.child("email").setValue(mAuth.getCurrentUser().getEmail());
                                                newPost.child("username").setValue(dataSnapshot.child("name").getValue());
                                                newPost.child("profileImg").setValue(dataSnapshot.child("images").getValue());
                                                //Passing Default PDF Image for Web App Viewing
                                                newPost.child("images").setValue("https://firebasestorage.googleapis.com/v0/b/e-notice-board-83d16.appspot.com/o/pdf-file-format-symbol.png?alt=media&token=b9661fd2-0644-4340-82e8-c96662db26dc");
                                                newPost.child("time").setValue(currentDate);
                                                newPost.child("servertime").setValue(currentLongTime);
                                                newPost.child("link").setValue(downloadUrl.toString());
                                                newPost.child("department").setValue(Dept);
                                                newPost.child("approved").setValue(Approved).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(PdfUpload.this,"Uploaded Successfully", Toast.LENGTH_LONG).show();
                                                            if (lvlCheck.equals("2")) {
                                                                departmentPushDept(TitleText,"Notice by HOD ".concat(dataSnapshot.child("name").getValue().toString()),Dept);
                                                            }
                                                            else if (lvlCheck.equals("1")) {
                                                                departmentPushHOD(TitleText,"Pending Notice Approval sent by ".concat(dataSnapshot.child("name").getValue().toString()),Dept);
                                                            }
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toasty.error(PdfUpload.this,"Connection Error").show();
                                            }
                                        });

                                        dialog1.dismiss();
                                        startActivity(new Intent(PdfUpload.this,AddNoticeTabbed.class));
                                        finish();

                                    }

                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
                                                mProgress.dismiss();
                                            }
                                        });
                            }
                        }).show();

            }
        });

    }

    public ArrayList<File> getfile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    // fileList.add(listFile[i]);
                    getfile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".pdf") || listFile[i].getName().endsWith(".txt")
                            || listFile[i].getName().endsWith(".docx")
                            || listFile[i].getName().endsWith(".xlsx")
                            || listFile[i].getName().endsWith(".doc")
                            )

                    {
                        String ab = listFile[i].toString();
                        String bbb = ab.substring(ab.lastIndexOf("/")+1,ab.length());
                        fileList.add(listFile[i]);
                        fileList12.add(bbb);
                    }
                }

            }
        }
        return fileList;
    }

    private void departmentPushDept(final String t, final String m, final String dept) {
        final String title = t;
        final String message = m;
        final String email = "dhanajay@gmail.com";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH_DEPT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();

                        //Toast.makeText(AddNoticeActivityUser.this, response, Toast.LENGTH_LONG).show();
                        //Toasty.custom(getActivity().getApplicationContext(), "Department Teachers will be notified of your Notice", R.mipmap.ic_launcher, getResources().getColor(R.color.colorWhite), getResources().getColor(R.color.colorBg), 100, false, true).show();
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

    private void departmentPushHOD(final String t,final String m,final String dept) {
        final String title = t;
        final String message = m;
        final String email = "dhanajay@gmail.com";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH_HOD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();

                        //Toast.makeText(AddNoticeActivityUser.this, response, Toast.LENGTH_LONG).show();
                        //Toasty.custom(getActivity().getApplicationContext(), "HOD will be notified of your Notice", R.mipmap.ic_launcher, getResources().getColor(R.color.colorWhite), getResources().getColor(R.color.colorBg), 100, false, true).show();
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

}