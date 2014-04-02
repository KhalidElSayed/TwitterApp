package com.inez.twitterapp.dialogs;

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
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.inez.twitterapp.R;
import com.inez.twitterapp.TwitterApp;
import com.inez.twitterapp.TwitterClient;
import com.inez.twitterapp.helpers.Ids;
import com.inez.twitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeDialog extends DialogFragment {
	
	public interface ComposeDialogListener {
        void onFinishComposeDialog(Tweet tweet);
    }
	
	private View view;
	private Uri outputFileUri;
	private Tweet tweet;
	private EditText et_tweet;
	private ImageView iv_medium;
	private Button button_post;
	private Button button_add_photo;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.dialog_compose, container);
		getDialog().setTitle("Compose wisely!");
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		
		if ( getArguments() != null ) {
			tweet = (Tweet) getArguments().getSerializable(Ids.TWEET_KEY);
		}
		
		et_tweet = (EditText) view.findViewById(R.id.et_tweet);
		iv_medium = (ImageView) view.findViewById(R.id.iv_medium);
		button_post = (Button) view.findViewById(R.id.button_post);
		button_add_photo = (Button) view.findViewById(R.id.button_add_photo);
		

		if(tweet != null) {
			et_tweet.setText("@" + tweet.getUser().getScreenName() + " ");
			et_tweet.setSelection(et_tweet.length());
		}
		
		button_post.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final SimpleProgressDialog dialog = SimpleProgressDialog.build(getActivity());
				dialog.show();
						
				
				EditText et_tweet = (EditText) view.findViewById(R.id.et_tweet);
				TwitterClient client = TwitterApp.getRestClient();

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
						dialog.dismiss();
						Tweet tweet = Tweet.fromJson(jsonTweet);
						ComposeDialogListener listener = (ComposeDialogListener) getActivity();
						listener.onFinishComposeDialog(tweet);
						dismiss();
					}
				});
				
			}
		});
		
		button_add_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "TwitterApp" + File.separator);
				root.mkdirs();
				final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
				final File sdImageMainDirectory = new File(root, fname);
				outputFileUri = Uri.fromFile(sdImageMainDirectory);
				
			    final List<Intent> cameraIntents = new ArrayList<Intent>();
			    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			    final PackageManager packageManager = getActivity(). getPackageManager();
			    final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
			    for(ResolveInfo res : listCam) {
			        final String packageName = res.activityInfo.packageName;
			        final Intent intent = new Intent(captureIntent);
			        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			        intent.setPackage(packageName);
			        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			        cameraIntents.add(intent);
			    }
			    
			    final Intent galleryIntent = new Intent();
			    galleryIntent.setType("image/*");
			    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

			    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

			    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

			    getActivity().startActivityForResult(chooserIntent, Ids.MEDIA_REQUEST);
			}
		});
		
		et_tweet.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				TextView tv_counter = (TextView) view.findViewById(R.id.tv_counter);
				tv_counter.setText( et_tweet.getText().toString().length() + "/140" );
			}
		});
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode == Ids.MEDIA_REQUEST ) {
			if ( resultCode == Activity.RESULT_OK ) {
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
