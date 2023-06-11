package com.example.updesk.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.updesk.Attendance.AttendanceEmployee;
import com.example.updesk.Attendance.EmployeesAttendanceAdapter;
import com.example.updesk.Attendance.ViewEmployeesAttendanceAdapter;
import com.example.updesk.ModelClasses.Employee;
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;
import com.example.updesk.databinding.FragmentAttendanceBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;


public class Attendance extends Fragment {


    FragmentAttendanceBinding binding;
    RelativeLayout rl;

    Uri imageuri;
    String[] datearray;
    FirebaseFirestore firebaseFirestore;
    PreferenceManager pref;
    ProgressDialog progressDialog;
    ArrayList<Employee> employeesList;
    ArrayList<AttendanceEmployee> attendanceEmployees;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAttendanceBinding.inflate(inflater, container, false);
        inflater.inflate(R.layout.fragment_attendance, container, false);


        init();
        setListeners();

        return (binding.getRoot());
    }


    private void init() {
        pref = new PreferenceManager(getContext());
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getContext());
        employeesList = new ArrayList<>();
        attendanceEmployees = new ArrayList<>();
    }

    private void setListeners() {

        if (pref.getBoolean(CONSTANTS.IS_EMPLOYER_SIGN_UP)) {

            firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_EMPLOYEE)
                    .whereEqualTo(CONSTANTS.EMPLOYEE_OC, pref.getString(CONSTANTS.EMPLOYER_OC))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {


                                    employeesList.add(new Employee(documentSnapshot.getString(CONSTANTS.EMPLOYEE_ID),
                                            documentSnapshot.getString(CONSTANTS.EMPLOYEE_NAME), documentSnapshot.getString(CONSTANTS.EMPLOYEE_EMAIL)
                                            , documentSnapshot.getString(CONSTANTS.EMPLOYEE_OC), documentSnapshot.getString(CONSTANTS.EMPLOYEE_PASSWORD)
                                            , documentSnapshot.getString(CONSTANTS.EMPLOYEE_PROFILE_PHOTO_URL)));

                                }
                                EmployeesAttendanceAdapter employeesAttendanceAdapter = new EmployeesAttendanceAdapter(employeesList, getContext());
                                binding.rvEmployeesListForAttendance.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                binding.rvEmployeesListForAttendance.setAdapter(employeesAttendanceAdapter);

                            }
                        }
                    });


        } else if (pref.getBoolean(CONSTANTS.IS_EMPLOYEE_SIGN_UP)) {
            binding.btnPayRollGenerator.setVisibility(View.VISIBLE);
            binding.tvMarkAttendance.setText("Attendance Record");
            Calendar calendar = Calendar.getInstance();
            String currentYear = String.valueOf(calendar.get(Calendar.YEAR));

            String currentMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_ATTENDANCE)
                    .whereEqualTo(CONSTANTS.KEY_ATTENDANCE_ID, pref.getString(CONSTANTS.EMPLOYEE_ID))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                    datearray = documentSnapshot.getString(CONSTANTS.KEY_ATTENDANCE_DATE).split("-");

                                    if ((datearray[1].equals(currentMonth)) && (datearray[2].equals(currentYear))) {
                                        attendanceEmployees.add(new AttendanceEmployee(documentSnapshot.getId(),
                                                documentSnapshot.getString(CONSTANTS.KEY_ATTENDANCE_NAME),
                                                documentSnapshot.getString(CONSTANTS.KEY_ATTENDANCE_OC),
                                                documentSnapshot.getString(CONSTANTS.KEY_ATTENDANCE_STATUS),
                                                documentSnapshot.getString(CONSTANTS.KEY_ATTENDANCE_WORKED_HOURS),
                                                documentSnapshot.getString(CONSTANTS.KEY_PAYMENT_PER_HOUR),
                                                documentSnapshot.getString(CONSTANTS.KEY_ATTENDANCE_DATE)));
                                    }


                                }
                                String s = String.valueOf(attendanceEmployees.size());
                                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                                ViewEmployeesAttendanceAdapter viewEmployeesAttendanceAdapter = new ViewEmployeesAttendanceAdapter(attendanceEmployees, getContext());


                                binding.rvEmployeesListForAttendance.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                binding.rvEmployeesListForAttendance.setAdapter(viewEmployeesAttendanceAdapter);


                            }


                        }
                    });


            binding.btnPayRollGenerator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    final View calculatePayrollPopupView = getLayoutInflater().
                            inflate(R.layout.calculate_payroll_popup, null);

                    TextView name = calculatePayrollPopupView.findViewById(R.id.tvpayrollName);
                    TextView email = calculatePayrollPopupView.findViewById(R.id.tvpayrollEmail);
                    TextView oc = calculatePayrollPopupView.findViewById(R.id.tvpayrollOC);
                    TextView month = calculatePayrollPopupView.findViewById(R.id.tvPayrollMonth);
                    TextView workedHours = calculatePayrollPopupView.findViewById(R.id.tvPayrollWorkedHours);
                    TextView totalPaytv = calculatePayrollPopupView.findViewById(R.id.tvpayrollTotalPay);
                    TextView btnSavePayrollreceipt = calculatePayrollPopupView.findViewById(R.id.btnSave);


                    int wh;
                    int pph;
                    int total = 0;
                    int twh = 0;


                    for (AttendanceEmployee singleEmployeeObject : attendanceEmployees) {

                        wh = Integer.parseInt(singleEmployeeObject.getWorkedHours());
                        pph = Integer.parseInt(singleEmployeeObject.getPaymentPerHour());
                        total = total + (wh * pph);
                        twh = twh + wh;

                    }
                    String totalPay = Integer.toString(total);

                    name.setText(attendanceEmployees.get(0).getName());
                    email.setText(pref.getString(CONSTANTS.EMPLOYEE_EMAIL));
                   oc.setText(attendanceEmployees.get(0).getOc());
                    month.setText(getMonthName(Integer.parseInt(datearray[1])));
                    workedHours.setText(Integer.toString(twh) );
                    totalPaytv.setText("$"+totalPay);


                    rl= calculatePayrollPopupView.findViewById(R.id.rLPayrollReceipt);
               btnSavePayrollreceipt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            share();
                        }
                    });



                    builder.setView(calculatePayrollPopupView);
                    builder.create();
                    builder.show();
                }
            });


        }
    }



private void share()
{
    Bitmap bitmap=generateBitmapReceiptFromView(rl);

     try {
         File file=new File(requireActivity().getApplicationContext().getFilesDir(),"payroll_receipts.png");
         imageuri=FileProvider.getUriForFile(getActivity().getApplicationContext(),"com.example.updesk.fileProvider",file);
       OutputStream fileOutputStream=requireActivity().getApplicationContext().getContentResolver().openOutputStream(imageuri);
         bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);

         fileOutputStream.close();



         Intent intent=new Intent(Intent.ACTION_SEND);
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
         intent.putExtra(Intent.EXTRA_STREAM,imageuri);
         intent.setType("image/png");
         startActivity(Intent.createChooser(intent,"share by"));
    }
    catch (FileNotFoundException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
         throw new RuntimeException(e);
     }
}



    @SuppressLint("ResourceAsColor")
    private Bitmap  generateBitmapReceiptFromView(View view) {
        Bitmap returnedBitmap= Bitmap.createBitmap(view.getWidth(),
                view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(returnedBitmap);
        Drawable bgDrawable=view.getBackground();
        if(bgDrawable!=null)
        {
            bgDrawable.draw(canvas);
        }
        else
        {
            canvas.drawColor(android.R.color.white);
        }
        view.draw(canvas);
        Toast.makeText(getContext(), "f2", Toast.LENGTH_SHORT).show();
        return returnedBitmap;
    }


    private void showList() {
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        progressDialog.dismiss();
    }
    private String getMonthName(int monthNumber) {
        String name;
        switch (monthNumber) {
            case 1:
               name="January";
                break;
            case 2:
                name="February";
                break;
            case 3:
                name="March";
                break;
            case 4:
                name="April";
                break;
            case 5:
                name="May";
                break;
            case 6:
                name="June";
                break;
            case 7:
               name="July";
                break;
            case 8:
                name="August";
                break;
            case 9:
                name="September";
                break;
            case 10:
                name="October";
                break;
            case 11:
                name="November";
                break;
            case 12:
                name="December";
                break;
            default:
               name="Invalid month.";
                break;
        }
        return name;
    }
}