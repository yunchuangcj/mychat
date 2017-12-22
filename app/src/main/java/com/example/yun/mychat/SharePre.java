package com.example.yun.mychat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * ��ѡ��Ĳ�����
 * 
 * @author Administrator
 * 
 */
public class SharePre {
	//
	private String FILE_NAME = "";
	private SharedPreferences sp = null;
	private static SharePre temPre;

	private SharePre(Context context) {
		FILE_NAME = context.getApplicationContext().getPackageName()
				+ "_sharepre";
		sp = context.getSharedPreferences(FILE_NAME, 0);
	}

	public static SharePre getInstance(Context context) {
		if (temPre == null) {
			temPre = new SharePre(context);
		}
		return temPre;
	}

	/**
	 * get string value
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public String getString(String key, String defValue) {
		return sp.getString(key, defValue);
	}

	/**
	 * set string value
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public void setString(String key, String defValue) {
		Editor editor = sp.edit();
		editor.putString(key, defValue);
		editor.commit();
	}

	/**
	 * get boolean value
	 * 
	 * @param key
	 * @param defvalue
	 * @return
	 */
	public boolean getBoolean(String key, boolean defvalue) {
		return sp.getBoolean(key + "", defvalue);
	}

	/**
	 * set boolean value
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public void setBoolean(String key, boolean defValue) {
		Editor editor = sp.edit();
		editor.putBoolean(key, defValue);
		editor.commit();
	}

	/**
	 * set int value
	 * 
	 * @param key
	 * @param defValue
	 */
	public void setInt(String key, int defValue) {
		Editor editor = sp.edit();
		editor.putInt(key, defValue);
		editor.commit();
	}

	/**
	 * get int value
	 * 
	 * @param keyId
	 * @return
	 */
	public int getInt(String key, int defValue) {
		return sp.getInt(key, defValue);
	}
}
