package net.archenemy.archenemyapp.ui;

import java.io.Serializable;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.DataAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

public class TwitterUser implements 
	ListElement,
	Serializable, 
	DataAdapter.BitmapCallback{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String mImageUrl ;
	protected transient ImageView mImageView;
	protected Bitmap mBitmap;
	private String mName;
	private String mUserName;
	private BaseAdapter mAdapter;
	
	public TwitterUser(String name, String userName, String imageUrl) {
		mImageUrl = imageUrl;
		mName = name;
		mUserName = userName;
	}
	@Override
	public View getView(Activity activity){
		View view = null;
	    LayoutInflater inflater =
	            (LayoutInflater) activity
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    view = inflater.inflate(R.layout.twitter_user, null);
	    
	    TextView nameView = (TextView) view.findViewById(R.id.nameView);
		TextView userNameView = (TextView) view.findViewById(R.id.userNameView);
		mImageView = (ImageView) view.findViewById(R.id.imageView);
		
		if(nameView != null) {
			nameView.setText(mName);
		}
		if(userNameView != null) {
			userNameView.setText("@"+mUserName);
		}
				
		//Bitmap already loaded?
    	if (mBitmap != null) {
    		mImageView.setImageBitmap(mBitmap);	
    	// URL provided? -> load bitmap
    	} else if (mImageUrl != null){
    		DataAdapter.loadBitmap(mImageUrl, mImageView, this);
    	} 
	   
	    return view;
	}
	
	@Override
	public void onPostExecute(Bitmap bitmap) {
		mBitmap = bitmap;		
	}
	@Override
	public int compareTo(ListElement element) {
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
		// TODO Auto-generated method stub
		return null;
	}

}
