package com.rcoem.enotice.enotice_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class AwatingForApproval extends AppCompatActivity {
    private Button signOut;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awating_for_approval);
        mAuth = FirebaseAuth.getInstance();
        signOut = (Button) findViewById(R.id.signoutadmin);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AwatingForApproval.this,"Not successfully Uploaded", Toast.LENGTH_LONG).show();
                mAuth.signOut();
            }
        });
    }
}
