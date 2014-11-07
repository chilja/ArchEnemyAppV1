package net.archenemy.archenemyapp.presenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import net.archenemy.archenemyapp.model.Utility;

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
	protected List<FeedElement> mListElements = new ArrayList<FeedElement>();
	protected transient ListAdapter mListAdapter;
	protected int mUserId;
	protected static ArrayList<SocialMediaUser> mBandMembers;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.page_fragment, container, false);
	    
	    //get the user id of band member
    	Bundle args = getArguments();
    	mUserId = args.getInt(FacebookPageFragment.USER_ID, 1);
    	
		mBandMembers = mDataAdapter.getEnabledSocialMediaUsers();
	    
	    // Find the list view
	 	mListView = (ListView) view.findViewById(R.id.listView);
		mListView.setOnItemClickListener(new OnItemClickListener());
	    
	    refresh();
  	
	    return view;
	}
		
	public void refresh() {
		if (mIsAttached) {
			mListElements = getListElements();
			if (mActivity != null && mListView != null) {
			    mListAdapter = new ListAdapter(mActivity, R.id.listView, mListElements);
			    mListView.setAdapter(mListAdapter);
			}
			if (mListAdapter != null) {
				mListAdapter.notifyDataSetChanged();
			}
		}
	}
	
	protected abstract List<FeedElement> getListElements();
		
	protected class OnItemClickListener implements ListView.OnItemClickListener {	
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
        	String link = mListElements.get(position).getLink();
        	if (link != null)  {
        		Utility.startBrowserActivity(mActivity, link);
        	}
        }
	}
		
	protected class ListAdapter extends ArrayAdapter<FeedElement> {

	    protected ListAdapter(Context context, int resourceId, 
	                             List<FeedElement> feedElements) {
	        super(context, resourceId, feedElements);
	        //set list adapter for each element
	        if (feedElements != null && feedElements.size() > 0){        
		        for (int i = 0; i < feedElements.size(); i++) {
		            feedElements.get(i).setAdapter(this);
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
