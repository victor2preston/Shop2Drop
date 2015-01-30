package com.victorpreston.shop2drop;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.support.v4.app.FragmentActivity;


public class Shop2DropActivity extends FragmentActivity {
	
	static final String TAG = "Shop2DropActivity";
//	private Activity mActivity = null;
	public static final int MENU_ID1 = 1;
	public static final int MENU_ID2 = 2;
	public static final String S2DDATABASE_NAME= "s2dDatabase.db";
	public static final String S2DDATABASE_FIRST_VERSION_COLUMN = "large";
	public static final String S2DDATABASE_SECOND_VERSION_COLUMN = "small";
	//private SQLiteCursor mCursor;
    private SQLiteDatabase mDb = null;
	private S2DItemAdapter mAdapter = null;
	private Bundle mSavedInstanceState = null;
	private S2DButtonFragment mButtonFragment;
	private S2DListFragment mListFragment;
	private FragmentManager mFragmentManager = null;
	private FragmentTransaction mFragmentTransaction = null;
	enum menuIds{
		MEMU_ID,
		MENU_ID1
	}
	//public SQLiteCursor getCursor() { return mCursor; }
	public SQLiteDatabase getDatabase() { return mDb; }
	public S2DItemAdapter getItemAdapter() { return mAdapter; }
	public Bundle getSavedInstanceState() { return mSavedInstanceState; }
	public FragmentManager getLocalFragmentManager() { return mFragmentManager; }
	public FragmentTransaction getFragmentTransaction() { return mFragmentTransaction; }
	
    final class s2dItemOpenHelper extends SQLiteOpenHelper {
    	static final int currentLargeVersion = 1;
    	static final int currentSmallVersion = 1;
    	static final int S2DDATABASE_VERSION = 1;
    	static final String S2DDATABASE_VERSION_TABLE = "versionTable";
    	
    	s2dItemOpenHelper(Context context,String name, int version ){
    		super(context,name,null,version);
    	}
    	@Override
    	public void onCreate(SQLiteDatabase db){
    		if(db != null) {
    			mDb = db;
    			mFragmentManager = getSupportFragmentManager();
    			Toast.makeText(getApplicationContext(), "Using existing database", Toast.LENGTH_LONG).show();
    		}
    		else {
	    		String databasePath = Environment.getExternalStorageDirectory() + File.separator + S2DDATABASE_NAME;
	    		String version_table_create = "CREATE TABLE IF NOT EXISTS " + S2DDATABASE_VERSION_TABLE + " (large int NOT NULL, small int);" ;
	    		String versionStrings[] ={ S2DDATABASE_FIRST_VERSION_COLUMN , S2DDATABASE_SECOND_VERSION_COLUMN };
	    		try {
	    			File tryThis = new File(databasePath);
	    			if(tryThis.exists())
	    				tryThis.delete();
	    			mDb = SQLiteDatabase.openOrCreateDatabase( databasePath, null);
	    			if(mDb != null){
	    				mDb.execSQL(version_table_create);
	    				
	    				ContentValues values = new ContentValues();
	    				values.put(S2DDATABASE_FIRST_VERSION_COLUMN, 1);
	    				values.put(S2DDATABASE_SECOND_VERSION_COLUMN, 0);
						mDb.insert(S2DDATABASE_VERSION_TABLE , null, values );
	    				Cursor versionCursor = mDb.query(S2DDATABASE_VERSION_TABLE , versionStrings, null, null, null, null, null);
	    				int n = versionCursor.getCount();
	    				if(!versionCursor.moveToFirst() || n != 1) {
	    					Exception e = new Exception("No version table in database: " + S2DDATABASE_NAME);
	    					throw e;
	    				}
	    				int largeVersion = versionCursor.getInt(0);
	    				int smallVersion = versionCursor.getInt(1);
	    				if(n > 1 && largeVersion != currentLargeVersion  && smallVersion != currentSmallVersion ){
	    					Exception e = new Exception("Database versions disagree");
	    					throw e;
	    				}
	    			}
	    		}
	    		catch(Exception e) {
	    			Toast.makeText(getApplicationContext(), "Database creation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
	    		}
    		}
    	}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
				int newVersion) {
			//TODO: upgrade the database
			
    	}
    }	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        

        try{
            Log.i(TAG,"Shop2DropActivity created.");

            s2dItemOpenHelper openHelper = new Shop2DropActivity.s2dItemOpenHelper(getApplicationContext(),"s2dDatabase",1);
	        openHelper.onCreate(getDatabase());


	        mFragmentManager = getSupportFragmentManager();
//	        mButtonFragment = (S2DButtonFragment)mFragmentManager.findFragmentById(R.id.buttonFragmentLayout);
	        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	        mButtonFragment = (S2DButtonFragment)S2DButtonFragment.instantiate(getApplicationContext(), S2DButtonFragment.class.getName(), savedInstanceState);
        	transaction.add(R.id.buttonFragmentLayout, mButtonFragment);

        	Log.i(TAG,"S2DButtonFragment added: " + String.valueOf(mButtonFragment));
        	//S2DListFragment s2dListFragment = new S2DListFragment();
//	        mListFragment = (S2DListFragment)mFragmentManager.findFragmentById(R.id.listFragment);
	        mListFragment  = (S2DListFragment)S2DListFragment.instantiate(getApplicationContext(), S2DListFragment.class.getName(), savedInstanceState);
        	mListFragment.setListAdapter(getItemAdapter());
        	transaction.add(R.id.listFragment, mListFragment);
        	Log.i(TAG,"S2DListFragment added: " + String.valueOf(mListFragment));
////        	S2DButtonFragment s2dButtonFragment = new S2DButtonFragment();
        	
        	transaction.commit();
        	Log.i(TAG,"transaction(s) committed");

        	Log.i(TAG,"Calling setContentView with " + String.valueOf(R.layout.activity_shop2_drop));
            setContentView(R.layout.activity_shop2_drop);
            Log.i(TAG, "setContentView complete!");
            
        }
        catch(Exception e){
        	Log.e(TAG, "Exception cause: " + String.valueOf(e.getCause()) + ", "  + e.getMessage());
        	Log.i(TAG,"Fragments: " + String.valueOf(mButtonFragment) + ", " + String.valueOf(mListFragment));
        	Log.i(TAG,"Just taking up space");
        }
    }
    @Override 
    public View onCreateView(String name, Context context, AttributeSet attrSet) {
    	Log.i(TAG, "onCreateView, name = " + name + " context " + context + " AttributeSet = " + attrSet);
        if (!"fragment".equals(name)) {
            return super.onCreateView(name, context, attrSet);
        }
        
    	View view = null;
    	try {
    	    Log.i(TAG,"passed to LayoutInflater: " + R.layout.activity_shop2_drop);
    	}
    	catch(Exception e) {
    		Log.e(TAG, "onCreateView failed: " + e.getMessage());
    	}
    	return view; //This should be null for the default behavior
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
    	mSavedInstanceState = savedInstanceState;
    }
    @Override
    public void onAttachFragment(Fragment fragment) {
    	super.onAttachFragment(fragment);
    	Log.i(TAG,"onAttachFragment = " + String.valueOf(fragment));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	menu.add("First Option");
    	menu.add("Second Option");
        return true;
    }
    
    @Override 
    public boolean onOptionsItemSelected(MenuItem item){ 
    	if(!super.onOptionsItemSelected(item)){
    		switch (item.getItemId()) { 
        	case Shop2DropActivity.MENU_ID1:
        		Toast.makeText(getApplicationContext(), "onOptionsSelect,  menu Id = " + Shop2DropActivity.MENU_ID1,Toast.LENGTH_LONG).show();
        		break;
        	case Shop2DropActivity.MENU_ID2:
        		Toast.makeText(getApplicationContext(), "onOptionsSelect,  menu Id = " + Shop2DropActivity.MENU_ID2,Toast.LENGTH_LONG).show();
        		break;
        	default:
        		Toast.makeText(getApplicationContext(),  "Wrong button", Toast.LENGTH_SHORT).show();
    		}
    	}
        return true;
    }
    @Override
    public void onResume() {
    	super.onResume();
    	Log.i(TAG, "onResume");
/*        try{
	        s2dItemOpenHelper openHelper = new Shop2DropActivity.s2dItemOpenHelper(getApplicationContext(),"s2dDatabase",1);
	        openHelper.onCreate(getDatabase());

	        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); // Don't do this -- appears to put things on the BackStack, and they get Created again!
        	S2DButtonFragment s2dButtonFragment = new S2DButtonFragment();
        	transaction.add(R.id.ButtonFragment, s2dButtonFragment);
        	Log.i(TAG,"s2dButtonFragment added: " + String.valueOf(s2dButtonFragment));
        	
        	S2DListFragment s2dListFragment = new S2DListFragment();
        	transaction.add(R.id.listFragment, s2dListFragment);
        	Log.i(TAG,"s2dListFragment added: " + String.valueOf(s2dListFragment));
        	
        	transaction.commit();
            
        }
        catch(Exception e){
        	Log.i(TAG, e.getMessage());
        }
 */   }
}
        