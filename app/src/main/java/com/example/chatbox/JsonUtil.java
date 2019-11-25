package com.example.chatbox;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.ponnamkarthik.richlinkpreview.MetaData;

public class JsonUtil {

    public static String toJson(MetaData metaData){

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url", metaData.getUrl());
            jsonObject.put("imageurl", metaData.getImageurl());
            jsonObject.put("title", metaData.getTitle());
            jsonObject.put("description", metaData.getDescription());
            jsonObject.put("sitename", metaData.getSitename());
            jsonObject.put("mediatype", metaData.getMediatype());
            jsonObject.put("favicon", metaData.getFavicon());
            return jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    return null;

    }


}
