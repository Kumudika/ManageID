package com.meetsid.userApp.FaceDetectionUtil;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.meetsid.userApp.Activities.FaceRegister;
import com.meetsid.userApp.FaceDetectionUtil.common.CameraSource;
import com.meetsid.userApp.FaceDetectionUtil.common.CameraSourcePreview;
import com.meetsid.userApp.FaceDetectionUtil.common.FaceRecognitionProcessor;
import com.meetsid.userApp.FaceDetectionUtil.common.FrameMetadata;
import com.meetsid.userApp.FaceDetectionUtil.common.GraphicOverlay;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.FaceDetectUtils.BlinkCB;
import com.meetsid.userApp.Utils.FaceDetectUtils.BlinkReader;
import com.meetsid.userApp.Utils.FaceDetectUtils.FaceEventCB;
import com.meetsid.userApp.Utils.FaceDetectUtils.FaceGesture;
import com.meetsid.userApp.Utils.FaceDetectUtils.IBlinkReader;
import com.meetsid.userApp.Utils.FaceDetectUtils.PhoneHoldCB;
import com.meetsid.userApp.Utils.FaceDetectUtils.TurnSide;
import com.meetsid.userApp.Utils.FaceDetectUtils.TurnerCB;
import com.meetsid.userApp.Utils.FaceDetectUtils.TurnerReader;
import com.meetsid.userApp.Utils.FirebaseMessage;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;
import com.meetsid.userApp.Utils.TransparentCircle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.meetsid.userApp.Utils.FaceDetectionScanner.Constants.KEY_CAMERA_PERMISSION_GRANTED;
import static com.meetsid.userApp.Utils.FaceDetectionScanner.Constants.PERMISSION_REQUEST_CAMERA;

public class ScannerActivity extends AppCompatActivity implements FaceEventCB {

    String TAG = "ScannerActivity";

    @BindView(R.id.overlay)
    GraphicOverlay overlay;
    @BindView(R.id.preview)
    CameraSourcePreview preview;
    @BindView(R.id.circle)
    TransparentCircle circle;
    @BindView(R.id.mInfoText)
    TextView mInfoText;
    @BindView(R.id.alertText)
    TextView alertText;
    @BindView(R.id.btnCapture)
    Button btnCapture;
    @BindView(R.id.topic)
    TextView topic;
    public static int faceTrackingId;
    HashMap<String, String> livenessData = new HashMap<>();

    private CameraSource mCameraSource = null;
    FaceDetectionResultListener faceDetectionResultListener = null;

    Bitmap bmpCapturedImage;
    List<FirebaseVisionFace> capturedFaces;
    FaceActionDetector faceActionDetector;
    BlinkDetector blinkDetector;
    TurnerReader rightTurner;
    TurnerReader leftTurner;
    File capturedImage;
    String uri;
    String method;
    boolean isEnable;

    FaceGesture[] gestures = new FaceGesture[2];

    private static final int STEP_INITIAL = 0;
    private static final int FIRST_GESTURE = 1;
    private static final int SECOND_GESTURE = 2;

    private int currentStep;
    private FaceGesture currentGesture;
    File outputDir;
    boolean isHeadUpward = true;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    public static Map<String, Bitmap> images = new HashMap<>();
    String pushID = null;
    boolean isDeclined = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            Log.e(TAG, "Barcode scanner could not go into fullscreen mode!");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        Intent intent = getIntent();
        method = intent.getStringExtra("method");
        if(intent.hasExtra("pushID")) {
            pushID = intent.getStringExtra("pushID");
        }
        if(intent.hasExtra("isDeclined")) {
            isDeclined = intent.getBooleanExtra("isDeclined", false);
            Common.isDeclined = isDeclined;
        }
        Common.faceMethod = method;
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        if (method.equalsIgnoreCase("login"))
            topic.setText(getString(R.string.face_login));
        //set layout width
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        preview.getLayoutParams().width = (width / 3) * 2;
        overlay.getLayoutParams().width = (width / 3) * 2;
        outputDir = this.getCacheDir();
        //set two random gestures
        setRandomGestures();
        currentStep = STEP_INITIAL;

        if (preview != null)
            createCameraSource();
        if (preview.isPermissionGranted(true, mMessageSender))
            new Thread(mMessageSender).start();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        blinkDetector.blinkReader.resetBlinkReader();
        if (leftTurner != null)
            leftTurner.reset();
        if (rightTurner != null)
            rightTurner.reset();
        currentStep = STEP_INITIAL;
        if (preview != null)
            createCameraSource();
        if (preview.isPermissionGranted(true, mMessageSender))
            new Thread(mMessageSender).start();
        if (rightTurner != null)
            rightTurner = null;
        if (leftTurner != null)
            leftTurner = null;
        if (gestures.length != 0) {
            currentStep = STEP_INITIAL;
            setRandomGestures();
        }
    }

    @Override
    public void onBackPressed() {
//        Intent intent = null;
//        if (Common.faceMethod.equals("register")) {
//            intent = new Intent(this, SuccessActivity.class);
//            intent.putExtra("activity", "MobileVerification");
//            intent.putExtra("msg", "Wallet Created");
//        } else {
//            intent = new Intent(this, MainActivity.class);
//        }
//        startActivity(intent);
        super.onBackPressed();
    }

    private void createCameraSource() {
        if (mCameraSource == null) {
            mCameraSource = new CameraSource(this, overlay);
            mCameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
        }
        overlay.getWidth();
        FaceRecognitionProcessor faceRecognitionProcessor = new FaceRecognitionProcessor();
        faceRecognitionProcessor.setFaceDetectionResultListener(getFaceDetectionListener());
        mCameraSource.setMachineLearningFrameProcessor(faceRecognitionProcessor);
        faceActionDetector = new FaceActionDetector();
        faceActionDetector.setEventCB(this);
        blinkDetector = faceActionDetector.create();
        startCameraSource();
    }

    private FaceDetectionResultListener getFaceDetectionListener() {
        if (faceDetectionResultListener == null)
            faceDetectionResultListener = new FaceDetectionResultListener() {
                @Override
                public void onSuccess(@Nullable Bitmap originalCameraImage, @NonNull List<FirebaseVisionFace> faces, @NonNull FrameMetadata frameMetadata, @NonNull GraphicOverlay graphicOverlay) {
                    bmpCapturedImage = originalCameraImage;
                    if (faces.size() == 0) {
                        blinkDetector.blinkReader.resetBlinkReader();
                    }
                    for (FirebaseVisionFace face : faces) {
                        showMessage(face, new PhoneHoldCB() {
                            @Override
                            public void onSuccess() {
                                alertText.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertText.setVisibility(View.INVISIBLE);
                                    }
                                }, 3000);
                            }

                            @Override
                            public void onFailure() {
                                isHeadUpward = false;
                            }
                        });
                        if (true) {
//                            System.out.println("Y:" + face.getHeadEulerAngleY());
                            if (currentStep == STEP_INITIAL) {
                                faceActionDetector.setFaceTrackingId(face.getTrackingId());
                                currentGesture = gestures[0];
                                currentStep = FIRST_GESTURE;
                                showInfoText();
//                                blinkDetector.blinkCapture(face);
                            }
                            //TODO
//                            Bitmap faceBitmap = Bitmap.createBitmap(originalCameraImage,
//                                    (int) (face.getBoundingBox().exactCenterX()/2),
//                                    (int) (face.getBoundingBox().exactCenterY()/2),
//                                    face.getBoundingBox().width(),
//                                    face.getBoundingBox().height());
                            triggerGesture(face);
                        }
                    }
//                    runOnUiThread(() -> {
//                        Log.d(TAG, "button enable true ");
//                        capturedFaces = faces;
//                        btnCapture.setEnabled(isEnable);
//                    });
                }

                @Override
                public void onFailure(@NonNull Exception e) {
                    currentStep = STEP_INITIAL;
                }
            };

        return faceDetectionResultListener;
    }

    private void triggerGesture(FirebaseVisionFace face) {
        if (currentGesture == FaceGesture.BLINK)
            blinkDetector.blinkCapture(face);
        else if (currentGesture == FaceGesture.SMILE)
            faceActionDetector.eventCB.onSmileDetected(face.getSmilingProbability());
        else if (currentGesture == FaceGesture.LEFT_TURN)
            faceActionDetector.eventCB.onLeftTurnerDetected(face.getHeadEulerAngleY());
        else if (currentGesture == FaceGesture.RIGHT_TURN)
            faceActionDetector.eventCB.onRightTurnerDetected(face.getHeadEulerAngleY());

    }

    private void gestureCompleted() {
        if (currentStep == FIRST_GESTURE) {
            livenessCapture();
            currentStep = SECOND_GESTURE;
            currentGesture = gestures[1];
            showInfoText();
        } else if (currentStep == SECOND_GESTURE) {
            faceCapture();
        }
    }

    private void cropImage() {
        int img_height = bmpCapturedImage.getHeight();
        int img_width = bmpCapturedImage.getWidth();
        Resources r = getResources();
        int px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        int s_height = (displayMetrics.heightPixels - px) / 10 * 6;
        int s_width = (displayMetrics.widthPixels / 3) * 2;
        int diameter = s_width;
        int s_y = (s_height - diameter) / 2;
        float rw = (diameter / (s_width * 1.0f)) * img_width;
        int x = (int) ((img_width - rw) / 2);
        int w = (int) (rw);

        int h = (img_height / img_width) * w;
        int inside_h = (int) ((h / (diameter * 1.0f)) * s_height);
        int delta = (img_height - inside_h) / 2;
        int inside_y = (int) ((h / diameter * 1.0f) * (s_y));
        int y = delta + inside_y;

        bmpCapturedImage = Bitmap.createBitmap(bmpCapturedImage, x, y, w, h);
    }

    private void showMessage(FirebaseVisionFace face, PhoneHoldCB phoneHoldCB) {
        DecimalFormat value = new DecimalFormat("#.#");
        double rightEye = Double.parseDouble(value.format(face.getRightEyeOpenProbability()));
        double leftEye = Double.parseDouble(value.format(face.getLeftEyeOpenProbability()));
        if (currentGesture == FaceGesture.LEFT_TURN || currentGesture == FaceGesture.RIGHT_TURN) {
            phoneHoldCB.onFailure();
        } else {
            if (rightEye <= 0.7 && rightEye > 0.0 && leftEye <= 0.7 && leftEye > 0.0) {
                isHeadUpward = true;
                phoneHoldCB.onSuccess();
            } else {
                phoneHoldCB.onFailure();
            }
        }
    }

    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());

        Log.d(TAG, "startCameraSource: " + code);

        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, PERMISSION_REQUEST_CAMERA);
            dlg.show();
        }

        if (mCameraSource != null && preview != null && overlay != null) {
            try {
                Log.d(TAG, "startCameraSource: ");
                preview.start(mCameraSource, overlay);
            } catch (IOException e) {
                Log.d(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        } else
            Log.d(TAG, "startCameraSource: not started");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
        preview.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (preview != null)
            preview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: ");

            if (preview != null)
                createCameraSource();

        }
    };

    private final Runnable mMessageSender = () -> {
        Log.d(TAG, "mMessageSender: ");
        Message msg = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_CAMERA_PERMISSION_GRANTED, false);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    };

    @OnClick(R.id.backBtn)
    public void backPressed() {
        onBackPressed();
    }

    @OnClick(R.id.btnCapture)
    public void onViewClicked() {
        Log.d(TAG, "onViewClicked: ");
    }

    private void faceUpload() {
        Context context = this;
        SharedPreferences prf = getSharedPreferences(Common.username, Context.MODE_PRIVATE);
        String username = prf.getString(Common.username, null);
        HashMap<String, String> param = new HashMap<>();
        param.put("username", username);
        HashMap<String, String> filePara = new HashMap<>();
        filePara.put("image", capturedImage.getAbsolutePath());
        if (livenessData != null) {
            for (Map.Entry<String, String> entry : ((Map<String, String>) livenessData).entrySet()) {
                filePara.put(entry.getKey(), entry.getValue());
            }
        }
        if(Common.loginType.equalsIgnoreCase("v_login")) {
            param.put("id", pushID);
            param.put("isDecline", String.valueOf(isDeclined));
            ConnectServer.connect().faceVerification(param, context, filePara);
        } else {
            FirebaseMessage.getToken(this);
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String token = instanceIdResult.getToken();
                    Log.i("Token", token);
                    param.put("fcmToken", token);
                    ConnectServer.connect().faceLogin(param, context, filePara);
                }

            });
        }
    }

    @Override
    public void onBlinkDetected() {
        gestureCompleted();
    }

    @Override
    public void onBlinkCount(int count) {
        if (currentGesture != FaceGesture.BLINK) {
            return;
        } else {
            if (count >= 1)
                mInfoText.setText(R.string.blink_info_anther);
        }
    }

    @Override
    public void onSmileDetected(float probability) {
        if (currentGesture != FaceGesture.SMILE)
            return;
        if (probability > 0.75)
            gestureCompleted();
//        button.setEnabled(true);
//        showInfoText(R.string.capture);
    }

    @Override
    public void onLeftTurnerDetected(float angle) {
        if (leftTurner == null)
            leftTurner = new TurnerReader(TurnSide.LEFT, getTurnerCB());
        if (currentGesture == FaceGesture.LEFT_TURN) {
            if (angle > 0)
                leftTurner.addValue(angle);
        }
    }

    @Override
    public void onRightTurnerDetected(float angle) {
        if (rightTurner == null)
            rightTurner = new TurnerReader(TurnSide.RIGHT, getTurnerCB());
        if (currentGesture == FaceGesture.RIGHT_TURN) {
            if (angle < 0)
                rightTurner.addValue(angle);
        }
    }

    private void showInfoText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentGesture == FaceGesture.BLINK)
                    mInfoText.setText(R.string.blink_info);
                else if (currentGesture == FaceGesture.SMILE)
                    mInfoText.setText(R.string.smile_info);
                else if (currentGesture == FaceGesture.LEFT_TURN)
                    mInfoText.setText(R.string.turn_left);
                else if (currentGesture == FaceGesture.RIGHT_TURN)
                    mInfoText.setText(R.string.turn_right);
                mInfoText.setVisibility(View.VISIBLE);
            }
        });
    }

    private class FaceActionDetector {
        FaceEventCB eventCB;

        private BlinkDetector create() {
            return new BlinkDetector(this.eventCB);
        }

        public void setEventCB(FaceEventCB eventCB) {
            this.eventCB = eventCB;
        }

        public int getFaceTrackingId() {
            return faceTrackingId;
        }

        public void setFaceTrackingId(int faceTrackingId) {
            ScannerActivity.faceTrackingId = faceTrackingId;
        }
    }

    private class BlinkDetector implements BlinkCB {
        private final FaceEventCB faceEventCB;
        private IBlinkReader blinkReader;

        BlinkDetector(FaceEventCB faceEventCB) {
            this.faceEventCB = faceEventCB;
            blinkReader = new BlinkReader(2, this);
        }

        @Override
        public void onBlinkComplete() {
            this.faceEventCB.onBlinkDetected();
        }

        @Override
        public void onBlink(int count) {
            this.faceEventCB.onBlinkCount(count);
        }

        @Override
        public void onUpdate(Detector.Detections<Face> detectionResults, Face face) {

        }

        public void blinkCapture(FirebaseVisionFace face) {
            double left = face.getLeftEyeOpenProbability();
            double right = face.getRightEyeOpenProbability();
            double smile = face.getSmilingProbability();
            blinkReader.addValue(left, right);
//            if (smile > 0.75 && currentStep == STEP_BLINK) {
//                this.faceEventCB.onSmileDetected();
//                mInfoText.setVisibility(View.INVISIBLE);
//                isEnable = true;
////                btnCapture.performClick();
//                //button.setEnabled(true);
//                faceCapture();
//            }
        }
    }

    private void faceCapture() {
        runOnUiThread(() -> {
            try {
                capturedImage = File.createTempFile("final", ".jpg", outputDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!capturedImage.exists())
                capturedImage.mkdirs();
            OutputStream outStream = null;
//        File file = new File(Environment.getExternalStorageDirectory() + "/inpaint/"+"seconds"+".png");
            try {
                outStream = new FileOutputStream(capturedImage);
                cropImage();
                bmpCapturedImage.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                Bitmap resized = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(capturedImage.getPath()), 300, 300, true);
                images.put("final", resized);
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        preview.stop();
        mCameraSource.stop();
        currentStep = STEP_INITIAL;
        mInfoText.setText("");
        if (images.size() == 2) {
            if (method.equals("register")) {
                Intent intent = new Intent(this, FaceRegister.class);
                startActivity(intent);
            } else if (method.equals("login")) {
                faceUpload();
            } else if (method.equals("verification")) {

            }
        }
    }

    private void livenessCapture() {
        runOnUiThread(() -> {
            File live0 = null;
            try {
                live0 = File.createTempFile("live0", ".jpg", outputDir);
                if (!live0.exists())
                    live0.mkdirs();
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream outStream = null;
            Resources r = getResources();
            int px = Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
            try {
                outStream = new FileOutputStream(live0);
                cropImage();
                bmpCapturedImage.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                Bitmap resized = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(live0.getPath()), 300, 300, true);
                images.put("live0", resized);
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (live0 != null) {
                livenessData.put("live0", live0.getAbsolutePath());
            }
        });
    }

    private FaceGesture pickRandomGesture() {
        return FaceGesture.values()[new Random().nextInt(FaceGesture.values().length)];
    }

    private void setRandomGestures() {
        List<FaceGesture> teams = new ArrayList<>();
        Collections.addAll(teams, FaceGesture.values());
        Collections.shuffle(teams);
        gestures[0] = teams.get(0);
        gestures[1] = teams.get(1);
    }

    private TurnerCB getTurnerCB() {
        TurnerCB turnerCB = new TurnerCB() {
            @Override
            public void onComplete() {
                gestureCompleted();
            }

            @Override
            public void onFailure() {

            }
        };
        return turnerCB;
    }
}
