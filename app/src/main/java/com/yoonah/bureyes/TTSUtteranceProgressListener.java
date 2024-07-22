package com.yoonah.bureyes;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.UtteranceProgressListener;



public class TTSUtteranceProgressListener extends UtteranceProgressListener {
    private Handler mainHandler;

    public TTSUtteranceProgressListener(Handler handler){
        mainHandler = handler;
    }
    @Override
    public void onStart(String s) {

        Message message = mainHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_TTS_START );
        message.setData(bundle);
        mainHandler.sendMessage(message);
    }

    @Override
    public void onDone(String s) {
        Message message = mainHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_TTS_DONE);
        message.setData(bundle);
        mainHandler.sendMessage(message);

    }

    @Override
    public void onError(String s) {

    }
}
