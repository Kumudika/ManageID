package com.meetsid.userApp.Models;

public class Token {
    private String tokenId;
    private String tokenShare;
    private Type type;
    private String faceTokenId;
    private String faceTokenShare;
    private String voiceTokenId;
    private String voiceTokenShare;
    private String nicTokenId;
    private String nicTokenShare;
    private String newNicTokenId;
    private String newNicTokenShare;
    private String passportTokenId;
    private String passportTokenShare;
    private String DLTokenId;
    private String DLTokenShare;
    private String passcodeTokenId;
    private String passcodeTokenShare;
    private String paymentTokenId;
    private String paymentTokenShare;
    private int verificationMedal;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getTokenShare() {
        return tokenShare;
    }

    public void setTokenShare(String tokenShare) {
        this.tokenShare = tokenShare;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFaceTokenId() {
        return faceTokenId;
    }

    public void setFaceTokenId(String faceTokenId) {
        this.faceTokenId = faceTokenId;
    }

    public String getFaceTokenShare() {
        return faceTokenShare;
    }

    public void setFaceTokenShare(String faceTokenShare) {
        this.faceTokenShare = faceTokenShare;
    }

    public String getVoiceTokenId() {
        return voiceTokenId;
    }

    public void setVoiceTokenId(String voiceTokenId) {
        this.voiceTokenId = voiceTokenId;
    }

    public String getVoiceTokenShare() {
        return voiceTokenShare;
    }

    public void setVoiceTokenShare(String voiceTokenShare) {
        this.voiceTokenShare = voiceTokenShare;
    }

    public String getNicTokenId() {
        return nicTokenId;
    }

    public void setNicTokenId(String nicTokenId) {
        this.nicTokenId = nicTokenId;
    }

    public String getNicTokenShare() {
        return nicTokenShare;
    }

    public void setNicTokenShare(String nicTokenShare) {
        this.nicTokenShare = nicTokenShare;
    }

    public String getNewNicTokenId() {
        return newNicTokenId;
    }

    public void setNewNicTokenId(String newNicTokenId) {
        this.newNicTokenId = newNicTokenId;
    }

    public String getNewNicTokenShare() {
        return newNicTokenShare;
    }

    public void setNewNicTokenShare(String newNicTokenShare) {
        this.newNicTokenShare = newNicTokenShare;
    }

    public String getPassportTokenId() {
        return passportTokenId;
    }

    public void setPassportTokenId(String passportTokenId) {
        this.passportTokenId = passportTokenId;
    }

    public String getPassportTokenShare() {
        return passportTokenShare;
    }

    public void setPassportTokenShare(String passportTokenShare) {
        this.passportTokenShare = passportTokenShare;
    }

    public String getDLTokenId() {
        return DLTokenId;
    }

    public void setDLTokenId(String DLTokenId) {
        this.DLTokenId = DLTokenId;
    }

    public String getDLTokenShare() {
        return DLTokenShare;
    }

    public void setDLTokenShare(String DLTokenShare) {
        this.DLTokenShare = DLTokenShare;
    }

    public String getPasscodeTokenId() {
        return passcodeTokenId;
    }

    public void setPasscodeTokenId(String passcodeTokenId) {
        this.passcodeTokenId = passcodeTokenId;
    }

    public String getPasscodeTokenShare() {
        return passcodeTokenShare;
    }

    public void setPasscodeTokenShare(String passcodeTokenShare) {
        this.passcodeTokenShare = passcodeTokenShare;
    }

    public String getPaymentTokenId() {
        return paymentTokenId;
    }

    public void setPaymentTokenId(String paymentTokenId) {
        this.paymentTokenId = paymentTokenId;
    }

    public String getPaymentTokenShare() {
        return paymentTokenShare;
    }

    public void setPaymentTokenShare(String paymentTokenShare) {
        this.paymentTokenShare = paymentTokenShare;
    }

    public int getVerificationMedal() {
        return verificationMedal;
    }

    public void setVerificationMedal(int verificationMedal) {
        this.verificationMedal = verificationMedal;
    }

    public class Type {
        private String tokenType;
        private boolean transferable;
        private boolean linkable;
        private boolean executable;

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public boolean isTransferable() {
            return transferable;
        }

        public void setTransferable(boolean transferable) {
            this.transferable = transferable;
        }

        public boolean isLinkable() {
            return linkable;
        }

        public void setLinkable(boolean linkable) {
            this.linkable = linkable;
        }

        public boolean isExecutable() {
            return executable;
        }

        public void setExecutable(boolean executable) {
            this.executable = executable;
        }
    }
}
