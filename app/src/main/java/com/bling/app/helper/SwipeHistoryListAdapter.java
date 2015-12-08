package com.bling.app.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bling.app.R;

import java.util.List;

public class SwipeHistoryListAdapter extends BaseAdapter {

    public static final String TAG = SwipeHistoryListAdapter.class.getSimpleName();


    private Activity activity;
    private LayoutInflater inflater;
    private List<Message> messageList;

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

        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_distance, null);
        }

        if (messageList.get(position).type != null) {

            if (messageList.get(position).type.equals(Constant.MESSAGE_TYPE_FRIEND_REQUEST)) {
                convertView = inflater.inflate(R.layout.list_item_friend_request, null);
            }

            if (messageList.get(position).type.equals(Constant.MESSAGE_TYPE_DISTANCE)) {
                convertView = inflater.inflate(R.layout.list_item_distance, null);
            }

            if (messageList.get(position).type.equals(Constant.MESSAGE_TYPE_POSITION)) {
                convertView = inflater.inflate(R.layout.list_item_position, null);
            }

            TextView from = (TextView) convertView.findViewById(R.id.sender);
            TextView created = (TextView) convertView.findViewById(R.id.time);

            from.setText(messageList.get(position).from);
            created.setText(messageList.get(position).getAge());

            if (!messageList.get(position).read) {
                created.append(" - tap to view");
            }

            ImageView img = (ImageView) convertView.findViewById(R.id.icon);
        } else {
            Log.e(TAG, "Invalid message");
            //from.setText("Invalid message.");
        }

        return convertView;
    }

}