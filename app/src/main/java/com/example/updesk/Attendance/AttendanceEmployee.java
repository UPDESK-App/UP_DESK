package com.example.updesk.Attendance;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AttendanceEmployee implements Parcelable {
    private String id;
    private String name;

    private String oc;
    private String attendanceStatus;

    private String workedHours;
    private String paymentPerHour;
    private String date;


    protected AttendanceEmployee(Parcel in) {
        id = in.readString();
        name = in.readString();
        oc = in.readString();
        attendanceStatus = in.readString();
        workedHours = in.readString();
        paymentPerHour = in.readString();
        date = in.readString();
    }

    public static final Creator<AttendanceEmployee> CREATOR = new Creator<AttendanceEmployee>() {
        @Override
        public AttendanceEmployee createFromParcel(Parcel in) {
            return new AttendanceEmployee(in);
        }

        @Override
        public AttendanceEmployee[] newArray(int size) {
            return new AttendanceEmployee[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getOc() {
        return oc;
    }

    public void setOc(String oc) {
        this.oc = oc;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(String workedHours) {
        this.workedHours = workedHours;
    }

    public String getPaymentPerHour() {
        return paymentPerHour;
    }

    public void setPaymentPerHour(String paymentPerHour) {
        this.paymentPerHour = paymentPerHour;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public AttendanceEmployee(String id, String name, String oc, String attendanceStatus, String workedHours, String paymentPerHour, String date) {
        this.id = id;
        this.name = name;
        this.oc = oc;
        this.attendanceStatus = attendanceStatus;
        this.workedHours = workedHours;
        this.paymentPerHour = paymentPerHour;
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(oc);
        parcel.writeString(attendanceStatus);
        parcel.writeString(workedHours);
        parcel.writeString(paymentPerHour);
        parcel.writeString(date);
    }
}
