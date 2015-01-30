package com.victorpreston.shop2drop;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class S2DListFragment extends ListFragment {

	public static final String TAG = "S2DListFragment";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	Log.i(TAG,"onActivityCreated; savedInstanceState = " + String.valueOf(savedInstanceState));
        super.onActivityCreated(savedInstanceState);
        Shop2DropActivity activity = (Shop2DropActivity)getActivity();
        
        //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        S2DItemAdapter s2dItemAdapter = new S2DItemAdapter(activity.getDatabase(), getActivity());
        Log.i(TAG,"activity = " + String.valueOf(activity) + "; s2dItemAdapter = " + String.valueOf(s2dItemAdapter));
        setListAdapter(s2dItemAdapter);
        Log.i(TAG,"End onActivityCreated");
    }

    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	Log.i(TAG,"Activity = " + String.valueOf(activity));
    }
    @Override
	public View onCreateView(LayoutInflater layoutInflater,ViewGroup container,Bundle savedInstanceState) {
		Log.i(TAG,"onCreateView inflater = " + String.valueOf(layoutInflater));
		//View view = super.onCreateView(layoutInflater, container, savedInstanceState); //just to see what happens!
		View view = layoutInflater.inflate(R.layout.list_fragment, container, true);
		Log.i(TAG,"Passed to inflater = " + R.id.listFragment);
    	return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
        // What should we do when an item is clicked?
    }

}
