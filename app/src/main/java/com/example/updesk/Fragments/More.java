package com.example.updesk.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.updesk.Chat.ContactsList;
import com.example.updesk.LoginActivities.EmployeeSignUpActivity;
import com.example.updesk.LoginActivities.EmployerLoginActivity;
import com.example.updesk.LoginActivities.EmployerSignUpActivity;
import com.example.updesk.More.EditProfileActivity;
import com.example.updesk.More.Help_And_FAQ;
import com.example.updesk.More.MyOrganizationActivity;
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.FragmentMoreBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;


public class More extends Fragment {

   FragmentMoreBinding binding;
   PreferenceManager pref;

   FirebaseFirestore firebaseFirestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentMoreBinding.inflate(inflater,container,false);
        inflater.inflate(R.layout.fragment_more,container,false);
        pref=new PreferenceManager(getContext());
        firebaseFirestore=FirebaseFirestore.getInstance();



        setListeners();
        return (binding.getRoot());
    }

    private void setListeners() {

        binding.tvMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pref.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
                    Toast.makeText(getContext(),pref.getString(CONSTANTS.EMPLOYEE_NAME), Toast.LENGTH_SHORT).show();

                } else if (pref.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP)) {

                    Toast.makeText(getContext(),pref.getString(CONSTANTS.EMPLOYER_NAME), Toast.LENGTH_SHORT).show();
                }

            }
        });
        binding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to logout this account? ")
                        .setPositiveButton("Logout", (dialogInterface, i) -> {

                            HashMap<String,Object> updates=new HashMap<>();
                            updates.put(CONSTANTS.KEY_FCM_TOKEN, FieldValue.delete());
                            if(pref.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {

                                DocumentReference documentReference= firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE)
                                        .document(pref.getString(CONSTANTS.EMPLOYEE_ID));
                                documentReference.update(updates)
                                        .addOnSuccessListener(unused -> {
                                                    shift();
                                                }


                                        ).addOnFailureListener(e->{
                                            Toast.makeText(getContext(), "Unable to Sign Out", Toast.LENGTH_SHORT).show();
                                        });

                                pref.clear();


                            }

                            else{

                                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                                        .document(pref.getString(CONSTANTS.EMPLOYER_ID)).update(updates).
                                        addOnSuccessListener(unused ->{
                                            shift();})
                                        .addOnFailureListener(e->{
                                            Toast.makeText(getContext(), "Unable to Sign Out", Toast.LENGTH_SHORT).show();
                                        });
                                pref.clear();
                            }

                        });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.create();
                builder.show();




            }
        });


        binding.tvDeleteAccountOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to permanently delete this account? ")
                        .setPositiveButton("Yes",(dialogInterface, i) -> {

                            if(pref.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP))
                            {
                                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE)
                                        .whereEqualTo(CONSTANTS.EMPLOYEE_ID,pref.getString(CONSTANTS.EMPLOYEE_ID))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()&& task.getResult().getDocuments().size()>0)
                                                {
                                                    DocumentReference documentReference = task.getResult().getDocuments().get(0).getReference();
                                                    documentReference.delete();

                                                    Toast.makeText(getContext(), "Account deleted permanently", Toast.LENGTH_SHORT).show();
                                                    pref.clear();
                                                    Intent intent=new Intent(getActivity(), EmployeeSignUpActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });

                            }

                            else if(pref.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP))
                            {
                                firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                                        .whereEqualTo(CONSTANTS.EMPLOYER_ID,pref.getString(CONSTANTS.EMPLOYER_ID))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()&& task.getResult().getDocuments().size()>0)
                                                {
                                                     DocumentReference documentReference = task.getResult().getDocuments().get(0).getReference();
                                                    documentReference.delete();


                                                    Toast.makeText(getContext(), "Account deleted permanently", Toast.LENGTH_SHORT).show();
                                                    pref.clear();
                                                    Intent intent=new Intent(getActivity(), EmployerSignUpActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });

                            }



                        });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.create();
                builder.show();


            }
        });
        binding.tvFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), Help_And_FAQ.class);
                startActivity(i);
            }
        });
        binding.tvMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), EditProfileActivity.class);
                startActivity(i);
            }
        });

        binding.tvOrganizationInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyOrganizationActivity.class));
            }
        });
    }


    private void shift() {
        Intent intent=new Intent(getContext(), EmployerLoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


}