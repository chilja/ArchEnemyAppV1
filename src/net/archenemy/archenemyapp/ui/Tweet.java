package net.archenemy.archenemyapp.ui;

import java.util.Date;

import net.archenemy.archenemyapp.R;
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

public class Tweet 
	implements 
		FeedElement, DataAdapter.BitmapCallback{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BaseAdapter mAdapter;
	private String mMessage;
	private Date mDate;
	private String mLink;
	private String mImageUrl ;
	private transient ImageView mImageView;
	private Bitmap mBitmap;
	
	public Tweet(Activity activity, String name, String message, Date createdAt, String link) {
		mDate = createdAt;
		mLink = link;
		mMessage = message;
	}
	
	public Tweet(Activity activity, String name, String message, Date createdAt, String link, String imageUrl) {
		mDate = createdAt;
		mLink = link;
		mMessage = message;
		mImageUrl = imageUrl;
	}
	
	@Override
	public int compareTo(FeedElement element) {
		if (element instanceof Tweet) {
			if ((mDate.getTime() - ((Tweet)element).mDate.getTime())<0) return 1;
			if ((mDate.getTime() - ((Tweet)element).mDate.getTime())>0) return -1;
		}
		return 0;
	}
	
	@Override
	public View getView(Activity activity){
		View view = null;
        
        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tweet, null);
        }

    	TextView messageView = (TextView) view.findViewById(R.id.messageView);
    	TextView dateView = (TextView) view.findViewById(R.id.dateView);
    	
    	mImageView = (ImageView) view.findViewById(R.id.imageView);
    	
    	//Bitmap already loaded?
    	if (mBitmap != null) {
    		mImageView.setImageBitmap(mBitmap);	
    	// URL provided? -> load bitmap
    	} else if (mImageUrl != null){
    		DataAdapter.loadBitmap(mImageUrl, mImageView, this, 400, 400);
    		mImageView.setVisibility(View.VISIBLE);
    	// no picture -> hide image view	
    	} else {
    		mImageView.setVisibility(View.GONE);
    	}

    	if(messageView != null) {
    		messageView.setText(mMessage);
    	}

    	if(dateView != null) {
    		dateView.setText(mDate.toString());
    	}		            
        
        return view;
	}
	
	@Override
	public void onPostExecute(Bitmap bitmap) {
		mBitmap = bitmap;		
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
		return mLink;
	}
}
