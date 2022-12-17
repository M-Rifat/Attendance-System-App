package com.example.attendancesystemapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {

    Toolbar toolbar;
    private String className;
    private String subjectName;
    private int position;
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Studentitem> studentitems = new ArrayList<>();
    private DBHelper dbHelper;
    private long cid;
    private MyCalender calender;
    private TextView subtitle ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        calender= new MyCalender();
        dbHelper = new DBHelper(this);
        Intent intent = getIntent();
        className = intent.getStringExtra("className");
        subjectName = intent.getStringExtra("subjectName");
        position = intent.getIntExtra("position",-1);
        cid = intent.getLongExtra("cid",-1);

        setToolbar();
        loadData();
        recyclerView = findViewById(R.id.student_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StudentAdapter(this,studentitems);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position->changeStatus(position));
        loadStatusData();
    }

    private void loadData() {
        Cursor cursor = dbHelper.getStudentTable(cid);
        Log.i("1234567890","loadData: "+cid);
        studentitems.clear();
        while(cursor.moveToNext()){
            long sid = cursor.getLong(cursor.getColumnIndex(DBHelper.S_ID));
            int roll = cursor.getInt(cursor.getColumnIndex(DBHelper.STUDENT_ROLL_KEY));
            String name = cursor.getString(cursor.getColumnIndex(DBHelper.STUDENT_NAME_KEY));
            studentitems.add(new Studentitem(sid,roll,name));
        }
        cursor.close();
    }

    private void changeStatus(int position) {
        String status = studentitems.get(position).getStatus();

        if(status.equals("P")) status = "A";
        else status = "P";

        studentitems.get(position).setStatus(status);
        adapter.notifyItemChanged(position);
    }

    private void setToolbar() {

        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);
        save.setOnClickListener(v->saveStatus());

        title.setText(className);
        subtitle.setText(subjectName+" | "+calender.getDate());

        back.setOnClickListener(v -> onBackPressed());
        toolbar.inflateMenu(R.menu.student_menu);
        toolbar.setOnMenuItemClickListener(menuItem->onMenuItemClick(menuItem));

    }

    private void saveStatus() {
        for(Studentitem studentitem : studentitems){
            String status = studentitem.getStatus();
            if(status!="P") status = "A";
           long value = dbHelper.addStatus(studentitem.getSid(),cid,calender.getDate(),status);
           if(value == -1) dbHelper.updateStatus(studentitem.getSid(),calender.getDate(),status);
        }
    }
    private void loadStatusData() {
        for (Studentitem studentitem : studentitems) {
            String status = dbHelper.getStatus(studentitem.getSid(), calender.getDate());
            if(status!=null) studentitem.setStatus(status);
            else studentitem.setStatus("");
        }
        adapter.notifyDataSetChanged();
    }

    private boolean onMenuItemClick(MenuItem menuItem) {
        if(menuItem.getItemId()==R.id.add_student){
            showAddStudentDialog();
        }
        else if(menuItem.getItemId()==R.id.show_Calendar){
            showCalendar();
        }
        else if(menuItem.getItemId()==R.id.show_Attendence_sheet){
            openSheetList();
        }
        return true;
    }

    private void openSheetList() {
        long[] idArray = new long[studentitems.size()];
        String[] nameArray = new String[studentitems.size()];
        int[] rollArray = new int[studentitems.size()];
        for(int i=0;i<idArray.length;i++)
            idArray[i]=studentitems.get(i).getSid();
        for(int i=0;i<rollArray.length;i++)
            rollArray[i]=studentitems.get(i).getRoll();
        for(int i=0;i<nameArray.length;i++)
            nameArray[i]=studentitems.get(i).getName();
        Intent intent = new Intent(this,SheetListActivity.class);
        intent.putExtra("cid",cid);
        intent.putExtra("idArray",idArray);
        intent.putExtra("rollArray",rollArray);
        intent.putExtra("nameArray",nameArray);
        startActivity(intent);
    }

    private void showCalendar() {

        calender.show(getSupportFragmentManager(),"");
        calender.setOnCalendarOkClickListener(this::onCalendarOkClicked);
    }

    private void onCalendarOkClicked(int year, int month, int day) {
       calender.setDate(year,month,day);
       subtitle.setText(subjectName+" | "+calender.getDate());
       loadStatusData();
    }

    private void showAddStudentDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.STUDENT_ADD_DIALOG);
        dialog.setListener((roll,name)->addStudent(roll,name));
    }

    private void addStudent(String roll_string, String name) {
        int roll = Integer.parseInt(roll_string);
        long sid = dbHelper.addStudent(cid,roll,name);
        Studentitem studentitem = new Studentitem(sid,roll,name);
        studentitems.add(studentitem);
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case 0 :
                showUpdateStudentDialog(item.getGroupId());
                break;
            case 1:
                deleteStudent(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateStudentDialog(int position) {
        MyDialog dialog = new MyDialog(studentitems.get(position).getRoll(),studentitems.get(position).getName());
        dialog.show(getSupportFragmentManager(),MyDialog.STUDENT_UPDATE_DIALOG);
        dialog.setListener((roll_string,name)->updateStudent(position,name));
    }

    private void updateStudent(int position, String name) {
        dbHelper.updateStudent(studentitems.get(position).getSid(),name);
        studentitems.get(position).setName(name);
        adapter.notifyItemChanged(position);
    }

    private void deleteStudent(int position){
        dbHelper.deleteStudent(studentitems.get(position).getSid());
        studentitems.remove(position);
        adapter.notifyItemRemoved(position);
    }
}