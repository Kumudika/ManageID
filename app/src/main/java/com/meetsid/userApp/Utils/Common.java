package com.meetsid.userApp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.meetsid.userApp.Models.Token;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.ServerUtils.ErrorObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.android.volley.misc.ImageUtils.calculateInSampleSize;

public class Common extends Activity {
    public static String voice_phrase = "MeetSID is a global identity verification and protection solution that gives its users complete control over their identity.";
    public static String voice_phrase_login = null;
    public static final String sharedPref = "meetsid";
    public static final String userPref = "username";
    public static final String tokenData = "tokenData";
    public static final String username = "username";
    public static final String emailToken = "mailToken";
    public static final String mobile = "mobile";
    public static String nic = null;
    SharedPreferences prf = null;
    public static JSONObject jsonObject = null;
    public static String faceMethod = null;
    public static HashMap<String, ErrorObject> errorObjects;
    public static String VERIFICATION_TOKEN;
    public static int amount = 0;
    public static LinkedHashMap<String, String> dataList;
    public static boolean isRegCompleted = false;
    public static String country = "Sri Lanka";
    public static String qrToken = null;
    public static long time = 0;
    public static String loginType= "n_login";
    public static boolean isDeclined = false;

    public JSONObject getUserDetails() {
        JSONObject user = null;
        if (prf == null) {
            prf = getSharedPreferences(Common.userPref, Context.MODE_PRIVATE);
        }
        try {
            user = new JSONObject(prf.getString("user", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean isPassportAdded() {
        if (getUserDetails().has("passport_location"))
            return true;
        return false;
    }

    public boolean isPersonalDocumentAdded() {
        if (getUserDetails().has("nic_location"))
            return true;
        return false;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "new_image", null);
        return Uri.parse(path);
    }

    public boolean isPaymentCompleted() {
        if (getUserDetails().has("card_token"))
            return true;
        return false;
    }

    public static String getBase64FromPath(String path) {
        String base64 = "";
        try {/*from   w w w .  ja  va  2s  .  c om*/
            File file = new File(path);
            byte[] buffer = new byte[(int) file.length() + 100];
            @SuppressWarnings("resource")
            int length = new FileInputStream(file).read(buffer);
            base64 = Base64.encodeToString(buffer, 0, length,
                    Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }

    public static Bitmap convert(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",") + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String getSHA256Hash(String data) {
        String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return bin2hex(hash); // make it printable
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private static String bin2hex(byte[] data) {
        StringBuilder hex = new StringBuilder(data.length * 2);
        for (byte b : data)
            hex.append(String.format("%02x", b & 0xFF));
        return hex.toString();
    }

    public static Token getTokenData(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
        String data = sharedPreferences.getString(Common.tokenData, "");
        Token token = gson.fromJson(data, Token.class);
        return token;
    }

    public static Bitmap getBitmap(String path) {
        try {
            Bitmap bitmap = null;
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = calculateInSampleSize(options, 500, 500);
            options.inJustDecodeBounds = false;

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
//            image.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getBase64ImageString(Bitmap photo) {
        String imgString;
        if (photo != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            byte[] profileImage = outputStream.toByteArray();

            imgString = Base64.encodeToString(profileImage,
                    Base64.NO_WRAP);
        } else {
            imgString = "";
        }

        return imgString;
    }

    public static String loadJSONFromAsset(int id, Context context) {
        String json;
        try {
            InputStream is = context.getResources().openRawResource(id);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public String getCountry(String code, Context context) {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(context));
            JSONArray m_jArry = obj.getJSONArray("countries");
            ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String countryName = jo_inside.getString("country");
                String countryCode = jo_inside.getString("alpha_3");
                if (countryCode.equalsIgnoreCase(code)) {
                    return countryName;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.country);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}

