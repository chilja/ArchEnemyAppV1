package net.archenemy.archenemyapp.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.Utility;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public abstract class PageFragment extends BaseFragment 
	implements Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String TAG = "FacebookPageFragment";
	
	public static final String USER_ID = TAG + "mUserId";
	
	protected transient ListView mListView;
	protected List<FeedListElement> mListElements = new ArrayList<FeedListElement>();
	protected transient FeedListAdapter mFeedListAdapter;
	protected int mUserId;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.list_fragment, container, false);
	
	    init();
	    
	    //get the user id of band member
    	Bundle args = getArguments();
    	mUserId = args.getInt(FacebookPageFragment.USER_ID, 1);
	    
	    // Find the list view
	 	mListView = (ListView) view.findViewById(R.id.listView);
		mListView.setOnItemClickListener(new OnItemClickListener());
	    
	    refresh();
  	
	    return view;
	}
		
	public void refresh() {
		mListElements = getListElements();
		if (mActivity != null && mListView != null) {
		    mFeedListAdapter = new FeedListAdapter(mActivity, R.id.listView, mListElements);
		    mListView.setAdapter(mFeedListAdapter);
		}
		if (mFeedListAdapter != null) {
			mFeedListAdapter.notifyDataSetChanged();
		}
	}
	
	protected abstract List<FeedListElement> getListElements();
		
	protected class OnItemClickListener implements ListView.OnItemClickListener {	
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
        	String link = mListElements.get(position).getLink();
        	if (link != null)  {
        		Utility.startBrowserActivity(mActivity, link);
        	}
        }
	}
		
	protected class FeedListAdapter extends ArrayAdapter<FeedListElement> {

	    protected FeedListAdapter(Context context, int resourceId, 
	                             List<FeedListElement> listElements) {
	        super(context, resourceId, listElements);
	        //set list adapter for each element
	        if (listElements != null && listElements.size() > 0){        
		        for (int i = 0; i < listElements.size(); i++) {
		            listElements.get(i).setAdapter(this);
		        }
	        }
	    }
	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {	    	
	    	if (mListElements != null && mListElements.size() > 0){
		         return mListElements.get(position).getView(mActivity);
	    	}
	    	return null;
	    }
	}
}
