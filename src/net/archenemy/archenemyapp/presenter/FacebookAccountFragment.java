package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.Utility;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.model.GraphUser;
import android.support.v7.app.ActionBarActivity;

public class FacebookAccountFragment extends AccountFragment 
	implements FacebookAdapter.UserCallback{
	
	public static final String TAG = "FacebookAccount";
	protected static final int TITLE = R.string.title_facebook;

	protected FacebookAdapter mFacebookAdapter;
	
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
	    mFacebookAdapter = new FacebookAdapter(mActivity);
	    mProviderAdapter = mFacebookAdapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.facebook_account, container, false);
	    
		// Find the user's name view
		mUserNameView = (TextView) view.findViewById(R.id.userNameView);
		mSubtext = (TextView) view.findViewById(R.id.subTextView);	
		// Find the facebook login button
		mLoginButton = (Button) view.findViewById(R.id.loginButton);

		init();
		
		if (savedInstanceState != null)
			mName = savedInstanceState.getString(Constants.FACEBOOK_USER_NAME, mName);
		
		if (mName != null) { 			
			mUserNameView.setText(mName);
		} else {		
			if (Utility.isConnectedToNetwork(mActivity, false) && mProviderAdapter.isLoggedIn()) {
			    mFacebookAdapter.makeMeRequest(this);
			 }
		}
		
		return view;
	}
	
	public void onFacebookLogin() {
	    if (mFacebookAdapter.isLoggedIn()) {
	    	
	    	//set the logged in state
	    	setLoggedIn();
	        mFacebookAdapter.makeMeRequest(this); 
	        
	    } else {	    	
	    	// set the logged out state
	    	setLoggedOut();
	    }	    
	}
	
	public void onUserRequestCompleted(GraphUser user) {
		if (user != null) {
            // Set the text to the user's name.
			mName = user.getName();
            mUserNameView.setText(mName);
        }	
	}
}
