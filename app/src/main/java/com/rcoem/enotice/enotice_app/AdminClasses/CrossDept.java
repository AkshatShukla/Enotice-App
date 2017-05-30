package com.rcoem.enotice.enotice_app.AdminClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rcoem.enotice.enotice_app.AddNoticeClasses.AddNoticeTabbed;
import com.rcoem.enotice.enotice_app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrossDept extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DepartmentAdapter adapter;
    private List<String> departmentList;
    public Map<String,String> departments;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_dept);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle(R.string.nav_cross);

        context = getApplicationContext();


        //Add departments here.
        departments = new HashMap<>();
        departments.put("Vice Chancellor","ALL");
        departments.put("Computer Science and Engineering","CSE");
        departments.put("Mechanical Engineering","Mech");
        departments.put("Electronics and Communication Engineering","ECE");


        recyclerView = (RecyclerView) findViewById(R.id.cross_dept__recylView_list);

        departmentList = new ArrayList<>();
        for(String dept: departments.keySet()){
            departmentList.add(dept);
        }

        adapter = new DepartmentAdapter(departmentList, context, departments);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

}

class Department{
    String name;
}

class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.MyViewHolder> {

    private List<String> deptList;
    private Context context;
    private Map<String,String> map;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CardView crossDeptCardView;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.deptName);
            crossDeptCardView = (CardView) view.findViewById(R.id.cross_dept_card_view);
        }
    }


    public DepartmentAdapter(List<String> deptList, Context context, Map<String,String> map) {
        this.deptList = deptList;
        this.context = context;
        this.map = map;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_cross_dept_department_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final String deptName = deptList.get(position);
        holder.name.setText(deptName);
        holder.crossDeptCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,AddNoticeTabbed.class);
                String code = map.get(deptName);
                intent.putExtra("postkey",code);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deptList.size();
    }
}
