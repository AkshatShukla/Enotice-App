package com.rcoem.enotice.enotice_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserNoticeStatus extends AppCompatActivity {
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private StorageReference mStoarge;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Query  mquery;
    private ProgressDialog mProgress;

    private DatabaseReference mDataBaseDepartment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notice_status);
        mAuth = FirebaseAuth.getInstance();
        mDataBaseDepartment = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mDataBaseDepartment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String str = dataSnapshot.child("department").getValue().toString();
                startjugad(str);
                Toast.makeText(UserNoticeStatus.this,str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //   mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");




    }

    private void startjugad(String str) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(str);
        mquery =  mDatabase.orderByChild("UID").equalTo(mAuth.getCurrentUser().getUid());
        mStoarge = FirebaseStorage.getInstance().getReference();

        mBlogList = (RecyclerView) findViewById(R.id.blog_recylView_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        //  mProgress.setMessage("Uploading Details");
        //   mProgress.show();
        FirebaseRecyclerAdapter<BlogModel,RetriverData.BlogViewHolder> firebaseRecyclerAdapter =new
                FirebaseRecyclerAdapter<BlogModel, RetriverData.BlogViewHolder>(

                        BlogModel.class,
                        R.layout.blog_row,
                        RetriverData.BlogViewHolder.class,
                        mquery
                ) {
                    @Override
                    protected void populateViewHolder(RetriverData.BlogViewHolder viewHolder, BlogModel model, int position) {

                        final String Post_Key = getRef(position).toString();
                        Intent intent = getIntent();
                        final String str = intent.getStringExtra("location");
                        //   viewHolder.setDesc(model.getUsername());
                        viewHolder.setTitle(model.getTitle());


                        viewHolder.setImage(getApplicationContext(), model.getImages());
                        if(model.getApproved().equals("false")) {
                            viewHolder.setDesc("Pending or Rejected");
                        }
                        else{
                            viewHolder.setDesc("On Notice Board");
                        }

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                Toast.makeText(UserNoticeStatus.this,Post_Key,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(UserNoticeStatus.this,UserStatusViewSinglePost.class);
                                intent.putExtra("postkey",Post_Key);
                                startActivity(intent);
                                //intent.putExtra("Post_key",str);
                                //  startActivity(intent);

                            }
                        });
                    }
                };
        //  mProgress.dismiss();
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){

            TextView post_title = (TextView) mView.findViewById(R.id.title_card);
            post_title.setText(title);
        }



        public void setImage(Context context, String image){

            ImageView post_image = (ImageView) mView.findViewById(R.id.card_thumbnail123);
            Picasso.with(context).load(image).into(post_image);

        }
        public void setApproved(String approved){

            TextView post_Desc = (TextView) mView.findViewById(R.id.card_prof_name);

            post_Desc.setText(approved);
        }


    }
}