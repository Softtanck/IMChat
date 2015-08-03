/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.softtanck.imchat.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.softtanck.imchat.R;

import io.rong.imlib.model.Message;
import io.rong.message.VoiceMessage;

public class VoicePlayClickListener implements View.OnClickListener {

    private Message message;
    private Uri voiceUri;
    private ImageView voiceIconView;

    private AnimationDrawable voiceAnimation = null;
    private MediaPlayer mediaPlayer = null;
    private Context context;

    public static boolean isPlaying = false;
    public static VoicePlayClickListener currentPlayListener = null;
    private OnVoiceStopListener onVoiceStopListener;
    static Message currentMessage = null;

    public void setOnVoiceStopListener(OnVoiceStopListener onVoiceStopListener) {
        this.onVoiceStopListener = onVoiceStopListener;
    }

    /**
     * @param message
     * @param v
     * @param context
     */
    public VoicePlayClickListener(Message message, ImageView v, Context context) {
        this.message = message;
        voiceUri = ((VoiceMessage) message.getContent()).getUri();
        this.context = context;
        voiceIconView = v;
    }

    public void stopPlayVoice() {
        voiceAnimation.stop();
        if (message.getMessageDirection() == Message.MessageDirection.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.chat_msg_voice_left);
        } else {
            voiceIconView.setImageResource(R.drawable.chat_msg_voice_right);
        }
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;

        if (null != onVoiceStopListener) {
            onVoiceStopListener.onStop();
        }
    }

    public void playVoice() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
//        audioManager.setSpeakerphoneOn(true);//关闭扬声器
//        audioManager.setMode(AudioManager.MODE_IN_CALL);//把声音设定成Earpiece（听筒）出来，设定为正在通话中
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        try {
            mediaPlayer.setDataSource(context, voiceUri);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopPlayVoice(); // stop animation
                }

            });
            isPlaying = true;
            currentPlayListener = this;
            currentMessage = message;
            mediaPlayer.start();
            showAnimation();
            if (null != onVoiceStopListener) {
                onVoiceStopListener.onStart();
            }
        } catch (Exception e) {
        }
    }

    // show the voice playing animation
    private void showAnimation() {
        // play voice, and start animation
        if (message.getMessageDirection() == Message.MessageDirection.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.chat_voice_left_anim);
        } else {
            voiceIconView.setImageResource(R.drawable.chat_voice_right_anim);
        }
        voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
        voiceAnimation.start();
    }

    @Override
    public void onClick(View v) {

        if (isPlaying) {
            currentPlayListener.stopPlayVoice();
            if (currentMessage != null && currentMessage.hashCode() == message.hashCode()) {
                currentMessage = null;
                return;
            }
        }

        playVoice();
        //判断是否为发送,如果为发送就可以获取本地,如果是接受,就必须去down.
    }
}

interface OnVoiceStopListener {
    void onStop();

    void onStart();
}