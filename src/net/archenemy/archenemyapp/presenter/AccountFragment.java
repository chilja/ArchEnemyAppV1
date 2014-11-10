package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.ProviderAdapter;
import net.archenemy.archenemyapp.model.Utility;
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
		if (mProviderAdapter != null && mProviderAdapter.isLoggedIn()) {
    		setLoggedIn();
	    } else {
	    	setLoggedOut();
    	}
		if (Utility.isConnectedToNetwork(getActivity(), false)) {
			setOnline();
		} else {
			setOffline();
		}		
	}
	
	protected void setLoggedOut(){
		mLoginButton.setEnabled(true);
		mLoginButton.setTextColor(getResources().getColor(Constants.WHITE));		
		mSubtext.setText(R.string.account_logged_out);		
		mUserNameView.setText(null);  	
	}

	protected void setLoggedIn(){
		mLoginButton.setEnabled(true);
		mLoginButton.setTextColor(getResources().getColor(Constants.WHITE));		
		mSubtext.setText(R.string.account_logged_in);
	}
	
	protected void setOffline() {
		mLoginButton.setEnabled(false);
		mLoginButton.setTextColor(getResources().getColor(Constants.LIGHTGREY));		
		mSubtext.setText(R.string.account_offline);
		mUserNameView.setText(null);
	}
	
	protected void setOnline() {
		mLoginButton.setEnabled(true);
		mLoginButton.setTextColor(getResources().getColor(Constants.WHITE));
	}	
}

