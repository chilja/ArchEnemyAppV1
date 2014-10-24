package net.archenemy.archenemyapp.ui;

import java.io.Serializable;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.FacebookShareElement;
import net.archenemy.archenemyapp.data.Show;

public class ShowShareElement extends FacebookShareElement 
	implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Show mShow;

	public ShowShareElement(ActionBarActivity activity, Show show) {
	    super(activity);
	    
	    mShow = show;	
		mText1 = activity.getResources().getString(R.string.fb_share_name);
		mText2 = mShow.getDescription();
		mImageUri = mShow.getImageUri();
	}
    
    @Override
	public Bundle getPostingParameters() {
        Bundle postParams = new Bundle();
        postParams.putString("name", mActivity.getResources().getString(R.string.fb_share_name));
        postParams.putString("caption", mActivity.getResources().getString(R.string.fb_share_caption));
        postParams.putString("description", mShow.getDescription());
        postParams.putString("link", mShow.getTicketUri());
        postParams.putString("picture", mShow.getImageUri());
        return postParams;			
	}
}

