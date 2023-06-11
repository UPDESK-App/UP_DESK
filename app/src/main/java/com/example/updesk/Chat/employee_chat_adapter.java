package com.example.updesk.Chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.updesk.ModelClasses.Employee;
import com.example.updesk.R;

import java.util.ArrayList;

public class employee_chat_adapter extends RecyclerView.Adapter<employee_chat_adapter.Holder> {
    private ArrayList<Employee> employees;
    private Context context;

    public employee_chat_adapter(ArrayList<Employee> employees, Context context) {
        this.employees = employees;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_layout,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Employee selected_User = employees.get(position);
        holder.userName.setText(selected_User.getEmployeeName());
        holder.userType.setText("Employee");
        Glide.with(context).load(selected_User.getEmployeeProfilePhotoUrl()).placeholder(R.drawable.person).into(holder.profileImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), Chat_Activity.class);
                intent.putExtra("userObject", selected_User);
                intent.putExtra("userType","employee");
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView profileImage;
        private TextView userName;
        private TextView userType;

        public Holder(@NonNull View itemView) {
            super(itemView);

            profileImage=itemView.findViewById(R.id.imgusr);
            userName=itemView.findViewById(R.id.tvUserName);
            userType=itemView.findViewById(R.id.tvUserType);

        }
    }
}
