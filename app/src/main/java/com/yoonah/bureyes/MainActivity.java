package com.yoonah.bureyes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements MapView.OpenAPIKeyAuthenticationResultListener, MapView.MapViewEventListener {
   // private static final int RECORD_AUDIO_PERMISSION_CODE = 100;
  //  private static final int ACCESS_FINE_LOCATION_PERMISSION_CODE = 101;

    private PermissionSupport permission;

    TextView textViewContents;

    GifImageView gifImageViewOn;
    GifImageView gifImageViewOff;

    //com.yoonah.bureyes
    //KeyHash : ahH4SK1h1Y91Hsu2zSGyVPCSY9w=
    MapView mapView;
    MapCircle centerCircle1;
    boolean reverseColor = false;

    //다 안드로이드에서 음성인식 기능 제공하기 위한 라이브러리
    SpeechRecognizer speechRecognizer;
    //음성인식 후 변환된 텍스트를 전달해주기위한 리스너 클라스
    VoiceRecognitionListener voiceRecognitionListener;
    //음성인식 라이브러리 환경설정 위한 클라스
    Intent speechRecognizerIntent;
    Boolean voiceRecognizing = false;

    //TTs 안드로이드 라이브러리 control
    TextToSpeech textToSpeech;

    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    TTSLocationCallback ttsLocationCallback;

    Boolean requestingLocationUpdates = false;
    MainHandler handler = new MainHandler();

    SearchingLocation searchingLocation;
    Boolean guidencing = false;
    ArrayList<Place>  mPlaces;

    @Override
    public void onMapViewInitialized(MapView mapView) {
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.537229,127.005515), 2, true);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {
        Log.i("key",	String.format("Open API Key Authentication Result : code=%d, message=%s", i, s));
    }


    class MainHandler extends Handler{
       @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String type = bundle.getString(MessageType.MESSAGE_TYPE);
            if(type == null){
                return;
            }
            switch(type){
                case MessageType.MESAGE_TYPE_SEARCH_PLACES:
                    String value = bundle.getString(MessageType.MESAGE_TYPE_SEARCH_PLACES);
                    searchLocation(value);
                   // textViewContents.setText(value);
                   // startRecord();
                    break;
                case MessageType.MESAGE_TYPE_SEARCH_RESULT:
                    if (mPlaces != null){
                        mPlaces.clear();
                    }
                    mPlaces = (ArrayList<Place>) bundle.getSerializable(MessageType.MESAGE_TYPE_SEARCH_RESULT);
                    foundPlaces(mPlaces);
                   // textViewContents.setText(places);
                   // startRecord();
                    break;
                case MessageType.MESAGE_TYPE_VOICE_RCOGNITION_START:
                    startRecord();
                    break;
                case MessageType.MESAGE_TYPE_TTS_START:
                    voiceRecognitionListener.setSpeeking(true);
                    stopRecord();
                    gifImageViewOn.setVisibility(View.INVISIBLE);
                    gifImageViewOff.setVisibility(View.VISIBLE);
                    break;
                case MessageType.MESAGE_TYPE_TTS_DONE:
                    voiceRecognitionListener.setSpeeking(false);
                    startRecord();
                    gifImageViewOn.setVisibility(View.VISIBLE);
                    gifImageViewOff.setVisibility(View.INVISIBLE);
                    break;
                case MessageType.MESAGE_TYPE_GUIDENCE_STOP:
                    if (guidencing == false) {
                        startRecord();
                        break;
                    }
                    stopRecord();
                    guidencing = false;
                    stopGuidPlace();
                    startRecord();
                    break;
                case MessageType.MESAGE_TYPE_GUIDENCE_START:
                    if(guidencing == true){
                        Message message = handler.obtainMessage(100);
                        handler.removeMessages(message.what);
                    }
                    if(mPlaces == null){
                        startRecord();
                        guidencing = false;
                        break;
                    }
                    String index = bundle.getString(MessageType.MESAGE_TYPE_GUIDENCE_START);
                    if(index == null){
                        guidencing = false;
                        return;
                    }
                    guidencing = true;
                    guidePlace(Integer.valueOf(index)-1);
                    break;
                case MessageType.MESAGE_TYPE_LOCATION_UPDATE:
                    double lat = bundle.getDouble(MessageType.MESAGE_TYPE_LOCATION_UPDATE_LATITUDE);
                    double lon = bundle.getDouble(MessageType.MESAGE_TYPE_LOCATION_UPDATE_LONGITUDE);
                    locationUpdate(lat,lon);
                    break;

            }

        }
    }

    private void locationUpdate(double lat, double lon) {
        if (centerCircle1 == null) {
            centerCircle1 = new MapCircle(
                    MapPoint.mapPointWithGeoCoord(lat, lon), // center
                    50, // radius
                    Color.argb(128, 255, 0, 0), // strokeColor
                    Color.argb(128, 0, 255, 0) // fillColor
            );
            centerCircle1.setTag(1234);
            mapView.addCircle(centerCircle1);
        } else {
            mapView.removeCircle(centerCircle1);
            centerCircle1.setCenter(MapPoint.mapPointWithGeoCoord(lat, lon));
            if (reverseColor) {
                centerCircle1.setFillColor(Color.argb(69, 0, 128, 0));
            } else {
                centerCircle1.setFillColor(Color.argb(128, 0, 255, 0));
            }
            mapView.addCircle(centerCircle1);
            reverseColor = !reverseColor;
        }
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat, lon), 2, true);
    }

    public void stopRecord() {
        speechRecognizer.stopListening();

    }
    private void createDefaultMarker(MapView mapView, Double lat, Double lon, String name) {
        MapPOIItem marker = new MapPOIItem();
        // String name = "Default Marker";
        marker.setItemName(name);
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(lat, lon));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.addPOIItem(marker);
        mapView.selectPOIItem(marker, true);
        // mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lon), false);
    }

    protected void foundPlaces(ArrayList<Place> places) {
        StringBuffer guidence = new StringBuffer();
        if (places.size() <= 0) {
           // speachOut("주변에 찾으시는 장소가 없습니다");
            return;
        }
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(ttsLocationCallback.getLatitude(), ttsLocationCallback.getLongitude()),2, true);
        mapView.removeAllPOIItems();


        for (int i = 0; i < places.size(); i++) {
            Place place = places.get(i);

            createDefaultMarker(mapView, place.getLat(), place.getLon(), place.getName());
            guidence.append(i+1)
                    .append("번 결과 ")
                    .append(place.getName())
                    .append("이 ")
                    .append(place.getDistance())
                    .append("미터 거리에 있습니다.\n\n");
        }


        speechOut(guidence.toString());
    }


    protected void guidePlace(int index){
        if(guidencing == false){
            return;
        }
        if (mPlaces.size() <=0 ){
            speechOut("주변에 찾으시는 장소가 없습니다 ");
            return;
        }
        stopRecord();

        voiceRecognitionListener.setSpeeking(true);

        MapPOIItem[] poiItems = mapView.getPOIItems();
        MapPOIItem poiItem = poiItems[index];
        mapView.selectPOIItem(poiItem, true);

        Place place = mPlaces.get(index);
        int distance1 = place.getDistance();

        place.calcDistance(ttsLocationCallback.getLatitude(), ttsLocationCallback.getLongitude());
        int distance2 = place.getDistance();
        if (distance2 < 20){
            guidencing = false;
            Message message = handler.obtainMessage(100);
            handler.removeMessages(message.what);
            speechOut("목적지가 10미터 안에 있습니다. 안내를 종료합니다");
            return;
        }
        distance1 = distance2;
        StringBuffer guidence = new StringBuffer();
        guidence.append(place.getName())
                .append("이")
                .append(place.getDistance())
                .append("미터 거리에 있습니다.");
        speechOut(guidence.toString());

        Message message = handler.obtainMessage(100);
        Bundle bundle = new Bundle();
        String strIndex = String.valueOf(index+1);

        bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_GUIDENCE_START);
        bundle.putString(MessageType.MESAGE_TYPE_GUIDENCE_START, strIndex);

        message.setData(bundle);
        handler.sendMessageDelayed(message, 10000);
    }

    private void stopGuidPlace() {
        Message message = handler.obtainMessage(100);
        handler.removeMessages(message.what);

        mPlaces = null;
        guidencing = false;
        stopRecord();
        speechOut("안내를 중지합니다");
    }

    public void searchLocation(String keyword){
        if (ttsLocationCallback != null) {
            searchingLocation.searchLocation(keyword, ttsLocationCallback.getLatitude(),ttsLocationCallback.getLongitude());
        }
    }


/*
    // 권한 여부 확인, 권환 없으면 요청하기 위한 코드
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
*/

    // 권한 체크
    private void permissionCheck() {
        // PermissionSupport.java 클래스 객체 생성
        permission = new PermissionSupport(this, this);
        // 권한 체크 후 리턴이 false로 들어오면
        if (!permission.checkPermission()){
            //권한 요청
            permission.requestPermission();
        }
    }

    // Request Permission에 대한 결과 값 받아와
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //여기서도 리턴이 false로 들어온다면 (사용자가 권한 허용 거부)
        if (!permission.permissionResult(requestCode, permissions, grantResults)) {
            // 다시 permission 요청
            permission.requestPermission();
        }
        //initVoiceRecognition();
       // initLocationClient();
        startRecord();
        position();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void initVoiceRecognition() {
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
//            return;
//        }

            //speechrecognizer instance 생성
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        //Lister instance 생성
        voiceRecognitionListener = new VoiceRecognitionListener(handler);
        //recognizer lister 등록
        speechRecognizer.setRecognitionListener(voiceRecognitionListener);
        //환경설정 위한 intent 설정
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
    }

    protected void initLocationClient() {
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
//            return;
//        }
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
//            return;
//        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create()
                .setInterval(100)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);
        ttsLocationCallback = new TTSLocationCallback(handler);
        position();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionCheck();

        initVoiceRecognition();
        initLocationClient();

        textViewContents = findViewById(R.id.textViewResult);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        gifImageViewOn= findViewById(R.id.GifImageView);
        gifImageViewOff = findViewById(R.id.GifImageView2);
        //권한 여부 확인하기위한 호출
       // checkPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION_CODE);
       // checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_PERMISSION_CODE);

        searchingLocation = new SearchingLocation(handler);

        //speech의 상태(시작, 에러, 종료)를 받는 lister class
        TTSUtteranceProgressListener ttsUtteranceProgressListener = new TTSUtteranceProgressListener(handler);

        textToSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
           // 인스턴스 생성과정에서 tts 초기화 환경설정
            @Override
            public void onInit(int status) {
                if ( status == TextToSpeech.SUCCESS){
                    int language = textToSpeech.setLanguage(Locale.KOREA);
                    textToSpeech.setOnUtteranceProgressListener(ttsUtteranceProgressListener);
                } else{
                    Toast.makeText(MainActivity.this, "TextToSpeech has not initialized", Toast.LENGTH_SHORT).show();

                }
            }
        });

        mapView = (MapView)findViewById(R.id.map_view);
        mapView.setDaumMapApiKey("9114e00becb7674c2b47796ad49bc72c");

        mapView.setMapViewEventListener(this);
        mapView.setOpenAPIKeyAuthenticationResultListener(this);
        mapView.setMapType(MapView.MapType.Standard);

        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
        mapView.setZoomLevel(7, true);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true);
        mapView.zoomIn(true);
        mapView.zoomOut(true);
        mapView.setShowCurrentLocationMarker(true);
        mapView.setCurrentLocationRadius(0);
        mapView.setDefaultCurrentLocationMarker();

        getHashKey();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                speechOut("찾으시는 장소를 말씀하세요");
            }
        }, 500);
    }
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
    public void position() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (requestingLocationUpdates == false) {
            fusedLocationClient.requestLocationUpdates(locationRequest, ttsLocationCallback, Looper.myLooper());
            requestingLocationUpdates  = true;
        } else {
            fusedLocationClient.removeLocationUpdates(ttsLocationCallback);
            requestingLocationUpdates  = false;
        }
    }

    //텍스트를 음성으로 출력하기 위한 함수
    protected void speechOut(String text){
        textViewContents.setText(text);
        //오디오 환경설정에서 볼륨정보 가져오기
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //max volume 출력 가능 가져오기
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //현재볼륨 가져오기
        int curVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        //최대로 출력 set volume
        am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

        if(Build.VERSION.SDK_INT >= 11){
            speakApi13(text);
        } else {
            Bundle params = new Bundle();
            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f);
            params.putString(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params, "thisNeedsToBeSet");
        }
        am.setStreamVolume(AudioManager.STREAM_MUSIC,curVolume,0);
    }
    public void startRecord(){
        speechRecognizer.startListening(speechRecognizerIntent);
        voiceRecognizing = true;
    }

    //텍스트에서 음성 출력 코드
    private void speakApi13(String text) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, "1.0");
        params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, params);
    }

    public void onClickVoiceRecognition(View view){
        //textview control UI instance 얻기 위한 코드
     ///   TextView textView  = (TextView) findViewById(R.id.textViewVoiceRecognition);
        //음성 인식 시아
        speechRecognizer.startListening(speechRecognizerIntent);


    }
    //입력한 텍스트 음성으로
    public void onClickTTS(View view){
      ///  EditText textView  = (EditText) findViewById(R.id.editTextTextPersonName_TTS);
      //  String text = textView.getText().toString();
     //   speechOut(text);

    }

}