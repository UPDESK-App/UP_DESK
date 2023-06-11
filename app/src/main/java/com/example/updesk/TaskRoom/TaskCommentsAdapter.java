package com.example.updesk.TaskRoom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.updesk.R;

import java.util.List;

public class TaskCommentsAdapter extends RecyclerView.Adapter<TaskCommentsAdapter.Holder> {
    private List<CommentClass> commentClasses;
    private Context context;

    public TaskCommentsAdapter(List<CommentClass> commentClasses, ViewTaskDetailsActivity context) {
        this.commentClasses = commentClasses;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.remarkmessage_item_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        CommentClass comment=commentClasses.get(holder.getAdapterPosition());

        holder.comment.setText(comment.getComment());
        holder.senderName.setText(comment.getCommentSendername());
        holder.dateTime.setText(comment.getCommentDateTime());

    }

    @Override
    public int getItemCount() {
        return commentClasses.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView comment,senderName,dateTime;
        public Holder(@NonNull View itemView) {
            super(itemView);

            comment=itemView.findViewById(R.id.tvComment);
            senderName=itemView.findViewById(R.id.tvSenderName);
            dateTime=itemView.findViewById(R.id.tvCommentDatetime);
        }


    }
}
