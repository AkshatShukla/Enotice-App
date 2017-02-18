package com.rcoem.enotice.enotice_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

/**
 * Created by Akshat Shukla on 12-02-2017.
 */

public class AddDocNoticeFragment extends Fragment {

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

    private String noticeType;
    private String titleDoc_value;
    private String descDoc_value;

    private boolean docOK;

    private String [] typeArray =
            {       "For Teachers",
                    "For Students",
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
                    Intent intent = new Intent(context , PdfUpload.class);
                    intent.putExtra("title_value",titleDoc_value);
                    intent.putExtra("desc_value",descDoc_value);
                    intent.putExtra("noticeType",noticeType);
                    startActivity(intent);
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
