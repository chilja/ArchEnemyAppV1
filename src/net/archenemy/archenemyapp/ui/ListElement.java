package net.archenemy.archenemyapp.ui;

import android.app.Activity;
import android.view.View;
import android.widget.BaseAdapter;

public interface ListElement extends Comparable<ListElement>{
	public BaseAdapter getAdapter() ;	
	public void setAdapter(BaseAdapter adapter) ;	
	public View getView(Activity activity);
	public String getLink();
}
