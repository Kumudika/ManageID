package com.meetsid.userApp.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

public class UserInfo extends HashMap {
    HashMap<String, String> userInfo;

    public UserInfo() {
        userInfo = new HashMap<>();
    }

    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.enableComplexMapKeySerialization().setPrettyPrinting().create();
        String userData = gson.toJson(userInfo);
        return userData;
    }
}
