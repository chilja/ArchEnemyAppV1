package net.archenemy.archenemyapp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.presenter.TwitterAccountFragment.OnClickListener;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class TwitterLoginFragment extends BaseFragment {
	
	public static final int TITLE = R.string.title_twitter;
	public static final String TAG = "TwitterLoginFragment";

	private TwitterLoginButton mTwitterLoginButton;
	private Button mLoginButton;
	
	private OnLTwitteroginListener mOnLoginListener;
	
	public int getTitle() {
		return TITLE;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}
	
	protected int getLayout() {
		return R.layout.twitter_login_fragment;
	}
		
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
            mOnLoginListener = (OnLTwitteroginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnLoginListener");
        }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mActivity = (ActionBarActivity) getActivity();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);	 
	    // Pass the activity result to the login button.
	    if (mTwitterLoginButton != null) 
	    	mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(getLayout(), container, false);
	    
	    final ImageView providerIcon = (ImageView) view.findViewById(R.id.providerIcon);
	    final Animation birdAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.circle);
	    providerIcon.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	providerIcon.startAnimation(birdAnimation);
		    }
		});

		// Find the login button
		mLoginButton = (Button) view.findViewById(R.id.twitterButton);
		mLoginButton.setText(R.string.twitter_login);
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
		    public void onClick(View view) {
		    	mTwitterLoginButton.performClick();
			}
		});
		
		//invisible widget to perform login
		mTwitterLoginButton = new TwitterLoginButton(mActivity);	
		mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
			@Override
			public void success(Result<TwitterSession> result) {
				mOnLoginListener.onTwitterLogin();
			}
			
			@Override
			public void failure(TwitterException exception) {
			// Do something on failure
			}
		});				
		return view;
	}
	
	public interface OnLTwitteroginListener {
		void onTwitterLogin();
	}
}
