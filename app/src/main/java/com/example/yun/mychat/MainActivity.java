package com.example.yun.mychat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.yun.R;
import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "yunchuang";
    private boolean isStart = false;
    private EditText edittext_username;
    private EditText edittext_password;
    private LinearLayout linearlayout_login;
    private TextView textview_register;
    private TextView textview_forget;
    private Intent mIntent;
    public SharePre sharePre;
    private MsgReceiver msgReceiver;
    private Intent sIntent = new Intent("com.example.yun.mychat.SocketService.RECEIVER");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_fullscreen);
        init();
    }

    private void init() {
        sharePre = SharePre.getInstance(this);
        if (sharePre.getBoolean("islogin", false) && !sharePre.getString("username", "").equals("")) {
            MyMessage msg = new MyMessage();
            msg.setType("1");
            msg.setContent(sharePre.getString("username", ""));
            Gson gson = new Gson();
            sIntent.putExtra("data", gson.toJson(msg));
            sendBroadcast(sIntent);
        }
        mIntent = new Intent(MainActivity.this, SocketService.class);
        startService(mIntent);

        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.yun.mychat.MainActivity.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);

        textview_register=(TextView)findViewById(R.id.register);
        textview_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(),NoteActivity.class);
                startActivity(intent);
            }
        });

        linearlayout_login = (LinearLayout) findViewById(R.id.loginbutton);
        edittext_username = (EditText) findViewById(R.id.username);
        linearlayout_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edittext_username.getText().toString();
                if (username == null || username.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter Username", Toast.LENGTH_SHORT).show();
                } else {
                    MyMessage msg = new MyMessage();
                    msg.setType("1");
                    msg.setContent(username);
                    Gson gson = new Gson();
                    sIntent.putExtra("data", gson.toJson(msg));
                    sendBroadcast(sIntent);
                }
                /*Intent intent = new Intent();
                intent.setClass(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.acivity_animation, R.anim.activity_exit);
                finish();*/
            }
        });
    }

    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String data = intent.getStringExtra("result");
            Log.d(TAG, data);
            if (data.equals("1")) {
                sharePre.setString("username", edittext_username.getText().toString());
                sharePre.setBoolean("islogin", true);
                Intent intent1 = new Intent();
                intent1.setClass(getApplicationContext(), HomeActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.acivity_animation, R.anim.activity_exit);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Login Fail ,Try Again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        //stopService(mIntent);
        unregisterReceiver(msgReceiver);
        Log.d(TAG, "MainActivity destroy");
        super.onDestroy();
    }

}
