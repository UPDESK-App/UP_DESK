package com.example.updesk.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Employer implements  Parcelable {
        private  String EmployerID;
    private String EmployerName;
    private String EmployerEmail;

    public Employer(String employerID, String employerName, String employerEmail, String employerOC, String employerPassword, String employerProfilePhotoUrl, String fcmToken) {
        EmployerID = employerID;
        EmployerName = employerName;
        EmployerEmail = employerEmail;
        EmployerOC = employerOC;
        EmployerPassword = employerPassword;
        EmployerProfilePhotoUrl = employerProfilePhotoUrl;
        this.fcmToken = fcmToken;
    }

    private String EmployerOC;
    private String EmployerPassword;
    private String EmployerProfilePhotoUrl;

    public Employer(String employerID, String employerName, String employerEmail, String fcmToken) {
        EmployerID = employerID;
        EmployerName = employerName;
        EmployerEmail = employerEmail;
        this.fcmToken = fcmToken;
    }
public Employer(){

}
    protected Employer(Parcel in) {
        EmployerID = in.readString();
        EmployerName = in.readString();
        EmployerEmail = in.readString();
        EmployerOC = in.readString();
        EmployerPassword = in.readString();
        EmployerProfilePhotoUrl = in.readString();
        fcmToken = in.readString();
    }

    public static final Creator<Employer> CREATOR = new Creator<Employer>() {
        @Override
        public Employer createFromParcel(Parcel in) {
            return new Employer(in);
        }

        @Override
        public Employer[] newArray(int size) {
            return new Employer[size];
        }
    };

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    private String fcmToken;

    public Employer(String employerID, String employerName, String employerEmail, String employerOC, String employerPassword, String employerProfilePhotoUrl) {
        EmployerID = employerID;
        EmployerName = employerName;
        EmployerEmail = employerEmail;
        EmployerOC = employerOC;
        EmployerPassword = employerPassword;
        EmployerProfilePhotoUrl = employerProfilePhotoUrl;
    }





    public String getEmployerID() {
        return EmployerID;
    }

    public void setEmployerID(String employerID) {
        EmployerID = employerID;
    }

    public String getEmployerName() {
        return EmployerName;
    }

    public void setEmployerName(String employerName) {
        EmployerName = employerName;
    }

    public String getEmployerEmail() {
        return EmployerEmail;
    }

    public void setEmployerEmail(String employerEmail) {
        EmployerEmail = employerEmail;
    }

    public String getEmployerOC() {
        return EmployerOC;
    }

    public void setEmployerOC(String employerOC) {
        EmployerOC = employerOC;
    }

    public String getEmployerPassword() {
        return EmployerPassword;
    }

    public void setEmployerPassword(String employerPassword) {
        EmployerPassword = employerPassword;
    }

    public String getEmployerProfilePhotoUrl() {
        return EmployerProfilePhotoUrl;
    }

    public void setEmployerProfilePhotoUrl(String employerProfilePhotoUrl) {
        EmployerProfilePhotoUrl = employerProfilePhotoUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(EmployerID);
        parcel.writeString(EmployerName);
        parcel.writeString(EmployerEmail);
        parcel.writeString(EmployerOC);
        parcel.writeString(EmployerPassword);
        parcel.writeString(EmployerProfilePhotoUrl);
        parcel.writeString(fcmToken);
    }
}
