package com.inez.twitterapp;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.inez.twitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends Activity {

	private PullToRefreshListView lv_tweets;
	private TweetsAdapter adapter;
	private ArrayList<Tweet> tweets;
	private TwitterClient client;
	public static final String TWEET_KEY = "tweet";
	public static final int COMPOSE_REQUEST = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);

		client = TwitterApp.getRestClient();
		lv_tweets = (PullToRefreshListView) findViewById(R.id.lv_tweets);
		tweets = new ArrayList<Tweet>();
		adapter = new TweetsAdapter(this, tweets);
		lv_tweets.setAdapter(adapter);

		lv_tweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.d("onLoadMore", "page: " + page + ", totalItemsCount: " + totalItemsCount);
				if ( totalItemsCount > 1 ) {
					fetchTimelineAsync(adapter.getItem(totalItemsCount - 2).getId() - 1, new FetchTimelineHandler() {
						@Override
						public void onFetched(ArrayList<Tweet> tweets) {
							adapter.addAll(tweets);
						}
					});
				}
			}
		});

		lv_tweets.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				fetchTimelineAsync(0, new FetchTimelineHandler() {
					@Override
					public void onFetched(ArrayList<Tweet> tweets) {
						adapter.clear();
						adapter.addAll(tweets);
						lv_tweets.onRefreshComplete();
					}
				});				
			}
		});

		fetchTimelineAsync(0, new FetchTimelineHandler() {
			@Override
			public void onFetched(ArrayList<Tweet> tweets) {
				adapter.addAll(tweets);
			}
		});

	}
	
	private void fetchTimelineAsync(long max_id, final FetchTimelineHandler handler) {
		client.getHomeTimeline(max_id, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONArray jsonTweets) {
					ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
					handler.onFetched(tweets);
				}
		});
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}
	
	public void onComposeClick(MenuItem menuItem) {
		Intent intent = new Intent(this, ComposeActivity.class);
		startActivityForResult(intent, COMPOSE_REQUEST);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode == COMPOSE_REQUEST ) {
			if ( resultCode == RESULT_OK ) {
				Tweet tweet = (Tweet) data.getSerializableExtra(TWEET_KEY);
				adapter.insert(tweet, 0);
			}
		}
	}

}
