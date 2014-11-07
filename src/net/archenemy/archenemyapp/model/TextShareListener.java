package net.archenemy.archenemyapp.model;

import android.app.Activity;
import android.view.View;

public class TextShareListener implements View.OnClickListener {
		
	private Activity mActivity;
	private TextSharable mSharable;
	
	public TextShareListener(Activity activity, TextSharable sharable) {
		mActivity = activity;
		mSharable = sharable;
	}
	
	@Override
	public void onClick(View view) {
		Utility.makeTextShareIntent(mActivity, mSharable.getText(mActivity), mSharable.getSubject(mActivity));			
	}		
}

