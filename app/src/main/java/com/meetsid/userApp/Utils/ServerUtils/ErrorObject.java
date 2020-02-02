package com.meetsid.userApp.Utils.ServerUtils;

import com.meetsid.userApp.Utils.Common;

import java.util.HashMap;

public class ErrorObject {
    private String endpoint;
    private String side;
    private String code;
    private String title;
    private String description;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static void addErrorObject(String code, ErrorObject errorObject) {
        if (Common.errorObjects == null)
            Common.errorObjects = new HashMap<>();
        Common.errorObjects.put(code, errorObject);
    }

    public static ErrorObject getErrorObject(String code) {
        if (Common.errorObjects.containsKey(code)) {
            return Common.errorObjects.get(code);
        }
        return null;
    }
}
