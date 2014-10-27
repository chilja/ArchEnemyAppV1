package net.archenemy.archenemyapp.ui;

import java.io.Serializable;
import java.util.Date;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.BandMember;
import net.archenemy.archenemyapp.data.DataAdapter;
import net.archenemy.archenemyapp.data.FacebookAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedListElement 
implements 
	Comparable<FeedListElement>,
	Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected transient DataAdapter mDataAdapter;
	protected transient BaseAdapter mAdapter;
	
	//feed attributes
	protected Date mDate;
	protected String mMessage;
	protected String mName;
	protected String mLink;
	protected BandMember mBandMember;
	
	private FeedListElement(Activity activity, String name, String message, Date createdAt){
		mDataAdapter = new DataAdapter(activity);
		setName(name);
		setMessage(message);
		setCreatedAt(createdAt);
	}
    	
	public BaseAdapter getAdapter() {
		return mAdapter;
	}
	public void setAdapter(BaseAdapter adapter) {
		mAdapter = adapter;
	}
	
	public View getView(Activity activity){
		return null;
	}
	
	public boolean isFromMember(BandMember member){
		return false;
	}
	
	@Override
	public int compareTo(FeedListElement element) {
		if ((mDate.getTime() - element.mDate.getTime())<0) return 1;
		if ((mDate.getTime() - element.mDate.getTime())>0) return -1;
		return 0;
	}
	
	protected void setMessage(String message) {
		mMessage = message;
	}
	
	public String getMessage() {
		return mMessage;
	}
	
	public String getName() {
		return mName;
	}

	protected void setName(String mName) {
		this.mName = mName;
	}

	public Date getCreatedAt() {
		return mDate;
	}

	protected void setCreatedAt(Date mCreatedAt) {
		mDate = mCreatedAt;
	}
	
	public String getLink() {
		return mLink;
	}

	protected void setLink(String mLink) {
		this.mLink = mLink;
	}

	static public class TwitterElement extends FeedListElement implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public TwitterElement(Activity activity, String name, String message, Date createdAt, String link) {
			super(activity, name, message, createdAt);
			setLink(link);
		}
		
		public boolean isFromMember(BandMember member){
			if (member != null) {
			String user = member.getTwitterUser();
			if (user != null)
				return user.equals(getName());
			}
			return false;
			
		}
		
		@Override
		public View getView(Activity activity){
			View view = null;
	        
	        if (view == null) {
	            LayoutInflater inflater =
	                    (LayoutInflater) activity
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            view = inflater.inflate(R.layout.twitter_feed_element, null);
	        }
	        
	        TextView userView = (TextView) view.findViewById(R.id.userView);
	    	TextView messageView = (TextView) view.findViewById(R.id.messageView);
	    	TextView dateView = (TextView) view.findViewById(R.id.dateView);

	    	if (userView != null) {
	    		userView.setText(getName());
	    	}

	    	if(messageView != null) {
	    		messageView.setText(getMessage());
	    	}

	    	if(dateView != null) {
	    		dateView.setText(mDate.toString());
	    	}		            
	        
	        return view;
		}
		
	}
	static public class FacebookElement extends FeedListElement 
	implements DataAdapter.BitmapCallback{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;


		protected String mPicture ;
		protected transient ImageView mImageView;
		protected String mId;
		protected Bitmap mPictureBitmap;

		public FacebookElement (Activity activity, String name, String id, String message, String createdAt, String picture, String link) {
			super(activity, name, message, FacebookAdapter.getDate(createdAt));
			setPicture(picture);
			setLink(link);
			setId(id);
		}

		public String getId() {
			return mId;
		}

		private void setId(String id) {
			this.mId = id;
		}

		@Override
		public View getView(Activity activity){
			View view = null;
	        LayoutInflater inflater =
	                (LayoutInflater) activity
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        view = inflater.inflate(R.layout.facebook_feed_element, null);
	        
	        TextView userView = (TextView) view.findViewById(R.id.userView);
	    	TextView messageView = (TextView) view.findViewById(R.id.messageView);
	    	TextView dateView = (TextView) view.findViewById(R.id.dateView);
	    	mImageView = (ImageView) view.findViewById(R.id.imageView);
	        
	    	if (userView != null) {
	    		userView.setText(getName());
	    	}
	
	    	if(messageView != null) {
	    		messageView.setText(getMessage());
	    	}
	
	    	if(dateView != null) {
	    		dateView.setText(mDate.toString());
	    	}
	    	
	    	//Bitmap already loaded?
	    	if (mPictureBitmap != null) {
	    		mImageView.setImageBitmap(mPictureBitmap);	
	    	// URL provided? -> load picture
	    	} else if (getPicture() != null){
	    		DataAdapter.loadBitmap(getPicture(), mImageView, this);
	    		mImageView.setVisibility(View.VISIBLE);
	    	// no picture -> hide image view	
	    	} else {
	    		mImageView.setVisibility(View.GONE);
	    	}
	       
	        return view;
		}
		
		
		public boolean isFromMember(BandMember member){
			if (member != null) {
				String userId = member.getFacebookUserId();
				String id = getId();
				if (userId != null && id != null)
					return id.startsWith(userId);
			}
			return false;
		}
		
		public String getPicture() {
			return mPicture;
		}
		
		private void setPicture(String picture) {
			this.mPicture = picture;
		}
		
		@Override
		public void onPostExecute(Bitmap bitmap) {
			mPictureBitmap = bitmap;		
		}		
	}
}

