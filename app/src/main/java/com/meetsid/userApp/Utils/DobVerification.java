package com.meetsid.userApp.Utils;

public class DobVerification {

    public String calculateDOB(String nic) {
        String dob = null;
        if (!Verification.isNullOrEmpty(nic)) {
            if (nic.length() < 5) {
                return null;
            } else {
                nic = nic.substring(0, 5);
                if (Verification.isNullOrEmpty(nic) || Integer.parseInt(nic) < 0 || !NICValidity(nic)) {
                    return null;
                } else {
                    try {
                        dob = DandM(nic) + "/" + Years(nic);
                    } catch (Exception ex) {
                        return null;
                    }
                }
            }
        }
        return dob;
    }

    String Years(String nic) {
        nic = nic.substring(0, 2);
        int val = Integer.parseInt(nic);
        String year = null;
        if (val >= 0 && val <= 29) {
            year = "20" + val;
        } else if (val >= 30 || val <= 99) {
            year = "19" + val;
        }
        return year;
    }

    boolean NICValidity(String nic) {
        nic = nic.substring(2, 5);
        int val = Integer.parseInt(nic);
        if (val > 500) {
            val = val - 500;
        }
        if (val >= 0 && val <= 365) {
            return true;
        } else {
            return false;
        }
    }

    public String getGender(String nic) {
        String gender = null;
        nic = nic.substring(2, 5);
        int val = Integer.parseInt(nic);
        if (val > 500) {
            gender = "Female";
        } else {
            gender = "Male";
        }
        return gender;
    }

    String DandM(String nic) {
        String M = "";
        String D = "";
        nic = nic.substring(2, 5);
        int val = Integer.parseInt(nic);
        if (val > 500) {
            val = val - 500;
        } else {
            val = val;
        }
        if (val <= 31) {
            M = "01";
            D = String.valueOf(val);
        } else if (val <= 60) {
            M = "02";
            D = String.valueOf(val - 31);
        } else if (val <= 91) {
            M = "03";
            D = String.valueOf(val - 60);
        } else if (val <= 121) {
            M = "04";
            D = String.valueOf(val - 91);
        } else if (val <= 152) {
            M = "05";
            D = String.valueOf(val - 121);
        } else if (val <= 182) {
            M = "06";
            D = String.valueOf(val - 152);
        } else if (val <= 213) {
            M = "07";
            D = String.valueOf(val - 182);
        } else if (val <= 244) {
            M = "08";
            D = String.valueOf(val - 213);
        } else if (val <= 274) {
            M = "09";
            D = String.valueOf(val - 244);
        } else if (val <= 305) {
            M = "10";
            D = String.valueOf(val - 274);
        } else if (val <= 335) {
            M = "11";
            D = String.valueOf(val - 305);
        } else if (val <= 365) {
            M = "12";
            D = String.valueOf(val - 335);
        }
        return (D + "/" + M);
    }
}
