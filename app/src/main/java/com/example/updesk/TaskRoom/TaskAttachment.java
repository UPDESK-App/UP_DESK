package com.example.updesk.TaskRoom;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TaskAttachment  implements Parcelable{
    public String taskFileName,taskFileUrl,uploadedBy,OC,taskStatus,userType,taskDescription;

    protected TaskAttachment(Parcel in) {
        taskFileName = in.readString();
        taskFileUrl = in.readString();
        uploadedBy = in.readString();
        OC = in.readString();
        taskStatus = in.readString();
        userType = in.readString();
        taskDescription = in.readString();
    }

    public static final Creator<TaskAttachment> CREATOR = new Creator<TaskAttachment>() {
        @Override
        public TaskAttachment createFromParcel(Parcel in) {
            return new TaskAttachment(in);
        }

        @Override
        public TaskAttachment[] newArray(int size) {
            return new TaskAttachment[size];
        }
    };

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public TaskAttachment(String taskFileName, String taskFileUrl, String uploadedBy, String OC, String taskStatus, String userType, String taskDescription) {
        this.taskFileName = taskFileName;
        this.taskFileUrl = taskFileUrl;
        this.uploadedBy = uploadedBy;
        this.OC = OC;
        this.taskStatus = taskStatus;
        this.userType = userType;
        this.taskDescription=taskDescription;
    }



    public String getTaskFileName() {
        return taskFileName;
    }

    public void setTaskFileName(String taskFileName) {
        this.taskFileName = taskFileName;
    }

    public String getTaskFileUrl() {
        return taskFileUrl;
    }

    public void setTaskFileUrl(String taskFileUrl) {
        this.taskFileUrl = taskFileUrl;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getOC() {
        return OC;
    }

    public void setOC(String OC) {
        this.OC = OC;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(taskFileName);
        parcel.writeString(taskFileUrl);
        parcel.writeString(uploadedBy);
        parcel.writeString(OC);
        parcel.writeString(taskStatus);
        parcel.writeString(userType);
        parcel.writeString(taskDescription);
    }
}
