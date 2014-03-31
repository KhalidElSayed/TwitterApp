package com.inez.twitterapp.models;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.inez.twitterapp.helpers.Helpers;

@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
	private static final long serialVersionUID = -4849210389375451448L;
	
	@Column(name = "body")
	private String body;
	
	@Column(name = "remoteId")
	private long remoteId;
	
	@Column(name = "favorited")
	private boolean favorited;

	@Column(name = "retweeted")
	private boolean retweeted;
	
	@Column(name = "user")
	private User user;

	@Column(name = "createdAt")
	private Date createdAt;

	public User getUser() {
		return user;
	}

	public String getBody() {
		return body;
	}

	public long getRemoteId() {
		return remoteId;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public boolean isRetweeted() {
		return retweeted;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public static Tweet fromJson(JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		try {
			tweet.body = jsonObject.getString("text");
			tweet.remoteId = jsonObject.getLong("id");
			tweet.favorited = jsonObject.getBoolean("favorited");
			tweet.retweeted = jsonObject.getBoolean("retweeted");
			tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
			try {
				tweet.createdAt = Helpers.parseTwitterUTC(jsonObject.getString("created_at"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return tweet;
	}

	public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject tweetJson = null;
			try {
				tweetJson = jsonArray.getJSONObject(i);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			Tweet tweet = Tweet.fromJson(tweetJson);
			if (tweet != null) {
				tweets.add(tweet);
			}
		}

		return tweets;
	}

}
