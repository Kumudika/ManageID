package com.meetsid.userApp.Utils.ServerUtils;

import android.app.Activity;

public class ErrorResponseObject {
    int type;
    Class<? extends Activity> ActivityToOpen;
    String msgType;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Class<? extends Activity> getActivityToOpen() {
        return ActivityToOpen;
    }

    public void setActivityToOpen(Class<? extends Activity> activityToOpen) {
        ActivityToOpen = activityToOpen;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
