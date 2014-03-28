package com.inez.twitterapp;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.inez.twitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends Activity {

	private ListView lv_tweets;
	private TweetsAdapter adapter;
	private ArrayList<Tweet> tweets;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);

		lv_tweets = (ListView) findViewById(R.id.lv_tweets);
		tweets = new ArrayList<Tweet>();
		adapter = new TweetsAdapter(this, tweets);
		lv_tweets.setAdapter(adapter);
		lv_tweets.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.d("onLoadMore", "page: " + page + ", totalItemsCount: " + totalItemsCount);
				a(adapter.getItem(totalItemsCount - 1).getId());
			}

		});
		
		a(null);

	}
	
	private void a(final Long max_id) {
		TwitterApp.getRestClient().getHomeTimeline(max_id, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONArray jsonTweets) {
				ArrayList<Tweet> set = Tweet.fromJson(jsonTweets);
				if ( max_id != null ) {
					set.remove(0);					
				}
				adapter.addAll(set);
				super.onSuccess(jsonTweets);
			}

		});		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

}
