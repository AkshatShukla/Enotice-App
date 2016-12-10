package com.rcoem.enotice.enotice_app;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_upload);
        view = (LinearLayout) findViewById(R.id.view);
        mProgress  = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String string = dataSnapshot.child("department").getValue().toString().trim();
                valuemal(string);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
    //fvgfjdkghfjgfdsgfkgfdgfd

    private void valuemal(String string) {
        mData = FirebaseDatabase.getInstance().getReference().child("posts").child(string).child("Document");
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
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
            {
                String selectedmovie=arr.get(position).toString();
                String okk = selectedmovie.substring(selectedmovie.lastIndexOf("/")+1,selectedmovie.length());
                Toast.makeText(getApplicationContext(), okk,   Toast.LENGTH_LONG).show();
//Uri mImageUri = Uri.parse(selectedmovie);
                Uri mImageUri = Uri.fromFile(new File(selectedmovie));
                mProgress.setMessage("uploading");
                mProgress.show();

                /*



    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            // Get a URL to the uploaded content
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
        }
    })
    .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            // Handle unsuccessful uploads
            // ...
        }
    });
                 */


                StorageReference filepath = mStoarge.child("pdf").child(mImageUri.getLastPathSegment());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_LONG).show();
                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        final DatabaseReference newPost =mData.push();
                        mDataUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                newPost.child("link").setValue(downloadUrl.toString());
                                newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(PdfUpload.this,"uploaded successfully", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Toast.makeText(PdfUpload.this, "Database ", Toast.LENGTH_LONG).show();
                        mProgress.dismiss();


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



}
