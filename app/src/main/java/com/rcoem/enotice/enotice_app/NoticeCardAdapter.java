package com.rcoem.enotice.enotice_app;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NoticeCardAdapter extends RecyclerView.Adapter<NoticeCardAdapter.MyViewHolder>  {
    private Context mContext;
    private List<NoticeCard> dummyData;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView prof_name, timestamp;
        public  View horiline;
        public ImageView notice_thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            prof_name = (TextView) view.findViewById(R.id.card_prof_name);
            timestamp = (TextView) view.findViewById(R.id.card_timestamp);
            overflow = (ImageView) view.findViewById(R.id.overflow_card);
            //horiline = view.findViewById(R.id.hr);
           // notice_thumbnail = (ImageView) view.findViewById(R.id.card_thumbnail);
        }
    }


    public NoticeCardAdapter(Context mContext, List<NoticeCard> randomListing) {
        this.mContext = mContext;
        this.dummyData = randomListing;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_notice, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        NoticeCard dummy = dummyData.get(position);
        //holder.prof_name.setText(dummy.getName());
        //Glide.with(mContext).load(dummy.getThumbnail()).into(holder.notice_thumbnail);
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.settings, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //add code for option selection
            return true;
        }
    }

    @Override
    public int getItemCount() {
        return dummyData.size();
    }
}
