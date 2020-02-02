package com.meetsid.userApp.Components;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.meetsid.userApp.Activities.DocCapturePage;
import com.meetsid.userApp.Activities.MultiDocumentUpload;
import com.meetsid.userApp.Activities.PassportScanner;
import com.meetsid.userApp.Models.Token;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFrag extends Fragment {
    JSONObject user;
    @BindView(R.id.mail)
    TextView mail;
    @BindView(R.id.mobileNo)
    TextView mobileNo;
    @BindView(R.id.kycId)
    TextView kycId;
    //    @BindView(R.id.country)
//    TextView country;
    @BindView(R.id.listView)
    LinearLayout listView;
    //    @BindView(R.id.passportTick)
//    ImageView passportTick;
//    @BindView(R.id.nicTick)
//    ImageView nicTick;
//    @BindView(R.id.bcTick)
//    ImageView bcTick;
//    @BindView(R.id.driveLicenTick)
//    ImageView driveLicenTick;
//    @BindView(R.id.passport)
//    LinearLayout passport;
//    @BindView(R.id.nic)
//    LinearLayout nic;
//    @BindView(R.id.bct)
//    LinearLayout bct;
//    @BindView(R.id.drvLicense)
    LinearLayout drvLicense;
    boolean nicTaken = false;
    boolean passportTaken = false;
    boolean DLTaken = false;
    boolean newNicTaken = false;
    Token token;
    Typeface textViewFont;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_profile, null);
        ButterKnife.bind(this, view);
        textViewFont = ResourcesCompat.getFont(getContext(), R.font.metropolis_regular);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setProfile();
    }

    public void setProfile() {
        SharedPreferences prf = getContext().getSharedPreferences(Common.username, Context.MODE_PRIVATE);
        String username = prf.getString(Common.username, null);
        Gson gson = new Gson();
        prf = getContext().getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
        String data = prf.getString(Common.tokenData, "");
        token = gson.fromJson(data, Token.class);
        prf = getContext().getSharedPreferences(Common.mobile, Context.MODE_PRIVATE);
        String mobile = prf.getString(Common.mobile, null);
        mail.setText(username);
        kycId.setText(token.getTokenId());
        mobileNo.setText(mobile);
//        country.setText(Common.country);
        setDocList();
    }

    private void setDocList() {
        JSONObject obj = null;
        JSONArray m_jArry = null;
        try {
            obj = new JSONObject(Objects.requireNonNull(Common.loadJSONFromAsset(R.raw.country_docs, getContext())));
            m_jArry = obj.getJSONArray("countries");
            String[] docList = null;
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String countryName = jo_inside.getString("name");
                if (countryName.equalsIgnoreCase(Common.country)) {
                    JSONObject object = jo_inside.getJSONObject("docs");
                    String docs = object.getString("name");
                    docList = docs.split(",");
                    break;
                }
            }
            setDocItems(docList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDocItems(String[] docsList) {
        View view1 = new View(getContext());
        LinearLayout.LayoutParams viewPara1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 4);
        view1.setLayoutParams(viewPara1);
        view1.setBackgroundColor(getResources().getColor(R.color.meetsid_light_grey));
        listView.addView(view1);
        if (docsList != null) {
            for (int i = 0; i < docsList.length; i++) {
                LinearLayout.LayoutParams lprams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout docLayout = new LinearLayout(getContext());
                docLayout.setLayoutParams(lprams);
                docLayout.setPadding(50, 50, 50, 50);
                docLayout.setOrientation(LinearLayout.HORIZONTAL);
                ImageView imageView = new ImageView(getContext());
                LinearLayout.LayoutParams imagePara = new LinearLayout.LayoutParams(50, 50);
                imagePara.rightMargin = 20;
                imagePara.gravity = Gravity.CENTER_VERTICAL;
                imageView.setLayoutParams(imagePara);
                imageView.setImageResource(R.drawable.unselected);
                docLayout.addView(imageView);
                TextView textView = new TextView(getContext());
                LinearLayout.LayoutParams textPara = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textPara.gravity = Gravity.CENTER_VERTICAL;
                textView.setLayoutParams(textPara);
                textView.setText(docsList[i]);
                textView.setTextAppearance(getContext(), R.style.defaultTextStyle);
                textView.setTypeface(textViewFont);
//                Typeface face = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    face = getResources().getFont(R.font.metropolis_extralight);
//                    textView.setTypeface(face);
//                }
                docLayout.addView(textView);
                listView.addView(docLayout);

                View view = new View(getContext());
                LinearLayout.LayoutParams viewPara = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 4);
                view.setLayoutParams(viewPara);
                view.setBackgroundColor(getResources().getColor(R.color.meetsid_light_grey));
                listView.addView(view);

                switch (docsList[i]) {
                    case "Passport":
                        if (token.getPassportTokenId() != null) {
                            passportTaken = true;
                            imageView.setImageResource(R.drawable.done);
                        }
                        docLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (passportTaken) {
                                    HashMap<String, String> para = new HashMap<>();
                                    para.put("walletid", token.getTokenId());
                                    para.put("secretshare", token.getTokenShare());
                                    para.put("tokenshare", token.getPassportTokenShare());
                                    para.put("payload", "{}");
                                    ConnectServer.connect().docRetrieve(para, getActivity(), "passport");
                                } else {
                                    Intent intent = new Intent(getActivity(), PassportScanner.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                    case "National Identity Card":
                        if (token.getNicTokenId() != null) {
                            nicTaken = true;
                            imageView.setImageResource(R.drawable.done);
                        }
                        docLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (nicTaken) {
                                    HashMap<String, String> para = new HashMap<>();
                                    para.put("walletid", token.getTokenId());
                                    para.put("secretshare", token.getTokenShare());
                                    para.put("tokenshare", token.getNicTokenShare());
                                    para.put("payload", "{}");
                                    ConnectServer.connect().docRetrieve(para, getActivity(), "nic");
                                } else {
                                    Intent intent = new Intent(getActivity(), MultiDocumentUpload.class);
                                    intent.putExtra("type", "n_nic_front");
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                    case "Driver’s License":
                        if (token.getDLTokenId() != null) {
                            DLTaken = true;
                            imageView.setImageResource(R.drawable.done);
                        }
                        docLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (DLTaken) {
                                    HashMap<String, String> para = new HashMap<>();
                                    para.put("walletid", token.getTokenId());
                                    para.put("secretshare", token.getTokenShare());
                                    para.put("tokenshare", token.getDLTokenShare());
                                    para.put("payload", "{}");
                                    ConnectServer.connect().docRetrieve(para, getActivity(), "license");
                                } else {
                                    Intent intent = new Intent(getActivity(), MultiDocumentUpload.class);
                                    intent.putExtra("type", "l_front");
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                    case "New National Identity Card":
                        if (token.getNewNicTokenId() != null) {
                            newNicTaken = true;
                            imageView.setImageResource(R.drawable.done);
                        }
                        docLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (newNicTaken) {
                                    HashMap<String, String> para = new HashMap<>();
                                    para.put("walletid", token.getTokenId());
                                    para.put("secretshare", token.getTokenShare());
                                    para.put("tokenshare", token.getNewNicTokenShare());
                                    para.put("payload", "{}");
                                    ConnectServer.connect().docRetrieve(para, getActivity(), "newNic");
                                } else {
                                    Intent intent = new Intent(getActivity(), MultiDocumentUpload.class);
                                    intent.putExtra("type", "n_nic_front");
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                }
            }
        }
    }

    public void viewPassport() {
        if (passportTaken) {
//            SharedPreferences prf = getContext().getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
//            Gson gson = new Gson();
//            String data = prf.getString(Common.tokenData, "");
//            Token token = gson.fromJson(data, Token.class);
            HashMap<String, String> para = new HashMap<>();
            para.put("walletid", token.getTokenId());
            para.put("secretshare", token.getTokenShare());
            para.put("tokenshare", token.getPassportTokenShare());
            para.put("payload", "{}");
            ConnectServer.connect().docRetrieve(para, getActivity(), "passport");
        } else {
            Intent intent = new Intent(getActivity(), PassportScanner.class);
            startActivity(intent);
        }
    }

    public void viewNic() {
        if (nicTaken) {
            HashMap<String, String> para = new HashMap<>();
            para.put("walletid", token.getTokenId());
            para.put("secretshare", token.getTokenShare());
            para.put("tokenshare", token.getNicTokenShare());
            para.put("payload", "{}");
            ConnectServer.connect().docRetrieve(para, getActivity(), "nic");
        } else {
            Intent intent = new Intent(getActivity(), DocCapturePage.class);
            intent.putExtra("type", "nicFront");
            startActivity(intent);
        }
    }

    public void viewDL() {
        if (DLTaken) {
            HashMap<String, String> para = new HashMap<>();
            para.put("walletid", token.getTokenId());
            para.put("secretshare", token.getTokenShare());
            para.put("tokenshare", token.getDLTokenShare());
            para.put("payload", "{}");
            ConnectServer.connect().docRetrieve(para, getActivity(), "license");
        } else {
            Intent intent = new Intent(getActivity(), MultiDocumentUpload.class);
            intent.putExtra("type", "l_front");
            startActivity(intent);
        }
    }
}
