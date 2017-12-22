package com.example.yun.mychat;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import me.yunchuang.greendao.Chat;
import me.yunchuang.greendao.DaoMaster;
import me.yunchuang.greendao.DaoSession;

/**
 * Created by Yun on 2016/11/16.
 */

public class SocketService extends Service {
    public static final String TAG = "yunchuang";
    private Socket socket;
    private OutputStream sendout;
    private InputStream recIn;
    private SocketAddress socketAddress = new InetSocketAddress("172.23.101.189", 8008);

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Cursor cursor;

    private MsgReceiver msgReceiver;
    private boolean isConnect = false;
    private MyThread myThread;

    private byte[] buf = new byte[8192];
    private Gson gson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        Log.d(TAG, "onCreate() excuted.");
    }

    public void init() {
        //
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.yun.mychat.SocketService.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);
        setupDatabase();
        myThread = new MyThread();
        myThread.start();


    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public class RecThread extends Thread {
        @Override
        public boolean isInterrupted() {
            Log.d(TAG, "RecThread is Interrupted");
            return super.isInterrupted();
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                if (!isConnect) {
                    break;
                } else {
                    try {
                        int len = recIn.read(buf);
                        String data = new String(buf, 0, len);
                        Log.d(TAG, data);
                        MyMessage msg = gson.fromJson(data, MyMessage.class);
                        Log.d(TAG, msg.getType() + "");
                        String type = msg.getType();
                        if (type.equals("2")) {
                            Intent intent1 = new Intent("com.example.yun.mychat.HomeActivity.RECEIVER");
                            intent1.putExtra("result", data);
                            sendBroadcast(intent1);
                        } else if (type.equals("3")) {
                            Chat chat = new Chat(null, msg.getFromid(), msg.getToid(), msg.getTime(), msg.getContent(), false, "");
                            daoSession.getChatDao().insert(chat);
                            Intent intent1 = new Intent("com.example.yun.mychat.HomeActivity.RECEIVER");
                            intent1.putExtra("result", data);
                            sendBroadcast(intent1);
                            intent1 = new Intent("com.example.yun.mychat.ChatActivity.RECEIVER");
                            intent1.putExtra("result", data);
                            sendBroadcast(intent1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            int flag = 0;
            socket = new Socket();
            try {
                socket.connect(socketAddress, 5000);
                sendout = socket.getOutputStream();
                recIn = socket.getInputStream();
                isConnect = true;
                new RecThread().start();
            } catch (IOException e) {
                isConnect = false;
                e.printStackTrace();
            }
            while (true) {
                if (flag == 1) {
                    break;
                }
                try {
                    socket.sendUrgentData(0xff);
                    // Log.d(TAG, "Connect living");
                    Thread.sleep(3 * 1000);
                } catch (IOException e) {
                    Log.d(TAG, "Disconnect");
                    isConnect = false;
                    e.printStackTrace();

                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    flag = 1;
                }
                if (!isConnect) {
                    while (true) {
                        if (flag == 1) {
                            break;
                        }
                        try {
                            socket = new Socket();
                            socket.connect(socketAddress, 5000);
                            sendout = socket.getOutputStream();
                            recIn = socket.getInputStream();
                            socket.setKeepAlive(true);
                            socket.sendUrgentData(0xff);
                            Log.d(TAG, "ReConnect Success");
                            isConnect = true;
                            new RecThread().start();
                            break;
                        } catch (IOException e1) {
                            Log.d(TAG, "ReConnect failed" + e1.toString());
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                                flag = 1;
                            }
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgReceiver);
        myThread.interrupt();
        Log.d(TAG, "onDestroy() excuted.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() excuted.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            Log.d(TAG, data);
            MyMessage msg = gson.fromJson(data, MyMessage.class);
            //Log.d(TAG, msg.getType() + "");
            sendMsg(msg.getType(), data);


        }

        private Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                switch (msg.what) {
                    case 1:
                        Intent intent1 = new Intent("com.example.yun.mychat.MainActivity.RECEIVER");
                        // Log.d(TAG, "" + bundle.getCharSequence("result"));
                        intent1.putExtra("result", bundle.getCharSequence("result"));
                        sendBroadcast(intent1);
                        break;
                    default:
                        break;
                }
                return false;
            }

        });

        public void sendMsg(String type, String data) {
            final int type1 = Integer.parseInt(type);
            final String data1 = data;

            Log.d(TAG, type1 + "");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int count = 5;
                    while (count >= 0) {
                        if (isConnect) {

                            try {
                                sendout.write(data1.getBytes());
                                Message msg = new Message();
                                msg.what = type1;
                                Bundle bundle = new Bundle();
                                bundle.putCharSequence("result", "1");
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                                break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (count < 0) {
                        Message msg = new Message();
                        msg.what = type1;
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence("result", "0");
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }

                }
            }).start();
        }
    }
}
