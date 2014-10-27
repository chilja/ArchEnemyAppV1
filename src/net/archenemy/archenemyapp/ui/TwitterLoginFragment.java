package net.archenemy.archenemyapp.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.Constants;
import net.archenemy.archenemyapp.data.TwitterAdapter;
import net.archenemy.archenemyapp.data.Utility;

public class TwitterLoginFragment extends BaseFragment {
	
	public static final int TITLE = R.string.title_twitter;
	public static final String TAG = "TwitterLoginFragment";
	
	private TwitterAdapter mTwitterAdapter;
	private Button mLoginButton;
	
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
	    mTwitterAdapter = new TwitterAdapter(mActivity);
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
		    	if (Utility.isConnectedToNetwork(mActivity, true))  {
		    		mTwitterAdapter.logIn(); 
		    	}
		    }
		});
		
		mLoginButton.setText(R.string.twitter_login);
		
		return view;
	}
}
