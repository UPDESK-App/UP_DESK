package com.example.updesk.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.updesk.ModelClasses.Employee;
import com.example.updesk.ModelClasses.Employer;
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.Chat.employee_chat_adapter;
import com.example.updesk.Chat.employer_chat_adapter;
import com.example.updesk.databinding.FragmentUserLIstBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;


public class UserLIst extends Fragment {

   FragmentUserLIstBinding binding;

    FirebaseFirestore firebaseFirestore;
    PreferenceManager pref;


    ArrayList<Employer> employers;
    ArrayList<Employee> employees;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentUserLIstBinding.inflate(inflater,container,false);
        inflater.inflate(R.layout.fragment_messenger,container,false);


        pref=new PreferenceManager(getContext());
        progressDialog=new ProgressDialog(getContext());

        firebaseFirestore=FirebaseFirestore.getInstance();
        showdialog();
        load_employers();
        load_emplyees();
        setListeners();
        getToken();
        return (binding.getRoot());
    }

    private void setListeners() {


    }


    private void load_emplyees() {
        employees=new ArrayList<>();

        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE) //accessing whole collection
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                employees.add(new Employee(documentSnapshot.getString(CONSTANTS.EMPLOYEE_ID), documentSnapshot.getString(CONSTANTS.EMPLOYEE_NAME),documentSnapshot.getString(CONSTANTS.EMPLOYEE_EMAIL),documentSnapshot.getString(CONSTANTS.EMPLOYEE_OC),documentSnapshot.getString(CONSTANTS.EMPLOYEE_PASSWORD),documentSnapshot.getString(CONSTANTS.EMPLOYEE_PROFILE_PHOTO_URL),documentSnapshot.getString(CONSTANTS.KEY_FCM_TOKEN) ));
                            }

                            ShowEmployees();
                            progressDialog.cancel();
                            Toast.makeText(getContext(),"Employees" + Integer.toString(employees.size()),Toast.LENGTH_SHORT).show();

                        }
                    }});
    }
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private  void updateToken(String token){
        pref.putString(CONSTANTS.KEY_FCM_TOKEN,token);
        if(pref.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)==true){
            DocumentReference documentReference=firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE)
                    .document(pref.getString(CONSTANTS.EMPLOYEE_ID));
            documentReference.update(CONSTANTS.KEY_FCM_TOKEN,token)
                    .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Token updated Successfully"+ token, Toast.LENGTH_SHORT).show();
                            }
                    );}
        else {
            DocumentReference documentReference=firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                    .document(pref.getString(CONSTANTS.EMPLOYER_ID));
            documentReference.update(CONSTANTS.KEY_FCM_TOKEN,token)
                    .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Token updated Successfully"+ token, Toast.LENGTH_SHORT).show();
                            }
                    );
        }
    }
    private void load_employers() {
        employers=new ArrayList<>();

        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER) //accessing whole collection
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                employers.add(new Employer(documentSnapshot.getString(CONSTANTS.EMPLOYER_ID), documentSnapshot.getString(CONSTANTS.EMPLOYER_NAME),documentSnapshot.getString(CONSTANTS.EMPLOYER_EMAIL),documentSnapshot.getString(CONSTANTS.EMPLOYER_OC),documentSnapshot.getString(CONSTANTS.EMPLOYER_PASSWORD),documentSnapshot.getString(CONSTANTS.EMPLOYER_PROFILE_PHOTO_URL),documentSnapshot.getString(CONSTANTS.KEY_FCM_TOKEN)  ));




                            }

                            ShowEmployeers();
                            progressDialog.cancel();
                            Toast.makeText(getContext(),"Employers" + Integer.toString(employers.size()),Toast.LENGTH_SHORT).show();

                        }
                    }});
    }

    private void ShowEmployeers() {
        employer_chat_adapter employer_chat_adapter=new employer_chat_adapter(employers,getContext());
        binding.rvEmployers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvEmployers.setAdapter(employer_chat_adapter);

    }
    private void ShowEmployees() {
        employee_chat_adapter employee_chat_adapter= new employee_chat_adapter(employees,getContext());
        binding.rvEmployees.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvEmployees.setAdapter(employee_chat_adapter);

    }
    public void showdialog() {
        progressDialog.setTitle(" Loading Members");
        progressDialog.setMessage("Connecting to Firebase");
        progressDialog.show();
    }
}