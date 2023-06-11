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
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.ActivityEmployerLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EmployerLoginActivity extends AppCompatActivity {
    ActivityEmployerLoginBinding binding;
    FirebaseFirestore firebase;
    StorageReference storageReference;
    PreferenceManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityEmployerLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebase = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        pref = new PreferenceManager(this);

        setListeners();
    }

    private void setListeners() {

        binding.txtemployeelogin.setOnClickListener(view -> {
           Intent intent=new Intent(EmployerLoginActivity.this,EmployeeLoginActivity.class);

           startActivity(intent);
        });


        binding.txtEmployerCreateAccount.setOnClickListener(view ->{
            loading(false);
            Intent intent= new Intent(getApplicationContext(),EmployerSignUpActivity.class);
            startActivity(intent);
            finish();
        } );

        binding.txtEmployerForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(EmployerLoginActivity.this, EmployerResetPassword.class);
            startActivity(intent);
            finish();
        });



        binding.mtBtnEmployerLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsValidDetails())
                {
                    loading(true);
                    firebase.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                            .whereEqualTo(CONSTANTS.EMPLOYER_EMAIL, binding.etEmployerEmail.getEditText().getText().toString().trim())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task)
                                {
                                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0)
                                    {
                                        DocumentSnapshot documentSnapshot= task.getResult().getDocuments().get(0);
                                        if (binding.etEmployerPassword.getEditText().getText().toString().trim()
                                                .equals(task.getResult().getDocuments().get(0).getString(CONSTANTS.EMPLOYER_PASSWORD)) &&
                                                binding.etEmployerOC.getEditText().getText().toString().trim()
                                                        .equals(task.getResult().getDocuments().get(0).getString(CONSTANTS.EMPLOYER_OC)))
                                        {
                                               pref.putBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP, true);
                                               pref.putBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP, false);
                                               pref.putString(CONSTANTS.EMPLOYER_ID, documentSnapshot.getId());
                                               pref.putString(CONSTANTS.EMPLOYER_EMAIL, documentSnapshot.getString(CONSTANTS.EMPLOYER_EMAIL));
                                               pref.putString(CONSTANTS.EMPLOYER_NAME, documentSnapshot.getString(CONSTANTS.EMPLOYER_NAME));
                                               pref.putString(CONSTANTS.EMPLOYER_PASSWORD, documentSnapshot.getString(CONSTANTS.EMPLOYER_PASSWORD));
                                               pref.putString(CONSTANTS.EMPLOYER_PROFILE_PHOTO_URL, documentSnapshot.getString(CONSTANTS.EMPLOYER_PROFILE_PHOTO_URL));
                                               pref.putString(CONSTANTS.EMPLOYER_OC,documentSnapshot.getString(CONSTANTS.EMPLOYER_OC));
                                               loading(false);


                                               pref.putString(CONSTANTS.TOPIC,"/topics/"+pref.getString(CONSTANTS.EMPLOYER_OC));

                                                FirebaseMessaging.getInstance()
                                                .subscribeToTopic(pref.getString(CONSTANTS.TOPIC));


                                               Intent intent = new Intent(EmployerLoginActivity.this, MainActivity.class);
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

    /*private boolean IsValidDetails() {
        if (binding.etEmployerEmail.getEditText().getText().toString().trim().isEmpty()) {
            ShowToast("Please Enter email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmployerEmail.getEditText().getText().toString().trim()).matches()) {
            ShowToast("Please Enter valid email");
            return false;
        }
        else if(binding.etEmployerOC.getEditText().getText().toString().trim().isEmpty())
        {
            ShowToast("Please Enter OC");
            return false;
        }
        else if (binding.etEmployerPassword.getEditText().getText().toString().trim().isEmpty()) {
            ShowToast("Please Enter Password");
            return false;
        }
        return true;
    }*/
    private boolean IsValidDetails() {
        if (binding.etEmployerEmail.getEditText().getText().toString().trim().isEmpty())
        {
            binding.etEmployerEmail.setError("Please enter email");
            return false;
        }
        else if(!(binding.etEmployerEmail.getEditText().getText().toString().trim().isEmpty()))
        {
            binding.etEmployerEmail.setError(null);
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmployerEmail.getEditText().getText().toString().trim()).matches()) {
            binding.etEmployerEmail.setError("Please enter valid email");
            return false;
        }
        else if(Patterns.EMAIL_ADDRESS.matcher(binding.etEmployerEmail.getEditText().getText().toString().trim()).matches()) {
                binding.etEmployerEmail.setError(null);
        }
        if (binding.etEmployerOC.getEditText().getText().toString().trim().isEmpty()){
                binding.etEmployerOC.setError("Please enter OC");
                return false;
        }
        else if (!(binding.etEmployerOC.getEditText().getText().toString().trim().isEmpty())){
                binding.etEmployerOC.setError(null);
        }
        if (binding.etEmployerPassword.getEditText().getText().toString().trim().isEmpty()){
            binding.etEmployerPassword.setError("Please enter password");
            return false;
        }
        else if (!(binding.etEmployerPassword.getEditText().getText().toString().trim().isEmpty())){
            binding.etEmployerPassword.setError(null);
        }
        return true;
    }
    private boolean IsEmailEmpty()
    {
        if (binding.etEmployerEmail.getEditText().getText().toString().trim().isEmpty())
        {
            binding.etEmployerEmail.setError("Please enter email");
            return false;
        }
        else
        {
            binding.etEmployerEmail.setError(null);
        }
        return true;
    }
    private boolean IsEmailValid()
    {
        if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmployerEmail.getEditText().getText().toString().trim()).matches()) {
            binding.etEmployerEmail.setError("Please enter valid email");
            return false;
        }
        else
        {
            binding.etEmployerEmail.setError(null);
        }
        return true;
    }
    private boolean IsOCEmpty()
    {
        if (binding.etEmployerOC.getEditText().getText().toString().trim().isEmpty())
        {
            binding.etEmployerOC.setError("Please enter OC");
            return false;
        }
        else
        {
            binding.etEmployerOC.setError(null);
        }
        return true;
    }
    private boolean IsPasswordEmpty()
    {
        if (binding.etEmployerPassword.getEditText().getText().toString().trim().isEmpty())
        {
            binding.etEmployerPassword.setError("Please enter OC");
            return false;
        }
        else
        {
            binding.etEmployerPassword.setError(null);
        }
        return true;
    }
    //progress bar function
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
}