package com.inez.twitterapp;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "JNamzEFe68j2qU0rtYsAA";       // Change this
    public static final String REST_CONSUMER_SECRET = "1uBNirGwE2MpW5fpb5K1iH744e8JYhXVPtmxaiaBtU"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://twitterapp"; // Change this (here and in manifest)
    
    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getHomeTimeline(AsyncHttpResponseHandler handler) {
    	getHomeTimeline(0, handler);
    }

    public void getHomeTimeline(long max_id, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
    	if ( max_id != 0 ) {
    		params.put("max_id", String.valueOf(max_id));
    	}
		params.put("count", "25");
    	client.get(apiUrl, params, handler);
    }
    
    public void postUpdate(String status, AsyncHttpResponseHandler handler) {
    	postUpdate(status, 0, handler);
    }

    public void postUpdate(String status, long inReplyToStatusId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/update.json");
    	RequestParams params = new RequestParams();
    	params.put("status", status);
    	if ( inReplyToStatusId != 0 ) {
    		params.put("in_reply_to_status_id", String.valueOf(inReplyToStatusId));
    	}
    	client.post(apiUrl, params, handler);
    }

}