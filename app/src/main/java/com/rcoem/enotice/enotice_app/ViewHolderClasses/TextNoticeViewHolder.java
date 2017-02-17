package com.rcoem.enotice.enotice_app.ViewHolderClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
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
public class TextNoticeViewHolder extends RecyclerView.ViewHolder {

    View mView;
    ImageView post_image;
    TextView notice_title;
    CardView textcard;

    public TextNoticeViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

        textcard = (CardView) mView.findViewById(R.id.card_view_textcard);
    }

    public void setProfilePic(final Context context, final String profpiclink){
        final ImageView notice_profpic = (ImageView) mView.findViewById(R.id.profpic_textcard);
        Glide.with(context).load(profpiclink)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .into(notice_profpic);

    }

    public void setProfname(String profname){

        TextView notice_ProfName = (TextView) mView.findViewById(R.id.profname_textcard);
        notice_ProfName.setText(profname);
    }

    public void setTitle(String title){

        notice_title = (TextView) mView.findViewById(R.id.title_textcard);
        notice_title.setText(title);
    }


    public void setDesc(String desc){

        TextView notice_desc = (TextView) mView.findViewById(R.id.desc_textcard);
        notice_desc.setText(desc);
    }

    public void setTime(String time){

        TextView post_Time = (TextView) mView.findViewById(R.id.date_textcard);
        post_Time.setText(time);
    }


    public static void populateTextNoticeCard(TextNoticeViewHolder viewHolder, BlogModel model, final int position, String PostKey, final Context context) {
        final String Post_Key = PostKey;


        viewHolder.setTitle(model.getTitle());

        viewHolder.setDesc(model.getDesc());

        viewHolder.setTime(model.getTime());

        viewHolder.setProfname(model.getUsername());

        viewHolder.setProfilePic(context, model.getProfileImg());

        viewHolder.textcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}