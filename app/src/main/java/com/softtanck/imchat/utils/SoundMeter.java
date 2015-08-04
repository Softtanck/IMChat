package com.softtanck.imchat.utils;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class SoundMeter {
    static final private double EMA_FILTER = 0.6;

    private MediaRecorder mRecorder = null;
    private double mEMA = 0.0;

    public void start(String name) {

        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    Log.d("Tanck", "录音成功");
                } else {
                    Log.d("Tanck", "录音失败");
                }
            }

            @Override
            protected Boolean doInBackground(String... params) {
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    return false;
                }
                if (mRecorder == null) {
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    File file = new File(Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                            + "/amr_0/");
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    mRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/amr_0/"
                            + params[0] + ".amr");
                    try {
                        mRecorder.prepare();
                        mRecorder.start();
                        mEMA = 0.0;
                    } catch (IllegalStateException e) {
                        System.out.print(e.getMessage());
                    } catch (IOException e) {
                        System.out.print(e.getMessage());
                    }

                }
                return true;
            }

        }.execute(name);

    }

    public void stop() {
        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                if (mRecorder != null) {
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                }
                return null;
            }

        }.execute("");

    }

    public void pause() {
        if (mRecorder != null) {
            mRecorder.stop();
        }
    }

    public void start() {
        if (mRecorder != null) {
            mRecorder.start();
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude() / 2700.0);
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }
}
