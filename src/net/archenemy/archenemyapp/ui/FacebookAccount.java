package net.archenemy.archenemyapp.ui;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.FacebookAdapter;
import net.archenemy.archenemyapp.data.Utility;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import android.support.v7.app.ActionBarActivity;

public class FacebookAccount extends AccountFragment 
	implements FacebookAdapter.UserCallback {
	
	public static final String TAG = "FacebookAccount";

	protected FacebookAdapter mFacebookAdapter;
		
	protected UiLifecycleHelper mUiHelper;
	
	protected Session.StatusCallback mCallback = new Session.StatusCallback() {
	    @Override
	    public void call(final Session session, final SessionState state, final Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
		
	protected static final int TITLE = R.string.title_facebook;
	
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
	    mUiHelper = new UiLifecycleHelper(mActivity, mCallback); 
	    
	    if (Utility.isConnectedToNetwork(mActivity, false)) {
		    mUiHelper.onCreate(savedInstanceState);  
	    } 
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

		
		if (Utility.isConnectedToNetwork(mActivity, false)) {
			if(mFacebookAdapter.isLoggedIn()) {
				// Get the user's data
				mFacebookAdapter.makeUserRequest(this);
				setLoggedIn();
			} else {
				setLoggedOut();
			}
		} else {
			setOffline();
		}
		return view;
	}

	private void setLoggedOut(){
		mLoginButton.setEnabled(true);
		mSubtext.setText(R.string.facebook_login_header);
		mUserNameView.setText(null);  	
	}

	private void setLoggedIn(){
		mLoginButton.setEnabled(true);
		mSubtext.setText(R.string.fb_subtext_in);
	}
	
	private void setOffline() {
		mLoginButton.setEnabled(false);
		mSubtext.setText(R.string.fb_subtext_offline);
	}

	public void onUserRequestCompleted(GraphUser user) {
		if (user != null) {
            // Set the text to the user's name.
            mUserNameView.setText(user.getName());
            setLoggedIn();
        }	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    mUiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	    	
	    	//set the logged in state
	    	setLoggedIn();
	    	
	        if (!state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
	            // Get the user's data.
	        	mFacebookAdapter.makeUserRequest(this);
	        }   
	        
	        ((LoginCallback) mActivity).onLogin();
	    } else {	    	
	    	// set the logged out state
	    	setLoggedOut();
	    	((LoginCallback) mActivity).onLogout();
	    }	    
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    mUiHelper.onResume();	
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    mUiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    mUiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    mUiHelper.onDestroy();
	}
	
	public interface LoginCallback {
		void onLogin();
		void onLogout();
	}
}
