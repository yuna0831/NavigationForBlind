package com.yoonah.bureyes;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class TTSLocationCallback extends LocationCallback {
    private Handler mainHandler;
    private Double latitude;
    private Double longitude;

    public TTSLocationCallback(Handler handler){
     mainHandler = handler;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        for (Location location : locationResult.getLocations()) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        Message message =  mainHandler.obtainMessage();
        Bundle bundle = new Bundle();

        bundle.putString(MessageType.MESSAGE_TYPE, MessageType.MESAGE_TYPE_LOCATION_UPDATE);
        bundle.putDouble(MessageType.MESAGE_TYPE_LOCATION_UPDATE_LATITUDE, latitude);
        bundle.putDouble(MessageType.MESAGE_TYPE_LOCATION_UPDATE_LONGITUDE, longitude);
        message.setData(bundle);
        mainHandler.sendMessage(message);
    }
}
