package com.inez.twitterapp;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.inez.twitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ComposeActivity extends Activity {

	private TwitterClient client;
	private Tweet tweet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		
		Intent intent = getIntent();
		tweet = (Tweet) intent.getSerializableExtra(TimelineActivity.TWEET_KEY);
		
		if(tweet != null) {
			EditText et_tweet = (EditText) findViewById(R.id.et_tweet);
			et_tweet.setText("@" + tweet.getUser().getScreenName() + " ");
			et_tweet.setSelection(et_tweet.length());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose, menu);
		return true;
	}
	
	public void onPostClick(View v) {
		EditText et_tweet = (EditText) findViewById(R.id.et_tweet);
		client = TwitterApp.getRestClient();
		client.postUpdate(et_tweet.getText().toString(), tweet == null ? 0 : tweet.getRemoteId(), new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject jsonTweet) {
				Intent data = new Intent();
				data.putExtra(TimelineActivity.TWEET_KEY, Tweet.fromJson(jsonTweet));
				setResult(RESULT_OK, data);
				finish();
			}
		});
	}

}
