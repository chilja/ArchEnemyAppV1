package net.archenemy.archenemyapp.ui;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.R.string;
import net.archenemy.archenemyapp.data.DataAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;


public abstract class BaseFragment extends Fragment {
	
	protected int mTitle = R.string.app_name;
	protected String mTitleString = "";
	protected DataAdapter mDataAdapter;
	protected ActionBarActivity mActivity;
	protected FragmentManager mFragmentManager;
	
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
	
	protected void init() {
		mDataAdapter = new DataAdapter(getActivity());
		mActivity = (ActionBarActivity)getActivity() ;
		mFragmentManager = mActivity.getSupportFragmentManager();	
	}

}
