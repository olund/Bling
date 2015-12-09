package com.bling.app.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bling.app.R;
import com.bling.app.activity.DistanceActivity;
import com.bling.app.activity.FriendRequestActivity;
import com.bling.app.activity.MainActivity;
import com.bling.app.activity.PositionActivity;
import com.bling.app.app.BlingApp;
import com.bling.app.helper.LocationModel;
import com.bling.app.helper.Message;
import com.bling.app.helper.SwipeHistoryListAdapter;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bling.app.helper.Constant;

public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LocationModel.OnCustomStateListener{

    public static final String TAG = HistoryFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SwipeHistoryListAdapter adapter;
    private List<Message> messageList;
    private Location mLocation;
    private String mUser;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        LocationModel.getInstance().setListener(this);

        listView = (ListView) rootView.findViewById(R.id.listView);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constant.USER_PREFS, 0);
        mUser = prefs.getString(Constant.USER_ID, "");

        setupListItemClickListener();

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1);

        messageList = new ArrayList<>();
        adapter = new SwipeHistoryListAdapter(getActivity(), messageList);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchMessages();
            }
        });
        return rootView;
    }

    private void setupListItemClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = (Message) listView.getItemAtPosition(position);

                Log.d(TAG, "Message from: " + message.from + " clicked.");

                switch (message.type) {
                    case Constant.MESSAGE_TYPE_DISTANCE:
                        Intent distanceIntent = new Intent(getContext(), DistanceActivity.class);
                        distanceIntent.putExtra("message", message);
                        distanceIntent.putExtra("currentLocation", mLocation);

                        startActivity(distanceIntent);
                        break;

                    case Constant.MESSAGE_TYPE_FRIEND_REQUEST:
                        Intent friendIntent = new Intent(getContext(), FriendRequestActivity.class);
                        friendIntent.putExtra("message", message);

                        startActivity(friendIntent);
                        break;

                    case Constant.MESSAGE_TYPE_POSITION:
                        Intent positionIntent = new Intent(getContext(), PositionActivity.class);
                        positionIntent.putExtra("message", message);
                        positionIntent.putExtra("currentLocation", mLocation);

                        startActivity(positionIntent);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void locationChanged() {
        this.mLocation = LocationModel.getInstance().getLocation();
        Log.d(TAG, "HistoryFragment says: Location changed: " + String.valueOf(this.mLocation));
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        fetchMessages();
    }

    /**
     * Fetching messages
     */
    private void fetchMessages() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // Clear list
        messageList.clear();

        String url = Constant.URL_HISTORY + mUser;
        JsonArrayRequest req = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray messages) {
                Log.d(TAG, messages.toString());

                if (messages.length() > 0) {
                    for (int i = 0; i < messages.length(); i++) {
                        try {
                            JSONObject message = messages.getJSONObject(i);

                            messageList.add(new Message(message));

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

                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        BlingApp.getInstance().addToRequestQueue(req);
    }
}