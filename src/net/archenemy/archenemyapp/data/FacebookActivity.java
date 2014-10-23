package net.archenemy.archenemyapp.data;

import net.archenemy.archenemyapp.ui.FacebookAccount;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/*Background activity to handle Facebook interaction */

public class FacebookActivity extends ActionBarActivity 
implements FacebookAccount.LoginCallback{
	
	protected FacebookAdapter mFacebookAdapter;
	protected boolean mPendingLogin = false;
	
	//facebook lifecycle helper	
	protected UiLifecycleHelper mUiHelper;
	
	protected Session.StatusCallback mCallback = 
	    new Session.StatusCallback() {
	    @Override
	    public void call(Session session, 
	            SessionState state, Exception exception) {
	    	onSessionStateChange(session, state, exception);
	    }
	};
	
	protected void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mUiHelper = new UiLifecycleHelper(this, mCallback);	   
	    if (Utility.isConnectedToNetwork(this, false)) {
		    mUiHelper.onCreate(savedInstanceState);  
	    }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    mUiHelper.onResume();
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    mUiHelper.onSaveInstanceState(outState);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mUiHelper.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void onLogin() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLogout() {
		// TODO Auto-generated method stub
		
	}
}
