package com.bling.app.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bling.app.R;
import com.bling.app.activity.MainActivity;
import com.bling.app.app.BlingApp;
import com.bling.app.helper.Friend;
import com.bling.app.helper.LocationModel;
import com.bling.app.helper.Message;
import com.bling.app.helper.SwipeHistoryListAdapter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LocationModel.OnCustomStateListener{

    public static final String TAG = HistoryFragment.class.getSimpleName();
    // Sample json data
    private String URL = "http://pastebin.com/raw.php?i=RnDWdKd7";

    private String ACTIVITY_NAME = HistoryFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SwipeHistoryListAdapter adapter;
    private List<Message> messageList;

    public HistoryFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        LocationModel.getInstance().setListener(this);

        listView = (ListView) rootView.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

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
            }
        );
        return rootView;
    }

    @Override
    public void stateChanged() {
        Location location = LocationModel.getInstance().getLocation();
        Log.d(TAG, "HistoryFragment says: Location changed: " + String.valueOf(location));
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

        // appending offset to url
        String url = URL;
        JsonArrayRequest req = new JsonArrayRequest(url,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray messages) {
                    Log.d(ACTIVITY_NAME, messages.toString());

                    if (messages.length() > 0) {
                        for (int i = 0; i < messages.length(); i++) {
                            try {
                                JSONObject message = messages.getJSONObject(i);

                                String from = message.getString("fromId");
                                String type = message.getString("type");
                                String time = message.getString("time");

                                messageList.add(new Message(from, type, time));

                            } catch (JSONException e) {
                                Log.e(ACTIVITY_NAME, "JSON Parsing error: " + e.getMessage());
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
            Log.e(ACTIVITY_NAME, "Server Error: " + error.getMessage());

            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        BlingApp.getInstance().addToRequestQueue(req);

    }

}