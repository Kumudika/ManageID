package com.meetsid.userApp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.airbnb.lottie.LottieAnimationView;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.AppAlertDialog;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;
import com.meetsid.userApp.Utils.ServerUtils.ErrorObject;
import com.meetsid.userApp.Utils.ServerUtils.MessageType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoiceLogin extends AppCompatActivity {
    String TAG = "VoiceRecord Activity";
    final int sdk = android.os.Build.VERSION.SDK_INT;

    @BindView(R.id.microphone)
    Button microphone;
    @BindView(R.id.backBtn)
    ImageButton backBtn;
    @BindView(R.id.readText)
    TextView readText;

    static final String AB = "abcdefghijklmnopqrstuvwxyz";
    static Random rnd = new Random();

    private MediaRecorder mediaRecorder;
    String voiceStoragePath;
    String subPath;
    int no_files = 0;
    String file = new String();
    MediaPlayer mediaPlayer;
    boolean startRecording = false;
    LottieAnimationView voiceAnim;
    File outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_voice_login);
        ButterKnife.bind(this);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        microphone.setEnabled(true);

        voiceAnim = findViewById(R.id.voiceAnime);
        voiceAnim.setAnimation(R.raw.voice);
        voiceAnim.setRepeatCount(10000);

//        voiceStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        File audioVoice = new File(voiceStoragePath + File.separator + "voices");
        File outputDir = this.getCacheDir(); // context being the Activity pointer
        try {
            outputFile = File.createTempFile("audio", ".m4a", outputDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        subPath = outputFile.getPath();
        requestPermission();
        initializeMediaRecord();
    }

    @OnClick(R.id.microphone)
    public void onRecord() {
        if (mediaRecorder == null) {
            initializeMediaRecord();
        }
        if (!startRecording) {
            startRecording = true;
            readText.setText(Common.voice_phrase);
            readText.setTextColor(getResources().getColor(R.color.meetsid_text_grey));
            readText.setTextSize(14);
            startAudioRecording();
        } else {
            startRecording = false;
            stopAudioRecording();
        }
    }

    @OnClick(R.id.backBtn)
    public void goBack() {
        if (mediaRecorder != null)
            mediaRecorder.release();
        this.finish();
        super.onBackPressed();
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 10);

        }
    }

    private boolean hasPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public void onSubmit() {
        Context context = this;
        SharedPreferences prf = getSharedPreferences(Common.username, Context.MODE_PRIVATE);
        String username = prf.getString(Common.username, null);
        HashMap<String, String> param = new HashMap<>();
        param.put("username", username);
        HashMap<String, String> filePara = new HashMap<>();
        filePara.put("voice", file);
        ConnectServer.connect().voiceLogin(param, context, filePara);
    }

    private String generateVoiceFilename(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private void startAudioRecording() {
        try {
            voiceAnim.playAnimation();
//            subPath = voiceStoragePath + File.separator + "voices/" + generateVoiceFilename(6) + ".3gpp";
            if (mediaRecorder == null) {
                initializeMediaRecord();
            }
            mediaRecorder.prepare();
            mediaRecorder.start();
            no_files = 1;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                microphone.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.stop));
            } else {
                microphone.setBackground(ContextCompat.getDrawable(this, R.drawable.stop));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopAudioRecording() {
        if (mediaRecorder != null) {
            voiceAnim.cancelAnimation();
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            file = subPath;
            microphone.setEnabled(false);
            onSubmit();
        }
    }

    private void initializeMediaRecord() {
        if (hasPermission()) {
            mediaRecorder = new MediaRecorder();
            no_files = 0;
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setOutputFile(subPath);
        } else {
            ErrorObject errorObject = Common.errorObjects.get("SPEECH_REG_PERMISSION");
            String msg = errorObject != null ? errorObject.getDescription() : "Permission Required,MeetSID requires speech recognition permission to continue.";
            AppAlertDialog.errorMessageDialog(this, msg, MessageType.ERROR);
        }
    }
}
