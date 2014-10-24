package net.archenemy.archenemyapp.ui;

import java.io.Serializable;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.FacebookAdapter;
import net.archenemy.archenemyapp.data.FacebookShareElement;
import net.archenemy.archenemyapp.data.DataAdapter;
import net.archenemy.archenemyapp.data.Utility;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;

public class FacebookShareFragment extends BaseFragment
	implements FacebookAdapter.UserCallback,
	DataAdapter.BitmapCallback{
	
	public static final String TAG = "FacebookShareFragment";
	protected static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	
	private FacebookShareElement mShareElement;
	protected FacebookAdapter mFacebookAdapter;
	
	private Button mShareButton;
	private ImageView mImageView;
	private TextView mNameView;
	private TextView mDescriptionView;
	
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
	    View view = inflater.inflate(R.layout.facebook_share_fragment, container, false);
	    
	  //Get the content to be shared
	    Bundle args = mActivity.getIntent().getBundleExtra(FacebookShareActivity.SHARE_ELEMENT);
	    if (args != null) {
	    	mShareElement = (FacebookShareElement) args.getSerializable(FacebookShareActivity.SHARE_ELEMENT); 
	    } else if(savedInstanceState != null)  {
	    	mShareElement = (FacebookShareElement) savedInstanceState.getSerializable(FacebookShareActivity.SHARE_ELEMENT);
	    }  
	    
		if (savedInstanceState != null) {
		    mFacebookAdapter.setPendingPublish(
		            savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false));
		}
		
		// Find the share button
		mShareButton = (Button) view.findViewById(R.id.shareButton);
	
	    mImageView = (ImageView) view.findViewById(R.id.imageView);
	    mNameView = (TextView) view.findViewById(R.id.nameView);
	    mDescriptionView = (TextView) view.findViewById(R.id.descriptionView);

		mNameView.setText(mShareElement.getText1());
		mDescriptionView.setText(mShareElement.getText2());
		
		mShareButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        mFacebookAdapter.publishStory(mShareElement);  
		    }
		});
		
		if (Utility.isConnectedToNetwork(mActivity, false)){
			if(mFacebookAdapter.isLoggedIn()) {
				// Get the user's data
				mFacebookAdapter.makeUserRequest(this);
				DataAdapter.getBitmap(mShareElement.getImageUri(), this);
				setLoggedIn();
			} else {
				setLoggedOut();
			}
			
		} else {
			setOffline();
		}
		
		return view;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}
	
	private void setOffline() {
		mShareButton.setEnabled(false);
	}

	private void setLoggedIn(){
		mShareButton.setEnabled(true);
	}

	private void setLoggedOut(){
		mShareButton.setEnabled(false);  	
	}
	
	public void onUserRequestCompleted(GraphUser user) {
		if (user != null) {
            setLoggedIn();
        }	
	}
	
	public void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	    	
	    	//set the logged in state
	    	setLoggedIn();
	    	
	        if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
	            // Session updated with new permissions
	            // so try publishing once more.
		        if (mFacebookAdapter.isPendingPublish()) {
		        	if (Utility.isConnectedToNetwork(mActivity, false))
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

	@Override
	public void onPostExecute(Bitmap bitmap) {
		mImageView.setImageBitmap(bitmap);		
	}
}