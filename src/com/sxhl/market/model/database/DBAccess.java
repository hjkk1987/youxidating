package com.sxhl.market.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sxhl.market.utils.DebugTool;

/**
 * @ClassName: DBAccess.java
 * @Description: 数据库操作
 * @author 吴绍东
 * @date 2012-12-12 下午12:48:19
 */
public final class DBAccess {
	private DBHelper helper = null;

	private static DBAccess m_dbAccessInstance = null;

	private DBAccess(Context context) {

		if (context == null) {
			DebugTool.error("DB:Context is null, can't Open db.", null);
			return;
		}

		helper = new DBHelper(context);
	}

	/** 创建数据库访问实例 **/
	synchronized public static DBAccess getInstance(Context context) {
		if (m_dbAccessInstance == null) {
			DebugTool.info("DB:Create new NewsSQLAccess");
			m_dbAccessInstance = new DBAccess(context);
		}
		return m_dbAccessInstance;
	}

	/**
	 * 执行RawQuery查询操作
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */
	public Cursor execRawQuery(String sql, String[] selectionArgs) {

		SQLiteDatabase mDataBase = helper.getWritableDatabase();
		Cursor cursor = mDataBase.rawQuery(sql, selectionArgs);
		DebugTool.info("DB:sql:" + sql);
		return cursor;
	}

	/**
	 * 
	 * @param sql
	 */
	public void execSQL(String sql) {

		SQLiteDatabase mDataBase = helper.getWritableDatabase();
		mDataBase.execSQL(sql);
		DebugTool.info("DB:sql:" + sql);
		closeDataBase(mDataBase);
	}

	/**
	 * 插入数据
	 * 
	 * @param tableName
	 * @param values
	 */
	public long insert(String tableName, ContentValues values) {

		SQLiteDatabase db = helper.getWritableDatabase();
		long id = db.insert(tableName, null, values);
		closeDataBase(db);
		return id;
	}

	/**
	 * 按系统的ID删除记录
	 * 
	 * @param tableName
	 *            表名称
	 * @param _id
	 *            系统自带的ID
	 */
	public void delete(String tableName, String _id) {

		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(tableName, "id=?", new String[] { _id });
		closeDataBase(db);
	}

	/**
	 * 按条件查询数据
	 * 
	 * @param tableName
	 * @param columns
	 * @param selection
	 * @return
	 */
	public Cursor query(String tableName, String[] columns, String selection) {

		SQLiteDatabase db = helper.getReadableDatabase();
		return db.query(tableName, columns, selection, null, null, null, null);
	}

	/**
	 * 更新记录信息
	 * 
	 * @param tableName
	 *            表名称
	 * @param contentValues
	 *            更新的值
	 * @param id
	 *            要更新的记录ID
	 * @return 影响的记录条数
	 */
	public int update(String tableName, ContentValues contentValues,
			String whereClause, String[] whereArgs) {
		int value = -1;

		SQLiteDatabase db = helper.getReadableDatabase();
		value = db.update(tableName, contentValues, whereClause, whereArgs);

		closeDataBase(db);
		return value;
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @param database
	 */
	private void closeDataBase(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		if (database != null && database.isOpen()) {
			database.close();
			database = null;
		}
	}

	/**
	 * 关闭整个数据库
	 */
	public void close() {

		if (helper != null) {
			helper.close();
			helper = null;
		}
		m_dbAccessInstance = null;
	}

}
