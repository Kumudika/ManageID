package com.meetsid.userApp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;
import com.meetsid.userApp.Utils.ServerUtils.ErrorObject;
import com.meetsid.userApp.Utils.Validation.TextValidator;
import com.meetsid.userApp.Utils.Verification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasscodeSetupActivity extends AppCompatActivity {
    @BindView(R.id.passcodeInput)
    TextInputLayout passcodeInput;
    @BindView(R.id.passcodeText)
    TextInputEditText passcodeText;
    @BindView(R.id.confirmInput)
    TextInputLayout confirmInput;
    @BindView(R.id.confirmText)
    TextInputEditText confirmText;
    @BindView(R.id.downloadText)
    TextView downloadText;
    @BindView(R.id.downloadRes)
    TextView downloadRes;
    @BindView(R.id.nextBtn)
    Button nextBtn;
    @BindView(R.id.passcodePanel)
    LinearLayout passcodePanel;

    String passcode = "";
    String confirmPasscode = "";
    boolean isEnabled = true;
    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_passcode_setup);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        font = ResourcesCompat.getFont(this, R.font.metropolis_regular);
        confirmInput.setTypeface(font);
        passcodeInput.setTypeface(font);
        passcodePanel.requestFocus();
        downloadText.setVisibility(View.INVISIBLE);
        downloadRes.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        nextBtn.setEnabled(false);
        passcodeText.addTextChangedListener(new TextValidator(passcodeText) {
            @Override
            public void validate(EditText textView, String text) {
                passcode = text;
                String msg = null;
                msg = Verification.isFielfNullOrEmpty(text);
                if (msg != null)
                    passcodeInput.setError(msg);
                else {
                    passcodeInput.setError(null);
                    String error = "";
                    msg = Verification.validatePasswordLength(text);
                    if (msg != null) {
                        if (error.isEmpty())
                            error = error + msg;
                        else
                            error = error + "\n" + msg;
                    }
                    msg = Verification.validatePasswordChar(text);
                    if (msg != null) {
                        if (error.isEmpty())
                            error = error + msg;
                        else
                            error = error + "\n" + msg;
                    }
                    msg = Verification.checkForInvalidCharactors(text);
                    if (msg != null) {
                        if (error.isEmpty())
                            error = error + msg;
                        else
                            error = error + "\n" + msg;
                    }
                    msg = Verification.checkForBlankSpaces(text);
                    if (msg != null) {
                        if (error.isEmpty())
                            error = error + msg;
                        else
                            error = error + "\n" + msg;
                    }
                    if (!error.isEmpty())
                        passcodeInput.setError(error);
                    else
                        passcodeInput.setError(null);
                }
            }
        });
        confirmText.addTextChangedListener(new TextValidator(confirmText) {
            @Override
            public void validate(EditText textView, String text) {
                confirmPasscode = text;
                if (passcode.equals(confirmPasscode)) {
                    confirmInput.setError(null);
                    downloadText.setVisibility(View.VISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);
                    nextBtn.setEnabled(true);
                } else {
                    downloadText.setVisibility(View.INVISIBLE);
                    nextBtn.setVisibility(View.INVISIBLE);
                    nextBtn.setEnabled(false);
                    ErrorObject errorObject = Common.errorObjects.get("MISMATCH_PSSWRD_FIELDS");
                    String msg = errorObject != null ? errorObject.getDescription() : "Fields do not match.";
                    confirmInput.setError(msg);
                }
            }
        });
    }

    @OnClick(R.id.downloadText)
    public void onDownloadClick() {
        if (passcode.equals(confirmPasscode)) {
            if (isWriteStoragePermissionGranted()) {
                writeToFile();
            } else {
                if (isWriteStoragePermissionGranted()) {
                    writeToFile();
                } else {

                }
            }
        }
    }

    private void writeToFile() {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/download");
        dir.mkdirs();
        File file = new File(dir, "passcode.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(passcode.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            FileOutputStream f = new FileOutputStream(file);
//            PrintWriter pw = new PrintWriter(f);
//            pw.println("Hi , How are you");
//            pw.println("Hello");
//            pw.flush();
//            pw.close();
//            f.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        downloadRes.setVisibility(View.VISIBLE);
    }

    boolean isNullOrEmpty(CharSequence error) {
        if (error == null)
            return true;
        else if (error.toString().isEmpty())
            return true;
        else
            return false;
    }

    @OnClick(R.id.nextBtn)
    public void onContinue() {
        passcodeInput.setPasswordVisibilityToggleEnabled(false);
        confirmInput.setPasswordVisibilityToggleEnabled(false);
        if (isNullOrEmpty(passcodeInput.getError()) && isNullOrEmpty(confirmInput.getError())
                && !Verification.isNullOrEmpty(passcodeText.getText().toString())
                && !Verification.isNullOrEmpty(confirmText.getText().toString())) {
            String passcode = Objects.requireNonNull(passcodeText.getText()).toString();
            String confirmPasscode = Objects.requireNonNull(confirmText.getText()).toString();
            if (passcode.equals(confirmPasscode)) {
                SharedPreferences prf = getSharedPreferences(Common.username, Context.MODE_PRIVATE);
                String username = prf.getString(Common.username, null);
                prf = getSharedPreferences(Common.emailToken, Context.MODE_PRIVATE);
                String token = prf.getString(Common.emailToken, null);
                HashMap<String, String> param = new HashMap<>();
                param.put("pincode", Common.getSHA256Hash(passcode));
                param.put("username", username);
                param.put("token", token);
                ConnectServer.connect().addPIN(param, this);
            } else {
                confirmInput.setError("Passcode mismatch");
            }
        }
    }

    @OnClick(R.id.backBtn)
    public void goBack() {
        this.finish();
        super.onBackPressed();
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        passcodeInput.setPasswordVisibilityToggleEnabled(true);
        confirmInput.setPasswordVisibilityToggleEnabled(true);
//        passcodeText.setInputType(129);
//        confirmText.setInputType(129);
//        confirmInput.setPasswordVisibilityToggleDrawable(R.drawable.visibility_off);
//        passcodeInput.setPasswordVisibilityToggleDrawable(R.drawable.visibility_off);
    }
}
