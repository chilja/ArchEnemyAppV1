package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.BitmapUtility;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.Utility;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;

public class FacebookShareFragment extends BaseFragment {
	
	public static final String TAG = "FacebookShareFragment";
	protected static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	
	private Bundle mShareParams;
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
	    mShareParams = mActivity.getIntent().getBundleExtra(Constants.SHARE_PARAMS);
	     
	    if(savedInstanceState != null)  {
	    	mShareParams = savedInstanceState.getBundle(Constants.SHARE_PARAMS);
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

		mNameView.setText(mShareParams.getString("name"));
		mDescriptionView.setText(mShareParams.getString("description"));
		String imageUrl = mShareParams.getString("picture");
		if (imageUrl == null) {			
			mImageView.setVisibility(View.GONE);
		} else {
			BitmapUtility.loadBitmap(imageUrl, 
				mImageView, 200, 200);
			mImageView.setVisibility(View.VISIBLE);
		}
		mShareButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	if (Utility.isConnectedToNetwork(mActivity, true) && mFacebookAdapter.isLoggedIn()) {
		    		setEnabled();
		    		mFacebookAdapter.publishFeedDialog(mShareParams);  
		    	} else {
			    	setDisabled();
		    	}
		    }
		});
		
		if (Utility.isConnectedToNetwork(mActivity, false) && mFacebookAdapter.isLoggedIn()) {
    		setEnabled();
	    } else {
	    	setDisabled();
    	}
		
		return view;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}
	
	private void setEnabled() {
//		mShareButton.setEnabled(true);
		mShareButton.setTextColor(getResources().getColor(Constants.WHITE));
	}
	
	private void setDisabled() {
//		mShareButton.setEnabled(false);
		mShareButton.setTextColor(getResources().getColor(Constants.LIGHTGREY));
	}
	
	public void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {    	
	        if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
	            // Session updated with new permissions
	            // so try publishing once more.
		        if (mFacebookAdapter.isPendingPublish()) {
		        	if (Utility.isConnectedToNetwork(mActivity, false))
		        		mFacebookAdapter.publishStory(mShareParams);
		        }
	        }   
	    }    
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    bundle.putBundle(Constants.SHARE_PARAMS, mShareParams);
	    bundle.putBoolean(PENDING_PUBLISH_KEY, mFacebookAdapter.isPendingPublish());
	}

//	@Override
//	public void onPostExecute(Bitmap bitmap) {
//		mImageView.setImageBitmap(bitmap);		
//	}
}