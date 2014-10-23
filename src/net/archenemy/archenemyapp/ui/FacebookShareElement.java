package net.archenemy.archenemyapp.ui;

import java.io.Serializable;
import java.util.ArrayList;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.DataAdapter;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public abstract class FacebookShareElement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected transient FacebookShareActivity mActivity;
	protected transient DataAdapter mDataAdapter;

	//UI elements
	protected transient Drawable mIcon;
	protected String mText1;
	protected String mText2;
	
	protected int mRequestCode;
	
	public FacebookShareElement(Drawable icon, String text1,
			int requestCode, FacebookShareActivity activity) {
		super();
		mIcon = icon;
		mText1 = text1;
//		mText2 = text2;
		mRequestCode = requestCode;
		mActivity = activity;
		mDataAdapter = new DataAdapter(activity);
	}
	
	public Drawable getIcon() {
		return mIcon;
	}
	public String getText1() {
		return mText1;
	}
	public void setText1(String text1) {
		mText1 = text1;
	}
	public String getText2() {
		return mText2;
	}
	public void setText2(String text2) {
		mText2 = text2;
	}
	public int getRequestCode() {
		return mRequestCode;
	}
    public Bundle getPostingParameters() {
    	return null;
    }

	public static class Show extends FacebookShareElement {
	
		private static final long serialVersionUID = 1L;
		private static final String SHOW_KEY = "show";
		private transient ArrayList<net.archenemy.archenemyapp.logic.Show> mShowList;
		private int mSelectedShowIndex;
		
		public Show(int requestCode, FacebookShareActivity activity, int showIndex) {
		    super(activity.getResources().getDrawable(R.drawable.tourposter),
		            activity.getResources().getString(R.string.fb_share_name),
//		            activity.getResources().getString(R.string.action_show_default),
		          requestCode,
		          activity);
		    
		    mShowList = mDataAdapter.getShowList();
		    setSelectedShow(showIndex, false);	    
		}
		
		protected net.archenemy.archenemyapp.logic.Show getShow(){
			return mShowList.get(mSelectedShowIndex);
		}
		
		private void setSelectedShow(int showIndex, boolean notifyDataChanged){
			if (showIndex >= 0) {
				mSelectedShowIndex = showIndex;
		        setShowText();
			}
		}
		
		public void setSelectedShow(int showIndex){
			setSelectedShow(showIndex, true);
		}
		
		private void setShowText() {
		    if (mShowList.get(mSelectedShowIndex) != null) {
		        setText2(mShowList.get(mSelectedShowIndex).getDescription());
		    } 
	    } 
	    
	    @Override
		public Bundle getPostingParameters() {
	    	net.archenemy.archenemyapp.logic.Show show = getShow();
	        Bundle postParams = new Bundle();
	        postParams.putString("name", mActivity.getResources().getString(R.string.fb_share_name));
	        postParams.putString("caption", mActivity.getResources().getString(R.string.fb_share_caption));
	        postParams.putString("description", show.getDescription());
	        postParams.putString("link", show.getTicketUri());
	        postParams.putString("picture", show.getImageUri());
	        return postParams;
			
		}
	}
}



	
