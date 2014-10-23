package net.archenemy.archenemyapp.ui;

import java.util.ArrayList;
import java.util.List;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.DataAdapter;
import net.archenemy.archenemyapp.data.Utility;
import net.archenemy.archenemyapp.logic.BandMember;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TwitterPageFragment extends BaseFragment {

	private static final String TAG = "TwitterPageFragment";
	
	private transient ListView mListView;
	private List<FeedListElement> mListElements = new ArrayList<FeedListElement>();
	private transient FeedListAdapter mFeedListAdapter;
	private int mUserId;
	public static final String USER_ID = "mUserId";
	public static final String LIST_ELEMENTS = "mListElemts";
	
	private int mFeedType;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.list, container, false);
	
	    init();
	       
    	Bundle args = getArguments();
    	mUserId = args.getInt(FacebookPageFragment.USER_ID, 1);
	    
	    // Find the list view
	 	mListView = (ListView) view.findViewById(R.id.listView);
		mListView.setOnItemClickListener(new OnItemClickListener());
		
		if (mActivity != null && mListView != null) {
		    mFeedListAdapter = new FeedListAdapter(mActivity, R.id.listView, mListElements);
		    mListView.setAdapter(mFeedListAdapter);
		}
	    
	    refresh();
  	
	    return view;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}

	public void refresh() {
		
	    mListElements = new ArrayList<FeedListElement>();
		mListElements.addAll(DataAdapter.getBandMember(mUserId).getTwitterFeedElements());

		if (mActivity != null && mListView != null) {
		    mFeedListAdapter = new FeedListAdapter(mActivity, R.id.listView, mListElements);
		    mListView.setAdapter(mFeedListAdapter);
		}
		
		if (mFeedListAdapter != null) {
			mFeedListAdapter.notifyDataSetChanged();
		}
	}
	
	private class OnItemClickListener implements ListView.OnItemClickListener {	
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
        	String link = mListElements.get(position).getLink();
        	if (link != null)  {
        		Utility.startBrowserActivity(mActivity, link);
        	}
        }
	}
	
	private class FeedListAdapter extends ArrayAdapter<FeedListElement> {

	    private FeedListAdapter(Context context, int resourceId, 
	                             List<FeedListElement> listElements) {
	        super(context, resourceId, listElements);
	        
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
