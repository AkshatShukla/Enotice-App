package com.rcoem.enotice.enotice_app.ViewHolderClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rcoem.enotice.enotice_app.AccountActivityAdmin;
import com.rcoem.enotice.enotice_app.AdminSinglePost;
import com.rcoem.enotice.enotice_app.BlogModel;
import com.rcoem.enotice.enotice_app.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by E-Notice on 2/16/2017.
 */


//View Holder for Text Notice
public  class TextNoticeViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public TextNoticeViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setUsername(String username){

        TextView post_Desc = (TextView) mView.findViewById(R.id.card_prof_name);
        post_Desc.setText(username);
    }
    public void setTitle(String title){

        TextView post_title = (TextView) mView.findViewById(R.id.title_card);
        post_title.setText(title);
    }

    public void setDesc(String Desc){

        TextView post_Desc = (TextView) mView.findViewById(R.id.card_name);
        post_Desc.setText(Desc);
    }

    public void setImage(final Context context, final String image){

        final ImageView post_image = (ImageView) mView.findViewById(R.id.card_thumbnail123);
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

        TextView post_Desc = (TextView) mView.findViewById(R.id.card_timestamp);
        post_Desc.setText(time);
    }




    public static  void populateTextNoticeCard(TextNoticeViewHolder viewHolder, BlogModel model, final int position, String PostKey, final Context context){
        final String Post_Key  = PostKey;

        viewHolder.setTitle(model.getTitle());

        viewHolder.setImage(context, model.getImages());

        viewHolder.setTime(model.getTime());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Card-expanding Code
                Intent intent = new Intent(context,AdminSinglePost.class);
                intent.putExtra("postkey",Post_Key);
                Toast.makeText(context,Post_Key, Toast.LENGTH_LONG).show();
                context.startActivity(intent);

            }
        });
    }
}