package com.inez.twitterapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.inez.twitterapp.models.Tweet;
import com.inez.twitterapp.models.User;
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
		
		lv_tweets.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Tweet tweet = adapter.getItem(position);
				Intent intent = new Intent(getApplicationContext(), TweetDetailsActivity.class);
				intent.putExtra(TWEET_KEY, tweet);
				startActivity(intent);
			}

		});
		
		loadTimelineFromDB();
		
		fetchTimelineAsync(0, new FetchTimelineHandler() {
			@Override
			public void onFetched(ArrayList<Tweet> tweets) {
				adapter.clear();
				adapter.addAll(tweets);
				new Delete().from(Tweet.class).execute();
				new Delete().from(User.class).execute();
				for(Tweet tweet : tweets) {
					tweet.getUser().save();
					tweet.save();
				}
			}
		});

	}
	
	private void loadTimelineFromDB() {
		List<Tweet> tweets = new Select()
			.from(Tweet.class)
			.orderBy("createdAt ASC")
			.execute();
		adapter.addAll(tweets);
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
