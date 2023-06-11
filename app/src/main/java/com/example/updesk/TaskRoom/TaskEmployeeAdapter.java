package com.example.updesk.TaskRoom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.updesk.Chat.Chat_Activity;
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TaskEmployeeAdapter extends RecyclerView.Adapter<TaskEmployeeAdapter.Holder> {
    private ArrayList<TaskAttachment> taskAttachments;
    private Context context;
    CardView cvUpdateStatus;

    TextView tvFilename;
    String status;
    Button btnApplyUpdate;
    FirebaseFirestore firebaseFirestore;

    TaskAttachment currentTask;

    PreferenceManager pref;


    public TaskEmployeeAdapter(ArrayList<TaskAttachment> taskAttachments, Context context, CardView cv) {
        this.taskAttachments = taskAttachments;
        this.context = context;
        this.cvUpdateStatus = cv;

    }

    public TaskEmployeeAdapter(ArrayList<TaskAttachment> taskAttachments, Context context) {
        this.taskAttachments = taskAttachments;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_employee_layout, parent, false);
        pref = new PreferenceManager(context);
        firebaseFirestore = FirebaseFirestore.getInstance();

        return new Holder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        currentTask = taskAttachments.get(holder.getAdapterPosition());
        holder.fileName.setText("Filename: " + currentTask.getTaskFileName());
        holder.userType.setText("Position:" + currentTask.getUserType());
        holder.userName.setText("Submitted By: " + currentTask.getUploadedBy());
        holder.status.setText("Status: " + currentTask.getTaskStatus());
        holder.btnSubmittedTaskDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ViewTaskDetailsActivity.class);
                intent.putExtra("taskFileObject", currentTask);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        if (currentTask.getTaskStatus().equals("Approved")) {
            holder.iv.setImageResource(R.drawable.task_accepted_vector);
        }
        else if (currentTask.getTaskStatus().equals("Rejected")) {
            holder.iv.setImageResource(R.drawable.task_rejected_vector);
        }
        else {
            holder.iv.setImageResource(R.drawable.baseline_pending_24);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setType("application/pdf");
                i.setData(Uri.parse(currentTask.getTaskFileUrl()));
                holder.itemView.getContext().startActivity(i);

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you really want to delete this task? ")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {

                            firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_TASK)
                                    .whereEqualTo(CONSTANTS.KEY_TASK_FILE_URL, currentTask.getTaskFileUrl()).get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
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


        if (pref.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP)) {
            holder.updateStatusBtn.setVisibility(View.VISIBLE);
        }

        holder.itemView.findViewById(R.id.btn_update_status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                tvFilename = cvUpdateStatus.findViewById(R.id.txtUpdateStatusFilename);
                tvFilename.setText("Selected Task:  " + taskAttachments.get(holder.getAdapterPosition()).getTaskFileName());
                cvUpdateStatus.setVisibility(View.VISIBLE);
                btnApplyUpdate = cvUpdateStatus.findViewById(R.id.updateSelectedBtn);
                btnApplyUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RadioGroup rg = cvUpdateStatus.findViewById(R.id.radioGroup);
                        int radioId = rg.getCheckedRadioButtonId();
                        if (radioId != 0) {


                            if (radioId == cvUpdateStatus.findViewById(R.id.radioBtnAcceptTask).getId()) {
                                status = "Approved";

                                updateTaskStatusinFirebase(holder.getAdapterPosition());


                            } else if (radioId == cvUpdateStatus.findViewById(R.id.radioBtnRejectTask).getId()) {
                                status = "Rejected";
                                updateTaskStatusinFirebase(holder.getAdapterPosition());


                            }
                        } else {
                            Toast.makeText(context, "No option selected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    private void updateTaskStatusinFirebase(int pos) {
        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_TASK)
                .whereEqualTo(CONSTANTS.KEY_TASK_FILE_URL, taskAttachments.get(pos).getTaskFileUrl())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                            task.getResult().getDocuments().get(0).getReference().update(CONSTANTS.KEY_TASK_Status, status)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            cvUpdateStatus.setVisibility(View.INVISIBLE);
                                            Toast.makeText(context, "Task Status Updated Successfully!  Refresh to view Changes", Toast.LENGTH_SHORT).show();
                                            notifyItemChanged(pos);
                                        }
                                    });

                        }
                    }
                });


    }


    @Override
    public int getItemCount() {
        return taskAttachments.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView fileName, userName, userType, status,btnSubmittedTaskDetails;
        Button updateStatusBtn;
        ShapeableImageView iv;


        public Holder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.tvSubmittedTaskFileName);
            userName = itemView.findViewById(R.id.tvSubmittedtaskUserName);
            userType = itemView.findViewById(R.id.tvSubmittedTaskUserType);
            status = itemView.findViewById(R.id.tvSubmittedtaskStatus);
            updateStatusBtn = itemView.findViewById(R.id.btn_update_status);
            iv = itemView.findViewById(R.id.taskStatusVector);
            btnSubmittedTaskDetails = itemView.findViewById(R.id.btnViewSubmittedTaskDetails);

        }
    }
}
