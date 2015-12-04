package com.bling.app.helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bling.app.R;

import java.util.List;

public class SwipeHistoryListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Message> messageList;
    private String POSITION = "position";
    private String DISTANCE = "distance";
    private String FRIEND_REQUEST = "friendReq";

    public SwipeHistoryListAdapter(Activity activity, List<Message> messageList) {
        this.activity = activity;
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int location) {
        return messageList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)

            convertView = inflater.inflate(R.layout.list_item_distance, null);

            if (messageList.get(position).type.equals(FRIEND_REQUEST))
                convertView = inflater.inflate(R.layout.list_item_friend_request, null);

            if (messageList.get(position).type.equals(DISTANCE))
                convertView = inflater.inflate(R.layout.list_item_distance, null);

            if (messageList.get(position).type.equals(POSITION))
                convertView = inflater.inflate(R.layout.list_item_position, null);

        TextView sender = (TextView) convertView.findViewById(R.id.sender);

        TextView time = (TextView) convertView.findViewById(R.id.time);

        sender.setText(messageList.get(position).from);
        time.setText(messageList.get(position).time);

        ImageView img = (ImageView) convertView.findViewById(R.id.icon);

        return convertView;
    }

}