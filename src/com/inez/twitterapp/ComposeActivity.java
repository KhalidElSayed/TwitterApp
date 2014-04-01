package com.inez.twitterapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.inez.twitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeActivity extends Activity {

	private TwitterClient client;
	private Tweet tweet;
	private ImageView iv_medium;
	private Uri outputFileUri;

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

		iv_medium = (ImageView) findViewById(R.id.iv_medium);
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
		byte[] bitmapdata = null;
		if(iv_medium.getDrawable() != null) {
		Bitmap bitmap = ((BitmapDrawable)iv_medium.getDrawable()).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    		bitmapdata = stream.toByteArray();
		}
		client.postUpdate(et_tweet.getText().toString(), bitmapdata, tweet == null ? 0 : tweet.getRemoteId(), new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject jsonTweet) {
				Intent data = new Intent();
				data.putExtra(TimelineActivity.TWEET_KEY, Tweet.fromJson(jsonTweet));
				setResult(RESULT_OK, data);
				finish();
			}
		});
	}
	
	public void onAddPhotoClick(View v) {
		openImageIntent();
	}

	public static final int MEDIA_REQUEST = 1;
	
	private void openImageIntent() {
		final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "TwitterApp" + File.separator);
		root.mkdirs();
		final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
		final File sdImageMainDirectory = new File(root, fname);
		outputFileUri = Uri.fromFile(sdImageMainDirectory);
		
		// Camera.
	    final List<Intent> cameraIntents = new ArrayList<Intent>();
	    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	    final PackageManager packageManager = getPackageManager();
	    final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
	    for(ResolveInfo res : listCam) {
	        final String packageName = res.activityInfo.packageName;
	        final Intent intent = new Intent(captureIntent);
	        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
	        intent.setPackage(packageName);
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        cameraIntents.add(intent);
	    }
	    
	    // Filesystem.
	    final Intent galleryIntent = new Intent();
	    galleryIntent.setType("image/*");
	    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

	    // Chooser of filesystem options.
	    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

	    // Add the camera options.
	    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

	    startActivityForResult(chooserIntent, MEDIA_REQUEST);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode == MEDIA_REQUEST ) {
			if ( resultCode == RESULT_OK ) {
				final boolean isCamera;
	            if(data == null) {
	                isCamera = true;
	            } else {
	                final String action = data.getAction();
	                if(action == null) {
	                    isCamera = false;
	                } else {
	                    isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	                }
	            }
	            Uri selectedImageUri;
	            if(isCamera) {
	                selectedImageUri = outputFileUri;
	            } else {
	                selectedImageUri = data == null ? null : data.getData();
	            }
	    		iv_medium.setVisibility(View.VISIBLE);
    			ImageLoader.getInstance().displayImage(selectedImageUri.toString(), iv_medium);
			}
		}
	}

}
