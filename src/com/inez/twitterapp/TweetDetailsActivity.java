package com.inez.twitterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.inez.twitterapp.ComposeDialog.ComposeDialogListener;
import com.inez.twitterapp.helpers.Ids;
import com.inez.twitterapp.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetDetailsActivity extends FragmentActivity implements ComposeDialogListener {

	private ComposeDialog composeDialog;
	private Tweet tweet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_details);
		
		Intent intent = getIntent();
		tweet = (Tweet) intent.getSerializableExtra(Ids.TWEET_KEY);

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

	public void onReplyClick(View v) {
		Bundle args = new Bundle();
		args.putSerializable(Ids.TWEET_KEY, tweet);
		FragmentManager fm = getSupportFragmentManager();
		composeDialog = new  ComposeDialog();
		composeDialog.setArguments(args);
		composeDialog.show(fm, "activity_compose");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tweet_details, menu);
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode == Ids.MEDIA_REQUEST ) {
			if ( resultCode == RESULT_OK ) {
				composeDialog.onActivityResult(requestCode, resultCode, data);
			}
		}		
	}

	@Override
	public void onFinishComposeDialog(Tweet tweet) {
		Intent data = new Intent();
		data.putExtra(Ids.TWEET_KEY, tweet);
		setResult(RESULT_OK, data);
		finish();
	}

}
