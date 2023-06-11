package com.example.updesk.Fragments;

import android.content.Intent;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.updesk.Chat.ContactsList;
import com.example.updesk.Chat.RecentConversationAdapter;
import com.example.updesk.ModelClasses.ChatMessage;
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.FragmentMessengerBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Messenger extends Fragment {


    FirebaseFirestore firebaseFirestore;
    PreferenceManager prefrenceManager;
    FragmentMessengerBinding binding;

    String id = null;

    private List<ChatMessage> conversations;
    private RecentConversationAdapter recentConversationAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMessengerBinding.inflate(inflater, container, false);
        inflater.inflate(R.layout.fragment_messenger, container, false);
        init();


        setListeners();
        listenConversations();
        return (binding.getRoot());
    }


    private void init() {
        prefrenceManager = new PreferenceManager(getContext());


        firebaseFirestore = FirebaseFirestore.getInstance();
        conversations = new ArrayList<>();
        recentConversationAdapter = new RecentConversationAdapter(conversations, getContext());

        binding.rvConversations.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        binding.rvConversations.setAdapter(recentConversationAdapter);
    }

    private void setListeners() {
        binding.fabNewChat.setOnClickListener(view -> {
            Intent intent=new Intent(getActivity(), ContactsList.class);
            startActivity(intent);
        });
    }

    private void listenConversations() {
        if (prefrenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {

            firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CONVERSATION)
                    .whereEqualTo(CONSTANTS.KEY_SENDER_ID, prefrenceManager.getString(CONSTANTS.EMPLOYEE_ID))
                    .addSnapshotListener(eventListener);
            firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CONVERSATION)
                    .whereEqualTo(CONSTANTS.KEY_RECIEVER_ID, prefrenceManager.getString(CONSTANTS.EMPLOYEE_ID))
                    .addSnapshotListener(eventListener);
        }
        else {
            firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CONVERSATION)
                    .whereEqualTo(CONSTANTS.KEY_SENDER_ID, prefrenceManager.getString(CONSTANTS.EMPLOYER_ID))
                    .addSnapshotListener(eventListener);
            firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CONVERSATION)
                    .whereEqualTo(CONSTANTS.KEY_RECIEVER_ID, prefrenceManager.getString(CONSTANTS.EMPLOYER_ID))
                    .addSnapshotListener(eventListener);

        }

    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {

            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(CONSTANTS.KEY_SENDER_ID);
                    chatMessage.recieverId = documentChange.getDocument().getString(CONSTANTS.KEY_RECIEVER_ID);
                    if (prefrenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
                        id = prefrenceManager.getString(CONSTANTS.EMPLOYEE_ID);
                    }
                    else {
                        id = prefrenceManager.getString(CONSTANTS.EMPLOYER_ID);
                    }
                    if (id.equals(chatMessage.senderId)) {

                        chatMessage.conversationImage = documentChange.getDocument().getString(CONSTANTS.KEY_RECIEVER_IMAGE);
                        chatMessage.conversationName = documentChange.getDocument().getString(CONSTANTS.KEY_RECIEVER_NAME);
                        chatMessage.conversationID = documentChange.getDocument().getString(CONSTANTS.KEY_RECIEVER_ID);
                    } else {
                        chatMessage.conversationImage = documentChange.getDocument().getString(CONSTANTS.KEY_SENDER_IMAGE);
                        chatMessage.conversationName = documentChange.getDocument().getString(CONSTANTS.KEY_SENDER_NAME);
                        chatMessage.conversationID = documentChange.getDocument().getString(CONSTANTS.KEY_SENDER_ID);
                    }

                    chatMessage.message = documentChange.getDocument().getString(CONSTANTS.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(CONSTANTS.KEY_TIME_STAMP);
                    chatMessage.conversationDocumentReferenceId=documentChange.getDocument().getId();
                    conversations.add(chatMessage);



                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversations.size(); i++) {
                        String senderID = documentChange.getDocument().getString(CONSTANTS.KEY_SENDER_ID);
                        String receiverID = documentChange.getDocument().getString(CONSTANTS.KEY_RECIEVER_ID);
                        if (conversations.get(i).senderId.equals(senderID) && conversations.get(i).recieverId.equals(receiverID)) {
                            conversations.get(i).message = documentChange.getDocument().getString(CONSTANTS.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(CONSTANTS.KEY_TIME_STAMP);
                            break;
                        }

                    }
                }
            }

            Collections.sort(conversations, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            Collections.reverse(conversations);
            recentConversationAdapter.notifyDataSetChanged();
            binding.rvConversations.smoothScrollToPosition(0);
            binding.rvConversations.setVisibility(View.VISIBLE);


        }


    };
}
