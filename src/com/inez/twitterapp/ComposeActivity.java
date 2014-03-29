package com.inez.twitterapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class ComposeActivity extends Activity {

	private TwitterClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
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
		client.postUpdate(et_tweet.getText().toString(), null);
	}

}
