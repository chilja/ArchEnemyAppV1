package net.archenemy.archenemyapp.ui;

import java.util.Date;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.FacebookAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Tweet implements ListElement{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BaseAdapter mAdapter;
	private String mMessage;
	private Date mDate;
	private String mLink;
	
	public Tweet(Activity activity, String name, String message, Date createdAt, String link) {
		mDate = createdAt;
		mLink = link;
		mMessage = message;
	}
	
	@Override
	public int compareTo(ListElement element) {
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

    	if(messageView != null) {
    		messageView.setText(mMessage);
    	}

    	if(dateView != null) {
    		dateView.setText(mDate.toString());
    	}		            
        
        return view;
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
