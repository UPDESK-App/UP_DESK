package com.example.updesk.More;


import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.updesk.Chat.employee_chat_adapter;
import com.example.updesk.ModelClasses.Employee;
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.ViewHolder> {

    private ArrayList<Questions> questions;
    private Context context;
    LayoutInflater inflater;

    public HelpAdapter(ArrayList<Questions> questions, Context context) {
        this.questions = questions;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.help_question_item_layout,parent,false);
        return new HelpAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Questions question = questions.get(holder.getAdapterPosition());
        holder.Q.setText(question.getQuestion());

        holder.Q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProgressDialog progressDialog=new ProgressDialog(context);
                progressDialog.setMessage("Loading answers...");
                progressDialog.show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final View answerDialog=inflater.inflate(R.layout.answerdialog,null);

                TextView tvQuestion=answerDialog.findViewById(R.id.tvQuestion);
                TextView tvAnswer=answerDialog.findViewById(R.id.tvAnswer);
                tvQuestion.setText("Q. "+question.getQuestion());
                FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

               firebaseFirestore.collection(CONSTANTS.KEY_HELP_ANSWER_COLLECTION)
                               .whereEqualTo(CONSTANTS.KEY_HELP_QUESTION_ID,question.getQuestionID())
                                       .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                               if(task.isSuccessful() && task.getResult().getDocuments().size()>0) {


                                   tvAnswer.setText(task.getResult().getDocuments().get(0).getString(CONSTANTS.KEY_HELP_ANSWER_STRING));
                               }
                           }
                       });

                builder.setView(answerDialog);
                builder.create();

                builder.show();
                progressDialog.dismiss();

            }
        });
    }

    @Override
    public int getItemCount() {
        return  questions.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView Q;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Q=itemView.findViewById(R.id.txtQuestion);
        }




    }
}
