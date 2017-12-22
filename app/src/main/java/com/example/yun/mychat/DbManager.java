package com.example.yun.mychat;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * 操作数据库
 * 
 * @author Administrator
 * 
 */
public class DbManager {

	// 全局
	private SQLiteDb db;// 数据库
	private final String DB_NAME = "com.mjiaowu.temp_datas.db";// 数据库名称

	// 考试模块
	public static class exam {
		public final static String TABLE_NAME = "ahut_exam";// 表名
		// 列名
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_TITLE1 = "title_1";
		public static final String COLUMN_TITLE2 = "title_2";
		public static final String COLUMN_TITLE3 = "title_3";
		public static final String COLUMN_TITLE4 = "title_4";
		public static final String[] columns = { COLUMN_ID, COLUMN_TITLE1,
				COLUMN_TITLE2, COLUMN_TITLE3, COLUMN_TITLE4 };

	}

	// 文章模块
	public static class article {
		public final static String TABLE_NAME = "ahut_article";
		public static final String COLUMN_ID = "_id";
		public static final String COLUMNID = "Id";
		public static final String COLUMN_TITLE = "Title";
		public static final String COLUMN_ABSTRACT = "Abstract";
		public static final String COLUMN_CONTENT = "Content";
		public static final String COLUMN_IMAGEURL = "imageUrl";
		public static final String COLUMN_COUNT = "Count";
		public static final String COLUMN_DATE = "Date";
		public static final String COLUMN_OTHER = "Other";
		public static final String[] columns = { COLUMN_ID, COLUMNID,
				COLUMN_TITLE, COLUMN_ABSTRACT, COLUMN_CONTENT, COLUMN_IMAGEURL,
				COLUMN_COUNT, COLUMN_DATE, COLUMN_OTHER };
	}

	// 通知公告模块
	public static class notice {
		public final static String TABLE_NAME = "ahut_notice";// 表名
		// 列名
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_TITLE = "title";
		public static final String COLUMN_TIME = "time";
		public static final String COLUMN_URL = "url";
		public static final String[] columns = { COLUMN_ID, COLUMN_TITLE,
				COLUMN_TIME, COLUMN_URL };

	}

	// 教学评价模块
	public static class remark {
		public final static String TABLE_NAME = "ahut_remark";// 表名
		// 列名
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_TITLE = "title";
		public static final String COLUMN_STATE = "state";
		public static final String COLUMN_URL = "url";
		public static final String[] columns = { COLUMN_ID, COLUMN_TITLE,
				COLUMN_STATE, COLUMN_URL };
	}

	// 课表模块
	public static class course {
		public final static String TABLE_NAME = "ahut_course";// 表名
		// 列名
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_XN = "xn";
		public static final String COLUMN_XQ = "xq";
		public static final String COLUMN_LESSONID = "lessonId";
		public static final String COLUMN_LESSONNAME = "lessonName";
		public static final String COLUMN_LESSONPROPERTY = "lessonProperty";
		public static final String COLUMN_TEACHERNAME = "teacherName";
		public static final String COLUMN_LESSONSCORE = "lessonScore";
		public static final String COLUMN_LESSONTIME = "lessonTime";
		public static final String COLUMN_LESSONPLACE = "lessonPlace";
		public static final String[] columns = { COLUMN_ID, COLUMN_XN,
				COLUMN_XQ, COLUMN_LESSONID, COLUMN_LESSONNAME,
				COLUMN_LESSONPROPERTY, COLUMN_TEACHERNAME, COLUMN_LESSONSCORE,
				COLUMN_LESSONTIME, COLUMN_LESSONPLACE };
	}

	public static class tcourse {
		public final static String TABLE_NAME = "ahut_tcourse";

		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_WEEKDAY = "weekDay";
		public static final String COLUMN_TIME = "time";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_TIMEPERWEEK = "timePerweek";
		public static final String COLUMN_PLACE = "place";
		public static final String COLUMN_STUDENTMAJOR = "studentMajor";

		public static final String[] columns = { COLUMN_ID, COLUMN_WEEKDAY,
				COLUMN_TIME, COLUMN_NAME, COLUMN_TIMEPERWEEK, COLUMN_PLACE,
				COLUMN_STUDENTMAJOR };

	}

	public DbManager(Context context) {
		db = new SQLiteDb(context, DB_NAME);
	}

	// 查询所有记录
	public ArrayList<HashMap<String, String>> query(String tableName,
			String[] columns) {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		// 查询数据
		Cursor cursor = db.query(tableName, columns, null, null, null, null,
				null, null);
		// 遍历数据
		while (cursor.moveToNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < columns.length; i++) {
				String string = cursor.getString(cursor
						.getColumnIndex(columns[i]));
				map.put(columns[i], string);
			}
			arrayList.add(map);
		}
		cursor.close();
		return arrayList;
	}

	// 插入记录
	public void insert(String tableName, String[] columns,
			HashMap<String, String> datas) {
		ContentValues cv = new ContentValues();
		for (int i = 0; i < columns.length; i++) {
			cv.put(columns[i], datas.get(columns[i]));
		}
		db.insert(tableName, cv);
	}

	// 删除记录
	public void delete(String tableName, String id) {
		db.delete(tableName, "_id=?", new String[] { id });
	}

	// 删除记录-所有
	public void deleteAll(String tableName) {
		try {
			db.delete(tableName, null, null);
		} catch (Exception e) {
			System.out.println("There is no such table.");
		}
	}

	// 删除记录-指定条件
	public void deleteAll(String tableName, String where, String[] values) {
		db.delete(tableName, where, values);
	}

	// 关闭
	public void close() {
		db.close();
	}
}
