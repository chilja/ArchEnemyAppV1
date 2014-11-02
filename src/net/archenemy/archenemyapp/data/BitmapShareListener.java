package net.archenemy.archenemyapp.data;

import android.app.Activity;
import android.view.View;

public class BitmapShareListener implements View.OnClickListener {

	private Activity mActivity;
	private BitmapSharable mSharable;
	
	public BitmapShareListener(Activity activity, BitmapSharable sharable) {
		mActivity = activity;
		mSharable = sharable;
	}
	
	@Override
	public void onClick(View view) {
		Utility.makeBitmapShareIntent(mActivity, mSharable.getBitmapUrl(mActivity));			
	}		
}
