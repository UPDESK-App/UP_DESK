package com.example.updesk.Attendance;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.updesk.Chat.employee_chat_adapter;
import com.example.updesk.ModelClasses.Employee;
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class EmployeesAttendanceAdapter extends RecyclerView.Adapter<EmployeesAttendanceAdapter.Holder> {

    private ArrayList<Employee> employees;
    private Context context;
    LayoutInflater inflater;
    String atndncStatus;
    EditText workedHours;
    EditText pph;
    TextView date;
    TextView name;

    Button btnOk;

    RadioGroup radioGroup;

    public EmployeesAttendanceAdapter(ArrayList<Employee> employees, Context context) {
        this.employees = employees;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_employee_layout, parent, false);
        return new Holder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Employee selectedEmployee = employees.get(holder.getAdapterPosition());


        holder.employeeName.setText(selectedEmployee.getEmployeeName());
        holder.employeeEmail.setText(selectedEmployee.getEmployeeEmail());

        holder.btnMarkAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                final View receiverUserDetailsPopupView = inflater.inflate(R.layout.attendance_popup, null);
                workedHours = receiverUserDetailsPopupView.findViewById(R.id.etWorkedHours);
                pph = receiverUserDetailsPopupView.findViewById(R.id.etPaymentPerHour);
                date = receiverUserDetailsPopupView.findViewById(R.id.tvDate);
                name = receiverUserDetailsPopupView.findViewById(R.id.tvName);
                btnOk = receiverUserDetailsPopupView.findViewById(R.id.btnOk);
                radioGroup = receiverUserDetailsPopupView.findViewById(R.id.radioGroupAttendance);


                name.setText(selectedEmployee.getEmployeeName());
                builder.setView(receiverUserDetailsPopupView);
                final AlertDialog ad=builder.create();;
                
              ad.show();


                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar calendar = Calendar.getInstance();

                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                date.setText(i2 + "-" + (i1 + 1) + "-" + i);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

                        datePickerDialog.show();
                    }
                });

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkforValidation()) {

                            int radioId = radioGroup.getCheckedRadioButtonId();
                            if (radioId == receiverUserDetailsPopupView.findViewById(R.id.radioBtnPresent).getId()) {
                                atndncStatus = "Present";


                            } else if (radioId == receiverUserDetailsPopupView.findViewById(R.id.radioBtnAbsent).getId()) {
                                atndncStatus = "Absent";

                            }
                            AttendanceEmployee attendanceEmployee = new AttendanceEmployee(selectedEmployee.getEmployeeID(),
                                    selectedEmployee.getEmployeeName(), selectedEmployee.getEmployeeOC(), atndncStatus, workedHours.getText().toString().trim(), pph.getText().toString().trim(), date.getText().toString().trim());
                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                            firebaseFirestore.collection(CONSTANTS.KEY_COLLECTION_ATTENDANCE)
                                    .add(attendanceEmployee)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Attendance Updated", Toast.LENGTH_SHORT).show();
                                             //   builder.setCancelable(true);
                                                ad.dismiss();

                                            }
                                        }
                                    });
                        }
                    }
                });


            }
        });

    }

    private Boolean checkforValidation() {
        if (workedHours.getText().toString().trim().isEmpty()) {
            workedHours.setError("Please Enter worked hours!");

            return false;
        } else if (pph.getText().toString().trim().isEmpty()) {
            pph.setError("Please Enter Payment Per Hour!");

            return false;
        } else if (date.getText().toString().equals("Select Date+")) {
            date.setError("Please Select Date!");

            return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView employeeName;
        private TextView employeeEmail;
        private Button btnMarkAttendance;

        public Holder(@NonNull View itemView) {
            super(itemView);


            employeeName = itemView.findViewById(R.id.tvEmployeeName);
            employeeEmail = itemView.findViewById(R.id.tvEmployeeEmail);
            btnMarkAttendance = itemView.findViewById(R.id.btnMarkAttendance);
        }
    }
}
