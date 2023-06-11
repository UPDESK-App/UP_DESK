package com.example.updesk.Fragments;



import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.updesk.Notifications.NotificationData;
import com.example.updesk.Notifications.PushNotification;
import com.example.updesk.Notifications.RetrofitInstance;
import com.example.updesk.TaskRoom.TaskAttachment;
import com.example.updesk.R;
import com.example.updesk.TaskRoom.TaskEmployeeAdapter;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.TaskRoom.TaskEmployerAdapter;

import com.example.updesk.databinding.FragmentTasksBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Tasks extends Fragment {


    FragmentTasksBinding binding;
    PreferenceManager pref;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    String userType, oC, userName;



    ArrayList<TaskAttachment> taskUploadedFiles;
    ProgressDialog progressDialog;


    ArrayList<TaskAttachment> taskSubmittedFiles;

    Uri taskFileUrl;

    NotificationManager manager;
    ActivityResultLauncher<Intent> SelectPdf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        inflater.inflate(R.layout.fragment_tasks, container, false);

        init();
        notifications();

        return (binding.getRoot());
    }

    private void notifications() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                new PreferenceManager(getContext()).putString(CONSTANTS.TOKEN_ID, s);
            }
        });

    }
    private void sendNotification(PushNotification notification) {
        Call<ResponseBody> responseCall = RetrofitInstance.getApi().postNotification(notification);
        responseCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });
    }

    private void init() {

        pref = new PreferenceManager(getContext());
        progressDialog = new ProgressDialog(getContext());

        firebaseFirestore = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        taskUploadedFiles = new ArrayList<>();
        taskSubmittedFiles = new ArrayList<>();
        showLoadingDialogue();
        setListeners();

    }

    private void showLoadingDialogue() {
        progressDialog.setTitle(" Loading Work");
        progressDialog.setMessage("Connecting to Firebase");
        progressDialog.show();
    }

    private void setListeners() {

        gettingUserData();
        loadUploadedFiles();
        loadSubmittedFiles();

        binding.cardCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.cardUpdateStatus.setVisibility(View.GONE);
            }
        });


        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.CardViewUploadTask.setVisibility(View.GONE);
                binding.LLAssignedTasks.setVisibility(View.VISIBLE);
                binding.LLSubmittedTasks.setVisibility(View.VISIBLE);
            }
        });

        SelectPdf = registerForActivityResult
                (new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                Intent intent = result.getData();
                                if (intent != null) {
                                    taskFileUrl = intent.getData();
                                    binding.tvSelectedFileName.setText(intent.getData().toString());
                                    showToast("File selected!");
                                }
                            }
                        });
        binding.btnShowBottomSheet.setOnClickListener(view -> {
            /*if(pref.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP))
            {
                binding.btnShowBottomSheet.findViewById(R.id.txtUpload).setVisibility(View.GONE);
                binding.btnShowBottomSheet.findViewById(R.id.txtSubmit).setVisibility(View.VISIBLE);
            }*/
            showDialog();
        });


        progressDialog.dismiss();
    }


    private void loadUploadedFiles() {
        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_TASK) //accessing whole collection
                .whereEqualTo(CONSTANTS.KEY_TASK_USER_OC, oC)
                .whereEqualTo(CONSTANTS.KEY_TASK_USER_TYPE, "Employer")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                taskUploadedFiles.add(new TaskAttachment(documentSnapshot.getString(CONSTANTS.KEY_TASK_NAME), documentSnapshot.getString(CONSTANTS.KEY_TASK_FILE_URL),
                                        documentSnapshot.getString(CONSTANTS.KEY_TASK_USER_NAME), documentSnapshot.getString(CONSTANTS.KEY_TASK_USER_OC),
                                        documentSnapshot.getString(CONSTANTS.KEY_TASK_Status), documentSnapshot.getString(CONSTANTS.KEY_TASK_USER_TYPE),
                                        documentSnapshot.getString(CONSTANTS.KEY_TASK_DESCRIPTION)));
                            }

                            showUploadedFiles();
                        }
                    }
                });
    }

    private void showUploadedFiles() {


        TaskEmployerAdapter taskEmployerAdapter = new TaskEmployerAdapter(taskUploadedFiles, getContext());


        binding.rvTaskUploadedFiles.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTaskUploadedFiles.setAdapter(taskEmployerAdapter);

    }

    private void loadSubmittedFiles() {
        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_TASK) //accessing whole collection
                .whereEqualTo(CONSTANTS.KEY_TASK_USER_OC, oC)
                .whereEqualTo(CONSTANTS.KEY_TASK_USER_TYPE, "Employee")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                taskSubmittedFiles.add(new TaskAttachment(documentSnapshot.getString(CONSTANTS.KEY_TASK_NAME), documentSnapshot.getString(CONSTANTS.KEY_TASK_FILE_URL),
                                        documentSnapshot.getString(CONSTANTS.KEY_TASK_USER_NAME), documentSnapshot.getString(CONSTANTS.KEY_TASK_USER_OC),
                                        documentSnapshot.getString(CONSTANTS.KEY_TASK_Status), documentSnapshot.getString(CONSTANTS.KEY_TASK_USER_TYPE),
                                        documentSnapshot.getString(CONSTANTS.KEY_TASK_DESCRIPTION)));
                            }

                            showSubmittedFiles();
                        }
                    }
                });
    }

    private void showSubmittedFiles() {

        TaskEmployeeAdapter taskEmployeeAdapter;
        if (pref.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP)) {
            taskEmployeeAdapter = new TaskEmployeeAdapter(taskSubmittedFiles, getContext(), binding.cardUpdateStatus);
        } else {
            taskEmployeeAdapter = new TaskEmployeeAdapter(taskSubmittedFiles, getContext());
        }

        binding.rvTaskSubmittedFiles.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTaskSubmittedFiles.setAdapter(taskEmployeeAdapter);
        progressDialog.dismiss();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.taskroom_bottom_sheet_layout);

        TextView tvUpload = dialog.findViewById(R.id.txtUpload);


        tvUpload.setOnClickListener(view -> {
            binding.LLAssignedTasks.setVisibility(View.INVISIBLE);
            binding.LLSubmittedTasks.setVisibility(View.INVISIBLE);

            binding.CardViewUploadTask.setVisibility(View.VISIBLE);
            binding.btnSelectFile.setOnClickListener(view12 -> {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                SelectPdf.launch(intent);

            });

            binding.btnUploadTask.setOnClickListener(view1 -> {
                if (isFileNameEntered()) {
                    if(isFileDescriptionAdded())
                    {
                        if (isFileSelected()) {

                            UploadTask(taskFileUrl);
                        }
                    }
                }
            });
            dialog.cancel();
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


    private void UploadTask(Uri data) {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        StorageReference reference = storageReference.child("UploadedTasksFiles/" + System.currentTimeMillis() + ".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {


                                        HashMap<String, Object> task = new HashMap<>();
                                        task.put(CONSTANTS.KEY_TASK_NAME, binding.etFileName.getText().toString().trim());
                                        task.put(CONSTANTS.KEY_TASK_FILE_URL, uri.toString());
                                        task.put(CONSTANTS.KEY_TASK_USER_NAME, userName);
                                        task.put(CONSTANTS.KEY_TASK_USER_OC, oC);
                                        task.put(CONSTANTS.KEY_TASK_Status, "Pending");
                                        task.put(CONSTANTS.KEY_TASK_USER_TYPE, userType);
                                        task.put(CONSTANTS.KEY_TASK_DESCRIPTION, binding.etFileDescription.getText().toString().trim());

                                        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_TASK)
                                                .add(task)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if (task.isSuccessful()) {
                                                            showToast("Task uploaded successfully!");

                                                            //Sending Notification
                                                            PushNotification notification = new PushNotification(
                                                                    new NotificationData("New Task uploaded",
                                                                            binding.etFileName.getText().toString().trim()),
                                                                    pref.getString(CONSTANTS.TOPIC) // either pass topic or tokenId
                                                            );
                                                            sendNotification(notification);
                                                            progressDialog.dismiss();
                                                            clearCardData();

                                                        }
                                                    }
                                                });
                                    }
                                });

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded: " + (int) progress + "%");
                    }
                });

    }

    private void gettingUserData() {
        if (pref.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
            userType = "Employee";
            oC = pref.getString(CONSTANTS.EMPLOYEE_OC);
            userName = pref.getString(CONSTANTS.EMPLOYEE_NAME);
            binding.btnUploadTask.setVisibility(View.VISIBLE);
        } else {
            userType = "Employer";
            oC = pref.getString(CONSTANTS.EMPLOYER_OC);
            userName = pref.getString(CONSTANTS.EMPLOYER_NAME);
        }
    }

    private boolean isFileNameEntered() {
        if (binding.etFileName.getText().toString().trim().isEmpty()) {
            binding.etFileName.setError("Please enter task name");
            return false;
        }
        return true;
    }
    private boolean isFileDescriptionAdded() {
        if (binding.etFileDescription.getText().toString().trim().isEmpty()) {
            binding.etFileDescription.setError("Please add task description");
            return false;
        }
        return true;
    }

    private boolean isFileSelected() {
        if (binding.tvSelectedFileName.getText().toString().trim().isEmpty()) {
            binding.tvSelectedFileName.setError("Please select task file");
            return false;
        }
        return true;
    }

    private void clearCardData() {
        binding.txtUploadFile.setText("");
        binding.etFileName.setText("");
        binding.CardViewUploadTask.setVisibility(View.GONE);
        binding.LLSubmittedTasks.setVisibility(View.VISIBLE);
        binding.LLAssignedTasks.setVisibility(View.VISIBLE);
    }


    private void showToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

}