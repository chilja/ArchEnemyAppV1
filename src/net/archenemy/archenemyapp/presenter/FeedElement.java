package net.archenemy.archenemyapp.presenter;

import android.app.Activity;
import android.view.View;
import android.widget.BaseAdapter;

public interface FeedElement extends Comparable<FeedElement>{
	public BaseAdapter getAdapter() ;	
	public void setAdapter(BaseAdapter adapter) ;	
	public View getView(Activity activity);
	public String getLink();
}
