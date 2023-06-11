package com.example.updesk.More;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.ActivityMyOrganizationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.harmony.awt.datatransfer.DataSnapshot;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class MyOrganizationActivity extends AppCompatActivity {
    ActivityMyOrganizationBinding binding;

    FirebaseFirestore firebaseFirestore;
    PieChart pieChart;
    Integer totalTasks = 0;
    Integer pendingTasks = 0;
    Integer rejectedTasks = 0;
    Integer acceptedTasks = 0;
    String ocValue=null;
    PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrganizationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pieChart = binding.pieChart;
        firebaseFirestore=FirebaseFirestore.getInstance();
        preferenceManager=new PreferenceManager(MyOrganizationActivity.this);

        checkoc();
        gettingUsers();
    gettingData();



    }

    private void gettingUsers() {
        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE)
                .whereEqualTo(CONSTANTS.EMPLOYEE_OC,ocValue)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult().getDocuments().size()>0)
                        {
                            binding.tvEmployees.setText("Total Employees:"+task.getResult().getDocuments().size());
                        }
                    }
                });
        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYER)
                .whereEqualTo(CONSTANTS.EMPLOYER_OC,ocValue)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult().getDocuments().size()>0)
                        {
                            binding.tvEmployers.setText("Total Employers:"+task.getResult().getDocuments().size());
                        }
                    }
                });
    }

    private void gettingData() {

        firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_TASK).whereEqualTo("OC",ocValue)
                .whereEqualTo("userType","Employee").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().getDocuments().size()>0)
                        {
                            Toast.makeText(MyOrganizationActivity.this, "sgfdfdsrds", Toast.LENGTH_SHORT).show();
                            String status=null;
                            for (DocumentSnapshot documentSnapshot:task.getResult())
                            {
                               status= documentSnapshot.getString(CONSTANTS.KEY_TASK_Status);
                               if(status.equals("Pending"))
                               {
                                   pendingTasks++;
                               } else if (status.equals("Approved"))
                               {
                                   acceptedTasks++;
                               }
                               else{
                                   rejectedTasks++;
                               }

                            }
                            binding.tvAccepted.setText("Accepted tasks:"+acceptedTasks.toString());
                            binding.tvRejected.setText("Rejected tasks:"+rejectedTasks.toString());
                            binding.tvPending.setText("Pending tasks:"+pendingTasks.toString());


                        }
                        addToPieChart();
                         }
                });
    }

    private void checkoc() {
        if(preferenceManager.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)){

            ocValue=preferenceManager.getString(CONSTANTS.EMPLOYEE_OC);

        }else{

            ocValue=preferenceManager.getString(CONSTANTS.EMPLOYER_OC);

        }
    }

    private void addToPieChart() {

        pieChart.addPieSlice(new PieModel("Accepted Tasks", acceptedTasks, Color.parseColor("#040453")));
        pieChart.addPieSlice(new PieModel("Rejected Tasks", rejectedTasks, Color.parseColor("#5A74C3")));
        pieChart.addPieSlice(new PieModel("Pending Tasks", pendingTasks, Color.parseColor("#8181A8")));
        pieChart.startAnimation();


    }
}