package com.example.yun.mychat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yun.R;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import me.yunchuang.greendao.Chat;
import me.yunchuang.greendao.ChatDao;
import me.yunchuang.greendao.DaoMaster;
import me.yunchuang.greendao.DaoSession;

/**
 * Created by Yun on 2016/11/17.
 */

public class ChatActivity extends Activity {

    private final static String TAG = "yunchuang";
    private TextView textView_name;
    private String toid = "";
    private LinearLayout sendbutton;
    private EditText textmsg;
    private ListView chatlistview;
    private Intent mIntent;
    public SharePre sharePre;
    private MsgReceiver msgReceiver;
    private String username;
    private ChatTextAdapter chatTextAdapter;
    private Intent sIntent = new Intent("com.example.yun.mychat.SocketService.RECEIVER");
    private List<MyMessage> mData;
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
        setContentView(R.layout.activity_chat);
        toid = this.getIntent().getStringExtra("toid");
        init();
    }

    private void init() {
        sharePre = SharePre.getInstance(this);
        username = sharePre.getString("username", "username");
        mIntent = new Intent(ChatActivity.this, SocketService.class);
        startService(mIntent);
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.yun.mychat.ChatActivity.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);
        setupDatabase();

        textView_name = (TextView) findViewById(R.id.name);
        textView_name.setText(toid);
        sendbutton = (LinearLayout) findViewById(R.id.sendbutton);
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textmsg.getText().toString();
                if (text != null && !text.equals("")) {
                    Date date = new Date();
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = format.format(date);
                    MyMessage msg = new MyMessage();
                    msg.setType("3");
                    msg.setContent(text);
                    msg.setFromid(username);
                    msg.setToid(toid);
                    msg.setTime(time);
                    daoSession.getChatDao().insert(new Chat(null, msg.getFromid(), msg.getToid(), msg.getTime(), msg.getContent(), true, ""));
                    sIntent.putExtra("data", gson.toJson(msg));
                    sendBroadcast(sIntent);
                    textmsg.setText("");
                    msg.setFlag(2);
                    mData.add(msg);
                    chatTextAdapter.notifyDataSetChanged();
                    chatlistview.smoothScrollToPosition(mData.size());
                }
            }
        });
        textmsg = (EditText) findViewById(R.id.textmsg);
        chatlistview = (ListView) findViewById(R.id.chatlist);
        mData = new ArrayList<MyMessage>();
        chatTextAdapter = new ChatTextAdapter(this, mData);
        chatlistview.setAdapter(chatTextAdapter);

        Query query = daoSession.getChatDao().queryBuilder()
                .whereOr(ChatDao.Properties.FromId.eq(toid), ChatDao.Properties.ToId.eq(toid))
                .orderAsc(ChatDao.Properties.Date)
                .build();

        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List<Chat> chats = query.list();
        for (int i = 0; i < chats.size(); i++) {
            Chat chat = chats.get(i);
            Log.d(TAG, chat.getFromId() + " " + chat.getDate() + " " + chat.getContent());
            if (chat.getFromId().equals(username)) {
                MyMessage msg = new MyMessage();
                msg.setFlag(2);
                msg.setType("3");
                msg.setContent(chat.getContent());
                msg.setFromid(chat.getFromId());
                msg.setToid(chat.getToId());
                msg.setTime(chat.getDate());
                mData.add(msg);
            } else {
                MyMessage msg = new MyMessage();
                msg.setFlag(1);
                msg.setType("3");
                msg.setContent(chat.getContent());
                msg.setFromid(chat.getFromId());
                msg.setToid(chat.getToId());
                msg.setTime(chat.getDate());
                mData.add(msg);
                chat.setIsRead(true);
                daoSession.getChatDao().update(chat);
            }
        }
        chatTextAdapter.notifyDataSetChanged();
        chatlistview.smoothScrollToPosition(mData.size());

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
        Log.d(TAG, "ChatActivity destroy");
        super.onDestroy();
    }

    public class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String data = intent.getStringExtra("result");
            Log.d(TAG, "ChatActivity>>>" + data);
            MyMessage msg = gson.fromJson(data, MyMessage.class);
            if (msg.getType().equals("3")) {
                Query query = daoSession.getChatDao().queryBuilder()
                        .where(ChatDao.Properties.FromId.eq(toid), ChatDao.Properties.IsRead.eq(false))
                        .orderAsc(ChatDao.Properties.Date)
                        .build();
                QueryBuilder.LOG_SQL = true;
                QueryBuilder.LOG_VALUES = true;
                List<Chat> chats = query.list();
                for (int i = 0; i < chats.size(); i++) {
                    Chat chat = chats.get(i);
                    Log.d(TAG, chat.getFromId() + " " + chat.getDate() + " " + chat.getContent());
                    msg.setFlag(1);
                    msg.setType("3");
                    msg.setContent(chat.getContent());
                    msg.setFromid(chat.getFromId());
                    msg.setToid(chat.getToId());
                    msg.setTime(chat.getDate());
                    mData.add(msg);
                    chat.setIsRead(true);
                    daoSession.getChatDao().update(chat);
                }
                chatTextAdapter.notifyDataSetChanged();
                chatlistview.smoothScrollToPosition(mData.size());
            }

        }
    }
}
