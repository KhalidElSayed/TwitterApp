package com.inez.twitterapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.codepath.oauth.OAuthLoginActivity;
import com.inez.twitterapp.R;
import com.inez.twitterapp.TwitterClient;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().setDisplayShowTitleEnabled(false);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
    	Log.d("LoginActivity", "onLoginSuccess");
    	Intent i = new Intent(this, TimelineActivity.class);
    	startActivity(i);
    }
    
    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
    	Log.d("LoginActivity", "onLoginFailure");
        e.printStackTrace();
    }
    
    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(View view) {
    	Log.d("LoginActivity", "loginToRest");
        getClient().connect();
    }

}
