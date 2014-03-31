package com.inez.twitterapp;

import java.util.ArrayList;

import com.inez.twitterapp.models.Tweet;

public abstract class FetchTimelineHandler {
	public abstract void onFetched(ArrayList<Tweet> tweets);
	public abstract void onFailure(Throwable arg0, String arg1);
}
