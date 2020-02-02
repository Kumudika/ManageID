package com.meetsid.userApp.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.meetsid.userApp.R;

public class TopicLayout extends FrameLayout {
    public TopicLayout(@NonNull Context context) {
        super(context);
//        View view =  LayoutInflater.from(getContext()).inflate(
////                R.layout.topic_layout, null);
////        this.addView(view);

//        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.topic_layout, this);
    }

    public void init(LayoutParams layoutParams) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.topic_layout, this);
        setLayoutParams(layoutParams);
    }
}
