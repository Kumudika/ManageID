package com.meetsid.userApp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;
import com.meetsid.userApp.Utils.Validation.TextValidator;
import com.meetsid.userApp.Utils.Verification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PassportDetails extends AppCompatActivity implements WheelPicker.OnItemSelectedListener {
    @BindView(R.id.imageLayout)
    LinearLayout imageLayout;
    @BindView(R.id.infoView)
    LinearLayout infoView;
    @BindView(R.id.countryLayout)
    LinearLayout countryLayout;
    @BindView(R.id.dateLayout)
    LinearLayout dateLayout;
    @BindView(R.id.sexLayout)
    LinearLayout sexLayout;
    @BindView(R.id.btnCancel)
    TextView btnCancel;
    @BindView(R.id.btnOk)
    TextView btnOk;
    @BindView(R.id.countryPicker)
    WheelPicker countryPicker;
    @BindView(R.id.datePicker)
    DatePicker datePicker;
    //    @BindView(R.id.yearInput)
//    WheelYearPicker yearInput;
//    @BindView(R.id.monthInput)
//    WheelPicker monthInput;
//    @BindView(R.id.dayInput)
//    WheelDayPicker dayInput;
    @BindView(R.id.sexPicker)
    WheelPicker sexPicker;
    @BindView(R.id.btnContinue)
    Button btnContinue;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    LinkedHashMap<String, String> formData;
    TextInputEditText issueCountry;
    TextInputEditText nationality;
    TextInputEditText passportNo;
    TextInputEditText dob;
    TextInputEditText expireDate;
    TextInputEditText nic;
    TextInputEditText lastName;
    TextInputEditText firstName;
    TextInputEditText sex;
    TextInputLayout issueCountryInput;
    TextInputLayout nationalityInput;
    TextInputLayout passportNoInput;
    TextInputLayout dobInput;
    TextInputLayout expireInput;
    TextInputLayout nicInput;
    TextInputLayout lNameInput;
    TextInputLayout fNameInput;
    TextInputLayout sexInput;
    String y;
    String m;
    String d;
    String type;
    String clickField;
    JSONArray m_jArry;
    ArrayList<String> gender;
    ArrayList<String> months;
    boolean isValidate = true;
    Typeface font;
    int x = 0;
    boolean isDifferent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_passport_details);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        formData = Common.dataList;
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        if (picker.getId() == R.id.countryPicker) {
            if (clickField.equals("nationality"))
                nationality.setText(data.toString());
            else if (clickField.equals("issueCountry"))
                issueCountry.setText(data.toString());
        }
//        else if (picker.getId() == R.id.yearInput) {
////            year = data.toString();
////        } else if(picker.getId() == R.id.monthInput) {
////            month = String.valueOf((position + 1));
////        } else if(picker.getId() == R.id.dayInput) {
////            day = data.toString();
////        }
        else if (picker.getId() == R.id.sexPicker) {
            if (clickField.equals("sex"))
                sex.setText(data.toString());
        }
    }

    private void init() {
        int index = 0;
        countryLayout.setVisibility(View.INVISIBLE);
        dateLayout.setVisibility(View.INVISIBLE);
        sexLayout.setVisibility(View.INVISIBLE);
        font = ResourcesCompat.getFont(this, R.font.metropolis_regular);
        if (type.equals("passport")) {
            ImageView passportFront = new ImageView(this);
            passportFront.setAdjustViewBounds(true);
            passportFront.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (PassportScanner.image == null)
                passportFront.setImageResource(R.drawable.nic_icon);
            else
                passportFront.setImageBitmap(PassportScanner.image);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    passportFront.setForeground(getDrawable(R.drawable.image_boarder));
                }
            }
            LinearLayout.LayoutParams frontPara = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    300);
            passportFront.setLayoutParams(frontPara);
            imageLayout.addView(passportFront);
        } else if (type.equals("nic")) {
            ImageView nicFront = new ImageView(this);
            nicFront.setAdjustViewBounds(true);
            nicFront.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (NicScanner.image == null)
                nicFront.setImageResource(R.drawable.nic_icon);
            else
                nicFront.setImageBitmap(NicScanner.image);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    nicFront.setForeground(getDrawable(R.drawable.image_boarder));
                }
            }
            LinearLayout.LayoutParams frontPara = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    300);
            frontPara.rightMargin = 40;
            nicFront.setLayoutParams(frontPara);

            ImageView nicBack = new ImageView(this);
            nicBack.setAdjustViewBounds(true);
            nicBack.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (NicBackScanner.bitmap == null)
                nicBack.setImageResource(R.drawable.nic_back_icon);
            else
                nicBack.setImageBitmap(NicBackScanner.bitmap);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    nicBack.setForeground(getDrawable(R.drawable.image_boarder));
                }
            }
            LinearLayout.LayoutParams backPara = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    300);
            nicBack.setLayoutParams(backPara);
            imageLayout.addView(nicFront, 0);
            imageLayout.addView(nicBack, 1);
        }
        if (formData != null) {
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                addDataField(key, value, true, index);
                index++;
            }
        }
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
        countryPicker.setCyclic(false);
        countryPicker.setCurved(true);
        countryPicker.setSelectedItemTextColor(Color.BLACK);
        countryPicker.setVisibleItemCount(6);
        countryPicker.setFocusable(true);
        countryPicker.setSoundEffectsEnabled(true);
        countryPicker.setData(countries);
        countryPicker.setSelectedItemPosition(6);
        countryPicker.setOnItemSelectedListener(this);

        sexPicker.setCurved(true);
        sexPicker.setSelectedItemTextColor(Color.BLACK);
        sexPicker.setVisibleItemCount(3);
        sexPicker.setSelectedItemPosition(2);
        sexPicker.setFocusable(true);
        sexPicker.setSoundEffectsEnabled(true);
        sexPicker.setOnItemSelectedListener(this);
        gender = new ArrayList<String>();
        gender.add("Female");
        gender.add("Male");
        gender.add("Other");
        sexPicker.setData(gender);
        months = new ArrayList<String>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
        datePicker.init(2000, 1, 1, new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                if (x == 0 && month != Integer.parseInt(m))
                    isDifferent = true;
                x = 1;
                if (isDifferent)
                    m = String.valueOf(month + 1);
                else
                    m = String.valueOf(month);
                y = String.valueOf(year);
                d = String.valueOf(dayOfMonth);
            }
        });
        infoView.requestFocus();
    }

    private void addDataField(String hint, String value, boolean isEnable, int index) {
        TextInputEditText editText = new TextInputEditText(this);
        editText.setText(value);
        editText.setId(index);
        editText.setEnabled(isEnable);
        editText.setTypeface(font);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setTypeface(font);
        if (type.equals("passport")) {
            switch (hint) {
                case "Nationality":
                    nationality = editText;
                    nationalityInput = textInputLayout;
                    nationality.setInputType(InputType.TYPE_NULL);
                    nationality.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickField = "nationality";
                            countryLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                case "Issue Country":
                    issueCountry = editText;
                    issueCountryInput = textInputLayout;
                    issueCountry.setInputType(InputType.TYPE_NULL);
                    issueCountry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickField = "issueCountry";
                            countryLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                case "Date of Birth":
                    dob = editText;
                    dobInput = textInputLayout;
                    dob.setInputType(InputType.TYPE_NULL);
                    dob.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickField = "dob";
                            String[] dd = dob.getText().toString().split("/");
                            d = dd[0];
                            m = dd[1];
                            y = dd[2];
                            datePicker.updateDate(Integer.parseInt(y), Integer.parseInt(m), Integer.parseInt(d));
                            dateLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    dob.addTextChangedListener(new TextValidator(dob) {
                        @Override
                        public void validate(EditText textView, String text) {
                            String validDob = Verification.vaidateNIC_DOB(nic.getText().toString(), dob.getText().toString());
                            if (validDob != null) {
                                dobInput.setError(validDob);
                                isValidate = false;
                            } else {
                                dobInput.setError(null);
                                isValidate = true;
                            }
                        }
                    });
                    break;
                case "Expire Date":
                    expireDate = editText;
                    expireInput = textInputLayout;
                    expireDate.setInputType(InputType.TYPE_NULL);
                    expireDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickField = "expireDate";
                            String[] dd = expireDate.getText().toString().split("/");
                            d = dd[0];
                            m = dd[1];
                            y = dd[2];
                            datePicker.updateDate(Integer.parseInt(y), Integer.parseInt(m), Integer.parseInt(d));
                            dateLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                case "Sex":
                    sex = editText;
                    sexInput = textInputLayout;
                    sex.setInputType(InputType.TYPE_NULL);
                    sex.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickField = "sex";
                            sexLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    sex.addTextChangedListener(new TextValidator(sex) {
                        @Override
                        public void validate(EditText textView, String text) {
                            String validGender = Verification.validateNIC_SEX(nic.getText().toString(), sex.getText().toString());
                            if (validGender != null) {
                                sexInput.setError(validGender);
                                isValidate = false;
                            } else {
                                sexInput.setError(null);
                                isValidate = true;
                            }
                        }
                    });
                    break;
                case "NIC":
                    nic = editText;
                    nicInput = textInputLayout;
                    nic.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    nic.addTextChangedListener(new TextValidator(nic) {
                        @Override
                        public void validate(EditText textView, String text) {
                            String msg = Verification.validateNIC(nic.getText().toString());
                            if (msg != null) {
                                nicInput.setError(msg);
                                isValidate = false;
                            } else {
                                nicInput.setError(null);
                                isValidate = true;
                            }
                        }
                    });
                    break;
                case "Passport No":
                    passportNo = editText;
                    passportNoInput = textInputLayout;
                    passportNo.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    passportNo.addTextChangedListener(new TextValidator(passportNo) {
                        @Override
                        public void validate(EditText textView, String text) {
                            String msg = Verification.validatePassportNumber(passportNo.getText().toString(), issueCountry.getText().toString());
                            if (msg != null) {
                                passportNoInput.setError(msg);
                                isValidate = false;
                            } else {
                                passportNoInput.setError(null);
                                isValidate = true;
                            }
                        }
                    });
                    break;
                case "Last Name":
                    lastName = editText;
                    lNameInput = textInputLayout;
                    lastName.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    lastName.addTextChangedListener(new TextValidator(lastName) {
                        @Override
                        public void validate(EditText textView, String text) {
                            String msg = Verification.isValidName(lastName.getText().toString());
                            if (msg != null) {
                                lNameInput.setError(msg);
                                isValidate = false;
                            } else {
                                lNameInput.setError(null);
                                isValidate = true;
                            }
                        }
                    });
                    break;
                case "First Name":
                    firstName = editText;
                    fNameInput = textInputLayout;
                    firstName.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    firstName.addTextChangedListener(new TextValidator(firstName) {
                        @Override
                        public void validate(EditText textView, String text) {
                            String msg = Verification.isValidName(firstName.getText().toString());
                            if (msg != null) {
                                fNameInput.setError(msg);
                                isValidate = false;
                            } else {
                                fNameInput.setError(null);
                                isValidate = true;
                            }
                        }
                    });
                    break;
            }

        } else if (type.equals("nic")) {
            switch (hint) {
                case "Nationality":
                    nationality = editText;
                    nationalityInput = textInputLayout;
                    nationality.setInputType(InputType.TYPE_NULL);
                    nationality.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickField = "nationality";
                            countryLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                case "Date of Birth":
                    dob = editText;
                    dobInput = textInputLayout;
                    dob.setInputType(InputType.TYPE_NULL);
                    dob.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickField = "dob";
                            String[] dd = dob.getText().toString().split("/");
                            d = dd[0];
                            m = dd[1];
                            y = dd[2];
                            datePicker.updateDate(Integer.parseInt(y), Integer.parseInt(m) - 1, Integer.parseInt(d));
                            dateLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    dob.addTextChangedListener(new TextValidator(dob) {
                        @Override
                        public void validate(EditText textView, String text) {
                            String validDob = Verification.vaidateNIC_DOB(nic.getText().toString(), dob.getText().toString());
                            if (validDob != null) {
                                dobInput.setError(validDob);
                                isValidate = false;
                            } else {
                                dobInput.setError(null);
                                isValidate = true;
                            }
                        }
                    });
                    break;
                case "Sex":
                    sex = editText;
                    sexInput = textInputLayout;
                    sex.setInputType(InputType.TYPE_NULL);
                    sex.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickField = "sex";
                            sexLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    sex.addTextChangedListener(new TextValidator(sex) {
                        @Override
                        public void validate(EditText textView, String text) {
                            String validGender = Verification.validateNIC_SEX(nic.getText().toString(), sex.getText().toString());
                            if (validGender != null) {
                                sexInput.setError(validGender);
                                isValidate = false;
                            } else {
                                sexInput.setError(null);
                                isValidate = true;
                            }
                        }
                    });
                    break;
                case "NIC":
                    nic = editText;
                    nicInput = textInputLayout;
                    nic.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    nic.addTextChangedListener(new TextValidator(nic) {
                        @Override
                        public void validate(EditText textView, String text) {
                            String msg = Verification.validateNIC(nic.getText().toString());
                            if (msg != null) {
                                nicInput.setError(msg);
                                isValidate = false;
                            } else {
                                nicInput.setError(null);
                                isValidate = true;
                            }
                        }
                    });
                    break;
                case "Last Name":
                    lastName = editText;
                    lNameInput = textInputLayout;
                    lastName.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    lastName.addTextChangedListener(new TextValidator(lastName) {
                        @Override
                        public void validate(EditText textView, String text) {
                            String msg = Verification.isValidName(lastName.getText().toString());
                            if (msg != null) {
                                lNameInput.setError(msg);
                                isValidate = false;
                            } else {
                                lNameInput.setError(null);
                                isValidate = true;
                            }
                        }
                    });
                    break;
                case "First Name":
                    firstName = editText;
                    fNameInput = textInputLayout;
                    firstName.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    firstName.addTextChangedListener(new TextValidator(firstName) {
                        @Override
                        public void validate(EditText textView, String text) {
                            String msg = Verification.isValidName(firstName.getText().toString());
                            if (msg != null) {
                                fNameInput.setError(msg);
                                isValidate = false;
                            } else {
                                fNameInput.setError(null);
                                isValidate = true;
                            }
                        }
                    });
                    break;
            }
        }

        LinearLayout.LayoutParams textInputLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textInputLayoutParams.topMargin = 50;
        textInputLayout.setLayoutParams(textInputLayoutParams);
        textInputLayout.addView(editText, editTextParams);
        textInputLayout.setHint(hint);
        infoView.addView(textInputLayout, index);
    }

    @OnClick({R.id.btnCancel, R.id.btnOk})
    public void onCountryViewClose() {
        countryLayout.setVisibility(View.INVISIBLE);
    }

    @OnClick({R.id.btnClose, R.id.btnDone})
    public void onDateViewClose() {
        if (clickField.equals("dob")) {
            String x = d + "/" + m + "/" + y;
            dob.setText(x);
        }
        dateLayout.setVisibility(View.INVISIBLE);
    }

    @OnClick({R.id.btnClose2, R.id.btnOk2})
    public void onSexViewClose() {
        sexLayout.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.btnContinue)
    public void onContinue() {
        if (isValidate && checkValidations()) {
            SharedPreferences prf = getSharedPreferences(Common.username, Context.MODE_PRIVATE);
            String username = prf.getString(Common.username, null);
            HashMap<String, String> param = new HashMap<>();
            if (type.equals("passport")) {
                param.put("type", "passport");
                param.put("dateOfBirth", dob.getText().toString());
                param.put("lastName", lastName.getText().toString());
                param.put("nationality", nationality.getText().toString());
                param.put("otherName", firstName.getText().toString());
                param.put("sex", sex.getText().toString());
                param.put("personalNo", nic.getText().toString());
                param.put("documentType", "passport");
                param.put("username", username);
//            param.put("passportType", String.valueOf(mrzRecord.code2));
                param.put("issueCountry", issueCountry.getText().toString());
                param.put("passportNo", passportNo.getText().toString());
                param.put("expireDate", expireDate.getText().toString());
                param.put("data", Common.getBase64ImageString(PassportScanner.image));
                ConnectServer.connect().addDocument(param, this);
            } else if (type.equals("nic") && checkRequiredFields()) {
                param.put("dateOfBirth", dob.getText().toString());
                param.put("lastName", lastName.getText().toString());
                param.put("nationality", nationality.getText().toString());
                param.put("otherName", firstName.getText().toString());
                param.put("sex", sex.getText().toString());
                param.put("personalNo", nic.getText().toString());
                param.put("type", "nic");
                param.put("username", username);
                param.put("data", Common.getBase64ImageString(NicScanner.image));
                param.put("additionalData", Common.getBase64ImageString(NicBackScanner.bitmap));
                ConnectServer.connect().addDocument(param, this);
            }
        }
    }

    private boolean checkRequiredFields() {
        boolean isValid = true;
        String msg = Verification.isValidName(firstName.getText().toString());
        if (msg != null) {
            fNameInput.setError(msg);
            isValidate = false;
            isValid = false;
        } else {
            fNameInput.setError(null);
            isValidate = true;
        }
        String msg2 = Verification.isValidName(lastName.getText().toString());
        if (msg != null) {
            lNameInput.setError(msg2);
            isValidate = false;
            isValid = false;
        } else {
            lNameInput.setError(null);
            isValidate = true;
        }
        return isValid;
    }

    boolean checkValidations() {
        String nicNo = nic.getText().toString();
        String d = dob.getText().toString();
        String gender = sex.getText().toString();
        boolean validity = true;
        if (Verification.validateNIC(nicNo) != null) {
            nicInput.setError(Verification.validateNIC(nicNo));
            validity = false;
        } else {
            if (Verification.vaidateNIC_DOB(nicNo, d) != null) {
                validity = false;
                dobInput.setError(Verification.vaidateNIC_DOB(nicNo, d));
            }
            if (Verification.validateNIC_SEX(nicNo, gender) != null) {
                validity = false;
                sexInput.setError(Verification.validateNIC_SEX(nicNo, gender));
            }
        }
        return validity;
    }

    @OnClick(R.id.btnRetry)
    public void onRetry() {
        onBackPressed();
    }
}
