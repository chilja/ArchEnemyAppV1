package net.archenemy.archenemyapp.data;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import net.archenemy.archenemyapp.R;

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
	public Bundle getPostingParameters(Activity activity) {
        Bundle postParams = new Bundle();
        postParams.putString("name", activity.getResources().getString(R.string.fb_share_name));
        postParams.putString("caption", activity.getResources().getString(R.string.fb_share_caption));
        postParams.putString("description", mShow.getDescription());
        postParams.putString("link", mShow.getTicketUri());
        postParams.putString("picture", mShow.getImageUri());
        return postParams;			
	}
}

