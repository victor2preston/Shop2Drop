package com.victorpreston.shop2drop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.database.sqlite.*;

public class S2DItemAdapter implements ListAdapter {
	
	public static final String TAG = "S2DItemAdapter";
	Context mContext;
	SQLiteDatabase mDb;
	public static final String S2DDATABASE_ITEM_TABLE = "ItemTable";
	public static final String S2DDATABASE_ITEM_COLUMNS_TABLE = "ItemColumnTable";
	public static final String S2DDATABASE_FIRST_VERSION_COLUMN = "largeVersion";
	public static final String S2DDATABASE_SECOND_VERSION_COLUMN= "smallVersion";
	public static final String S2DDATABASE_NAME_COLUMN = "name";
	public static final String S2DDATABASE_TYPE_COLUMN = "type";
	public static enum columnType {
		text,
		integer,
		real
	}
	public int mNumColumns = 3;
	S2DItemAdapter(SQLiteDatabase db, Context context){  // Always try to use getApplicationContext() for this, to avoid re-storing when phone position is changed
		mContext = context;
		mDb = db;
		if(mDb.isOpen()){
//			SQLiteCursor c;
    		String item_column_table_create = "CREATE TABLE IF NOT EXISTS " + S2DDATABASE_ITEM_COLUMNS_TABLE /*+ " " + S2DDATABASE_NAME*/ + " (" ;
    		String[] fieldNames = { "name","type"};
    		String[] fieldTypes = { "text", "text"};

    		for( int index = 0; index < fieldNames.length; index++) {
    			String fieldName = fieldNames[index];
    			String fieldType = fieldTypes[index];
    			item_column_table_create += fieldName + " " + fieldType + ",";
    			}
    		item_column_table_create = item_column_table_create.substring(0,item_column_table_create.length() - 1); // remove last character (the extra ",")
    		item_column_table_create += ");";
			
			mDb.beginTransaction();
			try {
				String nullColumnHack = "DontDoThis";
				mDb.execSQL(item_column_table_create);
				// Insert the bare minimum of column types
				ContentValues newItemTable = new ContentValues();
				newItemTable.put(S2DDATABASE_NAME_COLUMN, "name");
				newItemTable.put(S2DDATABASE_TYPE_COLUMN, "type");
				mDb.insert(S2DDATABASE_ITEM_COLUMNS_TABLE, nullColumnHack, newItemTable);
	    		String item_table_create = "CREATE TABLE IF NOT EXISTS " + S2DDATABASE_ITEM_TABLE + " AS SELECT ALL * FROM " + S2DDATABASE_ITEM_COLUMNS_TABLE + ";";
	    		mDb.execSQL(item_table_create);
				Log.i(TAG,"New database and columnTable created, default columns 'name','type'");
	    		String stringArray[] = {"*"};
	    		Cursor showMe = mDb.query(S2DDATABASE_ITEM_COLUMNS_TABLE, stringArray, null, null, null, null, null);
	    		int numColumns = showMe.getColumnCount();
	    		int n = showMe.getCount();
	    		if(n > 1) {
	    			Exception e = new SQLiteMisuseException("columnName table must have only one (1) row!");
	    			throw e;
	    		}
	    			
	    		String result = ""; 
	    		if(showMe.moveToFirst()) {
		    		for(int i = 0; i < numColumns; i++){
		    			result += showMe.getString(i);
		    			result += ",";
		    		}
	    		}
	    		result += " numColumns = " + numColumns;
	    		Log.i(TAG,result);
	    		
	    		// Create Default Columns:
	    		String[] defaultColumns = {"name","quantity","price","category"};
	    		String[] defaultTypes = {"text","real","integer","text"};
	    		
	    		String makeDefaultTable = "CREATE TABLE IF NOT EXISTS " + S2DDATABASE_ITEM_TABLE + " (";
	    		
	    		for(int i = 0; i < defaultColumns.length; i++){
		    		makeDefaultTable += defaultColumns[i] + " " + defaultTypes[i] + ",";
	    		}
	    		makeDefaultTable = makeDefaultTable.substring(0, makeDefaultTable.length() - 1); // remove that last comma!
	    		makeDefaultTable += ");";
	    		
	    		mDb.execSQL(makeDefaultTable);
	    		Log.i(TAG,makeDefaultTable);
	    		mDb.setTransactionSuccessful(); 
			}
			catch(Exception e) {
				Toast toast = Toast.makeText(mContext, "Could not create item table: " + e.getMessage(), Toast.LENGTH_LONG);
				toast.show();
			}
			finally {
				mDb.endTransaction();
			}

		}
	}

	public boolean addColumn(String newColumnName,columnType type) {
		
		if(mDb == null)
			return false;
		mDb.beginTransaction();
		try {
			String typeStr = null;
			switch(type) {
				case text:
					typeStr = "text";
					break;
				case integer:
					typeStr = "integer";
					break;
				case real:
					typeStr = "real";
					break;
				}
			
			ContentValues values = new ContentValues();
			values.put(S2DDATABASE_NAME_COLUMN, newColumnName);
			values.put(S2DDATABASE_TYPE_COLUMN, typeStr);
			mDb.insert(S2DDATABASE_ITEM_COLUMNS_TABLE, null, values);
			Cursor columnCursor = mDb.query(S2DDATABASE_ITEM_COLUMNS_TABLE, null, null, null, null, null, null);
	
			String alterTableString = "ALTER TABLE " + S2DDATABASE_ITEM_TABLE + " Add COLUMN " + newColumnName + " " + typeStr + ";";
			Log.i(TAG,alterTableString);
			mDb.beginTransaction();
			mDb.execSQL(alterTableString);
			
/*			//ContentValues addColumnValues = new ContentValues();
			// Get ALL the columns:
			//ArrayList<String> columns; 
			String columns = null;
			Cursor cursor = mDb.query(S2DDATABASE_ITEM_COLUMNS_TABLE, null, null, null, null, null, null);
			for(int i = 0; i < columnCursor.getCount(); i++) {
				//String columnKey = Integer.toString(i);
				//addColumnValues.put(columnKey,cursor.getString(i));
				columns += cursor.getString(i) + " ";
			}
			//String columnKey = Integer.toString(columnCursor.getCount());
			//addColumnValues.put(columnKey,newColumnName);
			columns += newColumnName;

			// First, copy all of the current table to a temporary table
			String oldItemDatabaseStr = "CREATE TEMP " + S2DDATABASE_ITEM_TABLE + " " + S2DDATABASE_ITEM_TABLE + "_TEMP AS SELECT ALL FROM " + S2DDATABASE_ITEM_TABLE + ";"	;
			mDb.execSQL(oldItemDatabaseStr);
			
			String removeOldTable = "DROP TABLE " + S2DDATABASE_ITEM_TABLE + ";";
			mDb.execSQL(removeOldTable);
			
			String newItemDatabaseStr = "CREATE TABLE " + S2DDATABASE_ITEM_TABLE + columns + " AS SELECT ALL FROM " + S2DDATABASE_ITEM_TABLE + "_TEMP;"; 
			mDb.execSQL(newItemDatabaseStr);
			
			String removeTemptable = "DROP TABLE " + S2DDATABASE_ITEM_TABLE + "_TEMP;";
			mDb.execSQL(removeTemptable);
*/			
			//sqlString = "Update " + S2DDATABASE_ITEM_TABLE 
			//mDb.execSQL(sqlString, bindArgs)(S2DDATABASE_ITEM_TABLE, null, values);

//			int numColumns = columnCursor.getCount();
			String allColumns = null;
			for( String column : columnCursor.getColumnNames()) {
				allColumns += column;
				allColumns += ",";
			}
			Log.i(TAG, "Successfully created new column: " + newColumnName + allColumns);
			mDb.setTransactionSuccessful();
		}
		catch(Exception e) {
			Toast.makeText(mContext, "Could not access database: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		finally {
			mDb.endTransaction();
		}
		return true;
	}
	public boolean addItem(String itemName){
		if(mDb == null)
			return false;
		mDb.beginTransaction();
		try {
			Log.i(TAG,"addItem " + itemName);
			ContentValues values = new ContentValues();
			values.put(S2DDATABASE_NAME_COLUMN, itemName);
			values.put(S2DDATABASE_TYPE_COLUMN, "text");
/*			// Get ALL the columns:
			String[] columns; 
			mDb.query(S2DDATABASE_ITEM_COLUMNS_TABLE, columns, "*", null, null, null, S2DDATABASE_ITEM_COLUMNS_TABLE);
			for(int i = 0; i < columns.length(); i++) {
				String columnKey = Integer.toString(i);
				values.put(columnKey,columns[i]);
			}
			newColumnNum = columns.length() + 1;
			String newColumnKey = Integer.toString(newColumnNum);
			values.put(newColumnKey,itemName);
*/				
			mDb.insert(S2DDATABASE_ITEM_TABLE, null, values);
			mDb.setTransactionSuccessful();
			
		}
		catch (Exception e) {
			Toast.makeText(mContext, "Cannot make new item: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		finally {
			mDb.endTransaction();
		}
		return true;
	}
	

	public int getCount() {
		if(!mDb.isOpen())
			return 0;
		else{
			String sql = "SELECT COUNT(*) FROM " + S2DDATABASE_ITEM_TABLE;
			SQLiteStatement stmt = mDb.compileStatement(sql);
			long count = stmt.simpleQueryForLong();
			return (int)count;
		}
	}

	public Object getItem(int position) {
		if(!mDb.isOpen())
			return null;
		try {
			String Sql = "id = " + position;
			
			String[] Columns = {"*"};
			String orderBy = null;
			
			Cursor c = mDb.query(S2DDATABASE_ITEM_TABLE,Columns,Sql,null,null,null,orderBy,"1");
			String result = ""; 
			for(int i = 0; i < c.getColumnCount(); i++){
				result += c.getString(i);
			}
			return result;
		}
		catch(Exception e){
			Toast.makeText(mContext,"execSQL failed" + e.toString(),Toast.LENGTH_LONG).show();
			return null;
		}
	}

	public long getItemId(int position) {

		// TODO Auto-generated method stub
		return (long)position;
	}

	public int getItemViewType(int position) {

		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		// TODO Auto-generated method stub
		return null;
	}

	public int getViewTypeCount() {

		return 1;
	}

	public boolean hasStableIds() {

		// TODO Auto-generated method stub
		return true;
	}

	public boolean isEmpty() {

		// TODO Auto-generated method stub
		return false;
	}

	public void registerDataSetObserver(DataSetObserver observer) {

		// TODO Auto-generated method stub

	}

	public void unregisterDataSetObserver(DataSetObserver observer) {

		// TODO Auto-generated method stub

	}

	public boolean areAllItemsEnabled() {

		// TODO Auto-generated method stub
		return true;
	}

	public boolean isEnabled(int position) {

		// TODO Auto-generated method stub
		return true;
	}

}
