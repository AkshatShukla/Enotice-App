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
import com.rcoem.enotice.enotice_app.AdminClasses.DocNoticeAdmin;
import com.rcoem.enotice.enotice_app.AdminApprovalClasses.DocNoticeApproval;
import com.rcoem.enotice.enotice_app.UserClasses.DocNoticeUser;
import com.rcoem.enotice.enotice_app.R;
import com.rcoem.enotice.enotice_app.UserNoticeStatusClasses.UserDocNoticeStatus;
import com.rcoem.enotice.enotice_app.UserNoticeStatusClasses.UserDocRejectStatus;
import com.rcoem.enotice.enotice_app.Utils;

/**
 * Created by E-Notice on 2/17/2017.
 */

public class DocumentNoticeViewHolder extends RecyclerView.ViewHolder {

    View mView;
    CardView doccard;
    int callingActivity;

    public DocumentNoticeViewHolder(View itemView, int callingActivity) {
        super(itemView);
        mView = itemView;
        this.callingActivity = callingActivity;
        doccard = (CardView) mView.findViewById(R.id.card_view_doccard);
    }

    public void setProfilePic(final Context context, final String profpiclink){
        final ImageView notice_profpic = (ImageView) mView.findViewById(R.id.profpic_doccard);
        Glide.with(context).load(profpiclink)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .into(notice_profpic);

    }

    public void setProfname(String profname){

        TextView notice_ProfName = (TextView) mView.findViewById(R.id.profname_doccard);
        notice_ProfName.setText(profname);
    }

    public void setLabel(String label){
        TextView notice_label = (TextView) mView.findViewById(R.id.label_doccard);
        notice_label.setText(label);
    }


    public void setTitle(String title){

        TextView notice_title = (TextView) mView.findViewById(R.id.title_doccard);
        notice_title.setText(title);
    }


    public void setDesc(String desc){

        TextView notice_desc = (TextView) mView.findViewById(R.id.desc_doccard);
        notice_desc.setText(desc);
    }

    public void setTime(String time){

        TextView post_Time = (TextView) mView.findViewById(R.id.date_doccard);
        post_Time.setText(time);
    }


    public static void populateDocumentNoticeCard(DocumentNoticeViewHolder viewHolder, BlogModel model, final int position, String PostKey, final Context context) {
        final String Post_Key = PostKey;


        viewHolder.setTitle(model.getTitle());

        viewHolder.setDesc(model.getDesc());

        viewHolder.setTime(model.getTime());

        viewHolder.setProfname(model.getUsername());

        viewHolder.setLabel(model.getLabel());

        viewHolder.setProfilePic(context, model.getProfileImg());

        final int callingActivity = viewHolder.callingActivity;

        final String status = model.getApproved();

        viewHolder.doccard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;

                switch (callingActivity) {
                    case Utils.ADMIN_VIEW:

                        intent = new Intent(context, DocNoticeAdmin.class);
                        intent.putExtra("postkey", Post_Key);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        break;

                    case Utils.ADMIN_APPROVE:

                        intent = new Intent(context, DocNoticeApproval.class);
                        intent.putExtra("postkey", Post_Key);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        break;

                    case Utils.USER_VIEW:

                        intent = new Intent(context, DocNoticeUser.class);
                        intent.putExtra("postkey", Post_Key);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        break;

                    case Utils.USER_STATUS:

                        if(status.equals("false")){
                            intent = new Intent(context, UserDocRejectStatus.class);
                            intent.putExtra("postkey", Post_Key);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        else{
                            intent = new Intent(context, UserDocNoticeStatus.class);
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
