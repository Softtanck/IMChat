package com.softtanck.imchat.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.softtanck.imchat.R;
import com.softtanck.imchat.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import io.rong.imlib.NativeObject;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

import static io.rong.imlib.model.Conversation.ConversationType;


public class MainActivity extends AppCompatActivity implements RongIMClient.OnReceiveMessageListener {

    private ListView listView;

    private EditText editText;

    private ChatAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private Message message;

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
        adapter = new ChatAdapter(MainActivity.this, messages);
        listView.setAdapter(adapter);
    }

    private void getHistoryMessage() {
        List<Message> tmessages = RongIMClient.getInstance().getHistoryMessages(ConversationType.PRIVATE, "2", -1, 10);
        Log.d("Tanck", "拿到消息了" + tmessages);
        messages.clear();
        messages.addAll(tmessages);
        adapter.notifyDataSetChanged();
        listView.setSelection(messages.size() - 1);
    }


    public void send(View view) {
        message = RongIMClient.getInstance().sendMessage(ConversationType.PRIVATE, "2", TextMessage.obtain(editText.getText().toString()), null, null, new RongIMClient.SendMessageCallback() {
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
        editText.setText("");
        adapter.notifyDataSetChanged();
        listView.setSelection(messages.size() - 1);
    }

    @Override
    public boolean onReceived(Message message, int i) {
        Log.d("Tanck", "收到了消息:" + ((TextMessage) message.getContent()).getContent());
        messages.add(message);
        handler.sendEmptyMessage(0);
        return true;
    }
}
