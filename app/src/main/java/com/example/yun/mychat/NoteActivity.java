package com.example.yun.mychat;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.example.yun.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import me.yunchuang.greendao.DaoMaster;
import me.yunchuang.greendao.DaoSession;
import me.yunchuang.greendao.Note;
import me.yunchuang.greendao.NoteDao;

/**
 * Created by Yun on 2016/11/23.
 */

public class NoteActivity extends Activity {
    private SQLiteDatabase db;
    private EditText editText;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Cursor cursor;
    private static final String TAG="yunchuang";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        listView=(ListView)findViewById(R.id.list);
        setupDatabase();
        getNoteDao();
        String textColumn= NoteDao.Properties.Text.columnName;
        String orderBy=textColumn+" COLLATE LOCALIZED ASC";
        cursor=db.query(getNoteDao().getTablename(),getNoteDao().getAllColumns(),null,null,null,null,orderBy);
        String[] from={textColumn,NoteDao.Properties.Comment.columnName};
        int[] to={android.R.id.text1,android.R.id.text2};
        SimpleCursorAdapter adapter=new SimpleCursorAdapter(this,android.R.layout.simple_list_item_2,cursor,from,to);
        listView.setAdapter(adapter);
        editText=(EditText)findViewById(R.id.editTextNote);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getNoteDao().deleteByKey(id);
                Log.d(TAG,"Deleted note,ID:"+id);
                cursor.requery();
            }
        });


    }
    private void setupDatabase(){
        DaoMaster.DevOpenHelper helper=new  DaoMaster.DevOpenHelper(this,"notes-db",null);
        db=helper.getWritableDatabase();
        daoMaster=new DaoMaster(db);
        daoSession=daoMaster.newSession();
    }
    private NoteDao getNoteDao(){
        return daoSession.getNoteDao();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onMyButtonClick(View view){
        switch (view.getId()){
            case R.id.buttonAdd:
                addNote();
                break;
            case R.id.buttonSearch:
                search();
                break;
            default:
                Log.d(TAG,"what has gone wrong?");
                break;
        }
    }
    private void addNote(){
        String noteText=editText.getText().toString();
        editText.setText("");
        final DateFormat df= DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM);
        String comment="Added on "+df.format(new Date());
        Note note=new Note(null,noteText,comment,new Date());
        getNoteDao().insert(note);
        Log.d(TAG,"Inserted new note:ID "+note.getId());
        cursor.requery();

    }
    private void search(){
        String searchs=editText.getText().toString();
        Query query=getNoteDao().queryBuilder()
                .where(NoteDao.Properties.Text.eq(searchs))
                .orderAsc(NoteDao.Properties.Date)
                .build();
        List<Note> notes=query.list();
        for(int i=0;i<notes.size();i++){
            Log.d(TAG,"green>>"+notes.get(i).getDate().toString());
        }
        QueryBuilder.LOG_SQL=true;
        QueryBuilder.LOG_VALUES=true;
    }
}
