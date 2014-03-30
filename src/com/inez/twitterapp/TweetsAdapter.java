package com.inez.twitterapp;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inez.twitterapp.helpers.Helpers;
import com.inez.twitterapp.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetsAdapter extends ArrayAdapter<Tweet> {

	public TweetsAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View view = convertView;
	    if (view == null) {
	    	view = LayoutInflater.from(getContext()).inflate(R.layout.tweet_item, null);
	    }
	     
        Tweet tweet = getItem(position);
        
        // profile picture
        ImageView imageView = (ImageView) view.findViewById(R.id.ivProfile);
        ImageLoader.getInstance().displayImage(tweet.getUser().getProfileImageUrl(), imageView);
        
        // user
        TextView nameView = (TextView) view.findViewById(R.id.tvName);
        String formattedName = "<b>" + tweet.getUser().getName() + "</b>" + " <small><font color='#777777'>@" + tweet.getUser().getScreenName() + "</font></small>";
        nameView.setText(Html.fromHtml(formattedName));

        // body
        TextView bodyView = (TextView) view.findViewById(R.id.tvBody);
        bodyView.setText(Html.fromHtml(tweet.getBody()));
        //bodyView.setText(Html.fromHtml(android.util.Patterns.WEB_URL.matcher(tweet.getBody()).replaceAll("<a href='$0'>$0</a>")));

        // time
        TextView timeView = (TextView) view.findViewById(R.id.tvTime);
        timeView.setText(Html.fromHtml("<small><font color='#777777'>" + Helpers.getRelativeTime(tweet.getCreatedAt()) + "</font></small>"));

        return view;
	}
}