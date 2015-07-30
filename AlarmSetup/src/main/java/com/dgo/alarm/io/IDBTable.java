package com.dgo.alarm.io;

import android.database.sqlite.SQLiteDatabase;

public interface IDBTable {
	public static final long INVALID_ID = -1;

	public boolean createTable(SQLiteDatabase db);
	public String getName();
	public long insert(SQLiteDatabase db,  IDBElement obj);
}
