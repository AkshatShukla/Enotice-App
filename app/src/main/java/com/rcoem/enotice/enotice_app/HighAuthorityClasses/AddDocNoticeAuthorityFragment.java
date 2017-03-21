package com.rcoem.enotice.enotice_app.HighAuthorityClasses;

/**
 * Created by Akshat Shukla on 28-02-2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
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
import com.rcoem.enotice.enotice_app.AddNoticeClasses.AddNoticeTabbed;
import com.rcoem.enotice.enotice_app.R;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class AddDocNoticeAuthorityFragment extends Fragment {

    private StorageReference mStoarge;
    private DatabaseReference mData;
    private DatabaseReference mDataUser;
    private DatabaseReference mDataBaseDepartment;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private EditText titleDoc;
    private EditText descDoc;
    private Button btnSubmitDoc;
    private Spinner spinnerDoc;
    private String Approved;

    private String noticeType;
    private String titleDoc_value;
    private String descDoc_value;
    private Uri uri;

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

    public AddDocNoticeAuthorityFragment() {
        // Required empty public constructor
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
                Toasty.warning(context, "No file chosen").show();
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
                                Approved = "true";
                                mData = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Approved");
                                final DatabaseReference newPost = mData.push();

                                newPost.child("type").setValue(3);
                                newPost.child("label").setValue(noticeType);
                                newPost.child("title").setValue(titleDoc_value);
                                newPost.child("Desc").setValue(descDoc_value);
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
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_LONG).show();

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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
