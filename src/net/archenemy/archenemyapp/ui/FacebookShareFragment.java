package net.archenemy.archenemyapp.ui;

import java.io.Serializable;
import java.util.ArrayList;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.R.id;
import net.archenemy.archenemyapp.R.layout;
import net.archenemy.archenemyapp.R.string;
import net.archenemy.archenemyapp.data.FacebookAdapter;
import net.archenemy.archenemyapp.data.FacebookAdapter.FeedCallback;
import net.archenemy.archenemyapp.data.FacebookAdapter.UserCallback;

import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;

public class FacebookShareFragment extends FacebookAccount
	implements FacebookAdapter.UserCallback{
	public static final String TAG = "FacebookShareFragment";
	private FacebookShareElement mShareElement;
	private Button mShareButton;
	private ImageView mImageView;
	private TextView mNameView;
	private TextView mDescriptionView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mActivity = (ActionBarActivity) getActivity();
	    mFacebookAdapter = new FacebookAdapter(mActivity);
	  //Get the content to be shared
	    Bundle args = getArguments();
	    if (args != null) {
	    	mShareElement = (FacebookShareElement) args.getSerializable(FacebookShareActivity.SHARE_ELEMENT); 
	    } else if(savedInstanceState != null)  {
	    	mShareElement = (FacebookShareElement) savedInstanceState.getSerializable(FacebookShareActivity.SHARE_ELEMENT);
	    }  
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.facebook_share_fragment, container, false);
	
		// Find the user's name view
		mUserNameView = (TextView) view.findViewById(R.id.userNameView);
		mSubtext = (TextView) view.findViewById(R.id.subTextView);
		
		// Find the login button
		mLoginButton = (Button) view.findViewById(R.id.loginButton);
//		
		// Find the share button
		mShareButton = (Button) view.findViewById(R.id.shareButton);
	
	    mImageView = (ImageView) view.findViewById(R.id.imageView);
	    mNameView = (TextView) view.findViewById(R.id.nameView);
	    mDescriptionView = (TextView) view.findViewById(R.id.descriptionView);

		mImageView.setImageDrawable(mShareElement.getIcon());
		mNameView.setText(mShareElement.getText1());
		mDescriptionView.setText(mShareElement.getText2());
		
		mShareButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        mFacebookAdapter.publishStory(mShareElement);  
		    }
		});

		setLoggedOut();
		
		// Get the user's data
		mFacebookAdapter.makeUserRequest(this);
		
		return view;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}

	private void setLoggedIn(){
		mShareButton.setEnabled(true);
	}

	private void setLoggedOut(){
		mShareButton.setEnabled(false);  	
	}
	
	public void onUserRequestCompleted(GraphUser user) {
		super.onUserRequestCompleted(user);
		if (user != null) {
            setLoggedIn();
        }	
	}
	
	public void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		super.onSessionStateChange(session, state, exception);
	    if (session != null && session.isOpened()) {
	    	
	    	//set the logged in state
	    	setLoggedIn();
	    	
	        if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
	            // Session updated with new permissions
	            // so try publishing once more.
		        if (mFacebookAdapter.isPendingPublish()) {
		            mFacebookAdapter.publishStory(mShareElement);
		        }
	        }   
	    } else {	    	
	    	// set the logged out state
	    	setLoggedOut();
	    }	    
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    bundle.putSerializable(FacebookShareActivity.SHARE_ELEMENT,(Serializable) mShareElement);
	    bundle.putBoolean(PENDING_PUBLISH_KEY, mFacebookAdapter.isPendingPublish());
	}
}