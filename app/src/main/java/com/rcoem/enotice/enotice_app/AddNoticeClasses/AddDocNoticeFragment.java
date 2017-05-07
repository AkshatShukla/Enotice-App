package com.rcoem.enotice.enotice_app.AddNoticeClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.rcoem.enotice.enotice_app.NotificationClasses.EndPoints;
import com.rcoem.enotice.enotice_app.NotificationClasses.MyVolley;
import com.rcoem.enotice.enotice_app.R;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

/**
 * Created by Akshat Shukla on 12-02-2017.
 */

public class AddDocNoticeFragment extends Fragment {

    private StorageReference mStoarge;
    private DatabaseReference mData;
    private DatabaseReference mDataUser;
    private DatabaseReference mDatabase1;
    private DatabaseReference mDataBaseDepartment;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private EditText titleDoc;
    private EditText descDoc;
    private Button btnSubmitDoc;
    private Spinner spinnerDoc;

    private String noticeType;
    private String strdept;
    private String titleDoc_value;
    private String descDoc_value;

    private Uri uri;
    private String Approved;

    private boolean docOK;

    private String [] typeArray =
            {       "For Students",
                    "For Teachers",
                    "Urgent",
                    "Normal",
                    "Assignment",
                    "Time Table"};

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    View docView;
    Activity context;

    public AddDocNoticeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strdept = getActivity().getIntent().getStringExtra("postkey");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        docView = inflater.inflate(R.layout.activity_add_doc_notice, container, false);

        // Inflate the layout for this fragment
        return docView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
            StringBuilder builder = new StringBuilder();
            String path = null;
            for (NormalFile file : list) {
                path = file.getPath();
                builder.append(path + "\n");
            }
            path = "file:///" + path;
            if (path.contains("null")) {
                Toasty.warning(context,"No file chosen").show();
            } else {
                uri = Uri.parse(path);
                Toast.makeText(context, uri.toString(), Toast.LENGTH_LONG).show();
                mStoarge = FirebaseStorage.getInstance().getReference();
                final AlertDialog dialog1 = new SpotsDialog(context, R.style.CustomProgress);
                dialog1.show();
                StorageReference filepath = mStoarge.child("pdf").child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context, "Uploaded", Toast.LENGTH_LONG).show();
                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);

                        month = calendar.get(Calendar.MONTH) + 1;    //Month in Calendar API start with 0.
                        day = calendar.get(Calendar.DAY_OF_MONTH);
                        //  Toast.makeText(AddNoticeActivityAdmin.this,day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                        final String currentDate = day + "/" + month + "/" + year;
                        final long currentLongTime = -1 * new Date().getTime();
                        final String currentTime = "" + currentLongTime;
                        mDataUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                final String Dept = dataSnapshot.child("department").getValue().toString().trim();
                                final String lvlCheck = dataSnapshot.child("level").getValue().toString().trim();
                                if (lvlCheck.equals("1")) {
                                    mData = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Pending").push();
                                    Approved = "pending";
                                } else if (lvlCheck.equals("2")) {
                                    if (strdept == null) {
                                        mData = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Approved").push();
                                        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Pending").push();
                                        Approved = "true";

                                        //For Archival Activity

                                        mDatabase1.child("type").setValue(3);
                                        mDatabase1.child("label").setValue(noticeType);
                                        mDatabase1.child("title").setValue(titleDoc_value);
                                        mDatabase1.child("Desc").setValue(descDoc_value);
                                        mDatabase1.child("UID").setValue(mAuth.getCurrentUser().getUid());
                                        mDatabase1.child("email").setValue(mAuth.getCurrentUser().getEmail());
                                        mDatabase1.child("username").setValue(dataSnapshot.child("name").getValue());
                                        mDatabase1.child("profileImg").setValue(dataSnapshot.child("images").getValue());
                                        //Passing Default Text Image for Web App Viewing
                                        mDatabase1.child("images").setValue("https://firebasestorage.googleapis.com/v0/b/e-notice-board-83d16.appspot.com/o/txt-file-symbol.png?alt=media&token=3a8beb43-561f-4f69-a6ad-58d2683abe81");
                                        mDatabase1.child("time").setValue(currentDate);
                                        mDatabase1.child("servertime").setValue(currentLongTime);
                                        //Default Link
                                        mDatabase1.child("link").setValue("gs://e-notice-board-83d16.appspot.com/pdf/debug.txt");
                                        mDatabase1.child("department").setValue(dataSnapshot.child("department").getValue().toString().trim());
                                        mDatabase1.child("approved").setValue(Approved);
                                    } else {
                                        mData = FirebaseDatabase.getInstance().getReference().child("posts").child(strdept).child("Pending").push();
                                        Approved = "pending";
                                    }
                                }

                                mData.child("type").setValue(3);
                                mData.child("label").setValue(noticeType);
                                mData.child("title").setValue(titleDoc_value);
                                mData.child("Desc").setValue(descDoc_value);
                                mData.child("UID").setValue(mAuth.getCurrentUser().getUid());
                                mData.child("email").setValue(mAuth.getCurrentUser().getEmail());
                                mData.child("username").setValue(dataSnapshot.child("name").getValue());
                                mData.child("profileImg").setValue(dataSnapshot.child("images").getValue());
                                //Passing Default PDF Image for Web App Viewing
                                mData.child("images").setValue("https://firebasestorage.googleapis.com/v0/b/e-notice-board-83d16.appspot.com/o/pdf-file-format-symbol.png?alt=media&token=b9661fd2-0644-4340-82e8-c96662db26dc");
                                mData.child("time").setValue(currentDate);
                                mData.child("servertime").setValue(currentLongTime);
                                mData.child("link").setValue(downloadUrl.toString());
                                mData.child("department").setValue(Dept);
                                mData.child("approved").setValue(Approved).addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                                            if (lvlCheck.equals("2")) {
                                                departmentPushDept(titleDoc_value, "Notice by HOD ".concat(dataSnapshot.child("name").getValue().toString()), Dept);
                                            } else if (lvlCheck.equals("1")) {
                                                departmentPushHOD(titleDoc_value, "Pending Notice Approval sent by ".concat(dataSnapshot.child("name").getValue().toString()), Dept);
                                            }
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        dialog1.dismiss();
                        startActivity(new Intent(context, AddNoticeTabbed.class));
                        context.finish();

                    }
                });

            }
        }

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

        MyVolley.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void departmentPushHOD(final String t,final String m,final String dept) {
        final String title = t;
        final String message = m;
        final String email = "dhanajay@gmail.com";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH_HOD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

        MyVolley.getInstance(context).addToRequestQueue(stringRequest);
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mStoarge = FirebaseStorage.getInstance().getReference();
        mCurrentUser = mAuth.getCurrentUser();

        spinnerDoc = (Spinner) context.findViewById(R.id.spinnerDoc);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, typeArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDoc.setAdapter(adapter);
        spinnerDoc.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {

                        int position = spinnerDoc.getSelectedItemPosition();

                        noticeType = typeArray[+position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }

                }
        );

        mDataBaseDepartment = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        mDataUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        titleDoc = (EditText) context.findViewById(R.id.titleDoc);
        descDoc = (EditText) context.findViewById(R.id.descDoc);

        btnSubmitDoc = (Button) context.findViewById(R.id.btnSubmitDoc);
        btnSubmitDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleDoc_value =  titleDoc.getText().toString().trim();
                descDoc_value = descDoc.getText().toString().trim();

                if(!TextUtils.isEmpty(titleDoc_value) && !TextUtils.isEmpty(descDoc_value)) {

                    Intent intent4 = new Intent(context, NormalFilePickActivity.class);
                    intent4.putExtra(Constant.MAX_NUMBER, 1);
                    intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                    startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                 /*  Intent intent = new Intent(context , PdfUpload.class);
                    intent.putExtra("title_value",titleDoc_value);
                    intent.putExtra("desc_value",descDoc_value);
                    intent.putExtra("noticeType",noticeType);
                    intent.putExtra("strdept",strdept);
                    startActivity(intent);*/
                }
                else if (TextUtils.isEmpty((titleDoc_value))) {
                    Toasty.warning(getActivity().getApplicationContext(),"Please Enter the Title").show();
                }
                else if (TextUtils.isEmpty(descDoc_value)) {
                    Toasty.warning(getActivity().getApplicationContext(),"Please Enter the Description").show();
                }

            }
        });

    }

}
