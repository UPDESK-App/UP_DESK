package com.example.updesk.TaskRoom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TaskEmployerAdapter extends RecyclerView.Adapter<TaskEmployerAdapter.Holder> {


    private ArrayList<TaskAttachment> taskAttachments;
    private Context context;

    FirebaseFirestore firebaseFirestore;
    public TaskEmployerAdapter(ArrayList<TaskAttachment> taskAttachments, Context context) {
        this.taskAttachments = taskAttachments;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout,parent,false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        return new Holder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
       TaskAttachment currentTask = taskAttachments.get(holder.getAdapterPosition());
        holder.fileName.setText("Filename: "+currentTask.getTaskFileName());//filename
        holder.userName.setText("Uploaded By: "+currentTask.getUploadedBy());//taskstatus N,A,R
        holder.userType.setText("Position: "+currentTask.getUserType()); ///emlployee/employer
        holder.btnAssignedTaskDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ViewTaskDetailsActivity.class);
                intent.putExtra("taskFileObject", currentTask);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setType("application/pdf");
                i.setData(Uri.parse(currentTask.getTaskFileUrl()));
                holder.itemView.getContext().startActivity(i);;
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(context);
                builder.setMessage("Do you really want to delete this task? ")
                        .setPositiveButton("Yes",(dialogInterface, i) -> {

                            Task<QuerySnapshot> query=firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_TASK)
                                    .whereEqualTo(CONSTANTS.KEY_TASK_FILE_URL,currentTask.getTaskFileUrl())
                                    .get()

                                    .addOnCompleteListener(task -> {
                                        if(task.isSuccessful()&& task.getResult().getDocuments().size()>0)
                                        {
                                            DocumentReference documentReference = task.getResult().getDocuments().get(0).getReference();
                                            documentReference.delete();
                                            taskAttachments.remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
                                            Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
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
        return taskAttachments.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView fileName,userName,userType,btnAssignedTaskDetails;


        public Holder(@NonNull View itemView) {
            super(itemView);
            fileName=itemView.findViewById(R.id.tvFileName);
            userType=itemView.findViewById(R.id.tvUserType);
            userName=itemView.findViewById(R.id.tvtaskUploadedBy);
            btnAssignedTaskDetails=itemView.findViewById(R.id.btnViewAssignedTaskDetails);

        }
    }
}
