package com.yoonah.bureyes;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class VoiceRecognitionListener implements RecognitionListener {
    private Handler mainHandler;
    private Boolean isSpeeking = false;

    public Boolean getSpeeking() {
        return isSpeeking;
    }

    public void setSpeeking(Boolean speeking) {
        isSpeeking = speeking;
    }

    public VoiceRecognitionListener(Handler handler){
        mainHandler = handler;
    }
    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        if(isSpeeking){
            return;
        }
        Message message =  mainHandler.obtainMessage();
        Bundle bundle = new Bundle();

        switch (error) {
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                Log.d("Speech onError", "ERROR_NETWORK_TIMEOUT");
                bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_VOICE_RCOGNITION_START);
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                return;
            case SpeechRecognizer.ERROR_AUDIO:
                break;
            case SpeechRecognizer.ERROR_SERVER:
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                Log.d("Speech onError", "ERROR_SPEECH_TIMEOUT");
                bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_VOICE_RCOGNITION_START);
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                Log.d("Speech onError", "ERROR_NO_MATCH");
                bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_VOICE_RCOGNITION_START);
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                break;
        }
        message.setData(bundle);
        mainHandler.sendMessage(message);
    }

    @Override
    public void onResults(Bundle results) {
        if(isSpeeking){
            return;
        }
        //음성 인식 완료 후 변환된 텍스트 값 받아
        ArrayList<String> strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        StringBuffer record = new StringBuffer();
        for (int i = 0; i < strlist.size(); i++) {
            record.append(strlist.get(i));
        }
        Message message = mainHandler.obtainMessage();
        Bundle bundle = new Bundle();

        if(record.indexOf("편의점") >= 0){
            bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_SEARCH_PLACES);
            bundle.putString(MessageType.MESAGE_TYPE_SEARCH_PLACES, "편의점");
        } else if (record.indexOf("약국") >= 0) {
            bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_SEARCH_PLACES);
            bundle.putString(MessageType.MESAGE_TYPE_SEARCH_PLACES, "약국");
        } else if (record.indexOf("병원") >= 0) {
            bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_SEARCH_PLACES);
            bundle.putString(MessageType.MESAGE_TYPE_SEARCH_PLACES, "병원");
        } else if (record.indexOf("카페") >= 0) {
            bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_SEARCH_PLACES);
            bundle.putString(MessageType.MESAGE_TYPE_SEARCH_PLACES, "카페");
        } else if (record.indexOf("정류장") >= 0) {
            bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_SEARCH_PLACES);
            bundle.putString(MessageType.MESAGE_TYPE_SEARCH_PLACES, "정류장");
        } else if (record.indexOf("안내중지") >= 0 || record.indexOf("안내 중지") >= 0 || record.indexOf("중지") >= 0
                || record.indexOf("종료")>= 0 ||record.indexOf("안내종료")>= 0 || record.indexOf("안내 종료")>= 0 ){
            bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_GUIDENCE_STOP);
        } else if (record.indexOf("안내시작") >= 0 || record.indexOf("안내 시작") >= 0
                || record.indexOf("시작") >= 0 || record.indexOf("안내") >= 0) {
            bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_GUIDENCE_START);
            bundle.putString(MessageType.MESAGE_TYPE_GUIDENCE_START, MessageType.MESAGE_TYPE_GUIDENCE_START_ONE);
        } else if (record.indexOf("1번") >= 0 || record.indexOf("일번") >= 0 || record.indexOf("첫 번째") >= 0) {
            bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_GUIDENCE_START);
            bundle.putString(MessageType.MESAGE_TYPE_GUIDENCE_START, MessageType.MESAGE_TYPE_GUIDENCE_START_ONE);
        } else if (record.indexOf("2번") >= 0 || record.indexOf("이번") >= 0 || record.indexOf("두 번째") >= 0) {
            bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_GUIDENCE_START);
            bundle.putString(MessageType.MESAGE_TYPE_GUIDENCE_START, MessageType.MESAGE_TYPE_GUIDENCE_START_TWO);
        } else if (record.indexOf("3번") >= 0 || record.indexOf("삼번") >= 0 || record.indexOf("세 번째") >= 0) {
            bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_GUIDENCE_START);
            bundle.putString(MessageType.MESAGE_TYPE_GUIDENCE_START, MessageType.MESAGE_TYPE_GUIDENCE_START_THREE);
        } else{
            bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_VOICE_RCOGNITION_START);
        }

        message.setData(bundle);
        mainHandler.sendMessage(message);
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }
}
