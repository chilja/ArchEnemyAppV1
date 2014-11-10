package net.archenemy.archenemyapp.presenter;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import net.archenemy.archenemyapp.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.TwitterAdapter;
import net.archenemy.archenemyapp.model.Utility;

public class TwitterAccountFragment extends AccountFragment {
	
	public static final int TITLE = R.string.title_twitter;	
	public static final String TAG = "TwitterAccount";
	
	private TwitterAdapter mTwitterAdapter;
	
	private TwitterLoginButton mTwitterLoginButton;
	
	public int getTitle() {
		return TITLE;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mTwitterAdapter = TwitterAdapter.getInstance();
	    mProviderAdapter = mTwitterAdapter;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		init();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.twitter_account, container, false);
		
		// Find the user's name view
		mUserNameView = (TextView) view.findViewById(R.id.userNameView);
		mSubtext = (TextView) view.findViewById(R.id.subTextView);	
		
		// Find the twitter login button in the layout
		mLoginButton = (Button) view.findViewById(R.id.twitterButton);
		mLoginButton.setText(R.string.twitter_logout);
		mLoginButton.setOnClickListener(new OnClickListener());
		
		// widget to perform login
		mTwitterLoginButton = new TwitterLoginButton(getActivity());	
		mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
			@Override
			public void success(Result<TwitterSession> result) {
				setLoggedIn();
			}
			
			@Override
			public void failure(TwitterException exception) {
			// Do something on failure
			}
		});		
		
		if (savedInstanceState != null) {
			mName = savedInstanceState.getString(Constants.TWITTER_USER_NAME, mName);
			mUserNameView.setText(mName);
		}

		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	 
	    // Pass the activity result to the login button.
	    mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
	}
	
	protected void setLoggedOut(){
		super.setLoggedOut();
		mLoginButton.setText(R.string.twitter_login);
	}
	
	protected void setLoggedIn() {
		super.setLoggedIn();		    
	    if (Utility.isConnectedToNetwork(getActivity(), false) && mTwitterAdapter.isLoggedIn()) {
	    	mName = mTwitterAdapter.getUserName();
	    	mUserNameView.setText(mName);
		 } 
		mLoginButton.setText(R.string.twitter_logout);
	}
	
	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    bundle.putString(Constants.TWITTER_USER_NAME, mName);
	}
	
	final class OnClickListener implements View.OnClickListener {

	    @Override
	    public void onClick(View view) {
	    	if (mTwitterAdapter.isLoggedIn()) {
	    		String logout = getResources().getString(R.string.twitter_logout);
                String cancel = getResources().getString(R.string.twitter_cancel);
                String message;
                if (mName != null) {
                	message = getResources().getString(R.string.account_logged_in) + ": " + mName;
                } else {
                	message = getResources().getString(R.string.account_logged_in);
                }
	    		
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                
                builder.setMessage(message)
                       .setCancelable(true)
                       .setPositiveButton(logout, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                        	   mTwitterAdapter.logOut();
                        	   setLoggedOut();		
                       		}
                           }
                       )
                       .setNegativeButton(cancel, null);
                builder.create().show();
	    		
	    	}else{
	    		mTwitterLoginButton.performClick(); 
	    	}
	    }		
	}
}
