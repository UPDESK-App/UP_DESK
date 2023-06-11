package com.example.updesk.More;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.databinding.ActivityHelpAndFaqBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Help_And_FAQ extends AppCompatActivity {
    ActivityHelpAndFaqBinding binding;
    ArrayList<Questions> questionsArrayList;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpAndFaqBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

    }

    private void init() {
        questionsArrayList = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_HELP_QUESTIONS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful() && task.getResult().getDocuments().size() != 0) {

                                                   for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                                       Questions questions = new Questions();
                                                       questions.setQuestion(documentSnapshot.getString(CONSTANTS.KEY_HELP_QUESTION_STRING));
                                                       questions.setQuestionID(documentSnapshot.getString(CONSTANTS.KEY_HELP_QUESTION_ID));
                                                       questionsArrayList.add(questions);

                                                   }


                                               }


                                           }

                                       }
                );
        HelpAdapter adapter = new HelpAdapter(questionsArrayList, Help_And_FAQ.this);
        binding.rvQuestions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rvQuestions.setAdapter(adapter);

    }


}