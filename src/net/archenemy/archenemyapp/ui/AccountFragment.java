package net.archenemy.archenemyapp.ui;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.Constants;
import net.archenemy.archenemyapp.data.ProviderAdapter;
import net.archenemy.archenemyapp.data.Utility;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public abstract class AccountFragment extends BaseFragment {
	//UI elements
	protected TextView mUserNameView;
	protected TextView mSubtext;
	protected Button mLoginButton;	
	protected String mName;
	
	protected ProviderAdapter mProviderAdapter;

	protected void init() {
		if (mProviderAdapter.isLoggedIn()) {
    		setLoggedIn();
	    } else {
	    	setLoggedOut();
    	}
		if (Utility.isConnectedToNetwork(mActivity, false)) {
			setOnline();
		} else {
			setOffline();
		}		
	}
	
	protected void setLoggedOut(){
		mLoginButton.setEnabled(true);
		mLoginButton.setTextColor(getResources().getColor(Constants.WHITE));		
		mSubtext.setText(R.string.facebook_logged_out);		
		mUserNameView.setText(null);  	
	}

	protected void setLoggedIn(){
		mLoginButton.setEnabled(true);
		mLoginButton.setTextColor(getResources().getColor(Constants.WHITE));		
		mSubtext.setText(R.string.facebook_logged_in);
	}
	
	protected void setOffline() {
		mLoginButton.setEnabled(false);
		mLoginButton.setTextColor(getResources().getColor(Constants.LIGHTGREY));		
		mSubtext.setText(R.string.facebook_offline);
		mUserNameView.setText(null);
	}
	
	protected void setOnline() {
		mLoginButton.setEnabled(true);
		mLoginButton.setTextColor(getResources().getColor(Constants.WHITE));
	}	
}

