package com.rcoem.enotice.enotice_app.ViewHolderClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rcoem.enotice.enotice_app.BlogModel;
import com.rcoem.enotice.enotice_app.CircleTransform;
import com.rcoem.enotice.enotice_app.AdminClasses.ImageNoticeAdmin;
import com.rcoem.enotice.enotice_app.AdminApprovalClasses.ImageNoticeApproval;
import com.rcoem.enotice.enotice_app.UserClasses.ImageNoticeUser;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.UserNoticeStatusClasses.UserImageNoticeStatus;
import com.rcoem.enotice.enotice_app.UserNoticeStatusClasses.UserImageRejectStatus;
import com.rcoem.enotice.enotice_app.Utils;
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
    TextView notice_title;
    CardView imagecard;
    int callingActivity;

    public ImageNoticeViewHolder(View itemView, int callingActivity) {
        super(itemView);
        mView = itemView;
        this.callingActivity = callingActivity;
        imagecard = (CardView) mView.findViewById(R.id.card_view_imagecard);
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

        notice_title = (TextView) mView.findViewById(R.id.title_imagecard);
        notice_title.setText(title);
    }

    public void setLabel(String label){
        TextView notice_label = (TextView) mView.findViewById(R.id.label_imagecard);
        notice_label.setText(label);
    }


    public void setImage(final Context context, final String image){

         post_image = (ImageView) mView.findViewById(R.id.pic_imagecard);
        //Picasso.with(context).load(image).into(post_image);

        final ProgressBar progressBar = (ProgressBar) mView.findViewById(R.id.progress);

        Glide.with(context)
                .load(image)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .thumbnail(0.5f)
                .fitCenter()
                .into(post_image);

        /*
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
        */
    }

    public void setTime(String time){

        TextView post_Time = (TextView) mView.findViewById(R.id.date_imagecard);
        post_Time.setText(time);
    }


    public static void populateImageNoticeCard(ImageNoticeViewHolder viewHolder, BlogModel model, final int position, String PostKey, final Context context) {
        final String Post_Key = PostKey;


        viewHolder.setTitle(model.getTitle());

        viewHolder.setImage(context, model.getImages());

        viewHolder.setTime(model.getTime());

        viewHolder.setProfname(model.getUsername());

        viewHolder.setLabel(model.getLabel());

        viewHolder.setProfilePic(context, model.getProfileImg());

        final int callingActivity = viewHolder.callingActivity;

        final String status = model.getApproved();

        viewHolder.imagecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;

                switch (callingActivity) {
                    case Utils.ADMIN_VIEW:

                        intent = new Intent(context, ImageNoticeAdmin.class);
                        intent.putExtra("postkey", Post_Key);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        break;

                    case Utils.ADMIN_APPROVE:

                        intent = new Intent(context, ImageNoticeApproval.class);
                        intent.putExtra("postkey", Post_Key);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        break;

                    case Utils.USER_VIEW:

                        intent = new Intent(context, ImageNoticeUser.class);
                        intent.putExtra("postkey", Post_Key);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        break;

                    case Utils.USER_STATUS:

                        if(status.equals("false")){
                            intent = new Intent(context, UserImageRejectStatus.class);
                            intent.putExtra("postkey", Post_Key);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        else{
                            intent = new Intent(context, UserImageNoticeStatus.class);
                            intent.putExtra("postkey", Post_Key);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        break;
                }
            }

        });


    }

}