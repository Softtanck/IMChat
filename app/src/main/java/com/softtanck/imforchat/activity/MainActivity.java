package com.softtanck.imforchat.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.softtanck.imforchat.R;
import com.softtanck.imforchat.adapter.ChatAdapter;
import com.softtanck.imforchat.adapter.ExpressionAdapter;
import com.softtanck.imforchat.adapter.ExpressionPagerAdapter;
import com.softtanck.imforchat.adapter.VoicePlayClickListener;
import com.softtanck.imforchat.utils.BaseUtils;
import com.softtanck.imforchat.utils.SmileUtils;
import com.softtanck.imforchat.view.expandview.ExpandGridView;
import com.softtanck.imforchat.view.recorderview.MySounderView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.Toast;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

import static io.rong.imlib.model.Conversation.ConversationType;


public class MainActivity extends AppCompatActivity implements RongIMClient.OnReceiveMessageListener, View.OnClickListener, MySounderView.OnRecordListener {

    private ListView listView;

    private EditText editText;

    private ChatAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private List<Message> sendQueues = new ArrayList<>();
    private MySounderView mySounderView;

    private ImageView imageView;

    /**
     * 滚动表情
     */
    private ViewPager viewPager;

    private ImageView ivEmoji;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
            listView.setSelection(messages.size() - 1);
        }
    };
    private String temp;
    private List<String> reslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.lv);
        editText = (EditText) findViewById(R.id.et);
        mySounderView = (MySounderView) findViewById(R.id.chat_voice_recorder);
        imageView = (ImageView) findViewById(R.id.chat_iv_voice);

        //初始化表情
        ivEmoji = (ImageView) findViewById(R.id.chat_iv_emoji);
        viewPager = (ViewPager) findViewById(R.id.chat_vp_emoji);
        initEmojiView();

        mySounderView.setOnRecordListener(this);
        imageView.setOnClickListener(this);
        ivEmoji.setOnClickListener(this);

        Log.d("Tanck", "start");
        String Token = "o5BxsUtDlaLh4ybB3RS/cnc4loIKiX+IyffcyOEutDvgDhFC7a2npbh41jhdY6BIndV8GyAM2Bk=";
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
        adapter = new ChatAdapter(MainActivity.this, messages, "2333");
        listView.setAdapter(adapter);
    }


    private void getHistoryMessage() {
        List<Message> tmessages = RongIMClient.getInstance().getHistoryMessages(ConversationType.PRIVATE, "2", -1, 200);
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
        switch (v.getId()) {
            case R.id.chat_iv_voice:
                if (View.GONE == mySounderView.getVisibility()) {
                    mySounderView.setVisibility(View.VISIBLE);
                } else {
                    mySounderView.setVisibility(View.GONE);
                }
                break;
            case R.id.chat_iv_emoji:
                if (View.GONE == viewPager.getVisibility()) {
                    viewPager.setVisibility(View.VISIBLE);
                } else {
                    viewPager.setVisibility(View.GONE);
                }
                break;
        }

    }

    @Override
    public void onStartRecord(String fileSrc) {
        //update ui
        if (VoicePlayClickListener.isPlaying) {
            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
        }
    }

    @Override
    public void onEndRecord(String fileSrc) {
        if (null != fileSrc && !fileSrc.equals(temp)) {
            temp = fileSrc;
            Log.d("Tanck", "文件路径:" + Environment.getExternalStorageDirectory() + "/amr_0/" + fileSrc + ".amr");
            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/amr_0/" + fileSrc + ".amr");
                int duration = BaseUtils.getAmrDuration(file);
                if (1 > duration) {
                    Toast.makeText(MainActivity.this, "录音必须大于1秒", Toast.LENGTH_SHORT).show();
                    file.delete();
                    return;
                }
                SendMsg(VoiceMessage.obtain(Uri.fromFile(file), duration));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void SendMsg(MessageContent messageContent) {
        RongIMClient.getInstance().sendMessage(ConversationType.PRIVATE, "2", messageContent, null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                for (Message message : sendQueues) {
                    message.setSentStatus(Message.SentStatus.FAILED);
                    sendQueues.remove(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onSuccess(Integer integer) {
                for (Message message : sendQueues) {
                    if (message.getMessageId() == integer) {
                        message.setSentStatus(Message.SentStatus.SENT);
                        adapter.notifyDataSetChanged();
                        sendQueues.remove(message);
                        break;
                    }
                }
            }
        }, new RongIMClient.ResultCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                sendQueues.add(message);
                messages.add(message);
                adapter.notifyDataSetChanged();
                listView.setSelection(messages.size() - 1);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }


    /**
     * 初始化表情
     */
    private void initEmojiView() {
        // 表情list
        reslist = getExpressionRes(35);
        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        views.add(gv1);
        views.add(gv2);
        viewPager.setAdapter(new ExpressionPagerAdapter(views));
    }

    /**
     * 获取表情的gridview的子view
     *
     * @param i
     * @return
     */
    private View getGridChildView(int i) {
        View view = View.inflate(this, R.layout.expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
        } else if (i == 2) {
            list.addAll(reslist.subList(20, reslist.size()));
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情
                    if (filename != "delete_expression") { // 不是删除键，显示表情
                        //这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                        Class clz = Class.forName("com.softtanck.imchat.utils.SmileUtils");
                        Field field = clz.getField(filename);
                        editText.append(SmileUtils.getSmiledText(MainActivity.this, (String) field.get(null)));
                    } else { // 删除文字或者表情
                        if (!TextUtils.isEmpty(editText.getText())) {

                            int selectionStart = editText.getSelectionStart();// 获取光标的位置
                            if (selectionStart > 0) {
                                String body = editText.getText().toString();
                                String tempStr = body.substring(0, selectionStart);
                                int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                if (i != -1) {
                                    CharSequence cs = tempStr.substring(i, selectionStart);
                                    if (SmileUtils.containsKey(cs.toString()))
                                        editText.getEditableText().delete(i, selectionStart);
                                    else
                                        editText.getEditableText().delete(selectionStart - 1, selectionStart);
                                } else {
                                    editText.getEditableText().delete(selectionStart - 1, selectionStart);
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                }

            }
        });
        return view;
    }

    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;

            reslist.add(filename);

        }
        return reslist;

    }
}
