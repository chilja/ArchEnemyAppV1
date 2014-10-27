package net.archenemy.archenemyapp.ui;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.Constants;
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

public class FacebookAccountFragment extends AccountFragment 
	implements FacebookAdapter.UserCallback {
	
	public static final String TAG = "FacebookAccount";
	protected static final int TITLE = R.string.title_facebook;

	protected FacebookAdapter mFacebookAdapter;
		
	protected UiLifecycleHelper mUiHelper;
	
	protected Session.StatusCallback mCallback = new Session.StatusCallback() {
	    @Override
	    public void call(final Session session, final SessionState state, final Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
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
	
	public void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	    	
	    	//set the logged in state
	    	setLoggedIn();
	    	
	        if (!state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
	            // Get the user's data.
	        	mFacebookAdapter.makeMeRequest(this);
	        }   
	        
	        ((LoginCallback) mActivity).onLogin();
	    } else {	    	
	    	// set the logged out state
	    	setLoggedOut();
	    	((LoginCallback) mActivity).onLogout();
	    }	    
	}
	
	public void onUserRequestCompleted(GraphUser user) {
		if (user != null) {
            // Set the text to the user's name.
			mName = user.getName();
            mUserNameView.setText(mName);
        }	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    mUiHelper.onActivityResult(requestCode, resultCode, data);
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
	    bundle.putString(Constants.FACEBOOK_USER_NAME, mName);
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
