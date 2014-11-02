package Tour;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.Constants;
import net.archenemy.archenemyapp.data.FacebookAdapter;
import net.archenemy.archenemyapp.data.FacebookSharable;
import net.archenemy.archenemyapp.data.Utility;
import net.archenemy.archenemyapp.ui.BaseFragment;

import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.PendingCall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ShareActionProvider.OnShareTargetSelectedListener;
import android.widget.TextView;

public class TourFragment extends BaseFragment {
	
	public static final String TAG = "TourFragment";
	protected static final int TITLE = R.string.title_tour;
	//restore keys
	public static final String SELECTED_SHOW_INDEX = "net.archenemy.archenemyapp.selectedShowIndex";
	public static final String POPUP_VISIBLE = "net.archenemy.archenemyapp.popupVisible";
	
	private ListView mListView;
	private List<ShowListElement> mListElements;
	private ShowListAdapter mAdapter;
	private int mSelectedShowIndex = -1;
	
	private ActionBarActivity mActivity;
	
	private ShareActionProvider mShareActionProvider;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.list_fragment, container, false);
	    
	    mListView = (ListView) view.findViewById(R.id.listView);
	    
		if (savedInstanceState!=null) {
			mSelectedShowIndex = savedInstanceState.getInt(SELECTED_SHOW_INDEX, -1);		
		}
		
 		setTitle(R.string.title_tour);
 		
	    mActivity = (ActionBarActivity) getActivity();
	    
 		// Set up the show list
 		mListElements = new ArrayList<ShowListElement>();
 		for (Show show: mDataAdapter.getShowList()){
 			mListElements.add(new ShowListElement(show));
 		}
 		mAdapter = new ShowListAdapter(mActivity, mListElements); 		
	    mListView.setAdapter(mAdapter);		
 		mListView.setOnItemClickListener(new OnItemClickListener());
	 		
	    return view;
	}
	
	public int getTitle() {
		return TITLE;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}
	
	public void onStart(){
		super.onStart();
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
	
	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    bundle.putInt(SELECTED_SHOW_INDEX, mSelectedShowIndex);
	}
	
	private void startBrowserActivity(ShowListElement element) {
		Utility.startBrowserActivity(mActivity, element.getShow().getTicketUri());
	}
	
	private void makeShareIntent(){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		Show show = mListElements.get(mSelectedShowIndex).getShow();
		intent.putExtra(Intent.EXTRA_TEXT, show.getShareText(getActivity()));
		intent.setType("text/plain");
		setShareIntent(intent);
	}
	
	private void setShareIntent(Intent shareIntent) {
	    if (mShareActionProvider != null) {
	        mShareActionProvider.setShareIntent(shareIntent);
	    }
	}
	
	public class OnItemClickListener implements ListView.OnItemClickListener {	
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
        	if (mSelectedShowIndex == position) {
        		mSelectedShowIndex = -1;
        	} else {
        		mSelectedShowIndex = position;
        	}      	
            mAdapter.notifyDataSetChanged();    
        }
	}	
	
	public void showPopup(View view) {
		//Set up the popup menu
	    final PopupMenu popup = new PopupMenu(mActivity, view);
	    
	    PopupMenu.OnMenuItemClickListener clickListener = new PopupMenu.OnMenuItemClickListener() {
	    	@Override
	    	public boolean onMenuItemClick(MenuItem item){
		    	switch (item.getItemId()) {
	            case R.id.actionBuyTicket: 
	            	startBrowserActivity(mListElements.get(mSelectedShowIndex));
	            	return true;
	            case R.id.actionFbShare:
	            	new FacebookAdapter(mActivity)
	            		.startShareDialog(
	            			new ShowShareElement(mActivity, mListElements.get(mSelectedShowIndex).getShow())
	            				.getPostingParameters(mActivity));
	            	return true;
	            }
		    	popup.dismiss();
		    	return false;
		    }	    	
	    };
	    popup.setOnMenuItemClickListener(clickListener);
	    
	    PopupMenu.OnDismissListener dismissListener = new PopupMenu.OnDismissListener(){
	    	@Override
	    	public void onDismiss(PopupMenu menu){
	    		mSelectedShowIndex = -1;
	    		mAdapter.notifyDataSetChanged();
	    	}
	    };    
	    popup.setOnDismissListener(dismissListener);
	    
	    MenuInflater inflater = popup.getMenuInflater();
	    Menu menu = popup.getMenu();
	    inflater.inflate(R.menu.on_tour, menu);
	    
	 // Locate MenuItem with ShareActionProvider
	    MenuItem item = menu.findItem(R.id.actionShare);

	    // Fetch and store ShareActionProvider
	    class MyShareActionProvider extends ShareActionProvider {
	    	public MyShareActionProvider(Context context) {
	    		super(context);
	    	}
	    	
	    	public void onPrepareSubMenu(SubMenu subMenu){
	    		super.onPrepareSubMenu(subMenu);
	    		int fbItemId = -1;
	    		for (int i= 0; i<subMenu.size(); i++) {
	    			MenuItem item = subMenu.getItem(i);
	    			String title = (String) item.getTitle();
	    			if (title.equalsIgnoreCase("Facebook")) {
	    				fbItemId = item.getItemId();
	    			}
	    		}
	    		if (fbItemId >= 0) {
	    			subMenu.removeItem(fbItemId);
	    		}
	    	}
	    };
	    mShareActionProvider = new MyShareActionProvider(mActivity);
	    item.setActionProvider(mShareActionProvider);
	    mShareActionProvider.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {

			@Override
			public boolean onShareTargetSelected(ShareActionProvider provider,
					Intent intent) {
				
				// TODO Auto-generated method stub
				return false;
			}
	    	
	    });
	    makeShareIntent();
	    popup.show();

	}
	
	private class ShowListAdapter extends ArrayAdapter<ShowListElement> {
	    private List<ShowListElement> mListElements;
	    private final int WHITE;
	    private final int BLACK;

	    public ShowListAdapter(Context context, 
	                             List<ShowListElement> listElements) {
	        super(context, R.layout.show_list_element, listElements);
	        this.mListElements = listElements;
	        for (int i = 0; i < listElements.size(); i++) {
	            listElements.get(i).setAdapter(this);
	        }
	        WHITE = getResources().getColor(android.R.color.white);
	        BLACK = getResources().getColor(android.R.color.black);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View view = convertView;
	        if (view == null) {
	            LayoutInflater inflater =
	                    (LayoutInflater) mActivity
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            view = inflater.inflate(R.layout.show_list_element, null);
	        }

	        ShowListElement listElement = mListElements.get(position);
	        if (listElement != null) {
	        	
	        	Show show = listElement.getShow();
	            TextView venueView = (TextView) view.findViewById(R.id.venueView);
	            TextView dateView = (TextView) view.findViewById(R.id.dateView);
	            TextView locationView =  (TextView) view.findViewById(R.id.locationView);
	            TextView countryView =  (TextView) view.findViewById(R.id.countryView);

	            if (venueView != null) {
	            	venueView.setText(show.getVenue());
	            }
	            if (dateView != null) {
	            	dateView.setText(show.getShowDate());
	            }
	            if (locationView != null) {
	            	locationView.setText(show.getLocation());
	            }
	            if (countryView != null) {
	            	countryView.setText(show.getCountry());
	            }

	            //selected element
	            if (mSelectedShowIndex == position) {
	            	view.setBackgroundResource(android.R.color.white);
	            	locationView.setTextColor(BLACK);
	            	dateView.setTextColor(BLACK);
	            	showPopup(view);
	            // not selected elements	
				} else {
					view.setBackgroundResource(android.R.color.transparent);
	            	locationView.setTextColor(WHITE);
	            	dateView.setTextColor(WHITE);
				}
	        }
	        return view;
	    }

	}
	// Container Activity must implement this interface
    public interface OnTourItemSelectedListener {
        public void onTourItemSelected(Show item);
    }
    
    class ShowListElement {
    	
    	private Show mShow;	
    	private BaseAdapter mAdapter;
    	
    	public ShowListElement(Show show) {
    		mShow = show;
    	}	
    	public BaseAdapter getAdapter() {
    		return mAdapter;
    	}
    	public void setAdapter(BaseAdapter adapter) {
    		mAdapter = adapter;
    	}

    	public Show getShow() {
    		return mShow;
    	}
    }  
}
