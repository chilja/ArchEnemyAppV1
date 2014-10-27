package net.archenemy.archenemyapp.data;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public abstract class FacebookShareElement implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected transient DataAdapter mDataAdapter;

	//UI elements
	protected String mImageUri;
	protected String mText1;
	protected String mText2;
	
	public FacebookShareElement(ActionBarActivity activity) {
		super();
		mDataAdapter = new DataAdapter(activity);
	}
	
	public String getImageUri() {
		return mImageUri;
	}
	
	public String getText1() {
		return mText1;
	}
	
	public String getText2() {
		return mText2;
	}
	
    public abstract Bundle getPostingParameters(Activity activity); 
}



	
