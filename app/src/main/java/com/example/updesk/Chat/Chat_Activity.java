package com.example.updesk.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.updesk.ModelClasses.ChatMessage;
import com.example.updesk.ModelClasses.Employee;
import com.example.updesk.ModelClasses.Employer;
import com.example.updesk.Notifications.NotificationData;
import com.example.updesk.Notifications.PushNotification;
import com.example.updesk.Notifications.RetrofitInstance;
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.ActivityEmployerChat2Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.checkerframework.checker.units.qual.C;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chat_Activity extends AppCompatActivity {
    ActivityEmployerChat2Binding binding;
    private Employee recieverUserEmployee;
    private Employer recieverUserEmployer;

    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;
    String type;

    private String conversationID = null;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployerChat2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listenMessages();
        clickListeners();

    }

    private void clickListeners() {
        binding.Userinfobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View receiverUserDetailsPopupView=getLayoutInflater().inflate(R.layout.receiver_user_detail_popup,null);

                ImageView image= receiverUserDetailsPopupView.findViewById(R.id.ivReceiverPropfilePhoto);
                TextView name=receiverUserDetailsPopupView.findViewById(R.id.receiverName);
                TextView email=receiverUserDetailsPopupView.findViewById(R.id.receiverEmail);
                TextView oc=receiverUserDetailsPopupView.findViewById(R.id.receiverOC);
                TextView userType=receiverUserDetailsPopupView.findViewById(R.id.receiverUserType);
                loadRecieverDetails();
               
                if (type.equals("employee")) {
                    name.setText("Name: "+recieverUserEmployee.getEmployeeName());
                    email.setText(recieverUserEmployee.getEmployeeEmail());
                    oc.setText("OC: "+recieverUserEmployee.getEmployeeOC());
                    Glide.with(Chat_Activity.this).load(recieverUserEmployee.getEmployeeProfilePhotoUrl()).placeholder(R.drawable.person).into(image);
                    userType.setText("Employee");
                }
                else if(type.equals("employer"))
                {
                    name.setText("Name: "+recieverUserEmployer.getEmployerName());
                    email.setText(recieverUserEmployer.getEmployerEmail());
                    oc.setText("OC: "+recieverUserEmployer.getEmployerOC());
                    Glide.with(Chat_Activity.this).load(recieverUserEmployer.getEmployerProfilePhotoUrl()).placeholder(R.drawable.person).into(image);
                    userType.setText("Employer");
                }
                builder.setView(receiverUserDetailsPopupView);
                builder.create();
                builder.show();
            }
        });

    }


    private void init() {
        builder=new AlertDialog.Builder(this);
        type = getIntent().getStringExtra("userType");
        preferenceManager = new PreferenceManager(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        chatMessages = new ArrayList<>();
        if (type.equals("employee") && preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {

            chatAdapter = new ChatAdapter(chatMessages, preferenceManager.getString(CONSTANTS.EMPLOYEE_ID));

        } else if (type.equals("employee") && preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP)) {

            chatAdapter = new ChatAdapter(chatMessages, preferenceManager.getString(CONSTANTS.EMPLOYER_ID));

        } else if (type.equals("employer") && preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {

            chatAdapter = new ChatAdapter(chatMessages, preferenceManager.getString(CONSTANTS.EMPLOYEE_ID));

        } else if (type.equals("employer") && preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP)) {

            chatAdapter = new ChatAdapter(chatMessages, preferenceManager.getString(CONSTANTS.EMPLOYER_ID));
        }
        loadRecieverDetails();
        setListeners();

        binding.chatrecyclerview.setAdapter(chatAdapter);
    }


    private void sendMessage() {


        HashMap<String, Object> message = new HashMap<>();

        if (type.equals("employee") && preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP) == true) {
            message.put(CONSTANTS.KEY_SENDER_ID, preferenceManager.getString(CONSTANTS.EMPLOYEE_ID));
            message.put(CONSTANTS.KEY_RECIEVER_ID, recieverUserEmployee.getEmployeeID());

        } else if (type.equals("employee") && preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP) == true) {
             message.put(CONSTANTS.KEY_SENDER_ID, preferenceManager.getString(CONSTANTS.EMPLOYER_ID));
            message.put(CONSTANTS.KEY_RECIEVER_ID, recieverUserEmployee.getEmployeeID());
        } else if (type.equals("employer") && preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP) == true) {
            message.put(CONSTANTS.KEY_SENDER_ID, preferenceManager.getString(CONSTANTS.EMPLOYEE_ID));
            message.put(CONSTANTS.KEY_RECIEVER_ID, recieverUserEmployer.getEmployerID());
        } else if (type.equals("employer") && preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP) == true) {
            message.put(CONSTANTS.KEY_SENDER_ID, preferenceManager.getString(CONSTANTS.EMPLOYER_ID));
            message.put(CONSTANTS.KEY_RECIEVER_ID, recieverUserEmployer.getEmployerID());
        }
        message.put(CONSTANTS.KEY_MESSAGE, binding.inputTypeMsg.getText().toString().trim());
        message.put(CONSTANTS.KEY_TIME_STAMP, new Date());
        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CHAT).add(message);

        if(conversationID!=null){
            updateConversation(binding.inputTypeMsg.getText().toString().trim());
        }else {
            HashMap<String,Object> converstaion=new HashMap<>();
            if(preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
                if(type.equals("employee")) {
                    converstaion.put(CONSTANTS.KEY_SENDER_ID, preferenceManager.getString(CONSTANTS.EMPLOYEE_ID));
                    converstaion.put(CONSTANTS.KEY_SENDER_NAME, preferenceManager.getString(CONSTANTS.EMPLOYEE_NAME));
                    converstaion.put(CONSTANTS.KEY_SENDER_IMAGE, preferenceManager.getString(CONSTANTS.EMPLOYEE_PROFILE_PHOTO_URL));
                    converstaion.put(CONSTANTS.KEY_RECIEVER_ID, recieverUserEmployee.getEmployeeID());
                    converstaion.put(CONSTANTS.KEY_RECIEVER_NAME, recieverUserEmployee.getEmployeeName());
                    converstaion.put(CONSTANTS.KEY_RECIEVER_IMAGE, recieverUserEmployee.getEmployeeProfilePhotoUrl());
                    converstaion.put(CONSTANTS.KEY_LAST_MESSAGE, binding.inputTypeMsg.getText().toString().trim());
                    converstaion.put(CONSTANTS.KEY_TIME_STAMP, new Date());
                    addConversation(converstaion);
                }else{
                    converstaion.put(CONSTANTS.KEY_SENDER_ID, preferenceManager.getString(CONSTANTS.EMPLOYEE_ID));
                    converstaion.put(CONSTANTS.KEY_SENDER_NAME, preferenceManager.getString(CONSTANTS.EMPLOYEE_NAME));
                    converstaion.put(CONSTANTS.KEY_SENDER_IMAGE, preferenceManager.getString(CONSTANTS.EMPLOYEE_PROFILE_PHOTO_URL));
                    converstaion.put(CONSTANTS.KEY_RECIEVER_ID, recieverUserEmployer.getEmployerID());
                    converstaion.put(CONSTANTS.KEY_RECIEVER_NAME, recieverUserEmployer.getEmployerName());
                    converstaion.put(CONSTANTS.KEY_RECIEVER_IMAGE, recieverUserEmployer.getEmployerProfilePhotoUrl());
                    converstaion.put(CONSTANTS.KEY_LAST_MESSAGE, binding.inputTypeMsg.getText().toString().trim());
                    converstaion.put(CONSTANTS.KEY_TIME_STAMP, new Date());
                    addConversation(converstaion);
                }
            }
            else{
                if(type.equals("employee")) {
                    converstaion.put(CONSTANTS.KEY_SENDER_ID, preferenceManager.getString(CONSTANTS.EMPLOYER_ID));
                    converstaion.put(CONSTANTS.KEY_SENDER_NAME, preferenceManager.getString(CONSTANTS.EMPLOYER_NAME));
                    converstaion.put(CONSTANTS.KEY_SENDER_IMAGE, preferenceManager.getString(CONSTANTS.EMPLOYER_PROFILE_PHOTO_URL));
                    converstaion.put(CONSTANTS.KEY_RECIEVER_ID, recieverUserEmployee.getEmployeeID());
                    converstaion.put(CONSTANTS.KEY_RECIEVER_NAME, recieverUserEmployee.getEmployeeName());
                    converstaion.put(CONSTANTS.KEY_RECIEVER_IMAGE, recieverUserEmployee.getEmployeeProfilePhotoUrl());
                    converstaion.put(CONSTANTS.KEY_LAST_MESSAGE, binding.inputTypeMsg.getText().toString().trim());
                    converstaion.put(CONSTANTS.KEY_TIME_STAMP, new Date());
                    addConversation(converstaion);
                }else{
                    converstaion.put(CONSTANTS.KEY_SENDER_ID, preferenceManager.getString(CONSTANTS.EMPLOYER_ID));
                    converstaion.put(CONSTANTS.KEY_SENDER_NAME, preferenceManager.getString(CONSTANTS.EMPLOYER_NAME));
                    converstaion.put(CONSTANTS.KEY_SENDER_IMAGE, preferenceManager.getString(CONSTANTS.EMPLOYER_PROFILE_PHOTO_URL));
                    converstaion.put(CONSTANTS.KEY_RECIEVER_ID, recieverUserEmployer.getEmployerID());
                    converstaion.put(CONSTANTS.KEY_RECIEVER_NAME, recieverUserEmployer.getEmployerName());
                    converstaion.put(CONSTANTS.KEY_RECIEVER_IMAGE, recieverUserEmployer.getEmployerProfilePhotoUrl());
                    converstaion.put(CONSTANTS.KEY_LAST_MESSAGE, binding.inputTypeMsg.getText().toString().trim());
                    converstaion.put(CONSTANTS.KEY_TIME_STAMP, new Date());
                    addConversation(converstaion);
                }

            }
        }
        if(type.equals("employee")){
        try {
            JSONArray tokens=new JSONArray();
            tokens.put(recieverUserEmployee.getFcmToken());
            JSONObject data=new JSONObject();
            data.put(CONSTANTS.EMPLOYEE_ID,preferenceManager.getString(CONSTANTS.EMPLOYEE_ID));
            data.put(CONSTANTS.EMPLOYEE_NAME,preferenceManager.getString(CONSTANTS.EMPLOYEE_NAME));
            data.put(CONSTANTS.KEY_FCM_TOKEN,preferenceManager.getString(CONSTANTS.KEY_FCM_TOKEN));
            data.put(CONSTANTS.KEY_MESSAGE,binding.inputTypeMsg.getText().toString().trim());

            JSONObject body=new JSONObject();
            body.put(CONSTANTS.REMOTE_MSG_DATA,data);
            body.put(CONSTANTS.REMOTE_MSG_REGISTRATION_IDS,tokens);
            //sendNotification(body.toString());


        }catch (Exception e){
            Toast.makeText(this,"Send Message:"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }}else{
            try {
                JSONArray tokens=new JSONArray();
                tokens.put(recieverUserEmployer.getFcmToken());
                JSONObject data=new JSONObject();
                data.put(CONSTANTS.EMPLOYER_ID,preferenceManager.getString(CONSTANTS.EMPLOYER_ID));
                data.put(CONSTANTS.EMPLOYER_NAME,preferenceManager.getString(CONSTANTS.EMPLOYER_NAME));
                data.put(CONSTANTS.KEY_FCM_TOKEN,preferenceManager.getString(CONSTANTS.KEY_FCM_TOKEN));
                data.put(CONSTANTS.KEY_MESSAGE,binding.inputTypeMsg.getText().toString().trim());

                JSONObject body=new JSONObject();
                body.put(CONSTANTS.REMOTE_MSG_DATA,data);
                body.put(CONSTANTS.REMOTE_MSG_REGISTRATION_IDS,tokens);
//                sendNotification(body.toString());



            }catch (Exception e){
                Toast.makeText(this,"Send Message:"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        binding.inputTypeMsg.setText(null);
    }
//private void sendNotification(String msgBody){
//        ApiClient.getClient().create(ApiService.class).sendMessage(CONSTANTS.getRemoteMsgHeaders(),msgBody)
//                .enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
//
//                        if(response.isSuccessful()){
//                            try{
//                                if(response.body()!=null){
//                                    JSONObject responseJson=new JSONObject(response.body());
//                                    JSONArray results=responseJson.getJSONArray("results");
//                                    if(responseJson.getInt("failure")==1){
//                                        JSONObject error=(JSONObject) results.get(0);
//                                        Toast.makeText(Chat_Activity.this, error.getString("error"), Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//
//                                }
//                            }catch (JSONException e){
//                                e.printStackTrace();
//                            }
//                            Toast.makeText(Chat_Activity.this, "Notification Sent Successfully", Toast.LENGTH_SHORT).show();
//                        }else{
//                            Toast.makeText(Chat_Activity.this, "Error: "+ response.code(), Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                        Toast.makeText(Chat_Activity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//}
    private void listenMessages() {

        //showing all sent messages to a specific reciever
        if (type.equals("employee")) {
            if (preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {

                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CHAT)
                        .whereEqualTo(CONSTANTS.KEY_SENDER_ID, preferenceManager.getString(CONSTANTS.EMPLOYEE_ID))
                        .whereEqualTo(CONSTANTS.KEY_RECIEVER_ID, recieverUserEmployee.getEmployeeID())
                        .addSnapshotListener(eventListener);
            } else if (preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP)) {

                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CHAT)
                        .whereEqualTo(CONSTANTS.KEY_SENDER_ID, preferenceManager.getString(CONSTANTS.EMPLOYER_ID))
                        .whereEqualTo(CONSTANTS.KEY_RECIEVER_ID, recieverUserEmployee.getEmployeeID())
                        .addSnapshotListener(eventListener);
            }

        } else if (type.equals("employer")) {
            if (preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CHAT)
                        .whereEqualTo(CONSTANTS.KEY_SENDER_ID, preferenceManager.getString(CONSTANTS.EMPLOYEE_ID))
                        .whereEqualTo(CONSTANTS.KEY_RECIEVER_ID, recieverUserEmployer.getEmployerID())
                        .addSnapshotListener(eventListener);
            } else if (preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP)) {
                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CHAT)
                        .whereEqualTo(CONSTANTS.KEY_SENDER_ID, preferenceManager.getString(CONSTANTS.EMPLOYER_ID))
                        .whereEqualTo(CONSTANTS.KEY_RECIEVER_ID, recieverUserEmployer.getEmployerID())
                        .addSnapshotListener(eventListener);
            }

        }

        //for showing all recieved messages from a specific reciever
        if (type.equals("employee")) {
            if (preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CHAT)
                        .whereEqualTo(CONSTANTS.KEY_SENDER_ID, recieverUserEmployee.getEmployeeID())
                        .whereEqualTo(CONSTANTS.KEY_RECIEVER_ID, preferenceManager.getString(CONSTANTS.EMPLOYEE_ID))
                        .addSnapshotListener(eventListener);
            } else {
                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CHAT)
                        .whereEqualTo(CONSTANTS.KEY_SENDER_ID, recieverUserEmployee.getEmployeeID())
                        .whereEqualTo(CONSTANTS.KEY_RECIEVER_ID, preferenceManager.getString(CONSTANTS.EMPLOYER_ID))
                        .addSnapshotListener(eventListener);
            }
        } else //type==employer
        {
            if (preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CHAT)
                        .whereEqualTo(CONSTANTS.KEY_SENDER_ID, recieverUserEmployer.getEmployerID())
                        .whereEqualTo(CONSTANTS.KEY_RECIEVER_ID, preferenceManager.getString(CONSTANTS.EMPLOYEE_ID))
                        .addSnapshotListener(eventListener);
            } else {
                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CHAT)
                        .whereEqualTo(CONSTANTS.KEY_SENDER_ID, recieverUserEmployer.getEmployerID())
                        .whereEqualTo(CONSTANTS.KEY_RECIEVER_ID, preferenceManager.getString(CONSTANTS.EMPLOYER_ID))
                        .addSnapshotListener(eventListener);
            }
        }
    }


    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(CONSTANTS.KEY_SENDER_ID);
                    chatMessage.recieverId = documentChange.getDocument().getString(CONSTANTS.KEY_RECIEVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(CONSTANTS.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(CONSTANTS.KEY_TIME_STAMP));

                    chatMessage.dateObject = documentChange.getDocument().getDate(CONSTANTS.KEY_TIME_STAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatrecyclerview.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatrecyclerview.setVisibility(View.VISIBLE);
        }
        binding.progressbar.setVisibility(View.GONE);
        if(conversationID==null){
            checkForConversation();
        }
    };

    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> {
            onBackPressed();
        });


        binding.frameLayout.setOnClickListener(view -> {
            sendMessage();
            //Sending Notification
            PushNotification notification = new PushNotification(
                    new NotificationData("New message",
                            binding.inputTypeMsg.getText().toString().trim()),
                    preferenceManager.getString(CONSTANTS.TOKEN_ID) // either pass topic or tokenId
            );


        });
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm:ss a", Locale.getDefault()).format(date);
    }

    private void loadRecieverDetails() {

        if (type.equals("employee")) {
            recieverUserEmployee = (Employee) getIntent().getParcelableExtra("userObject");
            binding.textname.setText(recieverUserEmployee.getEmployeeName());
        } else if(type.equals("employer")) {
            recieverUserEmployer = (Employer) getIntent().getParcelableExtra("userObject");
            binding.textname.setText(recieverUserEmployer.getEmployerName());
        }else{
            recieverUserEmployer = (Employer) getIntent().getParcelableExtra("userObject");
            binding.textname.setText(recieverUserEmployer.getEmployerName());
        }
    }

    private void addConversation(HashMap<String,Object> conversation){
        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CONVERSATION)
                .add(conversation)
                .addOnSuccessListener(documentReference -> {
                    conversationID=documentReference.getId();
                });

    }

    private void updateConversation(String message){
        DocumentReference documentReference=firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CONVERSATION)
                .document(conversationID);
        documentReference.update(
                CONSTANTS.KEY_LAST_MESSAGE,message,
                CONSTANTS.KEY_TIME_STAMP,new Date()
        );
    }
    private void checkForConversation(){
        if(chatMessages.size()!=0){
            if(preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
                if(type.equals("employee")) {
                    checkForConversationRemotely(preferenceManager.getString(CONSTANTS.EMPLOYEE_ID),
                            recieverUserEmployee.getEmployeeID());
                    checkForConversationRemotely(recieverUserEmployee.getEmployeeID(),
                            preferenceManager.getString(CONSTANTS.EMPLOYEE_ID));
                }
                else{
                    checkForConversationRemotely(preferenceManager.getString(CONSTANTS.EMPLOYEE_ID),
                            recieverUserEmployer.getEmployerID());
                    checkForConversationRemotely(recieverUserEmployer.getEmployerID(),
                            preferenceManager.getString(CONSTANTS.EMPLOYEE_ID));
                }
            }
            else{
                if(type.equals("employee")) {
                    checkForConversationRemotely(preferenceManager.getString(CONSTANTS.EMPLOYER_ID),
                            recieverUserEmployee.getEmployeeID());
                    checkForConversationRemotely(recieverUserEmployee.getEmployeeID(),
                            preferenceManager.getString(CONSTANTS.EMPLOYER_ID));
                }
                else{
                    checkForConversationRemotely(preferenceManager.getString(CONSTANTS.EMPLOYER_ID),
                            recieverUserEmployer.getEmployerID());
                    checkForConversationRemotely(recieverUserEmployer.getEmployerID(),
                            preferenceManager.getString(CONSTANTS.EMPLOYER_ID));
                }

            }
        }
    }

    private void checkForConversationRemotely(String senderID, String recieverID) {
        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CONVERSATION)
                .whereEqualTo(CONSTANTS.KEY_SENDER_ID, senderID)
                .whereEqualTo(CONSTANTS.KEY_RECIEVER_ID, recieverID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                                                   DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                                   conversationID = documentSnapshot.getId();
                                               }
                                           }
                                       }
                );


    }



}