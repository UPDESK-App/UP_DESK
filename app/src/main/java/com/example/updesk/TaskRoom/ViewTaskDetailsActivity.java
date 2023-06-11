package com.example.updesk.TaskRoom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.ActivityViewTaskDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ViewTaskDetailsActivity extends AppCompatActivity {
    ActivityViewTaskDetailsBinding binding;
    PreferenceManager pref;
    FirebaseFirestore firebaseFirestore;
    TaskAttachment taskFile;

    ArrayList<CommentClass> comments;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewTaskDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showTaskDetails();

        init();
        setListeners();


    }

    private void showTaskDetails() {
        intent = getIntent();
        taskFile = intent.getParcelableExtra("taskFileObject");
        binding.fileNametextView.setText(taskFile.getTaskFileName());
        binding.descriptiontextView.setText(taskFile.getTaskDescription());


    }

    private void init() {
        pref = new PreferenceManager(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        comments = new ArrayList<>();
        loadComments();
    }

    private void setListeners() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.CardViewAddnewComment.setVisibility(View.GONE);
                binding.fabNewComment.setVisibility(View.VISIBLE);
            }
        });
        binding.fabNewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.CardViewAddnewComment.setVisibility(View.VISIBLE);
                binding.fabNewComment.setVisibility(View.GONE);
                binding.btnUploadComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isCommentValid()) {
                            HashMap<String, Object> comment = new HashMap<>();
                            comment.put(CONSTANTS.KEY_COMMENT_MESSAGE, binding.etComment.getText().toString().trim());
                            if (pref.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
                                comment.put(CONSTANTS.KEY_COMMENT_SENDER_NAME, pref.getString(CONSTANTS.EMPLOYEE_NAME));
                            } else {
                                comment.put(CONSTANTS.KEY_COMMENT_SENDER_NAME, pref.getString(CONSTANTS.EMPLOYER_NAME));
                            }
                            Date currentDate = new Date();
                            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
                            comment.put(CONSTANTS.KEY_COMMENT_DATE_TIME, formatter.format(currentDate));


                            comment.put(CONSTANTS.KEY_COMMENT_MESSAGE, binding.etComment.getText().toString().trim());
                            comment.put(CONSTANTS.KEY_COMMENT_MESSAGE, binding.etComment.getText().toString().trim());
                            comment.put(CONSTANTS.KEY_COMMENT_TASK_FILE_NAME, taskFile
                                    .getTaskFileName());
                            firebaseFirestore.collection(CONSTANTS.KEY_COMMENT_COLLECTION)
                                    .add(comment)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(ViewTaskDetailsActivity.this, "Comment added!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
            }


        });
    }

    private boolean isCommentValid() {
        if (binding.etComment.getText().toString().trim().isEmpty()) {
            binding.etComment.setError("Please enter comment");
            return false;
        }
        return true;
    }

    private void loadComments() {
        firebaseFirestore.collection(CONSTANTS.KEY_COMMENT_COLLECTION)

                .whereEqualTo(CONSTANTS.KEY_COMMENT_TASK_FILE_NAME, taskFile.getTaskFileName())

                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                comments.add(new CommentClass(documentSnapshot.getString(CONSTANTS.KEY_COMMENT_MESSAGE),
                                        documentSnapshot.getString(CONSTANTS.KEY_COMMENT_SENDER_NAME),
                                        documentSnapshot.getString(CONSTANTS.KEY_COMMENT_DATE_TIME),
                                        documentSnapshot.getString(CONSTANTS.KEY_COMMENT_TASK_FILE_NAME)));
                            }

                            showComments();

                        }


                    }
                });

    }

    private void showComments() {
        TaskCommentsAdapter taskCommentsAdapter = new TaskCommentsAdapter(comments, ViewTaskDetailsActivity.this);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(ViewTaskDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.rvComments.setAdapter(taskCommentsAdapter);
    }
}

