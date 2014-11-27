package com.pinthecloud.item.databases;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.pinthecloud.item.model.AbstractItemModel;

public class AimDBHelper {
	private static final String DATABASE_NAME = "aim_db_helper.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "aims";

	// Messages Table Columns names
	private final String ID = "id";
	private final String JSON = "json";

	private ItOpenHelper openHelper;
	//	SQLiteDatabase db;

	public AimDBHelper(Context context) {
		openHelper = new ItOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	private <E extends AbstractItemModel<?>> ContentValues getContentValues(E obj) {
		ContentValues values = new ContentValues();
		values.put(ID, obj.getId());
		values.put(JSON, obj.toString());
		return values;
	}

	public <E extends AbstractItemModel<?>> int add(E obj) {
		SQLiteDatabase db = openHelper.getWritableDatabase();

		ContentValues values = getContentValues(obj);
		long result = db.insert(TABLE_NAME, null, values);
		openHelper.close();
		return (int)result;
	}

	public <E extends AbstractItemModel<?>> int update(E obj) {
		SQLiteDatabase db = openHelper.getWritableDatabase();

		String id = obj.getId();
		ContentValues values = getContentValues(obj);
		int result = db.update(TABLE_NAME, values, ID + " = ?", new String[]{ id });
		openHelper.close();
		return result;
	}

	public int delete(String id) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int result = db.delete(TABLE_NAME, ID + " = ?",
				new String[] { id });
		openHelper.close();
		return result;
	}

	public <E extends AbstractItemModel<?>> E get(E obj) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		E retVal = null;
		String selectQuery = "SELECT * FROM " + TABLE_NAME +
				" WHERE " + ID + " = ?";
		Cursor cursor = db.rawQuery(selectQuery, new String[]{ obj.getId() });
		retVal = getObjectFromCursor(cursor, obj.getClass());
		openHelper.close();
		return retVal;
	}

	@SuppressWarnings("unchecked")
	private <E extends AbstractItemModel<?>> E getObjectFromCursor(Cursor cursor, Class<?> clazz) {
		if (cursor != null && cursor.moveToFirst()){
			String jsonStr = cursor.getString(1);
			return (E)new Gson().fromJson(jsonStr, clazz);
		}
		return null;
	}

	public <E extends AbstractItemModel<?>> List<E> getList(E obj) {
		List<E> list = Lists.newArrayList();
		return null;
	}

	private class ItOpenHelper extends SQLiteOpenHelper {
		private SQLiteDatabase mDb;
		private AtomicInteger mCount = new AtomicInteger();

		public ItOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + 
					"("
					+ ID + " TEXT PRIMARY KEY," 
					+ JSON + " TEXT"
					+")";
			db.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}

		@Override
		public synchronized SQLiteDatabase getWritableDatabase() {
			if (mCount.incrementAndGet() == 1) {
				mDb = super.getWritableDatabase();
			}
			return mDb;
		}

		@Override
		public synchronized void close() {
			if (mCount.decrementAndGet() == 0) {
				mDb.close();
			}
		}
	}
}
