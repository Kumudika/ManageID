package com.meetsid.userApp.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.meetsid.userApp.BuildConfig;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.Country;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;
import com.meetsid.userApp.Utils.ServerUtils.ErrorObject;
import com.meetsid.userApp.Utils.Validation.TextValidator;
import com.meetsid.userApp.Utils.Verification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

public class SignUp extends AppCompatActivity implements WheelPicker.OnItemSelectedListener {
    public static boolean success = false;

    @BindView(R.id.wheelPicker)
    WheelPicker wheelPicker;
    @BindView(R.id.backBtn)
    ImageButton backBtn;
    @BindView(R.id.mailText)
    AppCompatEditText mailText;
    @BindView(R.id.countryTxt)
    EditText countryTxt;
    @BindView(R.id.mobileText)
    TextInputEditText mobileText;
    @BindView(R.id.mobileInput)
    TextInputLayout mobileInput;
    @BindView(R.id.mailInput)
    TextInputLayout mailInput;
    @BindView(R.id.bottomLayer)
    ConstraintLayout bottomLayer;
    @BindView(R.id.countryLayout)
    LinearLayout countryLayout;
    String country;
    boolean isEnabled = true;
    String mobileTag = "+94";
    JSONArray m_jArry;
    Context context;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        ButterKnife.bind(this);
        context = this;
        init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Typeface typeFaceMetro = getResources().getFont(R.font.metropolis_extralight);
            Typeface typeFaceMetroRegular = getResources().getFont(R.font.metropolis_regular);
            mailText.setTypeface(typeFaceMetroRegular);
            wheelPicker.setTypeface(typeFaceMetro);
        }
    }

    private void init() {
        setCountryDropDown();
        mailText.addTextChangedListener(new TextValidator(mailText) {
            @Override
            public void validate(EditText textView, String text) {
                String msg = Verification.verifyEmail(mailText.getText().toString());
                if (msg != null) {
                    mailInput.setError(msg);
                    isEnabled = false;
                } else {
                    mailInput.setError(null);
                    isEnabled = true;
                }
            }
        });
        mobileText.addTextChangedListener(new TextValidator(mobileText) {
            @Override
            public void validate(EditText textView, String text) {
                String m1 = Verification.isFielfNullOrEmpty(text);
                if (m1 != null) {
                    mobileInput.setError(null);
                    mobileText.setText(mobileTag);
                } else {
                    if (text.length() >= 12 && text.length() < 16) {
                        String msg = validateMobile(text);
                        int l = mobileTag.length();
                        if (mobileText.getText().length() == 0 || mobileText.getText().length() == l) {
                            mobileText.setText(mobileTag);
                        } else if (msg != null) {
                            mobileInput.setError(msg);
                        }
                    }
                }
            }
        });
//        mobileText.setOnFocusChangeListener((view, b) -> {
//            String mbile = mobileText.getText().toString();
//            String m1 = Verification.isFielfNullOrEmpty(mbile);
//            if (b) {
//                if (m1 == null) {
//                    mobileInput.setError(null);
//                    mobileText.setText(mobileTag);
//                }
//            } else {
//                if (m1 == null) {
//                    String msg = validateMobile(mobileText.getText().toString());
//                    int l = mobileTag.length();
//                    if (mobileText.getText().length() == 0 || mobileText.getText().length() == l) {
//                        mobileText.setText(mobileTag);
//                    } else if(msg != null) {
//                        mobileInput.setError(msg);
//                    }
//                } else {
//                    mobileInput.setError(m1);
//                }
//            }
//        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mobileText.addTextChangedListener(new PhoneNumberFormattingTextWatcher(mobileTag));
        }
    }

    private String validateMobile(String number) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String msg = null;
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, null);
            if (!phoneUtil.isValidNumber(phoneNumber)) {
                ErrorObject errorObject = Common.errorObjects.get("INVALID_MOBILE");
                msg = errorObject != null ? errorObject.getDescription() : "Please enter a valid mobile number.";
            }
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
            ErrorObject errorObject = Common.errorObjects.get("INVALID_MOBILE");
            msg = errorObject != null ? errorObject.getDescription() : "Please enter a valid mobile number.";
        }
        return msg;
    }

    private void setCountryDropDown() {
        JSONObject obj = null;
        ArrayList<String> countries = new ArrayList<>();
        try {
            obj = new JSONObject(Objects.requireNonNull(Common.loadJSONFromAsset(R.raw.country_code, this)));
            m_jArry = obj.getJSONArray("countries");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String countryName = jo_inside.getString("name");
                countries.add(countryName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(countries);
        wheelPicker.setCyclic(false);
        wheelPicker.setCurved(true);
        wheelPicker.setSelectedItemTextColor(Color.BLACK);
        wheelPicker.setVisibleItemCount(6);
        wheelPicker.setFocusable(true);
        wheelPicker.setSoundEffectsEnabled(true);
        countryTxt.setInputType(InputType.TYPE_NULL);
        countryTxt.setText("Sri Lanka");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            countryTxt.setShowSoftInputOnFocus(false);
        }
        country = "Sri Lanka";

//        Locale[] locales = Locale.getAvailableLocales();
////        ArrayList<String> countries = new ArrayList<>();
////        for (Locale locale : locales) {
////            String country = locale.getDisplayCountry();
////            if (country.trim().length() > 0 && !countries.contains(country)) {
////                countries.add(country);
////            }
////        }

//        Collections.sort(countries);
        // Apply the adapter to the your spinner
        wheelPicker.setData(countries);
        wheelPicker.setSelectedItemPosition(6);
        wheelPicker.setOnItemSelectedListener(this);
        Context context = this;
        countryLayout.setVisibility(View.INVISIBLE);
        bottomLayer.setVisibility(View.VISIBLE);
        if (BuildConfig.FLAVOR.equalsIgnoreCase("electionApp")) {
            ImageView poweredByIcon = new ImageView(context);
            poweredByIcon.setImageResource(R.drawable.colorpowered);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, 65);
            params.bottomMargin = 20;
            params.bottomToBottom = R.id.bottomLayer;
            params.leftToLeft = R.id.bottomLayer;
            params.rightToRight = R.id.bottomLayer;
            poweredByIcon.setLayoutParams(params);
            bottomLayer.addView(poweredByIcon);
        }
        TelephonyManager teleMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (teleMgr != null) {
            String country = teleMgr.getNetworkCountryIso();
            country = teleMgr.getSimCountryIso();
            Locale l = new Locale("", country);
            countryTxt.setText(l.getDisplayName());
            wheelPicker.setSelectedItemPosition(countries.indexOf(l.getDisplayName()));
            mobileTag = getMobileTag(l.getDisplayName());
            mobileText.setText(mobileTag);
        }
    }

    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Geocoder geocoder = new Geocoder(getApplicationContext());
        String country_name = null;
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    country_name = addresses.get(0).getCountryName();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getApplicationContext(), country_name, Toast.LENGTH_LONG).show();
//        String country = this.getResources().getConfiguration().locale.getCountry();
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
//                makeUseOfNewLocation(location);
                String cntry = null;
                if (location != null) {
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses != null && addresses.size() > 0) {
                            cntry = addresses.get(0).getCountryName();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(cntry);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private String getCode(String country) {
        try {
            if (m_jArry != null) {
                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject jo_inside = m_jArry.getJSONObject(i);
                    Log.d("Details-->", jo_inside.getString("name"));
                    String countryName = jo_inside.getString("name");
                    String countryCode = jo_inside.getString("code");
                    if (countryName.equalsIgnoreCase(country)) {
                        return countryCode;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getMobileTag(String country) {
        try {
            if (m_jArry != null) {
                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject jo_inside = m_jArry.getJSONObject(i);
                    Log.d("Details-->", jo_inside.getString("name"));
                    String countryName = jo_inside.getString("name");
                    String mobileCode = jo_inside.getString("dial_code");
                    if (countryName.equalsIgnoreCase(country)) {
                        return mobileCode;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @OnClick(R.id.backBtn)
    public void goBack() {
        this.finish();
        super.onBackPressed();
    }

    private void parseJSON() {
        Gson gson = new Gson();
        String reader;
        try {
            reader = new JsonReader(new FileReader("country.json")).toString();
            Type type = new TypeToken<List<Country>>() {
            }.getType();
            List<Country> contactList = gson.fromJson(reader, type);
            for (Country contact : contactList) {
                Log.i("Contact Details", contact.name + "-" + contact.alpha_3 + "-" + contact.country_code);
            }
        } catch (FileNotFoundException ex) {
            ex.getStackTrace();
        }
    }

    @OnTouch(R.id.countryTxt)
    public boolean setCountryOnTouch() {
        Common.hideSoftKeyboard(this);
        if (countryLayout.getVisibility() == View.INVISIBLE) {
            countryTxt.setFocusable(true);
            countryLayout.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @OnClick(R.id.countryTxt)
    public void setCountry() {
        Common.hideSoftKeyboard(this);
        if (countryLayout.getVisibility() == View.INVISIBLE) {
            countryTxt.setFocusable(true);
            countryLayout.setVisibility(View.VISIBLE);
            wheelPicker.setFocusable(true);
        }
    }

    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        if (picker.getId() == R.id.wheelPicker) {
            countryTxt.setText(data.toString());
//            country = data.toString();
            country = getCode(data.toString());
            mobileTag = getMobileTag(data.toString());
            mobileText.setText(mobileTag);
            wheelPicker.setSelectedItemPosition(position);
//            mobileText.setPrefix(mobileTag);
        }
    }

    @OnClick({R.id.btnCancel, R.id.btnOk})
    public void okClick() {
        countryLayout.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.signupBtn)
    public void onSubmit(View view) {
        if (fieldValidation()) {
            String email = mailText.getText().toString();
            String mobile = mobileText.getText().toString();
            signUp(email, country, mobile);
        }
    }

    private boolean fieldValidation() {
        String email = mailText.getText().toString();
        String mobile = mobileText.getText().toString();
        String country = countryTxt.getText().toString();
        if (!Verification.isNullOrEmpty(email) && !Verification.isNullOrEmpty(mobile) && !Verification.isNullOrEmpty(country)) {
            String m1 = Verification.verifyEmail(email);
            String m2 = validateMobile(mobile);
            if (m1 == null && validateMobile(mobile) == null)
                return true;
            else if (m1 != null) {
                mailInput.setError(m1);
                return false;
            } else if (m2 != null) {
                mobileInput.setError(m2);
                return false;
            }
        } else {
            String m1 = Verification.isFielfNullOrEmpty(email);
            String m2 = Verification.isFielfNullOrEmpty(mobile);
            String m3 = Verification.isFielfNullOrEmpty(country);
            if (m1 != null) {
                mailInput.setError(m1);
                return false;
            } else if (m2 != null) {
                mobileInput.setError(m2);
                return false;
            } else if (m3 != null) {
                countryTxt.setText("Sri Lanka");
                return false;
            }
        }
        return false;
    }

    /**
     * @param mail
     * @param country
     */
    public void signUp(String mail, String country, String mobile) {
        HashMap<String, String> param = new HashMap<>();
        param.put("country", country);
        param.put("username", mail);
        param.put("mobileNo", mobile);
        SharedPreferences preferences = getSharedPreferences(Common.mobile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Common.mobile, mobileText.getText().toString());
        editor.commit();
        ConnectServer.connect().create(param, this);
    }

    @OnTextChanged({R.id.mailText, R.id.mobileText})
    public void setMailError() {
        if (Verification.verifyEmail(mailText.getText().toString()) == null) {
            mailInput.setError(null);
        }
        if (validateMobile(mobileText.getText().toString()) == null) {
            mobileInput.setError(null);
        }
    }

    private static String getCountryBasedOnSimCardOrNetwork(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
}
