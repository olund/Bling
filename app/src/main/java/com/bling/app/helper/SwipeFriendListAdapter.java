package com.bling.app.helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.List;

import com.bling.app.R;

public class SwipeFriendListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Friend> friendList;

    public SwipeFriendListAdapter(Activity activity, List<Friend> friendList) {
        this.activity = activity;
        this.friendList = friendList;
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int location) {
        return friendList.get(location);
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
            convertView = inflater.inflate(R.layout.list_item_friend, null);

        TextView username = (TextView) convertView.findViewById(R.id.username);

        username.setText(friendList.get(position).username);

        ImageView img = (ImageView) convertView.findViewById(R.id.icon);

        return convertView;
    }

}