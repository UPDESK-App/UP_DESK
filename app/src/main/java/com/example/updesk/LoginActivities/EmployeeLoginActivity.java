package com.example.updesk.LoginActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.updesk.EmployerActivities.EmployerResetPassword;
import com.example.updesk.MainActivity;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.ActivityEmployeeLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EmployeeLoginActivity extends AppCompatActivity {
    ActivityEmployeeLoginBinding binding;
    FirebaseFirestore firebase;
    StorageReference storageReference;
    PreferenceManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEmployeeLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebase = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        pref = new PreferenceManager(this);

        setListeners();
    }

    private void setListeners() {


        binding.txtEmployeeCreateAccount.setOnClickListener(view ->{
            loading(false);
            Intent intent= new Intent(getApplicationContext(),EmployeeSignUpActivity.class);
            startActivity(intent);
            finish();
        } );

        binding.txtEmployeeForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(EmployeeLoginActivity.this, EmployerResetPassword.class);
            startActivity(intent);
        });



        binding.mtBtnEmployeeLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsValidDetails())
                {
                    loading(true);
                    firebase.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE)
                            .whereEqualTo(CONSTANTS.EMPLOYEE_EMAIL, binding.etEmployeeEmail.getEditText().getText().toString().trim())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task)
                                {
                                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0)
                                    {
                                        DocumentSnapshot documentSnapshot= task.getResult().getDocuments().get(0);
                                        if (binding.etEmployeePassword.getEditText().getText().toString().trim()
                                                .equals(task.getResult().getDocuments().get(0).getString(CONSTANTS.EMPLOYEE_PASSWORD)) &&
                                                binding.etEmployeeOC.getEditText().getText().toString().trim()
                                                        .equals(task.getResult().getDocuments().get(0).getString(CONSTANTS.EMPLOYEE_OC)))
                                        {
                                            pref.putBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP, true);
                                            pref.putBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP, false);
                                            pref.putString(CONSTANTS.EMPLOYEE_ID, documentSnapshot.getId());

                                            pref.putString(CONSTANTS.EMPLOYEE_EMAIL, documentSnapshot.getString(CONSTANTS.EMPLOYEE_EMAIL));

                                            pref.putString(CONSTANTS.EMPLOYEE_NAME, documentSnapshot.getString(CONSTANTS.EMPLOYEE_NAME));

                                            pref.putString(CONSTANTS.EMPLOYEE_PASSWORD, documentSnapshot.getString(CONSTANTS.EMPLOYEE_PASSWORD));

                                            pref.putString(CONSTANTS.EMPLOYEE_PROFILE_PHOTO_URL, documentSnapshot.getString(CONSTANTS.EMPLOYEE_PROFILE_PHOTO_URL));

                                            pref.putString(CONSTANTS.EMPLOYEE_OC,documentSnapshot.getString(CONSTANTS.EMPLOYEE_OC));


                                            loading(false);

                                            pref.putString(CONSTANTS.TOPIC,"/topics/"+pref.getString(CONSTANTS.EMPLOYEE_OC));

                                            FirebaseMessaging.getInstance()
                                                    .subscribeToTopic(pref.getString(CONSTANTS.TOPIC));


                                            Intent intent = new Intent(EmployeeLoginActivity.this, MainActivity.class);

                                            startActivity(intent);
                                            finish();

                                        }
                                        else
                                        {
                                            ShowToast("Incorrect Password or OC");
                                            loading(false);
                                        }

                                    }
                                    else
                                    {
                                        ShowToast("No account on this email");
                                        loading(false);
                                    }
                                }
                            });
                }
            }
        });

    }



    private boolean IsValidDetails() {
        if (binding.etEmployeeEmail.getEditText().getText().toString().trim().isEmpty())
        {
            binding.etEmployeeEmail.setError("Please enter email");
            return false;
        }
        else if(!(binding.etEmployeeEmail.getEditText().getText().toString().trim().isEmpty()))
        {
            binding.etEmployeeEmail.setError(null);
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmployeeEmail.getEditText().getText().toString().trim()).matches()) {
            binding.etEmployeeEmail.setError("Please enter valid email");
            return false;
        }
        else if(Patterns.EMAIL_ADDRESS.matcher(binding.etEmployeeEmail.getEditText().getText().toString().trim()).matches()) {
            binding.etEmployeeEmail.setError(null);
        }
        if (binding.etEmployeeOC.getEditText().getText().toString().trim().isEmpty()){
            binding.etEmployeeOC.setError("Please enter OC");
            return false;
        }
        else if (!(binding.etEmployeeOC.getEditText().getText().toString().trim().isEmpty())){
            binding.etEmployeeOC.setError(null);
        }
        if (binding.etEmployeePassword.getEditText().getText().toString().trim().isEmpty()){
            binding.etEmployeePassword.setError("Please enter password");
            return false;
        }
        else if (!(binding.etEmployeePassword.getEditText().getText().toString().trim().isEmpty())){
            binding.etEmployeePassword.setError(null);
        }
        return true;
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.mtBtnEmployeeLogIn.setVisibility(View.INVISIBLE);
            binding.ProgressBarLogin.setVisibility(View.VISIBLE);
        } else {
            binding.mtBtnEmployeeLogIn.setVisibility(View.VISIBLE);
            binding.ProgressBarLogin.setVisibility(View.INVISIBLE);
        }
    }
    private void ShowToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}