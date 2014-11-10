package net.archenemy.archenemyapp.model;

import android.app.Activity;
import android.view.View;

public class FacebookShareListener implements View.OnClickListener {
	
	private Activity mActivity;
	private FacebookSharable mSharable;
	private FacebookAdapter mFacebookAdapter;
	
	public FacebookShareListener(Activity activity, FacebookSharable sharable) {
		mActivity = activity;
		mSharable = sharable;
		mFacebookAdapter = FacebookAdapter.getInstance();
	}
	
	@Override
	public void onClick(View arg0) {
		mFacebookAdapter.startShareDialog(mSharable.getPostingParameters(mActivity), mActivity);			
	}		
}
