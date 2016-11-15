package com.rcoem.enotice.enotice_app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class PdfDownload extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private Uri mImageUri = null;
    private StorageReference mStoarge;
    private boolean process;
    private TextView link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_download);
        Intent intent = getIntent();
        final String str = intent.getStringExtra("postkey");
        link = (TextView) findViewById(R.id.downloadLink);
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(str);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                link.setText(dataSnapshot.child("link").getValue().toString().trim());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
