package com.meetsid.userApp.Models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.meetsid.userApp.Activities.SignUp;
import com.meetsid.userApp.R;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] images = {R.drawable.slide1, R.drawable.slide2, R.drawable.slide3};
    int count = 5;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView sliderNum = (TextView) view.findViewById(R.id.sliderNum);
        TextView sliderTopic = (TextView) view.findViewById(R.id.sliderTopic);
        TextView sliderText = (TextView) view.findViewById(R.id.sliderText);
        Button startButton = (Button) view.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SignUp.class);
                context.startActivity(intent);
            }
        });
        switch (position) {
            case 0:
                sliderNum.setText("1");
                imageView.setImageResource(R.drawable.face_icon);
                sliderTopic.setText(R.string.slider_1_title);
                sliderText.setText(R.string.slider_1_text);
                startButton.setVisibility(View.INVISIBLE);
                break;
            case 1:
                sliderNum.setText("2");
                imageView.setImageResource(R.drawable.record_icon);
                sliderTopic.setText(R.string.slider_2_title);
                sliderText.setText(R.string.slider_2_text);
                startButton.setVisibility(View.INVISIBLE);
                break;
            case 2:
                sliderNum.setText("3");
                imageView.setImageResource(R.drawable.doc_icon);
                sliderTopic.setText(R.string.slider_3_title);
                sliderText.setText(R.string.slider_3_text);
                startButton.setVisibility(View.INVISIBLE);
                break;
            case 3:
                sliderNum.setText("4");
                imageView.setImageResource(R.drawable.doc_scan);
                sliderTopic.setText(R.string.slider_4_title);
                sliderText.setText(R.string.slider_4_text);
                startButton.setVisibility(View.INVISIBLE);
                break;
            case 4:
                sliderNum.setText("5");
                imageView.setImageResource(R.drawable.card_icon);
                sliderTopic.setText(R.string.slider_5_title);
                sliderText.setText(R.string.slider_5_text);
                startButton.setVisibility(View.VISIBLE);
                break;
        }
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(position == 0){
//                    Toast.makeText(context, "Slide 1 Clicked", Toast.LENGTH_SHORT).show();
//                } else if(position == 1){
//                    Toast.makeText(context, "Slide 2 Clicked", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "Slide 3 Clicked", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        view.invalidate();
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
