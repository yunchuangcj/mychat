package com.example.yun.mychat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yun.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import me.yunchuang.greendao.Chat;
import me.yunchuang.greendao.ChatDao;
import me.yunchuang.greendao.DaoMaster;
import me.yunchuang.greendao.DaoSession;

/**
 * Created by Yun on 2016/11/9.
 */
public class HomeActivity extends AppCompatActivity {
    private final static String TAG = "yunchuang";
    private ListView listView_chatlist;
    private List<Person> mData;
    private Intent mIntent;
    public SharePre sharePre;
    private MsgReceiver msgReceiver;
    private Intent sIntent = new Intent("com.example.yun.mychat.SocketService.RECEIVER");
    private ChatlistAdapter chatlistAdapter;
    private ImageView imageView_refresh;
    private Gson gson = new Gson();


    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_home);
        init();
    }

    public void init() {
        sharePre = SharePre.getInstance(this);
        mIntent = new Intent(HomeActivity.this, SocketService.class);
        startService(mIntent);
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.yun.mychat.HomeActivity.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);
        setupDatabase();

        imageView_refresh = (ImageView) findViewById(R.id.refresh);
        imageView_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPersonList();
            }
        });
        mData = new ArrayList<Person>();
        chatlistAdapter = new ChatlistAdapter(this, mData);
        listView_chatlist = (ListView) findViewById(R.id.chatlist);
        listView_chatlist.setAdapter(chatlistAdapter);
        getPersonList();
        listView_chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, ChatActivity.class);
                intent.putExtra("toid", mData.get(position).getName());
                startActivity(intent);
            }
        });


    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(msgReceiver);
        Log.d(TAG, "HomeActivity destroy");
        super.onDestroy();
    }

    private void getPersonList() {
        MyMessage msg = new MyMessage();
        msg.setType("2");
        msg.setContent("");
        sIntent.putExtra("data", gson.toJson(msg));
        sendBroadcast(sIntent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
        Log.d(TAG, "back");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        for (int i = 0; i < mData.size(); i++) {
            Person data = mData.get(i);
            Query query = daoSession.getChatDao().queryBuilder()
                    .where(ChatDao.Properties.FromId.eq(data.getName()), ChatDao.Properties.IsRead.eq(false))
                    .orderAsc(ChatDao.Properties.Date)
                    .build();
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
            List<Chat> chats = query.list();
            mData.get(i).setMsgcount(chats.size());
            query = daoSession.getChatDao().queryBuilder()
                    .where(ChatDao.Properties.FromId.eq(data.getName()))
                    .orderAsc(ChatDao.Properties.Date)
                    .build();
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
            List<Chat> chats2 = query.list();
            if(chats2.size()>0){
                mData.get(i).setLastmsg(chats2.get(chats2.size()-1).getContent());
            }
        }
        chatlistAdapter.notifyDataSetChanged();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_slide_right1, R.anim.activity_slid_right);
    }

    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String data = intent.getStringExtra("result");
            Log.d(TAG, "HomeActivity>>>" + data);
            MyMessage msg = gson.fromJson(data, MyMessage.class);
            if (msg.getType().equals("3")) {
                Query query = daoSession.getChatDao().queryBuilder()
                        .where(ChatDao.Properties.FromId.eq(msg.getFromid()), ChatDao.Properties.IsRead.eq(false))
                        .orderAsc(ChatDao.Properties.Date)
                        .build();
                QueryBuilder.LOG_SQL = true;
                QueryBuilder.LOG_VALUES = true;
                List<Chat> chats = query.list();
                for (int i = 0; i < chats.size(); i++) {
                    Chat chat = chats.get(i);
                    Log.d(TAG, chat.getFromId() + " " + chat.getDate() + " " + chat.getContent());
                }
                for (int i = 0; i < mData.size(); i++) {
                    if (mData.get(i).getName().equals(msg.getFromid())) {
                        int count = chats.size();
                        if (count != 0) {
                            mData.get(i).setMsgcount(count);
                            mData.get(i).setLastmsg(chats.get(count - 1).getContent());
                        }
                        break;
                    }
                }
                chatlistAdapter.notifyDataSetChanged();
            } else if (msg.getType().equals("2")) {
                String username = sharePre.getString("username", "username");
                String personjson = msg.getContent();
                String[] persons = personjson.split("\\$");
                Log.d(TAG, personjson + persons.length);
                mData.clear();
                for (int i = 0; i < persons.length; i++) {
                    if (!persons[i].equals(username)) {
                        mData.add(new Person(persons[i]));
                    }
                }
                chatlistAdapter.notifyDataSetChanged();
            }
        }
    }
}
