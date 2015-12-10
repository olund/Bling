package com.bling.app.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bling.app.R;
import com.bling.app.activity.AddFriendActivity;
import com.bling.app.activity.DistanceActivity;
import com.bling.app.activity.FriendRequestActivity;
import com.bling.app.activity.MainActivity;
import com.bling.app.activity.PositionActivity;
import com.bling.app.app.BlingApp;
import com.bling.app.helper.Constant;
import com.bling.app.helper.Friend;
import com.bling.app.helper.LocationModel;
import com.bling.app.helper.Message;
import com.bling.app.helper.SwipeFriendListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LocationModel.OnCustomStateListener{

    private String TAG = FriendsFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SwipeFriendListAdapter adapter;
    private List<Friend> friendList;
    private Location mLocation;
    private String mUser;

    // initially offset will be 0, later will be updated while parsing the json
    private int offSet = 0;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        LocationModel.getInstance().setListener(this);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constant.USER_PREFS, 0);
        mUser = prefs.getString(Constant.USER_ID, "");

        listView = (ListView) rootView.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1);

        friendList = new ArrayList<>();
        adapter = new SwipeFriendListAdapter(getActivity(), friendList);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                intent.putExtra("mUser", mUser);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Friend friend = (Friend) listView.getItemAtPosition(position);

                Log.d(TAG, "Friend: " + friend.username + " clicked.");

                String names[] = {getString(R.string.alternative_distance),getString(R.string.alternative_position)};
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.alert_list, null);
                alertDialog.setView(convertView);
                alertDialog.setTitle(R.string.bling_alternatives);
                ListView lv = (ListView) convertView.findViewById(R.id.listView1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, names);
                lv.setAdapter(adapter);
                alertDialog.show();

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        if (pos == 0) {
                            sendRequest(friend, Constant.MESSAGE_TYPE_DISTANCE);
                        } else if(pos == 1){
                            sendRequest(friend, Constant.MESSAGE_TYPE_POSITION);
                        }
                        alertDialog.dismiss();
                    }
                });
            }
        });
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchFriends();
            }
        });
        return rootView;
    }

    private void sendRequest(Friend friend, String type) {
        String URL = Constant.URL_MESSAGES;

        // Create JSON object to SEND.
        JSONObject obj = new JSONObject();
        try {
            obj.put("fromId", mUser);
            obj.put("toId", friend.id);
            obj.put("type", type);
            obj.put("latitude", mLocation.getLatitude());
            obj.put("longitude", mLocation.getLongitude());
            obj.put("read", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send HTTP POST with JSON object.
        JsonObjectRequest req = new JsonObjectRequest(URL, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i(TAG, response.toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to send Bling.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
                Log.e(TAG, "Failed to send Bling.");
            }
        });

        // Adding request to request queue
        BlingApp.getInstance().addToRequestQueue(req);
    }

    @Override
    public void locationChanged() {
        mLocation = LocationModel.getInstance().getLocation();
        Log.d(TAG, "FriendsFragment says: Location changed: " + String.valueOf(mLocation));
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        fetchFriends();
    }

    /**
     * Fetching movies json by making http call
     */
    private void fetchFriends() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // Clear list
        friendList.clear();

        String url = Constant.URL_FRIENDS + mUser;
        JsonArrayRequest req = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray friends) {
                Log.d(TAG, friends.toString());

                if (friends.length() > 0) {
                    for (int i = 0; i < friends.length(); i++) {
                        try {
                            JSONObject friend = friends.getJSONObject(i);

                            String id = friend.getString("_id");
                            String username = friend.getString("username");

                            Friend f = new Friend(id, username);

                            friendList.add(f);
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                        }
                    }

                    adapter.notifyDataSetChanged();
                }

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

                Toast.makeText(getContext(), R.string.api_down, Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        BlingApp.getInstance().addToRequestQueue(req);
    }

}