package com.example.updesk.LoginActivities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.updesk.MainActivity;
import com.example.updesk.ModelClasses.Employer;
import com.example.updesk.ModelClasses.JavaMailAPI;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.ActivityEmployerSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class EmployerSignUpActivity extends AppCompatActivity {
    private ActivityEmployerSignUpBinding binding;
    private FirebaseFirestore firebase ;
    private StorageReference storageReference;
    private PreferenceManager pref;
    private Uri ProfilePhotoPath;
    boolean userFound=false;
    int random_OC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding= ActivityEmployerSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebase = FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        pref= new PreferenceManager(this);

        setListeners();
    }

    private void setListeners() {

        ActivityResultLauncher<Intent> SelectImage = registerForActivityResult
                (new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                Intent intent=result.getData();
                                if(intent!=null)
                                {
                                    ProfilePhotoPath = intent.getData();
                                    Glide.with(getApplicationContext()).load(ProfilePhotoPath).into(binding.mtIVPropfilePhoto);
                                }
                            }
                        });
        binding.mtIVPropfilePhoto.setOnClickListener(view -> {
            Intent intent=new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            SelectImage.launch(intent);
        });
        binding.btnEmployerGenerateOC.setOnClickListener(view -> {

            if(binding.etMail.getEditText().getText().toString().trim().isEmpty())
            {
                ShowToast("Please enter email first");
            }
            else
            {
                Random random=new Random();
                random_OC = 1000+ random.nextInt(10000-1000);
                ShowToast(String.valueOf(random_OC));

                AlertDialog.Builder builder= new AlertDialog.Builder(this);
                builder.setMessage("You'll receive OC through email,Please check your email")
                        .setPositiveButton("OK",(dialogInterface, i) -> {

                            JavaMailAPI javaMailAPI= new JavaMailAPI(this,binding.etMail.getEditText().getText().toString(),
                                    "UP-DESK organization code!",
                                    "Your OC is: "+ random_OC +" please enter this to register your organization");
                            javaMailAPI.execute();
                        }).show();



            }

        });

        binding.mtBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsDataValid())
                {
                    loading(true);
                    isDetailsUnique();
                }
            }
        });
        binding.txtSignIn.setOnClickListener(view -> {

        Intent intent= new Intent(getApplicationContext(),EmployerLoginActivity.class);
        startActivity(intent);
        finish();
    });
    }

    private boolean IsDataValid()
    {
        if(binding.etName.getEditText().getText().toString().trim().isEmpty()) {
            ShowToast("Please Enter username");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.etMail.getEditText().getText().toString().trim()).matches()) {
            ShowToast("Please Enter valid email");
            return false;
        }
        else if(!isValidPassword(binding.etPassword.getEditText().getText().toString().trim())) {
            ShowToast("Please Enter valid Password");
            return false;
        }
        else if(!binding.etPassword.getEditText().getText().toString().trim().equals(binding.etConfirmPassword.getEditText().getText().toString().trim())){
            ShowToast("Password and confirm password must be the same");
            return  false;
        }
        else if(binding.etOC.getEditText().getText().toString().trim().isEmpty()) {
            ShowToast("Please Enter Organization Code");
            return false;
        }
        else if(!(binding.etOC.getEditText().getText().toString().trim().equals(String.valueOf(random_OC))))
        {
            ShowToast("Invalid Organization Code");
            return false;
        }
        else if(ProfilePhotoPath==null){
            ShowToast("Please select profile Image");
            return  false;
        }

        return true;
    }

    public static boolean isValidPassword(String s) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile("^" +
                "(?=.*[@#$!,%^&+=-_.])" +     //1special character
                "(?=\\S+$)" +            // no space
                ".{8,}" +                // length=8 characters
                "$");

        return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches();
    }
    private void isDetailsUnique()
    {
        ShowToast("Details Unique mai");

        firebase.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                .whereEqualTo(CONSTANTS.EMPLOYER_EMAIL,binding.etMail.getEditText().getText().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().getDocuments().size()>0)
                        {
                            userFound = true;
                            //SignUp(userFound);
                        }

                    }

                });
        SignUp(userFound);
        ShowToast("Details Unique end mai");
    }
    private void SignUp(boolean userExist)
    {
        ShowToast("Signup k strt m");
        if(userExist)
        {
            loading(false);
            ShowToast("Organization with this email already exist");}
        else
        {
            ShowToast("SignUp ky else m");
            StorageReference reference= storageReference.child("EmployerProfileImages/"+ UUID.randomUUID().toString());
            reference.putFile(ProfilePhotoPath)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                ShowToast("Image uploaded to storage");
                                reference.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                ShowToast("Image url downloaded");
                                                DocumentReference document= firebase.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER).document();
                                                String name = binding.etName.getEditText().getText().toString().trim();
                                                String email  = binding.etMail.getEditText().getText().toString().trim();
                                                String password  = binding.etPassword.getEditText().getText().toString().trim();
                                                String oc= binding.etOC.getEditText().getText().toString().trim();
                                                Employer employer = new Employer(
                                                        document.getId(),
                                                        name,
                                                        email,
                                                        oc,
                                                        password,
                                                        uri.toString()
                                                );

                                                document.set(employer)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    ShowToast("setting employer details");

                                                                    pref.putBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP,true);
                                                                    pref.putBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP,false);
                                                                    pref.putString(CONSTANTS.EMPLOYER_ID,employer.getEmployerID());
                                                                    pref.putString(CONSTANTS.EMPLOYER_EMAIL,employer.getEmployerEmail());
                                                                    pref.putString(CONSTANTS.EMPLOYER_NAME,employer.getEmployerName());
                                                                    pref.putString(CONSTANTS.EMPLOYER_PASSWORD,employer.getEmployerPassword());
                                                                    pref.putString(CONSTANTS.EMPLOYER_OC,employer.getEmployerOC());
                                                                    pref.putString(CONSTANTS.EMPLOYER_PROFILE_PHOTO_URL,employer.getEmployerProfilePhotoUrl());
                                                                    ShowToast("Intent k upar");
                                                                    loading(false);

                                                                    pref.putString(CONSTANTS.TOPIC,"/topics/"+pref.getString(CONSTANTS.EMPLOYER_OC));

                                                                    FirebaseMessaging.getInstance()
                                                                            .subscribeToTopic(pref.getString(CONSTANTS.TOPIC));


                                                                    Intent intent=new Intent(EmployerSignUpActivity.this, MainActivity.class);
                                                                    intent.putExtra(CONSTANTS.EMPLOYER_ID,employer.getEmployerID());
                                                                    startActivity(intent);
                                                                    finish();

                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                    });


        }
    }
    private void loading(boolean isLoading)
    {
        if(isLoading)
        {
            binding.mtBtnSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.mtBtnSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void ShowToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}