package com.example.yun.mychat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yun.R;

import java.util.List;

/**
 * Created by Yun on 2016/11/22.
 */

public class ChatTextAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<MyMessage> mData;

    public ChatTextAdapter(Context context, List<MyMessage> mData) {
        this.layoutInflater = LayoutInflater.from(context);
        this.mData = mData;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_chat, null);
            viewHolder.timeText = (TextView) convertView.findViewById(R.id.msgtime);
            viewHolder.head1 = (ImageView) convertView.findViewById(R.id.head1);
            viewHolder.head2 = (ImageView) convertView.findViewById(R.id.head2);
            viewHolder.layoutitem=(LinearLayout)convertView.findViewById(R.id.layoutitem);
            viewHolder.chatitem = (LinearLayout) convertView.findViewById(R.id.chatitem);
            viewHolder.contentText = (TextView) convertView.findViewById(R.id.msgcontent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int flag = mData.get(position).getFlag();

        if (flag == 2) {
            viewHolder.head1.setImageResource(R.mipmap.nothing);
            viewHolder.head2.setImageResource(R.mipmap.people);
            viewHolder.chatitem.setBackgroundResource(R.mipmap.chatback1);
            viewHolder.layoutitem.setGravity(Gravity.RIGHT);
        } else if (flag == 1) {
            viewHolder.head2.setImageResource(R.mipmap.nothing);
            viewHolder.head1.setImageResource(R.mipmap.people);
            viewHolder.layoutitem.setGravity(Gravity.LEFT);
            viewHolder.chatitem.setBackgroundResource(R.mipmap.chatback2);
            viewHolder.timeText.setGravity(Gravity.LEFT);
            viewHolder.contentText.setGravity(Gravity.LEFT);
        }
        viewHolder.timeText.setText(mData.get(position).getTime());
        viewHolder.contentText.setText(mData.get(position).getContent());
        return convertView;
    }

    public final class ViewHolder {
        public LinearLayout layoutitem;
        public ImageView head1, head2;
        public LinearLayout chatitem;
        public TextView timeText;
        public TextView contentText;
    }
}
