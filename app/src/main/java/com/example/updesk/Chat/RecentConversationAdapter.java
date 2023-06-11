package com.example.updesk.Chat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.updesk.ModelClasses.ChatMessage;
import com.example.updesk.ModelClasses.Employee;
import com.example.updesk.ModelClasses.Employer;
import com.example.updesk.ModelClasses.JavaMailAPI;
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.databinding.ChatUserLayoutBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Collections;
import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ConversationsViewHolder> {

    private final List<ChatMessage> chatMessages;
    Boolean receiverFound = false;


    private final Context context;

    public RecentConversationAdapter(List<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;

        this.context = context;


    }

    @NonNull
    @Override
    public ConversationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ConversationsViewHolder(ChatUserLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        ));


    }

    @Override
    public void onBindViewHolder(@NonNull ConversationsViewHolder holder, int position) {


        holder.setData(chatMessages.get(holder.getAdapterPosition()));

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE)
                .whereEqualTo(CONSTANTS.EMPLOYEE_ID, chatMessages.get(holder.getAdapterPosition()).recieverId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                        receiverFound = true;
                        Employee receiverUser = new Employee();
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        receiverUser.setEmployeeID(documentSnapshot.getId());
                        receiverUser.setEmployeeName(documentSnapshot.getString(CONSTANTS.EMPLOYEE_NAME));
                        receiverUser.setEmployeeEmail(documentSnapshot.getString(CONSTANTS.EMPLOYEE_EMAIL));
                        receiverUser.setEmployeePassword(documentSnapshot.getString(CONSTANTS.EMPLOYEE_PASSWORD));
                        receiverUser.setEmployeeOC(documentSnapshot.getString(CONSTANTS.EMPLOYEE_OC));
                        receiverUser.setEmployeeProfilePhotoUrl(documentSnapshot.getString(CONSTANTS.EMPLOYEE_PROFILE_PHOTO_URL));

                        holder.itemView.setOnClickListener(view -> {

                            Intent intent = new Intent(holder.itemView.getContext(), Chat_Activity.class);
                            intent.putExtra("userObject", receiverUser);
                            intent.putExtra("userType", "employee");
                            holder.itemView.getContext().startActivity(intent);
                        });

                    }
                });

        if (!receiverFound) {
            firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                    .whereEqualTo(CONSTANTS.EMPLOYER_ID, chatMessages.get(holder.getAdapterPosition()).recieverId)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {

                            Employer receiverUser = new Employer();
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            receiverUser.setEmployerID(documentSnapshot.getId());
                            receiverUser.setEmployerName(documentSnapshot.getString(CONSTANTS.EMPLOYER_NAME));
                            receiverUser.setEmployerEmail(documentSnapshot.getString(CONSTANTS.EMPLOYER_EMAIL));
                            receiverUser.setEmployerPassword(documentSnapshot.getString(CONSTANTS.EMPLOYER_PASSWORD));
                            receiverUser.setEmployerOC(documentSnapshot.getString(CONSTANTS.EMPLOYER_OC));
                            receiverUser.setEmployerProfilePhotoUrl(documentSnapshot.getString(CONSTANTS.EMPLOYER_PROFILE_PHOTO_URL));

                            holder.itemView.setOnClickListener(view -> {
                                Intent intent = new Intent(holder.itemView.getContext(), Chat_Activity.class);
                                intent.putExtra("userObject", receiverUser);
                                intent.putExtra("userType", "employer");
                                holder.itemView.getContext().startActivity(intent);
                            });

                        }
                    });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you really want to delete this conversation? ")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            ProgressDialog progressDialog=new ProgressDialog(context);
                            progressDialog.setTitle("Deleting conversation..");
                            progressDialog.setMessage("Please wait..it may take a while");
                            progressDialog.show();
                            firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CONVERSATION)
                                    .document(chatMessages.get(holder.getAdapterPosition()).conversationDocumentReferenceId)
                                    .delete()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            chatMessages.remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
                                            Toast.makeText(context, "Conversation deleted successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            CollectionReference collections = firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_CHAT);

                            collections.whereEqualTo(CONSTANTS.KEY_RECIEVER_ID, chatMessages.get(holder.getAdapterPosition()).recieverId)
                                    .whereEqualTo(CONSTANTS.KEY_SENDER_ID, chatMessages.get(holder.getAdapterPosition()).senderId)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && task.getResult().size() > 0) {
                                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                                for (DocumentSnapshot documentSnapshot : documents) {
                                                    String id = documentSnapshot.getId();
                                                    collections.document(id).delete();

                                                }


                                            }
                                        }
                                    });
                            collections.whereEqualTo(CONSTANTS.KEY_SENDER_ID,chatMessages.get(holder.getAdapterPosition()).recieverId)
                                    .whereEqualTo(CONSTANTS.KEY_RECIEVER_ID,chatMessages.get(holder.getAdapterPosition()).senderId)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && task.getResult().size() > 0) {
                                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                                for (DocumentSnapshot documentSnapshot : documents) {
                                                    String id = documentSnapshot.getId();
                                                    collections.document(id).delete();

                                                }
                                            }
                                        }
                                    });
                            progressDialog.cancel();

                        });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.create();
                builder.show();


                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversationsViewHolder extends RecyclerView.ViewHolder {
        ChatUserLayoutBinding binding;

        public ConversationsViewHolder(ChatUserLayoutBinding chatUserLayoutBinding) {
            super(chatUserLayoutBinding.getRoot());
            binding = chatUserLayoutBinding;

        }

        void setData(ChatMessage chatMessage) {

            binding.tvUserName.setText(chatMessage.conversationName);
            binding.tvUserType.setText(chatMessage.message);///tvusertype=recent message
            Glide.with(context).load(chatMessage.conversationImage).placeholder(R.drawable.person).into(binding.imgusr);

        }
    }
}
