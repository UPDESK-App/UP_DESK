package com.example.updesk.LoginActivities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.updesk.MainActivity;
import com.example.updesk.ModelClasses.Employee;
import com.example.updesk.ModelClasses.Employer;
import com.example.updesk.ModelClasses.JavaMailAPI;
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.ActivityEmployeeSignUpBinding;
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
import java.util.regex.Pattern;

public class EmployeeSignUpActivity extends AppCompatActivity {

   private ActivityEmployeeSignUpBinding binding;
    private FirebaseFirestore firebase ;
    private StorageReference storageReference;
    private PreferenceManager pref;
    private Uri ProfilePhotoPath;
    boolean userFound=false;
    boolean oC=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEmployeeSignUpBinding.inflate(getLayoutInflater());
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

            Intent intent= new Intent(getApplicationContext(),EmployeeLoginActivity.class);
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

        firebase.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE)
                .whereEqualTo(CONSTANTS.EMPLOYEE_EMAIL,binding.etMail.getEditText().getText().toString().trim())
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
            ShowToast("Employee with this email already exist");}
        else {
            if (ocExist()) {
                ShowToast("OC exists");
                StorageReference reference=storageReference.child("EmployeeProfileImages/"+UUID.randomUUID().toString());
                reference.putFile(ProfilePhotoPath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    ShowToast("Image uploaded to storage");
                                    reference.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    ShowToast("Image url downloaded");
                                                    DocumentReference document = firebase.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE).document();
                                                    String name = binding.etName.getEditText().getText().toString().trim();
                                                    String email = binding.etMail.getEditText().getText().toString().trim();
                                                    String password = binding.etPassword.getEditText().getText().toString().trim();
                                                    String oc = binding.etOC.getEditText().getText().toString().trim();
                                                    Employee employee = new Employee(
                                                            document.getId(),
                                                            name,
                                                            email,
                                                            oc,
                                                            password,
                                                            uri.toString()
                                                    );

                                                    document.set(employee)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        ShowToast("setting employer details");

                                                                        pref.putBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP, true);
                                                                        pref.putBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP, false);
                                                                        pref.putString(CONSTANTS.EMPLOYEE_ID, employee.getEmployeeID());
                                                                        pref.putString(CONSTANTS.EMPLOYEE_EMAIL, employee.getEmployeeEmail());
                                                                        pref.putString(CONSTANTS.EMPLOYEE_NAME, employee.getEmployeeName());
                                                                        pref.putString(CONSTANTS.EMPLOYEE_PASSWORD, employee.getEmployeePassword());
                                                                        pref.putString(CONSTANTS.EMPLOYEE_OC, employee.getEmployeeOC());
                                                                        pref.putString(CONSTANTS.EMPLOYEE_PROFILE_PHOTO_URL, employee.getEmployeeProfilePhotoUrl());
                                                                        ShowToast("Intent k upar");
                                                                        loading(false);
                                                                        pref.putString(CONSTANTS.TOPIC,"/topics/"+pref.getString(CONSTANTS.EMPLOYEE_OC));

                                                                        FirebaseMessaging.getInstance()
                                                                                .subscribeToTopic(pref.getString(CONSTANTS.TOPIC));



                                                                        Intent intent = new Intent(EmployeeSignUpActivity.this, MainActivity.class);
                                                                        intent.putExtra(CONSTANTS.EMPLOYEE_ID, employee.getEmployeeID());
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
            else{
                loading(false);
                ShowToast("No Organization registered with this OC");
            }
        }
    }

    private boolean ocExist() {
        ShowToast("OC checking mai");

        firebase.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                .whereEqualTo(CONSTANTS.EMPLOYER_OC,binding.etOC.getEditText().getText().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().getDocuments().size()>0)
                        {
                            oC =true;
                            //SignUp(userFound);
                        }

                    }

                });
        return oC;


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