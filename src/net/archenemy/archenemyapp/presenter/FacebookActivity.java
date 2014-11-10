package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.Utility;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/*Background activity to handle Facebook interaction */

public abstract class FacebookActivity extends ActionBarActivity 
	implements FacebookLoginFragment.OnFacebookLoginListener {
	
	protected FacebookAdapter mFacebookAdapter;
	protected boolean mPendingLogin = false;
	
	//Facebook lifecycle helper	
	protected UiLifecycleHelper mUiHelper;
	
	protected Session.StatusCallback mCallback = 
	    new Session.StatusCallback() {
	    @Override
	    public void call(Session session, 
	            SessionState state, Exception exception) {
	    	onFacebookLogin();
	    }
	};
	
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
}
