package net.archenemy.archenemyapp.ui;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.FacebookAdapter;
import net.archenemy.archenemyapp.data.Utility;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.ProfilePictureView;

public class FacebookLoginFragment extends AccountFragment {
	
	protected FacebookAdapter mFacebookAdapter;
	public static final String TAG = "FacebookLoginFragment";
		
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

	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.facebook_login_fragment, container, false);

		// Find the facebook login button
		mLoginButton = (Button) view.findViewById(R.id.loginButton);
			
		return view;
	}

}
