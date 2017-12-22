package com.example.yun.mychat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库的操作类--可以通用--基类
 * 
 * @author Administrator
 * 
 */
public class SQLiteDb {

	private SQLiteDatabase sqDatabase = null;
	private SQLiteHelper sqHelper = null;

	public SQLiteDb(Context context, String dbName) {
		sqHelper = new SQLiteHelper(context, dbName, null, 1);
		sqDatabase = sqHelper.getWritableDatabase();
	}

	// 关闭数据库
	public void close() {
		sqHelper.close();
		sqDatabase.close();
	}

	// 执行sql语句
	public void execSql(String sql) {
		sqDatabase.execSQL(sql);
	}

	// 插入
	public long insert(String tableName, ContentValues values) {
		Long l = sqDatabase.insert(tableName, null, values);
		return l;
	}

	// 更新
	public int update(String tableName, ContentValues values,
			String whereClause, String[] whereArgs) {
		int i = sqDatabase.update(tableName, values, whereClause, whereArgs);
		return i;
	}

	// 删除
	public int delete(String tableName, String whereClause, String[] whereArgs) {
		int i = sqDatabase.delete(tableName, whereClause, whereArgs);
		return i;
	}

	// 查询
	public Cursor query(String tableName, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {

		Cursor c = sqDatabase.query(tableName, columns, selection,
				selectionArgs, groupBy, having, orderBy, limit);
		return c;
	}

	// 内部类，sqlLite的helper
	private static class SQLiteHelper extends SQLiteOpenHelper {

		public SQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			System.out.println("数据库创建。。。。。。。。。。。");
			String table = "";
			// 创建表--考试信息表
			table = "CREATE TABLE ahut_exam (_id text primary key , "
					+ "title_1 text, title_2 text, title_3 text, title_4 text)";
			db.execSQL(table);
			// 创建表--通知公告表
			table = "CREATE TABLE ahut_notice(_id text primary key , "
					+ "title text, time text, url text)";
			db.execSQL(table);
			// 创建表--教学评价表
			table = "CREATE TABLE ahut_remark(_id text primary key , "
					+ "title text, state text, url text)";
			db.execSQL(table);
			// 创建表--课程表
			table = "CREATE TABLE ahut_course(_id text  , "
					+ "xn text, xq text, lessonId text, lessonName text, lessonProperty text"
					+ ", teacherName text, lessonScore text, lessonTime text, lessonPlace text)";
			db.execSQL(table);
			//
			table = "CREATE TABLE ahut_tcourse(_id text, "
					+ "weekDay text, time text, name text, timePerweek text, place text"
					+ ", studentMajor text)";
			db.execSQL(table);
			//
			table = "CREATE TABLE ahut_article(_id text primary key , "
					+ "Id text, Title text, Abstract text, Content text, imageUrl text"
					+ ", Count text, Date text, Other text)";
			db.execSQL(table);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			System.out.println("数据库更新。。。。。。。。。。。");
		}

	}
}
