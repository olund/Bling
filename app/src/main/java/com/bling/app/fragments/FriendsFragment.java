package com.bling.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bling.app.R;
import com.bling.app.activity.AddFriendActivity;
import com.bling.app.app.BlingApp;
import com.bling.app.helper.Friend;
import com.bling.app.helper.SwipeFriendListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private String TAG = FriendsFragment.class.getSimpleName();

    private String URL = "http://pastebin.com/raw.php?i=UBgHPMgF";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SwipeFriendListAdapter adapter;
    private List<Friend> friendList;

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

        listView = (ListView) rootView.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);


        friendList = new ArrayList<>();
        adapter = new SwipeFriendListAdapter(getActivity(), friendList);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //    .setAction("Action", null).show();
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
            }
        );
        return rootView;
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

        // appending offset to url
        String url = URL;

        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //friendList.clear();
                        try {
                            JSONArray friends = response.getJSONArray("friends");

                            if (friends.length() > 0) {
                                for (int i = 0; i < friends.length(); i++) {
                                    try {
                                        JSONObject friendObj = friends.getJSONObject(i);

                                        String username = friendObj.getString("username");

                                        Friend m = new Friend(username);

                                        friendList.add(m);
                                    } catch (JSONException e) {
                                        Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                    }
                                }

                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Server Error: " + e.getMessage());
                        }

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        BlingApp.getInstance().addToRequestQueue(req);
    }

}