package com.meetsid.userApp.Utils;

import android.util.Log;

import com.meetsid.userApp.Utils.ServerUtils.ErrorObject;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Verification {
    public static String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";

    public static boolean isNullOrEmpty(String str) {
        if (str != null && !str.isEmpty())
            return false;
        return true;
    }

    public static String isFielfNullOrEmpty(String str) {
        String msg = null;
        if (str == null || str.isEmpty()) {
            ErrorObject errorObject = Common.errorObjects.get("EMPTY_FIELD");
            msg = errorObject != null ? errorObject.getDescription() : "Required";
        }
        return msg;
    }

    public static String verifyEmail(String email) {
        String msg = null;
        if (email == null || email.isEmpty()) {
            ErrorObject errorObject = Common.errorObjects.get("EMPTY_FIELD");
            msg = errorObject != null ? errorObject.getDescription() : "Required";
            return msg;
        }
        Pattern pat = Pattern.compile(emailRegex);
        if (!pat.matcher(email).matches()) {
            ErrorObject errorObject = Common.errorObjects.get("INVALID_MAIL");
            msg = errorObject != null ? errorObject.getDescription() : "Please enter a valid email.";
        }
        return msg;
    }

    public static String isValidMobile(String phone) {
        phone = phone.replaceAll(" ", "");
        String msg = null;
        if (phone.length() < 10 || phone.length() > 16) {
            return msg;
        } else if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            ErrorObject errorObject = Common.errorObjects.get("INVALID_MOBILE");
            msg = errorObject != null ? errorObject.getDescription() : "Please enter a valid mobile number.";
        }
        return msg;
    }

    public static String isValidCard(String card) {
        String regex = "^(?:(?<visa>4[0-9]{12}(?:[0-9]{3})?)|" +
                "(?<mastercard>5[1-5][0-9]{14})|" +
                "(?<discover>6(?:011|5[0-9]{2})[0-9]{12})|" +
                "(?<amex>3[47][0-9]{13})|" +
                "(?<diners>3(?:0[0-5]|[68][0-9])?[0-9]{11})|" +
                "(?<jcb>(?:2131|1800|35[0-9]{3})[0-9]{11}))$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(card);
        String msg = null;
        if (!matcher.matches()) {
            ErrorObject errorObject = Common.errorObjects.get("INVALID_CARDNO");
            msg = errorObject != null ? errorObject.getDescription() : "Invalid Card Number";
        }
        return msg;
    }

//    public static boolean isValidPassportNo(String passportNo) {
//        String regex = "^[A-Z]{1,2}[0-9]{6}[0-9A-F]";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(passportNo);
//        return matcher.matches();
//    }

    public static String isValidName(String name) {
        String msg = isFielfNullOrEmpty(name);
        if (msg != null) {
            return msg;
        }
        String regex = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            ErrorObject errorObject = Common.errorObjects.get("INVALID_NAME");
            msg = errorObject != null ? errorObject.getDescription() : "Invalid Name";
        }
        return msg;
    }

    public static String validateNIC(String nic) {
        Log.i("captured: ", nic);
        String regex = "^[0-9]{9}[vVxX]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nic);
        String msg = null;
        if (!matcher.matches()) {
            ErrorObject errorObject = Common.errorObjects.get("INVALID_NIC");
            msg = errorObject != null ? errorObject.getDescription() : "Invalid NIC number.";
        }
        return msg;
    }

    public static boolean validateNewNIC(String nic) {
        Log.i("captured: ", nic);
        String regex = "^[0-9]{7}[0][0-9]{4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nic);
        return matcher.matches();
    }

    public static String validatePassportNumber(String passportNo, String country) {
        String regex;
        if (country.equals("Sri Lanka")) {
            regex = "^[A-Z][0-9]{7}$";
        } else {
            regex = "^[a-zA-Z0-9]{2}[0-9]{5,10}$";
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(passportNo);
        String msg = null;
        if (!matcher.matches()) {
            ErrorObject errorObject = Common.errorObjects.get("INVALID_PASSPORTNO");
            msg = errorObject != null ? errorObject.getDescription() : "Invalid passport no";
        }
        return msg;
    }

    public static String vaidateNIC_DOB(String nic, String dob) {
        String msg = null;
        DobVerification dobVerification = new DobVerification();
        String gen = dobVerification.getGender(nic);
        int x = (gen.equalsIgnoreCase("Female")) ? 500 : 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = sdf.parse(Objects.requireNonNull(dob));
            Calendar cal = Calendar.getInstance();
            cal.set(1992, date.getMonth(), date.getDate());
            int year = date.getYear() % 100;
            int dayOfYear = cal.get(Calendar.DAY_OF_YEAR) + x;
            int nicYear = Integer.parseInt(nic.substring(0, 2));
            int nicDayOfYear = Integer.parseInt(nic.substring(2, 5));
            if (year == nicYear && dayOfYear == nicDayOfYear) {
                return msg;
            } else {
                ErrorObject errorObject = Common.errorObjects.get("INCRRCT_DOB");
                msg = errorObject != null ? errorObject.getDescription() : "DoB does not match your NIC number.";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return msg;
        }
        return msg;
    }

    public static String validateNIC_SEX(String nic, String sex) {
        String msg = null;
        DobVerification dobVerification = new DobVerification();
        String gen = dobVerification.getGender(nic);
        if (!gen.equals(sex)) {
            ErrorObject errorObject = Common.errorObjects.get("INCRRCT_SEX");
            msg = errorObject != null ? errorObject.getDescription() : "Sex does not match with NIC number.";
        }
        return msg;
    }

    public static String validatePasswordLength(String password) {
        String msg = null;
        if (password.length() < 8) {
            ErrorObject errorObject = Common.errorObjects.get("INVALID_PSSWRD_LEN_REQ");
            msg = errorObject != null ? errorObject.getDescription() : "Use eight or more characters.";
        }
        return msg;
    }

    public static String validatePasswordChar(String password) {
        String msg = null;
        Pattern p = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*(-|[-.#?!@$%^&*])).+$");
        Matcher m = p.matcher(password);
        if (!m.matches()) {
            ErrorObject errorObject = Common.errorObjects.get("INVALID_PSSWRD_REQ");
            msg = errorObject != null ? errorObject.getDescription() : "Must contain letters, numbers & symbols.";
        }
        return msg;
    }

    public static String checkForInvalidCharactors(String password) {
        String msg = null;
        Pattern p = Pattern.compile("[A-Za-z0-9!.#$%&*^\\-?@]+");
        Matcher m = p.matcher(password);
        if (!m.matches()) {
            ErrorObject errorObject = Common.errorObjects.get("INVALID_CHAR");
            msg = errorObject != null ? errorObject.getDescription() : "Contains a restricted symbol. Allowed symbols #?!@$%^&*.-";
        } else if (!Normalizer.isNormalized(password, Normalizer.Form.NFD)) {
            ErrorObject errorObject = Common.errorObjects.get("INVALID_CHAR");
            msg = errorObject != null ? errorObject.getDescription() : "Contains a restricted symbol. Allowed symbols #?!@$%^&*.-";
        }
        return msg;
    }

    public static String checkForBlankSpaces(String password) {
        String msg = null;
        if (password.contains(" ")) {
            ErrorObject errorObject = Common.errorObjects.get("CONTAINS_SPACES");
            msg = errorObject != null ? errorObject.getDescription() : "Cannot contain blank spaces.";
        }
        return msg;
    }

}
