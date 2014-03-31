package com.inez.twitterapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.inez.twitterapp.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_details);
		
		Intent intent = getIntent();
		Tweet tweet = (Tweet) intent.getSerializableExtra(TimelineActivity.TWEET_KEY);
		
		
		ImageView ivProfile = (ImageView) findViewById(R.id.ivProfile);
		ImageLoader.getInstance().displayImage(tweet.getUser().getProfileImageUrl(), ivProfile);
		
		TextView tvUser = (TextView) findViewById(R.id.tvUser);
		tvUser.setText(tweet.getUser().getScreenName());

		TextView tvFriends = (TextView) findViewById(R.id.tvFriends);
		tvFriends.setText(String.valueOf(tweet.getUser().getFriendsCount()));

		TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
		tvFollowers.setText(String.valueOf(tweet.getUser().getFollowersCount()));
		
		TextView tvTweet = (TextView) findViewById(R.id.tvTweet);
		tvTweet.setText(tweet.getBody());

		ImageView ivMedium = (ImageView) findViewById(R.id.ivMedium);
		if(tweet.getMediaUrls().size() > 0) {
			ImageLoader.getInstance().displayImage(tweet.getMediaUrls().get(0), ivMedium);
			ivMedium.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tweet_details, menu);
		return true;
	}

}
