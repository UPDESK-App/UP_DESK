package com.example.updesk.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class ChatMessage implements Parcelable{
    public String senderId,recieverId,message,dateTime,conversationID,conversationName,conversationImage,conversationDocumentReferenceId;
    public Date dateObject;

    public ChatMessage() {
    }


    protected ChatMessage(Parcel in) {
        senderId = in.readString();
        recieverId = in.readString();
        message = in.readString();
        dateTime = in.readString();
        conversationID = in.readString();
        conversationName = in.readString();
        conversationImage = in.readString();
        conversationDocumentReferenceId = in.readString();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(senderId);
        parcel.writeString(recieverId);
        parcel.writeString(message);
        parcel.writeString(dateTime);
        parcel.writeString(conversationID);
        parcel.writeString(conversationName);
        parcel.writeString(conversationImage);
        parcel.writeString(conversationDocumentReferenceId);
    }
}
