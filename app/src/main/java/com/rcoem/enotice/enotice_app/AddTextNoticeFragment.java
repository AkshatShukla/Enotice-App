package com.rcoem.enotice.enotice_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import dmax.dialog.SpotsDialog;

/**
 * Created by Akshat Shukla on 12-02-2017.
 */

public class AddTextNoticeFragment extends Fragment {
    View view;
    private EditText title;
    private EditText desc;
    private Button btnsubmit;
    private DatabaseReference mDatabase;
    private DatabaseReference mDataBaseDepartment;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    Activity context;
    public AddTextNoticeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        context = getActivity();
        view = inflater.inflate(R.layout.activity_add_text_notice, container, false);

        return inflater.inflate(R.layout.activity_add_text_notice, container, false);
    }

    public void onStart(){
        super.onStart();
        title = (EditText) context.findViewById(R.id.titleText);
        desc = (EditText) context.findViewById(R.id.descText);
        btnsubmit = (Button) context.findViewById(R.id.btnSubmit);
        mAuth = FirebaseAuth.getInstance();

        mDataBaseDepartment = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new SpotsDialog(getContext(), R.style.CustomProgress);
                dialog.show();

                final String titletext = title.getText().toString().trim();
                final String desctext =  desc.getText().toString().trim();
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);

                month = calendar.get(Calendar.MONTH) + 1;    //Month in Calendar API start with 0.
                day = calendar.get(Calendar.DAY_OF_MONTH);
                //  Toast.makeText(AddNoticeActivityAdmin.this,day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                final String currentDate = day + "/" + month + "/" + year;
                final long currentLongTime = -1 * new Date().getTime();
                final String currentTime = "" + currentLongTime;
                if(!TextUtils.isEmpty(titletext)&&!TextUtils.isEmpty(desctext)){
                    mDataBaseDepartment.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            mDatabase  = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("TextPost").push();
                            mDatabase.child("Desc").setValue(desctext);
                            mDatabase.child("UID").setValue(mAuth.getCurrentUser().getUid());
                            mDatabase.child("approved").setValue("true");
                           mDatabase.child("removed").setValue(1);
                            mDatabase.child("email").setValue(mAuth.getCurrentUser().getEmail());
                            mDatabase.child("title").setValue(titletext);
                           mDatabase.child("username").setValue(dataSnapshot.child("name").getValue().toString().trim());
                            mDatabase.child("department").setValue(dataSnapshot.child("department").getValue().toString().trim());
                            mDatabase.child("time").setValue(currentDate);
                            //  mData = FirebaseDatabase.getInstance().getReference().child("posts").child(dataSnapshot.child("department").getValue().toString().trim()).child("Approved");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(context, "Connection error", Toast.LENGTH_LONG)
                                    .show();
                        }

                    });
                    Toast.makeText(context,"Sucessfully Posted", Toast.LENGTH_LONG)
                            .show();
                    dialog.dismiss();
                    startActivity(new Intent(context,AddNoticeTabbed.class));
                }
                else{
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                    dialog.dismiss();
                }
            }
        });



    }


}
