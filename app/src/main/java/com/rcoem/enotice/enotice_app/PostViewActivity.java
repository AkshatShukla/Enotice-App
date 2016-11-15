package com.rcoem.enotice.enotice_app;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostViewActivity extends AppCompatActivity {
    private DatabaseReference mDatabase1;
    private DatabaseReference mCurrentUser;
    private DatabaseReference mDatabaseDepartment;
    FirebaseAuth mAuth;
    ImageView imageView;
    ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__view__activity);
        mAuth = FirebaseAuth.getInstance();
        progressBar  = new ProgressDialog(this);

        Intent intent = getIntent();
        String key = intent.getStringExtra("Post_key");
        String dept = intent.getStringExtra("dept");
        String Post_key = key.substring(key.lastIndexOf("/")+1,key.length());
        // Toast.makeText(getApplicationContext(),Post_key,Toast.LENGTH_LONG).show();

        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("posts");

        DatabaseReference okk =   mDatabase1.child(dept);

        //Toast.makeText(getApplicationContext(),okk.toString(),Toast.LENGTH_LONG).show();








        okk.child(Post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BlogModel m = dataSnapshot.getValue(BlogModel.class);




                Toast.makeText(getApplicationContext(),m.getImages(),Toast.LENGTH_LONG).show();

                MyTask myTask= new MyTask();
                myTask.execute(m.getImages());









                TextView user = (TextView)findViewById(R.id.user_name);
                TextView title = (TextView)findViewById(R.id.title_title);

                TextView desc = (TextView)findViewById(R.id.desc);
                user.setText("Posted By : "+m.getUsername());
                title.setText("Title : "+m.getTitle());

                desc.setText("Description : "+m.getDesc());




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //DatabaseReference okk1 =   mDatabase1.child(dept);

        okk.child(Post_key).child("approved").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView approve = (TextView)findViewById(R.id.approve);
                //approve.setText(dataSnapshot.getValue().toString());
                if(dataSnapshot.getValue().toString()=="true")
                {
                    approve.setText("Approved");
                }
                else
                {
                    approve.setText("Pending");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });














    }
    class MyTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            progressBar.setMessage("Loading...");
            progressBar.show();
        }

        @Override
        protected Bitmap doInBackground(String... voids) {

            Bitmap bitmap=null;
            try {
                URL url =new URL(voids[0]);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                InputStream inputStream= connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView img = (ImageView) findViewById(R.id.card_img);
            img.setImageBitmap(bitmap);
            progressBar.dismiss();


        }
    }
}
