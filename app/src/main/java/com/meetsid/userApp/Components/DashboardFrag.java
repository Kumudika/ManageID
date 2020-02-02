package com.meetsid.userApp.Components;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.meetsid.userApp.Activities.DocumentUpload;
import com.meetsid.userApp.Models.Token;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardFrag extends Fragment {
    JSONObject user;
    JSONObject verifiedFilelds;
    @BindView(R.id.phyVerify)
    LinearLayout phyVerify;
    @BindView(R.id.addVerify)
    LinearLayout addVerify;
    @BindView(R.id.medal)
    ImageView medal;
    @BindView(R.id.status)
    TextView status;
    Token token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_dashboard, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDashboard();
    }

    public void setDashboard() {
        Gson gson = new Gson();
        SharedPreferences prf = Objects.requireNonNull(getContext()).getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
        String data = prf.getString(Common.tokenData, "");
        token = gson.fromJson(data, Token.class);
        int medalID = token.getVerificationMedal();
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.gold);
        switch (medalID) {
            case 0:
                DrawableCompat.setTint(unwrappedDrawable, Color.parseColor("#CD7F32"));
                medal.setBackground(unwrappedDrawable);
                status.setText("Bronze Status");
                status.setTextColor(Color.parseColor("#CD7F32"));
                break;
            case 1:
                DrawableCompat.setTint(unwrappedDrawable, Color.parseColor("#C0C0C0"));
                medal.setBackground(unwrappedDrawable);
                status.setText("Silver Status");
                status.setTextColor(Color.parseColor("#C0C0C0"));
                break;
            case 2:
                DrawableCompat.setTint(unwrappedDrawable, Color.parseColor("#FFD700"));
                medal.setBackground(unwrappedDrawable);
                status.setText("Gold Status");
                status.setTextColor(Color.parseColor("#FFD700"));
                break;
        }
    }

    public void primaryDocOnClick() {
        ArrayList<String> docs = new ArrayList<>();
        if (!verifiedFilelds.has("nic_location"))
            docs.add("Personal Identification Document");
        if (!verifiedFilelds.has("passport_location"))
            docs.add("Passport");
        docs.add("Birth Certificate");
        docs.add("Driving Licence");
        Intent intent = new Intent(getActivity(), DocumentUpload.class);
        intent.putExtra("docList", docs);
        startActivity(intent);
    }

    @OnClick(R.id.phyVerify)
    public void physicalVerificationOnClick() {
        HashMap<String, String> param = new HashMap<>();
        param.put("instid", "00001");
        ConnectServer.connect().getQRCode(param, getContext());
    }

    @OnClick(R.id.addVerify)
    public void addressVerify() {
    }
}
