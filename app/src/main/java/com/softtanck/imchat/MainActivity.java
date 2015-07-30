package com.softtanck.imchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

import static io.rong.imlib.model.Conversation.ConversationType;


public class MainActivity extends AppCompatActivity {

    private ListView listView;

    private ChatAdapter adapter;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Tanck", "start");
        String Token = "ZHd3LAt9gCx7R9ej1iIx6kOBG78H+ewBb4Xr6KD9Ju0MHMiSx5VRWKCkzDu/NFp5shtJoySi1/o=";//test
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
                messages = RongIMClient.getInstance().getHistoryMessages(ConversationType.PRIVATE, "1", -1, 20);
                if (null == messages) {
                    messages = new ArrayList<>();
                }
                adapter = new ChatAdapter(MainActivity.this, messages);
                listView.setAdapter(adapter);
//                RongIMClient.getInstance().sendMessage(ConversationType.PRIVATE, "1", io.rong.message.TextMessage.obtain("+"), "push", null, new RongIMClient.SendMessageCallback() {
//                    @Override
//                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
//                        Log.d("Tanck", "发送失败");
//                    }
//
//                    @Override
//                    public void onSuccess(Integer integer) {
//                        Log.d("Tanck", "发送成功");
//                        adapter.notifyDataSetChanged();
//                    }
//                });
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
                Log.d("Tanck", "接受到了消息:" + message.getObjectName());
                return false;
            }
        });
    }
}
