package com.example.updesk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.updesk.LoginActivities.EmployerLoginActivity;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class SplashActivity extends AppCompatActivity {
    PreferenceManager pref;
    FirebaseFirestore firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebase = FirebaseFirestore.getInstance();
        pref = new PreferenceManager(this);


       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(pref.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP))
                {
                    firebase.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER).document(pref.getString(CONSTANTS.EMPLOYER_ID))
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful() && task.getResult().exists())
                                    {
                                        Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        pref.clear();
                                    }
                                }
                            });
                } else if (pref.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
                    Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                } else
                {
                    Intent intent=new Intent(SplashActivity.this, EmployerLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },1000);

    }



}