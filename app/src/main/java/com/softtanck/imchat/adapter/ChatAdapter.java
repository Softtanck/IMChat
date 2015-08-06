package com.softtanck.imchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.softtanck.imchat.App;
import com.softtanck.imchat.ImageLoaderConfig;
import com.softtanck.imchat.R;
import com.softtanck.imchat.activity.BaiduMapActivity;
import com.softtanck.imchat.utils.TimeFormatUtils;

import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * @author : Tanck
 * @Description : TODO
 * @date 7/28/2015
 */
public class ChatAdapter extends BaseAdapter {

    /**
     * 接受文本类型
     */
    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    /**
     * 发送文本类型
     */
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    /**
     * 发送图片类型
     */
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    /**
     * 发送位置
     */
    private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    /**
     * 接受位置
     */
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
    /**
     * 接受图片
     */
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    /**
     * 发送语音
     */
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    /**
     * 接受语音
     */
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    /**
     * 发送视频
     */
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    /**
     * 接受视频
     */
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    /**
     * 发送图文消息
     */
    private static final int MESSAGE_TYPE_SENT_RICH_CONTENT = 10;
    /**
     * 接受图文消息
     */
    private static final int MESSAGE_TYPE_RECV_RICH_CONTENT = 11;
    /**
     * 小灰色提示消息
     */
    private static final int MESSAGE_TYPE_RECV_NTF_MSG = 12;

    private ImageLoader imageLoader;

    private Context context;

    private List<Message> messages;

    /**
     * 标记聊天ID
     */
    private String tagId;
    /**
     * 默认间隔时间
     */
    private long DEAULT_TIME = 180 * 1000;


    public ChatAdapter(Context context, List<Message> messages, String tagId) {
        this.context = context;
        this.messages = messages;
        this.imageLoader = App.getInstance().imageLoader;
        this.tagId = tagId;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);
        MessageContent messageContent = message.getContent();
        if (messageContent instanceof TextMessage) {// 文本
            return message.getMessageDirection() == Message.MessageDirection.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        } else if (messageContent instanceof ImageMessage) { // 图片
            return message.getMessageDirection() == Message.MessageDirection.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;
        } else if (messageContent instanceof LocationMessage) {// 位置
            return message.getMessageDirection() == Message.MessageDirection.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
        } else if (messageContent instanceof RichContentMessage) {// 图文消息
            return message.getMessageDirection() == Message.MessageDirection.RECEIVE ? MESSAGE_TYPE_RECV_RICH_CONTENT : MESSAGE_TYPE_SENT_RICH_CONTENT;
        } else if (messageContent instanceof VoiceMessage) { // 语音消息
            return message.getMessageDirection() == Message.MessageDirection.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        } else if (messageContent instanceof InformationNotificationMessage) {//灰色提示消息
            return MESSAGE_TYPE_RECV_NTF_MSG;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 12;//暂时12种,后续根据业务会增加红包,广告等消息.
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder viewHoder = null;
        final Message message = getItem(position);
        MessageContent messageContent = message.getContent();
        if (null == convertView) {
            viewHoder = new ViewHoder();
            convertView = createViewByMessage(messages.get(position));
            if (message.getContent() instanceof TextMessage) {
                try {
                    viewHoder.time = (TextView) convertView.findViewById(R.id.chat_tv_time);
                    viewHoder.name = (TextView) convertView.findViewById(R.id.chat_tv_name);
                    viewHoder.head = (ImageView) convertView.findViewById(R.id.chat_iv_head);
                    viewHoder.content = (TextView) convertView.findViewById(R.id.chat_tv_content);
                    viewHoder.progressBar = (ProgressBar) convertView.findViewById(R.id.chat_pb);
                    viewHoder.state = (ImageView) convertView.findViewById(R.id.chat_iv_state);
                } catch (Exception e) {
                }
            } else if (message.getContent() instanceof ImageMessage) {
                try {
                    viewHoder.time = (TextView) convertView.findViewById(R.id.chat_tv_time);
                    viewHoder.name = (TextView) convertView.findViewById(R.id.chat_tv_name);
                    viewHoder.head = (ImageView) convertView.findViewById(R.id.chat_iv_head);
                    viewHoder.imageView = (ImageView) convertView.findViewById(R.id.chat_iv_content);
                    viewHoder.progressBar = (ProgressBar) convertView.findViewById(R.id.chat_pb);
                    viewHoder.state = (ImageView) convertView.findViewById(R.id.chat_iv_state);
                } catch (Exception e) {
                }
            } else if (message.getContent() instanceof VoiceMessage) {
                try {
                    viewHoder.time = (TextView) convertView.findViewById(R.id.chat_tv_time);
                    viewHoder.name = (TextView) convertView.findViewById(R.id.chat_tv_name);
                    viewHoder.head = (ImageView) convertView.findViewById(R.id.chat_iv_head);
                    viewHoder.voiceTime = (TextView) convertView.findViewById(R.id.chat_tv_voice_time);
                    viewHoder.voiceIcon = (ImageView) convertView.findViewById(R.id.chat_iv_voice);
                    viewHoder.voiceGroup = (LinearLayout) convertView.findViewById(R.id.chat_ll_content);
                    viewHoder.progressBar = (ProgressBar) convertView.findViewById(R.id.chat_pb);
                    viewHoder.state = (ImageView) convertView.findViewById(R.id.chat_iv_state);
                } catch (Exception e) {
                }
            } else if (message.getContent() instanceof LocationMessage) {
                try {
                    viewHoder.time = (TextView) convertView.findViewById(R.id.chat_tv_time);
                    viewHoder.name = (TextView) convertView.findViewById(R.id.chat_tv_name);
                    viewHoder.head = (ImageView) convertView.findViewById(R.id.chat_iv_head);
                    viewHoder.content = (TextView) convertView.findViewById(R.id.chat_tv_content);// 位置
                    viewHoder.location = (LinearLayout) convertView.findViewById(R.id.chat_ll_content);
                    viewHoder.progressBar = (ProgressBar) convertView.findViewById(R.id.chat_pb);
                    viewHoder.state = (ImageView) convertView.findViewById(R.id.chat_iv_state);
                } catch (Exception e) {
                }
            }
            convertView.setTag(viewHoder);
        } else {
            viewHoder = (ViewHoder) convertView.getTag();
        }

        handleTime(message, viewHoder, position);

        if (messageContent instanceof TextMessage) { // 文本消息
            handleTextMessage(message, viewHoder, position);
        } else if (messageContent instanceof ImageMessage) {// 图片消息
            handleImageMessage(message, viewHoder, position);
        } else if (messageContent instanceof VoiceMessage) { // 语音消息
            handleVoiceMessage(message, viewHoder, position);
        } else if (messageContent instanceof RichContentMessage) { // 图文消息
            handleRichContentMessage(message, viewHoder, position);
        } else if (messageContent instanceof InformationNotificationMessage) { // 灰色提示
            handleNotifiMessage(message, viewHoder, position);
        } else if (messageContent instanceof LocationMessage) { // 位置消息
            handleLocationMessage(message, viewHoder, position);
        } else {
            //not supported.
        }

        return convertView;
    }

    /**
     * 设置时间间隔是否需要显示
     *
     * @param message
     * @param viewHoder
     * @param position
     */
    private void handleTime(Message message, ViewHoder viewHoder, int position) {
        viewHoder.time.setVisibility(isShowTime(message.getSentTime(),
                position) ? View.VISIBLE : View.GONE);
        if (View.VISIBLE == viewHoder.time.getVisibility()) {
            viewHoder.time.setText(TimeFormatUtils.friendlyFormat(message.getSentTime()));
        }
    }

    /**
     * 判断是否需要展示时间
     *
     * @param sentTime
     * @param position
     * @return true 代表显示时间 ,false 代表隐藏时间
     */
    private boolean isShowTime(long sentTime, int position) {
        if (position - 1 < 0) { // 上一条消息
            return false;
        }
        long lastTime = getItem(position - 1).getSentTime();//上一条消息的发送时间
        if (sentTime - lastTime > DEAULT_TIME) {
            return true;
        }
        return false;
    }

    /**
     * 处理位置消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleLocationMessage(final Message message, ViewHoder holder, int position) {

        //set onclick listenter
        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lat = ((LocationMessage) message.getContent()).getLat(); // 维度
                double lng = ((LocationMessage) message.getContent()).getLng(); // 经度
                Intent intent = new Intent(context, BaiduMapActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                context.startActivity(intent);
            }
        });
        holder.content.setText(((LocationMessage) message.getContent()).getPoi());

        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            switch (message.getSentStatus()) {
                case DESTROYED://对方已销毁
                case FAILED://发送失败
                    holder.progressBar.setVisibility(View.GONE);
                    holder.state.setVisibility(View.VISIBLE);
                    break;
                case SENDING://发送中
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.state.setVisibility(View.GONE);
                    break;
                case READ:
                case RECEIVED://对方已接受
                case SENT://已发送
                    holder.progressBar.setVisibility(View.GONE);
                    holder.state.setVisibility(View.GONE);
                    break;
            }
        }
    }

    /**
     * 处理灰色小消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleNotifiMessage(Message message, ViewHoder holder, int position) {

    }

    /**
     * 处理图文消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleRichContentMessage(Message message, ViewHoder holder, int position) {

    }

    /**
     * 处理语音消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleVoiceMessage(final Message message, final ViewHoder holder, int position) {

        int voiceTime = ((VoiceMessage) message.getContent()).getDuration();
        if (0 < voiceTime) {//有效声音
            changeViewWidth(holder, voiceTime);
        }

        Log.d("Tanck", "收到语音地址:" + ((VoiceMessage) message.getContent()).getUri());
        //文档中说downloadMedia带缓存任务,
        RongIMClient.getInstance().downloadMedia(Conversation.ConversationType.PRIVATE, tagId, RongIMClient.MediaType.AUDIO, String.valueOf(((VoiceMessage) message.getContent()).getUri()), new RongIMClient.DownloadMediaCallback() {
            @Override
            public void onProgress(int i) {
                Log.d("Tanck", "正在下载:" + i);
            }

            @Override
            public void onSuccess(String s) {
                Log.d("Tanck", "下载完成:" + s);
                holder.voiceGroup.setOnClickListener(new VoicePlayClickListener(message, holder.voiceIcon, context));
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            switch (message.getSentStatus()) {
                case DESTROYED://对方已销毁
                case FAILED://发送失败
                    holder.progressBar.setVisibility(View.GONE);
                    holder.state.setVisibility(View.VISIBLE);
                    break;
                case SENDING://发送中
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.state.setVisibility(View.GONE);
                    break;
                case READ:
                case RECEIVED://对方已接受
                case SENT://已发送
                    holder.progressBar.setVisibility(View.GONE);
                    holder.state.setVisibility(View.GONE);
                    break;
            }
        }
    }

    /**
     * 改变语音视图宽度
     *
     * @param hoder
     */
    private void changeViewWidth(ViewHoder hoder, int voiceTime) {
        hoder.voiceTime.setText(voiceTime + "\"");
        hoder.voiceGroup.getLayoutParams().width = 150 + 30 * voiceTime;//这里的50是需要根据屏幕宽度去计算宽度因子
    }

    /**
     * 处理图片消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleImageMessage(Message message, ViewHoder holder, int position) {
        Log.d("Tanck", "收到图片消息:" + ((ImageMessage) message.getContent()).getThumUri());
        holder.imageView.setTag(((ImageMessage) message.getContent()).getThumUri());
        imageLoader.displayImage(String.valueOf(((ImageMessage) message.getContent()).getThumUri()), holder.imageView, App.getInstance().imageLoaderConfig.setImageLoaderByNormal(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (imageUri.equals(view.getTag())) {
                    view.setTag(null);//置空Tag
                    ((ImageView) view).setImageBitmap(loadedImage);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
//        holder.imageView.setImageResource(R.drawable.tmp_head_1); // 通过网络去获取图片
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            switch (message.getSentStatus()) {
                case DESTROYED://对方已销毁
                case FAILED://发送失败
                    holder.progressBar.setVisibility(View.GONE);
                    holder.state.setVisibility(View.VISIBLE);
                    break;
                case SENDING://发送中
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.state.setVisibility(View.GONE);
                    break;
                case READ:
                case RECEIVED://对方已接受
                case SENT://已发送
                    holder.progressBar.setVisibility(View.GONE);
                    holder.state.setVisibility(View.GONE);
                    break;
            }
        }
    }

    /**
     * 处理文本消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleTextMessage(Message message, ViewHoder holder, int position) {
        holder.content.setText(((TextMessage) message.getContent()).getContent());
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            switch (message.getSentStatus()) {
                case DESTROYED://对方已销毁
                case FAILED://发送失败
                    holder.progressBar.setVisibility(View.GONE);
                    holder.state.setVisibility(View.VISIBLE);
                    break;
                case SENDING://发送中
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.state.setVisibility(View.GONE);
                    break;
                case READ:
                case RECEIVED://对方已接受
                case SENT://已发送
                    holder.progressBar.setVisibility(View.GONE);
                    holder.state.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private class ViewHoder {
        TextView name;//名字
        ImageView head;//头像
        TextView content;//文本内容
        ProgressBar progressBar;//进度
        ImageView state;//状态
        TextView time;//消息时间
        ImageView imageView;//图片消息
        TextView voiceTime;//语音时间
        ImageView voiceIcon;//语音图片
        LinearLayout voiceGroup;//包裹语音
        LinearLayout location;//包裹地图
    }

    /**
     * 通过消息去创建对应的视图
     *
     * @param baseMessage
     */
    private View createViewByMessage(Message baseMessage) {
        MessageContent content = baseMessage.getContent();
        if (content instanceof TextMessage) { // 文本消息
            return View.inflate(context, baseMessage.getMessageDirection() == Message.MessageDirection.RECEIVE ? R.layout.chat_item_txt_revice : R.layout.chat_item_txt_send, null);
        } else if (content instanceof ImageMessage) {  // 图片消息
            return View.inflate(context, baseMessage.getMessageDirection() == Message.MessageDirection.RECEIVE ? R.layout.chat_item_img_revice : R.layout.chat_item_img_send, null);
        } else if (content instanceof LocationMessage) { // 位置消息
            return View.inflate(context, baseMessage.getMessageDirection() == Message.MessageDirection.RECEIVE ? R.layout.chat_item_location_revice : R.layout.chat_item_location_send, null);
        } else if (content instanceof VoiceMessage) { // 语音消息
            return View.inflate(context, baseMessage.getMessageDirection() == Message.MessageDirection.RECEIVE ? R.layout.chat_item_voice_revice : R.layout.chat_item_voice_send, null);
        } else if (content instanceof InformationNotificationMessage) { //小灰色提醒消息
            return null;
        } else {
            return View.inflate(context, baseMessage.getMessageDirection() == Message.MessageDirection.RECEIVE ? R.layout.chat_item_txt_revice : R.layout.chat_item_txt_send, null);
        }
    }
}
