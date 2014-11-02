package Tour;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.FacebookSharable;

public class ShowShareElement 
	implements 
	FacebookSharable, 
	Serializable{
	
	private static final long serialVersionUID = 1L;
	private Show mShow;

	public ShowShareElement(ActionBarActivity activity, Show show) {
	    mShow = show;	
	}
    
    @Override
	public Bundle getPostingParameters(Activity activity) {
        Bundle postParams = new Bundle();
        postParams.putString("name", activity.getResources().getString(R.string.fb_share_name));
        postParams.putString("caption", activity.getResources().getString(R.string.fb_share_caption));
        postParams.putString("description", mShow.getDescription());
        postParams.putString("link", mShow.getTicketUri());
        postParams.putString("picture", mShow.getImageUri());
        return postParams;			
	}
}

