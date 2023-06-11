package com.example.updesk.EmployerActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.updesk.LoginActivities.EmployeeLoginActivity;
import com.example.updesk.ModelClasses.JavaMailAPI;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.ActivityEmployerResetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

public class EmployerResetPassword extends AppCompatActivity {
    private ActivityEmployerResetPasswordBinding binding;
    private FirebaseFirestore firebaseFirestore;
    boolean emailsent = false;

    PreferenceManager pref;

    String sentcode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployerResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        pref = new PreferenceManager(this);
        binding.mtBtnEmployerLogIn.setOnClickListener(view -> {
            loading(true);
            if (binding.etemail.getText().toString().trim().isEmpty()) {
                loading(false);
                ShowToast("Please enter email first");
            } else {


                if (binding.empchkbox.isChecked()) {
                    CONSTANTS.resetkey = "Employer";
                    CONSTANTS.resetemail = "employerEmail";
                    CONSTANTS.resetusrID = "employerID";
                    CONSTANTS.resetPassowrd = "employerPassword";

                } else {
                    CONSTANTS.resetkey = "Employee";
                    CONSTANTS.resetemail = "employeeEmail";
                    CONSTANTS.resetusrID = "employeeID";
                    CONSTANTS.resetPassowrd = "employeePassword";
                }
                firebaseFirestore.collection(CONSTANTS.resetkey)
                        .whereEqualTo(CONSTANTS.resetemail, binding.etemail.getText().toString().trim())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                                    pref.putString(CONSTANTS.resetusrID, documentSnapshot.getId());
                                    Random random = new Random();
                                    CONSTANTS.cc = 1000 + random.nextInt(10000 - 1000);
                                    ShowToast(String.valueOf(CONSTANTS.cc));
                                    pref.putString("cc", String.valueOf(CONSTANTS.cc));
                                    sendemail();

                                } else {
                                    loading(false);
                                    ShowToast("Please Enter Valid and Registered Email");
                                }
                            }


                        });
            }
        });

    }


    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.mtBtnEmployerLogIn.setVisibility(View.INVISIBLE);
            binding.ProgressBarLogin.setVisibility(View.VISIBLE);
        } else {
            binding.mtBtnEmployerLogIn.setVisibility(View.VISIBLE);
            binding.ProgressBarLogin.setVisibility(View.INVISIBLE);
        }
    }

    private void ShowToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    private boolean sendemail() {

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, binding.etemail.getText().toString(),
                "Email Verification code!",
                "Your Confirmation Code is: " + CONSTANTS.cc + " please enter this to reset your Password");
        javaMailAPI.execute();


        changePassword();


        Log.d("Emaillll", binding.etemail.getText().toString());
        return emailsent;
    }

    private void changePassword() {
        binding.cardViewGetEmail.setVisibility(View.GONE);
        sentcode = pref.getString("cc");
        binding.verifybtn.setOnClickListener(View -> {
            ShowToast("Verify  Button");
            checkcc(binding.etcc.getText().toString().trim());
        });
    }

    private void checkcc(String code) {
        Log.d("code i entered",code);
        Log.d("code in pref",sentcode);
        if (code.equals(sentcode)) {

            ShowToast("Email Verified");
            changepassword(true);
            binding.btnchangepassword.setOnClickListener(view -> {
                firebaseFirestore.collection(CONSTANTS.resetkey)
                        .document(pref.getString(CONSTANTS.resetusrID))
                        .update(CONSTANTS.resetPassowrd,binding.txtpswrd.getText().toString());


                Intent i=new Intent(EmployerResetPassword.this, EmployeeLoginActivity.class);
                startActivity(i);
                finish();

            });


        }
        else{
            ShowToast("Email verification Failed!Kindly Retry");
        }

    }


        private void changepassword( boolean b){
            if (b == true) {
                binding.linearlayoutcc.setVisibility(View.INVISIBLE);
                binding.linearlayoutcp.setVisibility(View.VISIBLE);

        }
    }
}
