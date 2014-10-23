package net.archenemy.archenemyapp.ui;

import twitter4j.User;
import net.archenemy.archenemyapp.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import net.archenemy.archenemyapp.data.TwitterAdapter;
import net.archenemy.archenemyapp.data.DataAdapter;

public class TwitterAccount extends AccountFragment 
	implements TwitterAdapter.ProfileCallback {
	
	private TwitterAdapter mTwitterAdapter;
	
	private User mUser;
	
	protected static final int TITLE = R.string.title_twitter;
	
	public static final String TAG = "TwitterAccount";
	
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
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(getLayout(), container, false);
		
		// Find the user's name view
		mUserNameView = (TextView) view.findViewById(R.id.userNameView);
		mSubtext = (TextView) view.findViewById(R.id.subTextView);	
		// Find the login button
		mLoginButton = (Button) view.findViewById(R.id.twitterButton);
		mLoginButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	if (mTwitterAdapter.isLoggedIn()) {
		    		String logout = getResources().getString(R.string.twitter_logout);
                    String cancel = getResources().getString(R.string.twitter_cancel);
                    String message;
                    if (mUser != null) {
                    	message = getResources().getString(R.string.twitter_logged_in) + " " + mUser.getName();
                    } else {
                    	message = getResources().getString(R.string.twitter_logged_in);
                    }
		    		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage(message)
                           .setCancelable(true)
                           .setPositiveButton(logout, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                            	   mTwitterAdapter.logOut(TwitterAccount.this);
                               }
                           })
                           .setNegativeButton(cancel, null);
                    builder.create().show();
		    		
		    		
		    		
		    	}else{
		    		mTwitterAdapter.login(); 
		    	}
		    }
		});
		
	    mTwitterAdapter = new TwitterAdapter(mActivity);
	    if (mTwitterAdapter.isLoggedIn()) {
	    	mTwitterAdapter.getUserProfile(this);
	    	setLoggedIn();
	    } else {
	    	setLoggedOut();
	    }
			
		return view;
	}
	
	protected int getLayout() {
		return R.layout.twitter_account;
	}
	
	private void setLoggedIn(){
    	mSubtext.setText(R.string.fb_subtext_in);
    	mLoginButton.setText(R.string.twitter_logout);  	
	}
	
	private void setLoggedOut(){
    	mSubtext.setText(R.string.fb_subtext_out);
    	mUserNameView.setText(null);
    	mLoginButton.setText(R.string.twitter_login);
	}

	@Override
	public void onLogoutCompleted(boolean isLoggedIn) {
		setLoggedOut();		
	}

	@Override
	public void onAuthorizationCompleted(Boolean isAuthorized) {
		// TODO Auto-generated method stub
		mTwitterAdapter.getUserProfile(this);
	}

	@Override
	public void onProfileRequestCompleted(User user) {
		mUser = user;
		if (user != null) {	
			mUserNameView.setText(user.getName());
			setLoggedIn();
		}
	}	
}
