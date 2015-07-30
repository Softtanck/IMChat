package com.softtanck.imchat.activity;

import android.os.Bundle;
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
import io.rong.message.TextMessage;

import static io.rong.imlib.model.Conversation.ConversationType;


public class MainActivity extends AppCompatActivity {

    private ListView listView;

    private EditText editText;

    private ChatAdapter adapter;
    private List<Message> messages;
    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.et);

        Log.d("Tanck", "start");
        String Token = "l+b9+ZKBjf5tlpOn2G2RE+5BZXJeeKSqHSs6ZG1t8pRjFz23cyW53obPJOcToEhzggUnJS+7aaNEzFxyP3z6cg==";//test
        /**
         * IMKit SDK调用第二步
         *
         * 建立与服务器的连接
         *
         */
        RongIMClient.connect(Token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                //Connect Token 失效的状态处理，需要重新获取 Token
                Log.d("Tanck", "Token失效");
            }

            @Override
            public void onSuccess(String userId) {
                Log.d("Tanck", "userId:" + userId);
                messages = RongIMClient.getInstance().getHistoryMessages(ConversationType.PRIVATE, "2", -1, 20);
                if (null == messages) {
                    messages = new ArrayList<>();
                }
                adapter = new ChatAdapter(MainActivity.this, messages);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.d("Tanck", "error:" + errorCode.getMessage());
            }
        });
        Log.d("Tanck", "end");
        listView = (ListView) findViewById(R.id.lv);


        RongIMClient.getInstance().setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                Log.d("Tanck", "接受到了消息:" + ((TextMessage) message.getContent()).getContent());
                messages.add(message);
                updateMsg();
                return true;
            }
        });
    }

    public void send(View view) {
        TextMessage textMessage = TextMessage.obtain(editText.getText().toString());

        message = RongIMClient.getInstance().sendMessage(ConversationType.PRIVATE, "2", textMessage, null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                Log.d("Tanck", "发送失败:" + errorCode.getMessage());
                message.setSentStatus(Message.SentStatus.FAILED);
                updateMsg();
            }

            @Override
            public void onSuccess(Integer integer) {
                Log.d("Tanck", "发送成功:");
                message.setSentStatus(Message.SentStatus.SENT);
                updateMsg();
            }
        });
        messages.add(message);
        editText.setText("");
        updateMsg();
    }

    private void updateMsg() {
        adapter.notifyDataSetChanged();
        listView.setSelection(messages.size() - 1);
    }
}
