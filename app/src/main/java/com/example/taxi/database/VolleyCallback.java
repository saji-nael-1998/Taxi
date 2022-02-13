package com.example.taxi.database;

import android.graphics.Bitmap;

import org.json.JSONObject;

public interface VolleyCallback {
    public void onSuccess(JSONObject result) throws Exception;
    public void onSuccessImage(Bitmap bitmap) throws Exception;
    public void onError(String result);
}
