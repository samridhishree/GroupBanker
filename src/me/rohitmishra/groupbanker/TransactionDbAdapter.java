package me.rohitmishra.groupbanker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// This class is for handling the database operations to store and fetch details for the transactions

public class TransactionDbAdapter {

private static final String TAG = "TransactionDbAdapter" ;
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_AMOUNT = "amount";
	public static final String KEY_DESC = "description";
	public static final String KEY_TIME = "time";
	
	private DatabaseHelper mDbHelper ;
	private SQLiteDatabase mDb ;
	
	// Database creation SQL statement 
	//no separate storage class in sqlite to store date and time. Will use the built-in functions to store it as text
	
		private static final String DATABASE_CREATE = 
			"create table transaction (_id integer primary key autoincrement, " + 
			"amount real not null, description text not null, time text not null);" ;
		
		private static final String DATABASE_NAME = "groupbanker" ;
		private static final String TABLE_NAME = "transaction" ;
		private static final int DATABASE_VERSION = 1 ;
		
		private final Context mCtx ;
		
		private static class DatabaseHelper extends SQLiteOpenHelper {
			
			DatabaseHelper(Context context)	{
				super(context, DATABASE_NAME, null, DATABASE_VERSION) ;
			}
			
			@Override
			public void onCreate(SQLiteDatabase db)	{
				db.execSQL(DATABASE_CREATE);
			}
			
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)	{
				Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
	                    + newVersion + ", which will destroy all old data");
	            db.execSQL("DROP TABLE IF EXISTS transaction");
	            onCreate(db);
			}
		}
		
		/**
	     * Constructor - takes the context to allow the database to be
	     * opened/created
	     * 
	     * @param ctx the Context within which to work
	     */
	    public TransactionDbAdapter(Context ctx) {
	        this.mCtx = ctx;
	    }
	    
	    /**
	     * Open the groupbanker database. If it cannot be opened, try to create a new
	     * instance of the database. If it cannot be created, throw an exception to
	     * signal the failure
	     * 
	     * @return this (self reference, allowing this to be chained in an
	     *         initialization call)
	     * @throws SQLException if the database could be neither opened or created
	     */
	    public TransactionDbAdapter open() throws SQLException {
	        mDbHelper = new DatabaseHelper(mCtx);
	        mDb = mDbHelper.getWritableDatabase();
	        return this;
	    }

	    public void close() {
	        mDbHelper.close();
	    }
	    
	    
	    /**
	     * Create a new transaction using the amount, description and time. If the transaction is successfully 
	     * created, return the new rowId for the transaction, otherwise return -1 to indicate
	     * failure. 
	     * 
	     *  @param amount the total amount involved in a transaction
	     *  @param desc the decription of the transaction
	     *  @param time the date and time of the transaction
	     *  @return rowId or -1 if failed
	     */
	    
	    public long createTransaction(float amount, String desc, String time) throws SQLException	{
	    	ContentValues initialValues = new ContentValues();
	    	initialValues.put(KEY_AMOUNT, amount);
	    	initialValues.put(KEY_DESC, desc);
	    	initialValues.put(KEY_TIME, time);
	    	
	    	Log.v(TAG,"amount:" + amount + "description:" + desc + "time:" + time); 
	    	
	    	return mDb.insertOrThrow(TABLE_NAME, null, initialValues) ;
	    	
	    }
	    
	    /**
	     * Implementing fetchAllTransactions()
	     * Return a Cursor over the list of all transactions in the database
	     * 
	     * @return Cursor over all transactions
	     */
	    
	    public Cursor fetchAllTransactions() {
	    	
	    	return mDb.query(TABLE_NAME,new String[] {KEY_AMOUNT, KEY_DESC,KEY_TIME}, null, null, null, null, null);
	    }
	    
  //TODO implement fetchTransaction (specific), delete and update transaction is needed!!	    
}
