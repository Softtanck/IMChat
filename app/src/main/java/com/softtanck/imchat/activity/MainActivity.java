package com.softtanck.imchat.activity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.softtanck.imchat.R;
import com.softtanck.imchat.adapter.ChatAdapter;
import com.softtanck.imchat.utils.BaseUtils;
import com.softtanck.imchat.view.recorderview.MySounderView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.Toast;

import io.rong.imlib.NativeObject;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

import static io.rong.imlib.model.Conversation.ConversationType;


public class MainActivity extends AppCompatActivity implements RongIMClient.OnReceiveMessageListener, View.OnClickListener, MySounderView.OnRecordListener {

    private ListView listView;

    private EditText editText;

    private ChatAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private Message message;
    private MySounderView mySounderView;

    private ImageView imageView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
            listView.setSelection(messages.size() - 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.lv);
        editText = (EditText) findViewById(R.id.et);
        mySounderView = (MySounderView) findViewById(R.id.chat_voice_recorder);
        imageView = (ImageView) findViewById(R.id.chat_iv_voice);

        mySounderView.setOnRecordListener(this);
        imageView.setOnClickListener(this);

        Log.d("Tanck", "start");
        String Token = "l+b9+ZKBjf5tlpOn2G2RE+5BZXJeeKSqHSs6ZG1t8pRjFz23cyW53obPJOcToEhzggUnJS+7aaNEzFxyP3z6cg==";//test
        RongIMClient.connect(Token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

            }

            @Override
            public void onSuccess(String s) {
                Log.d("Tanck", "链接成功,当前UserId:" + s);
                getHistoryMessage();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
        RongIMClient.setOnReceiveMessageListener(this);
        adapter = new ChatAdapter(MainActivity.this, messages, "2");
        listView.setAdapter(adapter);
    }

    private void getHistoryMessage() {
        List<Message> tmessages = RongIMClient.getInstance().getHistoryMessages(ConversationType.PRIVATE, "2", -1, 50);
        Log.d("Tanck", "拿到消息了" + tmessages);
        if (null != tmessages) {
            messages.clear();
            messages.addAll(tmessages);
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(messages.size() - 1);
    }


    public void send(View view) {
        SendMsg(TextMessage.obtain(editText.getText().toString()));
        editText.setText("");
    }

    @Override
    public boolean onReceived(Message message, int i) {
//        Log.d("Tanck", "收到了消息:" + ((TextMessage) message.getContent()).getContent());
        messages.add(message);
        handler.sendEmptyMessage(0);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (View.GONE == mySounderView.getVisibility()) {
            mySounderView.setVisibility(View.VISIBLE);
        } else {
            mySounderView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStartRecord(String fileSrc) {
        //update ui
    }

    @Override
    public void onEndRecod(String fileSrc) {
        if (null != fileSrc) {
            Log.d("Tanck", "文件路径:" + Environment.getExternalStorageDirectory() + "/amr_0/" + fileSrc + ".amr");
            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/amr_0/" + fileSrc + ".amr");
                SendMsg(VoiceMessage.obtain(Uri.fromFile(file), BaseUtils.getAmrDuration(file)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void SendMsg(MessageContent messageContent) {
        message = RongIMClient.getInstance().sendMessage(ConversationType.PRIVATE, "2", messageContent, null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                message.setSentStatus(Message.SentStatus.FAILED);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onSuccess(Integer integer) {
                Log.d("Tanck", "success");
                message.setSentStatus(Message.SentStatus.SENT);
                adapter.notifyDataSetChanged();
            }
        });
        messages.add(message);
        adapter.notifyDataSetChanged();
        listView.setSelection(messages.size() - 1);
    }
}
