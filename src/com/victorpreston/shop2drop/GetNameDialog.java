/**
 * 
 */
package com.victorpreston.shop2drop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author victorpreston
 *
 */
public class GetNameDialog extends DialogFragment {

	public final static String TITLE_KEY = "title";
	public final static String TEXT_KEY = "text";
	
	private String mResultString;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		String title = args.getString(TITLE_KEY);
		String text = args.getString(TEXT_KEY);
		// Use the Builder class for convenience

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		mResultString = null;
		builder.setTitle(title);
		builder.setMessage(text);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				mResultString = dialog.toString();				
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				mResultString = null;		
			}
		});
		LayoutInflater factory = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View textEntryView = factory.inflate(R.layout.get_name_dialog, null);
		builder.setView(textEntryView);
		return builder.create();
	}
	
	public String getResultString() {
		return mResultString;
	}
	
	/* (non-Javadoc)
	 * @see android.content.DialogInterface#cancel()
	 */
	public void cancel() {
		Log.println(STYLE_NORMAL, "", "NOT using resultString: " + getResultString());

	}

	/* (non-Javadoc)
	 * @see android.content.DialogInterface#dismiss()
	 */
	public void dismiss() {
		Log.println(STYLE_NORMAL, "", "NOT using resultString: " + getResultString());

	}

}
