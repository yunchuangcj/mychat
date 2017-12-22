package com.example.yun.mychat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yun.R;


import java.util.List;
import java.util.Map;

/**
 * Created by Yun on 2016/11/17.
 */

public class ChatlistAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Person> mData;

    public ChatlistAdapter(Context context, List<Person> mData) {
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
            convertView = layoutInflater.inflate(R.layout.item_chatlist, null);
            viewHolder.headImage = (ImageView) convertView.findViewById(R.id.headimage);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.nametext);
            viewHolder.lastMsg = (TextView) convertView.findViewById(R.id.lastmsg);
            viewHolder.msgcount = (TextView) convertView.findViewById(R.id.msgcount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.headImage.setImageResource(R.mipmap.people);
        viewHolder.nameText.setText(mData.get(position).getName());
        viewHolder.lastMsg.setText(mData.get(position).getLastmsg());
        if (mData.get(position).getMsgcount() == 0) {
            viewHolder.msgcount.setVisibility(View.GONE);
        } else {
            viewHolder.msgcount.setVisibility(View.VISIBLE);
            viewHolder.msgcount.setText(mData.get(position).getMsgcount() + "");
        }

        return convertView;
    }

    public final class ViewHolder {
        public TextView msgcount;
        public ImageView headImage;
        public TextView nameText;
        public TextView lastMsg;
    }

}
