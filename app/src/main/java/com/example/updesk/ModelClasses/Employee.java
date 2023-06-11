package com.example.updesk.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Employee implements Parcelable  {
    private  String EmployeeID, EmployeeName,EmployeeEmail,EmployeeOC,
            EmployeePassword,EmployeeProfilePhotoUrl,FcmToken;

    public Employee(String employeeID, String employeeName, String employeeEmail, String fcmToken) {
        EmployeeID = employeeID;
        EmployeeName = employeeName;
        EmployeeEmail = employeeEmail;
        FcmToken = fcmToken;
    }
public Employee(){

}
    public Employee(String employeeID, String employeeName, String employeeEmail, String employeeOC, String employeePassword, String employeeProfilePhotoUrl, String fcmToken) {
        EmployeeID = employeeID;
        EmployeeName = employeeName;
        EmployeeEmail = employeeEmail;
        EmployeeOC = employeeOC;
        EmployeePassword = employeePassword;
        EmployeeProfilePhotoUrl = employeeProfilePhotoUrl;
        FcmToken = fcmToken;
    }

    protected Employee(Parcel in) {
        EmployeeID = in.readString();
        EmployeeName = in.readString();
        EmployeeEmail = in.readString();
        EmployeeOC = in.readString();
        EmployeePassword = in.readString();
        EmployeeProfilePhotoUrl = in.readString();
        FcmToken = in.readString();
    }

    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public Employee createFromParcel(Parcel in) {
            return new Employee(in);
        }

        @Override
        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };

    public String getFcmToken() {
        return FcmToken;
    }

    public void setFcmToken(String fcmToken) {
        FcmToken = fcmToken;
    }

    public String getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(String employeeID) {
        EmployeeID = employeeID;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getEmployeeEmail() {
        return EmployeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        EmployeeEmail = employeeEmail;
    }

    public String getEmployeeOC() {
        return EmployeeOC;
    }

    public void setEmployeeOC(String employeeOC) {
        EmployeeOC = employeeOC;
    }

    public String getEmployeePassword() {
        return EmployeePassword;
    }

    public void setEmployeePassword(String employeePassword) {
        EmployeePassword = employeePassword;
    }

    public String getEmployeeProfilePhotoUrl() {
        return EmployeeProfilePhotoUrl;
    }

    public void setEmployeeProfilePhotoUrl(String employeeProfilePhotoUrl) {
        EmployeeProfilePhotoUrl = employeeProfilePhotoUrl;
    }

    public Employee(String employeeID, String employeeName, String employeeEmail, String employeeOC, String employeePassword, String employeeProfilePhotoUrl) {
        EmployeeID = employeeID;
        EmployeeName = employeeName;
        EmployeeEmail = employeeEmail;
        EmployeeOC = employeeOC;
        EmployeePassword = employeePassword;
        EmployeeProfilePhotoUrl = employeeProfilePhotoUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(EmployeeID);
        parcel.writeString(EmployeeName);
        parcel.writeString(EmployeeEmail);
        parcel.writeString(EmployeeOC);
        parcel.writeString(EmployeePassword);
        parcel.writeString(EmployeeProfilePhotoUrl);
        parcel.writeString(FcmToken);
    }
}
