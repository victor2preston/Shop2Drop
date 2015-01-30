package com.victorpreston.shop2drop;

import android.app.Activity;
import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.victorpreston.shop2drop.GetNameDialog;

public class S2DButtonFragment extends Fragment {
	
	public static final String TAG = "S2DButtonFragment";
	Button mItemButton = null;
	Button mColumnButton = null;
	Button mExitButton = null;
	//SQLiteDatabase mDb = null;
	public S2DButtonFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) { //should this be done in onAttach? - need to have database created in Activity.OnCreate!
		super.onCreate(savedInstanceState);
		Log.i(TAG,"onCreate savedInstanceState: " + String.valueOf(savedInstanceState));
		//setRetainInstance(true); // Only do this so the fragment is not completely re-created on a flip; however, change in layout may cause us to remove this.
		
		//mDb = ((Shop2DropActivity)getActivity()).getDatabase();
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i(TAG,"Activity = " + String.valueOf(activity));
		
	}
	@Override
	public View onCreateView(LayoutInflater layoutInflater,ViewGroup container,Bundle savedInstanceState) {
		Log.i(TAG,"onCreateView S2DButtonFragment inflater = " + String.valueOf(layoutInflater));
		View view = null;
		try {
			view = layoutInflater.inflate(R.layout.button_fragment_layout, container,true);
//			view = getView();
			//view = layoutInflater.inflate(R.layout.button_fragment_layout, container,false);
			
			Button itemButton = (Button)view.findViewById(R.id.newItemButton);
			itemButton.setOnClickListener( new View.OnClickListener() {
				public void onClick(View thisButton){
					Bundle bundle = new Bundle();
					bundle.putString(GetNameDialog.TITLE_KEY, "Get text");
					bundle.putString(GetNameDialog.TEXT_KEY, "Get new Item name: ");
					GetNameDialog newItemDialog = new GetNameDialog();
					newItemDialog.setArguments(bundle);
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.add(newItemDialog, null);
					transaction.addToBackStack(null);
					transaction.commit();
					newItemDialog.show(getActivity().getSupportFragmentManager(), null);
					transaction.remove(newItemDialog);
					transaction.commit();
					if(newItemDialog.getResultString() != null) {
						String newItem = newItemDialog.getResultString();
						
						S2DItemAdapter s2dItemAdapter = (S2DItemAdapter)((Shop2DropActivity)getActivity()).getItemAdapter();
						s2dItemAdapter.addColumn(newItem, S2DItemAdapter.columnType.text);
						
					}
				}
			});
			
			Button columnButton = (Button)view.findViewById(R.id.newColumnButton);
			columnButton.setOnClickListener( new View.OnClickListener() {
				public void onClick(View thisButton) {
					Bundle bundle = new Bundle();
					bundle.putString(GetNameDialog.TITLE_KEY,"Get Name");
					bundle.putString(GetNameDialog.TEXT_KEY,"Get new Column name: ");
					GetNameDialog newColumnDialog = new GetNameDialog();
					newColumnDialog.setArguments(bundle);
					newColumnDialog.show(((Shop2DropActivity)getActivity()).getSupportFragmentManager(), null);
					String result = newColumnDialog.getResultString();
					if(result == null)
						Toast.makeText(getActivity().getApplicationContext(), "No new column entered", Toast.LENGTH_LONG).show();
					String newColumn = newColumnDialog.getResultString();

					S2DItemAdapter s2dItemAdapter = (S2DItemAdapter)((Shop2DropActivity)getActivity()).getItemAdapter();
					s2dItemAdapter.addColumn(newColumn,S2DItemAdapter.columnType.text);
				}
			});
			Button exitButton = (Button)view.findViewById(R.id.exitButton);
			exitButton.setOnClickListener( new View.OnClickListener() {			
				public void onClick(View view) {
					getActivity().finish();
					getActivity().moveTaskToBack(true);
					
				}
			});
		}catch(Exception e) {
			StackTraceElement[] stE = e.getStackTrace();
			String firstElement = stE[0].toString();
			Log.e(TAG,"Exception = " + e.getMessage() + " " + firstElement);	
		}
		
		
		return view;
	}
}
