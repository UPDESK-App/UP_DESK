package com.example.updesk.Attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.updesk.ModelClasses.Employee;
import com.example.updesk.R;
import com.example.updesk.Utilities.CONSTANTS;
import com.example.updesk.Utilities.PreferenceManager;

import java.util.ArrayList;

public class ViewEmployeesAttendanceAdapter extends RecyclerView.Adapter<ViewEmployeesAttendanceAdapter.Holder> {
    private ArrayList<AttendanceEmployee> attendanceEmployeesList;
    private Context context;
    LayoutInflater inflater;
    PreferenceManager pref;

    public ViewEmployeesAttendanceAdapter(ArrayList<AttendanceEmployee> attendanceEmployeesList, Context context) {
        this.attendanceEmployeesList = attendanceEmployeesList;
        this.context = context;
        inflater=LayoutInflater.from(context);
        pref=new PreferenceManager(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_employee_attendance_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        AttendanceEmployee selectedEmployee = attendanceEmployeesList.get(holder.getAdapterPosition());


        holder.date.setText("Date: " + selectedEmployee.getDate());
        holder.attendanceStatus.setText(selectedEmployee.getAttendanceStatus());
        holder.workedHours.setText("Worked Hours: " + selectedEmployee.getWorkedHours());
        holder.pph.setText("Payment Per Hour:  $" + selectedEmployee.getPaymentPerHour());
        if (selectedEmployee.getAttendanceStatus().equals("Present")) {

            holder.attendanceImg.setImageResource(R.drawable.present_img);

        }else if(selectedEmployee.getAttendanceStatus().equals("Absent")){
            holder.attendanceImg.setImageResource(R.drawable.absent_img);
        }


    }

    @Override
    public int getItemCount() {
        return attendanceEmployeesList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        private ImageView attendanceImg;
        private TextView attendanceStatus,date,pph,workedHours,totalPay;



        public Holder(@NonNull View itemView) {
            super(itemView);

            attendanceImg = itemView.findViewById(R.id.employeeAttendanceImg);

            attendanceStatus = itemView.findViewById(R.id.tvAttendanceStatus);
            date = itemView.findViewById(R.id.tvDate);
            pph = itemView.findViewById(R.id.tvPPH);
            workedHours = itemView.findViewById(R.id.tvTotalWorkedHours);


        }
    }
}
