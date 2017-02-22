package com.rcoem.enotice.enotice_app.ViewHolderClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rcoem.enotice.enotice_app.BlogModel;
import com.rcoem.enotice.enotice_app.CircleTransform;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.AdminClasses.TextNoticeAdmin;
import com.rcoem.enotice.enotice_app.AdminApprovalClasses.TextNoticeApproval;
import com.rcoem.enotice.enotice_app.UserClasses.TextNoticeUser;
import com.rcoem.enotice.enotice_app.UserNoticeStatusClasses.UserTextNoticeStatus;
import com.rcoem.enotice.enotice_app.UserNoticeStatusClasses.UserTextRejectStatus;
import com.rcoem.enotice.enotice_app.Utils;

/**
 * Created by E-Notice on 2/16/2017.
 */

//View Holder for Image Notice
public class TextNoticeViewHolder extends RecyclerView.ViewHolder {

    View mView;
    CardView textcard;
    int callingActivity;
    Intent intent;


    public TextNoticeViewHolder(View itemView, int callingActivity) {
        super(itemView);
        mView = itemView;
        this.callingActivity = callingActivity;
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

        TextView notice_title = (TextView) mView.findViewById(R.id.title_textcard);
        notice_title.setText(title);
    }

    public void setLabel(String label){
        TextView notice_label = (TextView) mView.findViewById(R.id.label_textcard);
        notice_label.setText(label);
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

        viewHolder.setLabel(model.getLabel());

        viewHolder.setProfname(model.getUsername());

        viewHolder.setProfilePic(context, model.getProfileImg());

        final int callingActivity = viewHolder.callingActivity;

        final String status = model.getApproved();

        viewHolder.textcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;

                switch (callingActivity) {
                    case Utils.ADMIN_VIEW:

                        intent = new Intent(context, TextNoticeAdmin.class);
                        intent.putExtra("postkey", Post_Key);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        break;

                    case Utils.ADMIN_APPROVE:

                        intent = new Intent(context, TextNoticeApproval.class);
                        intent.putExtra("postkey", Post_Key);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        break;

                    case Utils.USER_VIEW:

                        intent = new Intent(context, TextNoticeUser.class);
                        intent.putExtra("postkey", Post_Key);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        break;

                    case Utils.USER_STATUS:

                        if(status.equals("false")){
                            intent = new Intent(context, UserTextRejectStatus.class);
                            intent.putExtra("postkey", Post_Key);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        else{
                            intent = new Intent(context, UserTextNoticeStatus.class);
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