package com.example.updesk.Chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.updesk.ModelClasses.ChatMessage;
import com.example.updesk.databinding.ItemContainerRecieveMessageBinding;
import com.example.updesk.databinding.ItemContainerSentMessageBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final String senderId;

    private static final int View_Type_Sent = 1;
    private static final int View_Type_Recieved = 2;



    public ChatAdapter(List<ChatMessage> chatMessages, String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == View_Type_Sent) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false
                    )
            );

        } else {
            return new RecievedMessageViewHolder((
                    ItemContainerRecieveMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false)));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (getItemViewType(position) == View_Type_Sent) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));

        } else {
            ((RecievedMessageViewHolder) holder).setData(chatMessages.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)) {
            return View_Type_Sent;
        } else {
            return View_Type_Recieved;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.txtdatetime.setText(chatMessage.dateTime);
        }
    }

    static class RecievedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerRecieveMessageBinding binding;

        RecievedMessageViewHolder(ItemContainerRecieveMessageBinding itemContainerRecieveMessageBinding) {
            super(itemContainerRecieveMessageBinding.getRoot());
            binding = itemContainerRecieveMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.txtdatetime.setText(chatMessage.dateTime);
        }
    }


}
