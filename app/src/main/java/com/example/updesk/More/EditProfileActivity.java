package com.example.updesk.More;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.updesk.LoginActivities.EmployeeSignUpActivity;
import com.example.updesk.MainActivity;
import com.example.updesk.ModelClasses.Employee;
import com.example.updesk.ModelClasses.Employer;
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    PreferenceManager preferenceManager;
    Uri ProfilePhotoPath;
    boolean userFound = false;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }


    private void init() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        preferenceManager = new PreferenceManager(this);

    }

    private void setListeners() {


        ActivityResultLauncher<Intent> SelectImage = registerForActivityResult
                (new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                Intent intent = result.getData();
                                if (intent != null) {
                                    ProfilePhotoPath = intent.getData();
                                    Glide.with(getApplicationContext()).load(ProfilePhotoPath).into(binding.mtIVPropfilePhoto);
                                }
                            }
                        });
        binding.tvSelectImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            SelectImage.launch(intent);
        });


        if (preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
            binding.etName.getEditText().setText(preferenceManager.getString(CONSTANTS.EMPLOYEE_NAME));
            binding.etMail.getEditText().setText(preferenceManager.getString(CONSTANTS.EMPLOYEE_EMAIL));
            binding.etPassword.getEditText().setText(preferenceManager.getString(CONSTANTS.EMPLOYEE_PASSWORD));
            binding.tvOC.setText(preferenceManager.getString(CONSTANTS.EMPLOYEE_OC));
            Glide.with(this).load(preferenceManager.getString(CONSTANTS.EMPLOYEE_PROFILE_PHOTO_URL))
                    .placeholder(R.drawable.person).into(binding.mtIVPropfilePhoto);
        } else if (preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP)) {
            binding.etName.getEditText().setText(preferenceManager.getString(CONSTANTS.EMPLOYER_NAME));
            binding.etMail.getEditText().setText(preferenceManager.getString(CONSTANTS.EMPLOYER_EMAIL));
            binding.etPassword.getEditText().setText(preferenceManager.getString(CONSTANTS.EMPLOYER_PASSWORD));
            binding.tvOC.setText(preferenceManager.getString(CONSTANTS.EMPLOYER_OC));
            Glide.with(this).load(preferenceManager.getString(CONSTANTS.EMPLOYER_PROFILE_PHOTO_URL))
                    .placeholder(R.drawable.person).into(binding.mtIVPropfilePhoto);
        }

        progressDialog.dismiss();
        confirmPassVisibility();
        binding.etPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        binding.mtBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsDataValid()) {
                    loading(true);
                    isDetailsUnique();
                }
            }
        });

    }

    private void confirmPassVisibility() {
        binding.etPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.etConfirmPassword.setVisibility(View.VISIBLE);
                binding.viewPassword.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean IsDataValid() {
        if (binding.etName.getEditText().getText().toString().trim().isEmpty()) {
            ShowToast("Please Enter username");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etMail.getEditText().getText().toString().trim()).matches()) {
            ShowToast("Please Enter valid email");
            return false;
        } else if (!isValidPassword(binding.etPassword.getEditText().getText().toString().trim())) {
            ShowToast("Please Enter valid Password");
            return false;
        } else if (!binding.etPassword.getEditText().getText().toString().trim().equals(binding.etConfirmPassword.getEditText().getText().toString().trim())) {
            ShowToast("Password and confirm password must be the same");
            return false;
        } else if (binding.tvOC.getText().toString().trim().isEmpty()) {
            ShowToast("Please Enter Organization Code");
            return false;
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

    private void isDetailsUnique() {
        ShowToast("Details Unique mai");

        if (preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
            if (!(binding.etMail.getEditText().getText().toString().trim().
                    equals(preferenceManager.getString(CONSTANTS.EMPLOYEE_EMAIL)))) {
                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE)
                        .whereEqualTo(CONSTANTS.EMPLOYEE_EMAIL, binding.etMail.getEditText().getText().toString().trim())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                                    userFound = true;
                                    //SignUp(userFound);

                                }

                            }

                        });

                Update(userFound);
            }


        } else if (preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP)) {
            if (!(binding.etMail.getEditText().getText().toString().trim().
                    equals(preferenceManager.getString(CONSTANTS.EMPLOYER_EMAIL)))) {
                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                        .whereEqualTo(CONSTANTS.EMPLOYER_EMAIL, binding.etMail.getEditText().getText().toString().trim())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                                    Toast.makeText(EditProfileActivity.this, "eeeeeeeeee", Toast.LENGTH_SHORT).show();

                                    userFound = true;

                                    //SignUp(userFound);
                                }

                            }

                        });


            }
            Update(userFound);


        }
        ShowToast("Details Unique end mai");
    }

    private void Update(boolean userExist) {
        ShowToast("Signup k strt m");
        if (userExist) {
            loading(false);
            ShowToast("Employee with this email already exist");
        } else {

            ShowToast("OC exists");

            if (preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
                if (ProfilePhotoPath == null) {
                    ProfilePhotoPath = Uri.parse(preferenceManager.getString(CONSTANTS.EMPLOYEE_PROFILE_PHOTO_URL));
                }
                StorageReference reference = storageReference.child("EmployeeProfileImages/" + UUID.randomUUID().toString());
                reference.putFile(ProfilePhotoPath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            ShowToast("Image uploaded to storage");
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            ShowToast("Image url downloaded");
                                            DocumentReference document = firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE)
                                                    .document(preferenceManager.getString(CONSTANTS.EMPLOYEE_ID));
                                            String name = binding.etName.getEditText().getText().toString().trim();
                                            String email = binding.etMail.getEditText().getText().toString().trim();
                                            String password = binding.etPassword.getEditText().getText().toString().trim();
                                            String oc = binding.tvOC.getText().toString().trim();
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

                                                                preferenceManager.putBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP, true);
                                                                preferenceManager.putBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP, false);
                                                                preferenceManager.putString(CONSTANTS.EMPLOYEE_ID, employee.getEmployeeID());
                                                                preferenceManager.putString(CONSTANTS.EMPLOYEE_EMAIL, employee.getEmployeeEmail());
                                                                preferenceManager.putString(CONSTANTS.EMPLOYEE_NAME, employee.getEmployeeName());
                                                                preferenceManager.putString(CONSTANTS.EMPLOYEE_PASSWORD, employee.getEmployeePassword());
                                                                preferenceManager.putString(CONSTANTS.EMPLOYEE_OC, employee.getEmployeeOC());
                                                                preferenceManager.putString(CONSTANTS.EMPLOYEE_PROFILE_PHOTO_URL, employee.getEmployeeProfilePhotoUrl());
                                                                ShowToast("Intent k upar");
                                                                loading(false);


                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
                });
            } else if (preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP)) {

                if (ProfilePhotoPath == null) {
                    ProfilePhotoPath = Uri.parse(preferenceManager.getString(CONSTANTS.EMPLOYER_PROFILE_PHOTO_URL));
                }
                StorageReference reference = storageReference.child("EmployerProfileImages/" + UUID.randomUUID().toString());
                reference.putFile(ProfilePhotoPath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ShowToast("Image Uploaded to storage");
                        reference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        ShowToast("Image downloaded");
                                        DocumentReference document = firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                                                .document(preferenceManager.getString(CONSTANTS.EMPLOYER_ID));
                                        String name = binding.etName.getEditText().getText().toString().trim();
                                        String email = binding.etMail.getEditText().getText().toString().trim();
                                        String password = binding.etPassword.getEditText().getText().toString().trim();
                                        String oc = binding.tvOC.getText().toString().trim();
                                        Employer employer = new Employer(
                                                document.getId(),
                                                name, email, oc, password, uri.toString());
                                        document.set(employer)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            ShowToast("Setting Employer Details");
                                                            preferenceManager.putBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP, true);
                                                            preferenceManager.putBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP, false);
                                                            preferenceManager.putString(CONSTANTS.EMPLOYER_ID, employer.getEmployerID());
                                                            preferenceManager.putString(CONSTANTS.EMPLOYER_EMAIL, employer.getEmployerEmail());
                                                            preferenceManager.putString(CONSTANTS.EMPLOYER_NAME, employer.getEmployerName());
                                                            preferenceManager.putString(CONSTANTS.EMPLOYER_PASSWORD, employer.getEmployerPassword());
                                                            preferenceManager.putString(CONSTANTS.EMPLOYER_OC, employer.getEmployerOC());
                                                            preferenceManager.putString(CONSTANTS.EMPLOYER_PROFILE_PHOTO_URL, employer.getEmployerProfilePhotoUrl());
                                                            ShowToast("Intent k upar");
                                                            loading(false);
                                                        }

                                                    }
                                                });
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ShowToast("FAillleedddd.................");
                    }
                });

                   /* StorageReference reference = storageReference.child("EmployerProfileImages/" + UUID.randomUUID().toString());
                    reference.putFile(ProfilePhotoPath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                ShowToast("Image uploaded to storage");
                                reference.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                ShowToast("Image url downloaded");
                                                DocumentReference document = firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                                                        .document(preferenceManager.getString(CONSTANTS.EMPLOYER_ID));
                                                String name = binding.etName.getEditText().getText().toString().trim();
                                                String email = binding.etMail.getEditText().getText().toString().trim();
                                                String password = binding.etPassword.getEditText().getText().toString().trim();
                                                String oc = binding.tvOC.getText().toString().trim();
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
                                                                if (task.isSuccessful()) {
                                                                    ShowToast("setting employer details");

                                                                    preferenceManager.putBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP, true);
                                                                    preferenceManager.putBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP, false);
                                                                    preferenceManager.putString(CONSTANTS.EMPLOYER_ID, employer.getEmployerID());
                                                                    preferenceManager.putString(CONSTANTS.EMPLOYER_EMAIL, employer.getEmployerEmail());
                                                                    preferenceManager.putString(CONSTANTS.EMPLOYER_NAME, employer.getEmployerName());
                                                                    preferenceManager.putString(CONSTANTS.EMPLOYER_PASSWORD, employer.getEmployerPassword());
                                                                    preferenceManager.putString(CONSTANTS.EMPLOYER_OC, employer.getEmployerOC());
                                                                    preferenceManager.putString(CONSTANTS.EMPLOYER_PROFILE_PHOTO_URL, employer.getEmployerProfilePhotoUrl());
                                                                    ShowToast("Intent k upar");
                                                                    loading(false);


                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                    });*/
            }


        }
    }


    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.mtBtnUpdate.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.mtBtnUpdate.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void ShowToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}