package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.DataAdapter;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;


public abstract class BaseFragment extends Fragment {
	
	protected int mTitle = R.string.app_name;//default
	protected String mTitleString = "";
	protected DataAdapter mDataAdapter;
	protected ActionBarActivity mActivity;
	protected FragmentManager mFragmentManager;
	protected boolean mIsResumed = false;
	protected boolean mIsAttached = false;
	
	public abstract String getTAG();
	
	protected void refresh(){	
	}

	public int getTitle() {
		return mTitle;
	}

	public void setTitle(int title) {
		this.mTitle = title;
		mTitleString = getResources().getString(title);
	}

	public String getTitleString() {
		if (mTitleString != null)
			return mTitleString;
		return getResources().getString(mTitle);
	}

	public void setTitleString(String titleString) {
		this.mTitleString = titleString;
	}
	
	protected void init(Activity activity) {
		mActivity = (ActionBarActivity)activity ;
		mDataAdapter = new DataAdapter(mActivity);		
		mFragmentManager = mActivity.getSupportFragmentManager();	
	}

	@Override
	public void onPause() {
		super.onPause();
		mIsResumed = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		mIsResumed = true;
	}	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		init(activity);
		mIsAttached = true;
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		mIsAttached = false;
	}
}
