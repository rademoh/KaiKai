package com.ng.techhouse.tinggqr.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ng.techhouse.tinggqr.model.AirtimeBeneficiaryPojo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBhelper {

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "tinggpay.db";
	private static final int DATABASE_VERSION = 4;

	private final Context mCtx;


	private class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}




		public void onCreate(SQLiteDatabase db) {

			db.execSQL("Create table if not exists bankcode(_id integer primary key autoincrement,code Text(30),name Text(30))");
			db.execSQL("Create table if not exists airtimebeneficiary(_id integer primary key autoincrement,name Text(30),phoneno Text(30))");
			executeSQLScript(db, "bankcodes.sql");
			Log.d("bank table","create");

		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.execSQL("DROP TABLE IF EXISTS bankcode");
			db.execSQL("DROP TABLE IF EXISTS airtimebeneficiary");

			onCreate(db);
		}
	}

	public void Reset() {
		mDbHelper.onUpgrade(this.mDb, 1, 1);
	}

	public DBhelper(Context ctx) {
		mCtx = ctx;
		mDbHelper = new DatabaseHelper(mCtx);

	}

	public SQLiteDatabase open() throws SQLException {
		mDb = mDbHelper.getWritableDatabase();
		mDbHelper.onCreate(mDb);
		return mDb;
	}

	public void close() {
		mDbHelper.close();
	}

	private void executeSQLScript(SQLiteDatabase database, String dbname) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[4096];
		// ByteBuffer buf[] = new ByteBuffer [4096];
		int len;
		AssetManager assetManager = mCtx.getAssets();
		InputStream inputStream = null;
		try{
			inputStream = assetManager.open(dbname);
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();

			String[] createScript = outputStream.toString().split(";");


			for (int i = 0; i < createScript.length; i++) {
				String sqlStatement = createScript[i].trim();
				// TODO You may want to parse out comments here
				if (sqlStatement.length() > 0) {

					System.out.println("No of Script read " + sqlStatement );
					database.execSQL(sqlStatement + ";");
				}
			}
		} catch (IOException e){
			// TODO Handle Script Failed to Load
		} catch (SQLiteException e) {
			// TODO Handle Script Failed to Execute
		}
	}

	public String getBankCode(String bankname){
		SQLiteDatabase database = mDbHelper.getWritableDatabase();
		Cursor cursor=database.query("bankcode", null, " code=?", new String[]{bankname}, null, null, null);
		if(cursor.getCount()<1) // Bank Not Found
		{
			cursor.close();
			return "NOT EXIST";
		}
		cursor.moveToFirst();
		String bankCode= cursor.getString(cursor.getColumnIndex("name"));
		cursor.close();
		return bankCode;
	}

	public List<String> getAllBanks(){
		List<String> bankList = new ArrayList<String>();

		String selectQuery = "SELECT * FROM bankcode" ;

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				bankList.add(cursor.getString(1));
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();

		return bankList;
	}
	public void saveBeneficiary(HashMap<String, String> queryValues) {
		SQLiteDatabase database = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", queryValues.get("name"));
		values.put("phoneno", queryValues.get("phoneno"));
		database.insert("airtimebeneficiary", null, values);
		database.close();
	}

	public void delete(String name){

		SQLiteDatabase database = mDbHelper.getWritableDatabase();
		database.delete("airtimebeneficiary", "name" + " = ?", new String[] { name });
		database.close();
	}


	public List<AirtimeBeneficiaryPojo> getBeneficiaryList(){
		List<AirtimeBeneficiaryPojo> list = new ArrayList<AirtimeBeneficiaryPojo>();
		String selectQuery = "SELECT  * FROM airtimebeneficiary";
		SQLiteDatabase database = mDbHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				list.add(new AirtimeBeneficiaryPojo(cursor.getString(1),cursor.getString(2)));

			} while (cursor.moveToNext());
		}
		cursor.close();
		database.close();
		return list;
	}



}
