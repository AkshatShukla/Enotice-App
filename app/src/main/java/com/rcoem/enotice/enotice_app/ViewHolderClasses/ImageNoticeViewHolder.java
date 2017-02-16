package com.rcoem.enotice.enotice_app.ViewHolderClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rcoem.enotice.enotice_app.AccountActivityAdmin;
import com.rcoem.enotice.enotice_app.AdminSinglePost;
import com.rcoem.enotice.enotice_app.BlogModel;
import com.rcoem.enotice.enotice_app.CircleTransform;
import com.rcoem.enotice.enotice_app.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by E-Notice on 2/16/2017.
 */

//View Holder for Image Notice
public class ImageNoticeViewHolder extends RecyclerView.ViewHolder {

     View mView;
     ImageView post_image;

    public ImageNoticeViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setProfilePic(final Context context, final String profpiclink){
        final ImageView notice_profpic = (ImageView) mView.findViewById(R.id.profpic_imagecard);
        Glide.with(context).load(profpiclink)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .into(notice_profpic);

    }

    public void setProfname(String profname){

        TextView notice_ProfName = (TextView) mView.findViewById(R.id.profname_imagecard);
        notice_ProfName.setText(profname);
    }

    public void setTitle(String title){

        TextView notice_title = (TextView) mView.findViewById(R.id.title_imagecard);
        notice_title.setText(title);
    }


    public void setImage(final Context context, final String image){

         post_image = (ImageView) mView.findViewById(R.id.pic_imagecard);
        //Picasso.with(context).load(image).into(post_image);

        Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
            @Override
            public void onSuccess() {
                //Do Nothing
            }

            @Override
            public void onError() {
                Picasso.with(context).load(image).into(post_image);
            }
        });

    }

    public void setTime(String time){

        TextView post_Time = (TextView) mView.findViewById(R.id.date_imagecard);
        post_Time.setText(time);
    }


    public static void populateImageNoticeCard(ImageNoticeViewHolder viewHolder, BlogModel model, final int position, String PostKey, final Context context) {
        final String Post_Key = PostKey;


        viewHolder.setTitle(model.getProfileImg());

        viewHolder.setImage(context, model.getImages());

        viewHolder.setTime(model.getTime());

        viewHolder.setProfname(model.getUsername());

        viewHolder.setProfilePic(context, model.getProfileImg());

        viewHolder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Card-expanding Code
                Intent intent = new Intent(context, AdminSinglePost.class);
                intent.putExtra("postkey", Post_Key);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(context, Post_Key, Toast.LENGTH_LONG).show();
                context.startActivity(intent);

            }
        });
    }
}