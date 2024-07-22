package com.yoonah.bureyes;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchingLocation {
    private static final double MIN_LAT = 32.65319123021615;
    private static final double MAX_LAT = 39.3257110147935;
    private static final double MIN_LON = 123.5743975443522;
    private static final double MAX_LON = 130.48742322717598;

    private static final double DEFAULT_LAT = 37.56648210;
    private static final double DEFAULT_LON = 126.98502043;

    //SKT OpenAPI App Key
    private static final String SKT_APP_KEY = "l7xx0c77dab2786d4c14b8bbf5b2d6e274bc";

    private Handler mainHandler;

    public  SearchingLocation(Handler handler) {
        mainHandler = handler;
    }
    public void searchLocation(String keyword, double lat, double lon) {
        double cLat, cLon;
        if (!(MIN_LAT < lat && lat < MAX_LAT &&
                MIN_LON < lon && lon < MAX_LON)) {
            cLat = DEFAULT_LAT;
            cLon = DEFAULT_LON;
        } else {
            cLat = lat;
            cLon = lon;
        }
        Map<String, String> requestHeaders = new HashMap<>();
        StringBuffer urlBuffer = new StringBuffer("https://api2.sktelecom.com/tmap/pois");
        urlBuffer.append("?appKey=")
                .append(SKT_APP_KEY)
                .append("&searchKeyword=")
                .append(keyword)
                .append("&radius=1")
                .append("&searchtypCd=R")
                .append("&centerLon=")
                .append(cLon)
                .append("&centerLat=")
                .append(cLat)
                .append("&reqCoordType=WGS84GEO")
                .append("&format=json");
        new Thread() {
            @Override
            public void run() {
                String reqBody = get(urlBuffer.toString(), requestHeaders);
                JSONObject sktjsonObj = null;
                ArrayList<Place> places = new ArrayList<Place>();
                try {
                    sktjsonObj = new JSONObject(reqBody);
                    JSONObject searchPoiInfo = sktjsonObj.getJSONObject("searchPoiInfo");
                    JSONObject pois = searchPoiInfo.getJSONObject("pois");
                    JSONArray poiArray = pois.getJSONArray(("poi"));
                    int count = poiArray.length();
//                    if(count> 3){
//                        count = 3;
//                    }
                    String preId = "";

                    for(int i = 0; i < count; i++){
                      JSONObject poi = poiArray.getJSONObject(i);
                      String id = poi.getString("id");
                      if (id.compareTo(preId) == 0) {
                          continue;
                      }
                      preId = id;
                      String name = poi.getString("name");
                      Place place = new Place(name, poi.getDouble("frontLon"), poi.getDouble("frontLat"));
                      place.calcDistance(cLat,cLon);
                      places.add(place);
                      if (places.size() >= 3) {
                          break;
                      }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message message = mainHandler.obtainMessage();
                Bundle bundle = new Bundle();

                bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_SEARCH_RESULT);
                bundle.putSerializable( MessageType.MESAGE_TYPE_SEARCH_RESULT, places);

                message.setData(bundle);
                mainHandler.sendMessage(message);


            }
        }.start();

    }
    private static String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }
    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

}


