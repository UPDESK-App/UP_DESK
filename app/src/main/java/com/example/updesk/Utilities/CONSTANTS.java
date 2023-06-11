package com.example.updesk.Utilities;

import java.util.HashMap;

public class CONSTANTS {
    public static final String IS_EMPLOYER_SIGN_UP = "IS_EMPLOYER_SIGN_UP";
    public static final String IS_EMPLOYEE_SIGN_UP = "IS_EMPLOYEE_SIGN_UP";
    public static final String KEY_COLLECTION_EMPLOYER = "Employer";
    public static final String KEY_COLLECTION_EMPLOYEE = "Employee";
    //Employer's Constant Keys
    public static final String EMPLOYER_ID = "employerID";
    public static final String EMPLOYER_NAME = "employerName";
    public static final String EMPLOYER_EMAIL = "employerEmail";
    public static final String EMPLOYER_PASSWORD = "employerPassword";
    public static final String EMPLOYER_OC = "employerOC";
    public static final String EMPLOYER_PROFILE_PHOTO_URL = "employerProfilePhotoUrl";

//Employee's Constant Keys
    public static final String EMPLOYEE_ID = "employeeID";
    public static final String EMPLOYEE_NAME = "employeeName";
    public static final String EMPLOYEE_EMAIL = "employeeEmail";
    public static final String EMPLOYEE_PASSWORD = "employeePassword";
    public static final String EMPLOYEE_OC = "employeeOC";
    public static final String EMPLOYEE_PROFILE_PHOTO_URL = "employeeProfilePhotoUrl";
    public static final String SENDING_EMAIL = "restock.hms@gmail.com";
    public static final String SENDING_EMAIL_PASSWORD = "itrhhniyjaaneoyz";
    public static  int cc=0 ;

   public static String resetkey="";
    public static String resetemail="";

    public static  String resetusrID="";

    public static  String resetPassowrd="";

    public static String KEY_COLLECTION_CHAT="chat";

    public static String KEY_SENDER_ID="senderId";
    public static String KEY_RECIEVER_ID="recieverId";
    public static String KEY_MESSAGE="message";
    public static String KEY_TIME_STAMP="timeStamp";
    public static String KEY_FCM_TOKEN="fcmToken";

    public  static  final String KEY_COLLECTION_CONVERSATION="conversations";
    public  static  final String KEY_SENDER_NAME="senderName";
    public  static  final String KEY_RECIEVER_NAME="recieverName";
    public  static  final String KEY_LAST_MESSAGE="lastMessage";
    public static final String KEY_SENDER_IMAGE="senderImageUrl";
    public static final String KEY_RECIEVER_IMAGE="recieverImageUrl";


//task's constant keys
    public static String KEY_COLLECTION_TASK="Tasks";
    public static String KEY_TASK_NAME="taskFileName";
    public static String KEY_TASK_FILE_URL="taskFileUrl";
    public static String KEY_TASK_USER_NAME="uploadedBy";
    public static String KEY_TASK_USER_OC="OC";
    public static String KEY_TASK_Status="status";
    public static String KEY_TASK_USER_TYPE="userType";
    public static String KEY_TASK_DESCRIPTION="description";

///Attendance keys

    public static String KEY_COLLECTION_ATTENDANCE="Attendance";
    public static String KEY_ATTENDANCE_NAME="name";
    public static String KEY_ATTENDANCE_ID="id";
    public static String KEY_ATTENDANCE_WORKED_HOURS="workedHours";
    public static String KEY_PAYMENT_PER_HOUR="paymentPerHour";
    public static String KEY_ATTENDANCE_STATUS="attendanceStatus";
    public static String KEY_ATTENDANCE_DATE="date";
    public static String KEY_ATTENDANCE_OC="oc";


    ////Comment  Keys

    public static String KEY_COMMENT_COLLECTION="comments";
    public static String KEY_COMMENT_MESSAGE="message";
    public static String KEY_COMMENT_TASK_FILE_NAME="DocumentName";
    public static String KEY_COMMENT_SENDER_NAME="sendername";
    public static String KEY_COMMENT_DATE_TIME="datetime";




    //push Notification


    public final static  String REMOTE_MSG_AUTHORIZATION="Authorization";
    public final static  String REMOTE_MSG_CONTENT_TYPE="Content-Type";
    public final static  String REMOTE_MSG_DATA="data";
    public final static  String REMOTE_MSG_REGISTRATION_IDS="registration_ids";


//forNotifications
public final static  String SERVER_KEY="AAAANhvPk_s:APA91bFyf7Ib4JWlK274MF6Jfoxmwodb15xFGcDz4kk_mBWWnHvx93wHx2CR4I2eJcDbV4FgC0V7QMuN7bnX7hrHcknCvhBgxazUXxv0JVMI7Xs1e5zoGgQQyV2gBX6KxGa17NGbLulK";
    public static final String CONTENT_TYPE = "application/json";
    public static final String BASE_URL = "https://fcm.googleapis.com/";
    public static final String TOKEN_ID = "TOKEN_ID";
    public static final String TOPIC = "Topic";

    public static HashMap<String,String > remoteMsgHeaders=null;
  public static HashMap<String,String > getRemoteMsgHeaders(){
      if(remoteMsgHeaders==null){
          remoteMsgHeaders=new HashMap<>();
          remoteMsgHeaders.put(REMOTE_MSG_AUTHORIZATION,"key=AAAANhvPk_s:APA91bFyf7Ib4JWlK274MF6Jfoxmwodb15xFGcDz4kk_mBWWnHvx93wHx2CR4I2eJcDbV4FgC0V7QMuN7bnX7hrHcknCvhBgxazUXxv0JVMI7Xs1e5zoGgQQyV2gBX6KxGa17NGbLulK"
);
          remoteMsgHeaders.put(REMOTE_MSG_CONTENT_TYPE,"application/json");
      }
      return remoteMsgHeaders;

  }


////////Help Keys

    public final static  String KEY_COLLECTION_HELP_QUESTIONS="questions";
    public final static  String KEY_HELP_QUESTION_STRING="question";
    public final static  String KEY_HELP_QUESTION_ID="questionID";
    public final static  String KEY_HELP_ANSWER_COLLECTION="answers";
    public final static  String KEY_HELP_ANSWER_STRING="answer";

}
