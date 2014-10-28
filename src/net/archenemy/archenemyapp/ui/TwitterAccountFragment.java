package net.archenemy.archenemyapp.ui;

import twitter4j.User;
import net.archenemy.archenemyapp.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import net.archenemy.archenemyapp.data.Constants;
import net.archenemy.archenemyapp.data.TwitterAdapter;
import net.archenemy.archenemyapp.data.Utility;

public class TwitterAccountFragment extends AccountFragment 
	implements TwitterAdapter.UserCallback {
	
	public static final int TITLE = R.string.title_twitter;	
	public static final String TAG = "TwitterAccount";
	
	private TwitterAdapter mTwitterAdapter;
	
	private User mUser;
	
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
	    mActivity = (ActionBarActivity) getActivity(); 
	    mTwitterAdapter = new TwitterAdapter(mActivity);
	    mProviderAdapter = mTwitterAdapter;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.twitter_account, container, false);
		
		// Find the user's name view
		mUserNameView = (TextView) view.findViewById(R.id.userNameView);
		mSubtext = (TextView) view.findViewById(R.id.subTextView);	
		// Find the login button
		mLoginButton = (Button) view.findViewById(R.id.twitterButton);
		mLoginButton.setOnClickListener(new OnClickListener());
		
		init();
		
		if (savedInstanceState != null)
			mName = savedInstanceState.getString(Constants.TWITTER_USER_NAME, mName);
		
		if (mName != null) { 			
			mUserNameView.setText(mName);
		} else {		    
		    if (Utility.isConnectedToNetwork(mActivity, false) && mTwitterAdapter.isLoggedIn()) {
			    mTwitterAdapter.makeMeRequest(this);
			 } 
		}

		return view;
	}
	
	protected void setLoggedOut(){
		super.setLoggedOut();
		mLoginButton.setText(R.string.twitter_login);		
	}
	
	protected void setLoggedIn() {
		super.setLoggedIn();
		mLoginButton.setText(R.string.twitter_logout);
	}

	@Override
	public void onTokenRequestCompleted() {
		mTwitterAdapter.makeMeRequest(this);
	}

	@Override
	public void onUserRequestCompleted(User user) {
		mUser = user;
		if (user != null) {	
			mName = user.getName();
			mUserNameView.setText(mName);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    bundle.putString(Constants.TWITTER_USER_NAME, mName);
	}
	
	final class OnClickListener implements View.OnClickListener {

	    @Override
	    public void onClick(View v) {
	    	if (mTwitterAdapter.isLoggedIn()) {
	    		String logout = getResources().getString(R.string.twitter_logout);
                String cancel = getResources().getString(R.string.twitter_cancel);
                String message;
                if (mName != null) {
                	message = getResources().getString(R.string.twitter_logged_in) + ": " + mName;
                } else {
                	message = getResources().getString(R.string.twitter_logged_in);
                }
	    		
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                
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
	    		mTwitterAdapter.logIn(); 
	    	}
	    }		
	}
}
