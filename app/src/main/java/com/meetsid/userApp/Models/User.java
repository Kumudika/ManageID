package com.meetsid.userApp.Models;

import java.util.HashMap;

public class User {
    public String username;
    public String firstName;
    public String lastName;
    public String Dob;
    public String nationality;
    public String gender;
    public String nic;
    public HashMap<String, String> verifiedFields;
    public int verificationLevel = -1;
    public int verificationMedal = -1;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return Dob;
    }

    public void setDob(String dob) {
        Dob = dob;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public HashMap<String, String> getVerifiedFields() {
        return verifiedFields;
    }

    public void setVerifiedFields(HashMap<String, String> verifiedFields) {
        this.verifiedFields = verifiedFields;
    }

    public int getVerificationLevel() {
        return verificationLevel;
    }

    public void setVerificationLevel(int verificationLevel) {
        this.verificationLevel = verificationLevel;
    }

    public int getVerificationMedal() {
        return verificationMedal;
    }

    public void setVerificationMedal(int verificationMedal) {
        this.verificationMedal = verificationMedal;
    }
}
