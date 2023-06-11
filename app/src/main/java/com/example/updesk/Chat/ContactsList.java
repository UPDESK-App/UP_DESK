package com.example.updesk.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.example.updesk.ModelClasses.Employee;
import com.example.updesk.ModelClasses.Employer;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.ActivityContactsListBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class ContactsList extends AppCompatActivity {
    ActivityContactsListBinding binding;


    FirebaseFirestore firebaseFirestore;
    PreferenceManager pref;


    ArrayList<Employer> employers;
    ArrayList<Employee> employees;
String OC;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityContactsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pref=new PreferenceManager(this);
        progressDialog=new ProgressDialog(this);

        gettingOC();


        firebaseFirestore=FirebaseFirestore.getInstance();
        showdialog();
        load_employers();
        load_emplyees();
        getToken();
    }

    private void gettingOC() {
        if(pref.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP))
        {
            OC=pref.getString(CONSTANTS.EMPLOYER_OC);
        }
        else if(pref.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP))
        {
            OC=pref.getString(CONSTANTS.EMPLOYEE_OC);
        }
    }

    private void load_emplyees() {
        employees=new ArrayList<>();

        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE)//accessing whole collection
                .whereEqualTo(CONSTANTS.EMPLOYEE_OC,OC)//accessing whole collection
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                employees.add(new Employee(documentSnapshot.getString(CONSTANTS.EMPLOYEE_ID),
                                        documentSnapshot.getString(CONSTANTS.EMPLOYEE_NAME),documentSnapshot.getString(CONSTANTS.EMPLOYEE_EMAIL)
                                        ,documentSnapshot.getString(CONSTANTS.EMPLOYEE_OC),documentSnapshot.getString(CONSTANTS.EMPLOYEE_PASSWORD)
                                        ,documentSnapshot.getString(CONSTANTS.EMPLOYEE_PROFILE_PHOTO_URL),
                                        documentSnapshot.getString(CONSTANTS.KEY_FCM_TOKEN) ));
                            }

                            ShowEmployees();
                            progressDialog.cancel();

                        }
                    }});
    }
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private  void updateToken(String token){
        if(pref.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)==true){
            DocumentReference documentReference=firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE)
                    .document(pref.getString(CONSTANTS.EMPLOYEE_ID));
            documentReference.update(CONSTANTS.KEY_FCM_TOKEN,token)
                    .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Token updated Successfully"+ token, Toast.LENGTH_SHORT).show();
                            }
                    );}
        else {
            DocumentReference documentReference=firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                    .document(pref.getString(CONSTANTS.EMPLOYER_ID));
            documentReference.update(CONSTANTS.KEY_FCM_TOKEN,token)
                    .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Token updated Successfully"+ token, Toast.LENGTH_SHORT).show();
                            }
                    );
        }
    }
    private void load_employers() {
        employers=new ArrayList<>();

        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                .whereEqualTo(CONSTANTS.EMPLOYER_OC,OC)//accessing whole collection
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

                        }
                    }});
    }

    private void ShowEmployeers() {
       employer_chat_adapter employer_chat_adapter=new employer_chat_adapter(employers,this);
        binding.rvEmployers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvEmployers.setAdapter(employer_chat_adapter);

    }
    private void ShowEmployees() {
        employee_chat_adapter employee_chat_adapter= new employee_chat_adapter(employees,this);
        binding.rvEmployees.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvEmployees.setAdapter(employee_chat_adapter);

    }
    public void showdialog() {
        progressDialog.setTitle(" Loading Members");
        progressDialog.setMessage("Connecting to Firebase");
        progressDialog.show();
    }
}