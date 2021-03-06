package net.archenemy.archenemyapp.presenter;

import java.io.Serializable;
import com.facebook.widget.ProfilePictureView;
import net.archenemy.archenemyapp.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FacebookUser implements 
	FeedElement,
	Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String mProfileId ;
	protected transient ProfilePictureView mImageView;
	private String mName;
	private BaseAdapter mAdapter;
	
	public FacebookUser(String name, String profileId) {
		mProfileId = profileId;
		mName = name;
	}
	@Override
	public View getView(Activity activity){
		View view = null;
        LayoutInflater inflater =
                (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.facebook_user, null);
        
    	TextView userNameView = (TextView) view.findViewById(R.id.userNameView);
    	mImageView = (ProfilePictureView) view.findViewById(R.id.imageView);

    	if(userNameView != null) {
    		userNameView.setText(mName);
    	}

    	if (mImageView.getProfileId() != mProfileId)
    		mImageView.setProfileId(mProfileId);
       
        return view;
	}

	@Override
	public int compareTo(FeedElement element) {
		return 1;
	}
	@Override
	public BaseAdapter getAdapter() {
		return mAdapter;
	}
	@Override
	public void setAdapter(BaseAdapter adapter) {
		mAdapter = adapter;
	}
	@Override
	public String getLink() {
		return null;
	}	
}

