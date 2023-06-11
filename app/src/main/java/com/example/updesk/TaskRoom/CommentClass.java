package com.example.updesk.TaskRoom;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CommentClass implements Parcelable {
    String comment,commentSendername,commentDateTime,DocumentName;



    protected CommentClass(Parcel in) {
        comment = in.readString();
        commentSendername = in.readString();
        commentDateTime = in.readString();
        DocumentName = in.readString();
    }

    public static final Creator<CommentClass> CREATOR = new Creator<CommentClass>() {
        @Override
        public CommentClass createFromParcel(Parcel in) {
            return new CommentClass(in);
        }

        @Override
        public CommentClass[] newArray(int size) {
            return new CommentClass[size];
        }
    };

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentSendername() {
        return commentSendername;
    }

    public void setCommentSendername(String commentSendername) {
        this.commentSendername = commentSendername;
    }

    public String getCommentDateTime() {
        return commentDateTime;
    }

    public void setCommentDateTime(String commentDateTime) {
        this.commentDateTime = commentDateTime;
    }

    public String getDocumentName() {
        return DocumentName;
    }

    public void setDocumentName(String documentName) {
        DocumentName = documentName;
    }

    public CommentClass(String comment, String commentSendername, String commentDateTime, String documentName) {
        this.comment = comment;
        this.commentSendername = commentSendername;
        this.commentDateTime = commentDateTime;
        DocumentName = documentName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(comment);
        parcel.writeString(commentSendername);
        parcel.writeString(commentDateTime);
        parcel.writeString(DocumentName);
    }
}
