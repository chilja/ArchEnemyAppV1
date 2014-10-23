package net.archenemy.archenemyapp.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.TwitterAdapter;

public class TwitterLoginFragment extends BaseFragment {
	
	private TwitterAdapter mTwitterAdapter;
	protected Button mLoginButton;
	
	protected static final int TITLE = R.string.title_twitter;
	public static final String TAG = "TwitterLoginFragment";
	
	public int getTitle() {
		return TITLE;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}
	
	protected int getLayout() {
		return R.layout.twitter_login_fragment;
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


		// Find the login button
		mLoginButton = (Button) view.findViewById(R.id.twitterButton);
		mLoginButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	if (!mTwitterAdapter.isLoggedIn()) {
		    		mTwitterAdapter.login(); 
		    	}
		    }
		});
		
	    mTwitterAdapter = new TwitterAdapter(mActivity);
	    if (mTwitterAdapter.isLoggedIn()) {
	    	setLoggedIn();
	    } else {
	    	setLoggedOut();
	    }
			
		return view;
	}
	
	private void setLoggedIn(){
    	mLoginButton.setText(R.string.twitter_logout);  	
	}
	
	private void setLoggedOut(){
    	mLoginButton.setText(R.string.twitter_login);
	}
}
