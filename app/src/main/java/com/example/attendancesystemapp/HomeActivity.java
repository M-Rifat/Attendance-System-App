package com.example.attendancesystemapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
     FloatingActionButton fab;
     RecyclerView recyclerView;
     ClassAdapter classAdapter;
     RecyclerView.LayoutManager layoutManager;
     ArrayList<Classitem> classitems = new ArrayList<>();
     Toolbar toolbar;
     DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DBHelper(this);

        fab = findViewById(R.id.fab_main);
        fab.setOnClickListener(v -> showDailog());

        loadData();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(this,classitems);
        recyclerView.setAdapter(classAdapter);
        classAdapter.setOnItemClickListener(position -> gotoItemAcitvity(position));
        setToolbar();
    }

    private void loadData() {
        Cursor cursor = dbHelper.getClassTable();

        classitems.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(DBHelper.C_ID));
            String className = cursor.getString(cursor.getColumnIndex(DBHelper.CLASS_NAME_KEY));
            String subjectName = cursor.getString(cursor.getColumnIndex(DBHelper.SUBJECT_NAME_KEY));

            classitems.add(new Classitem(id,className,subjectName));
        }
    }

    private void setToolbar() {

        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        title.setText("Attendance App");
        subtitle.setVisibility(View.GONE);
        back.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
    }

    private void gotoItemAcitvity(int position) {
        Intent intent = new Intent(this,StudentActivity.class);

        intent.putExtra("className",classitems.get(position).getClassName());
        intent.putExtra("subjectName",classitems.get(position).getSubjectName());
        intent.putExtra("position",position);
        intent.putExtra("cid",classitems.get(position).getCid());
        startActivity(intent);
    }

   private void showDailog() {
      MyDialog dialog = new MyDialog();
      dialog.show(getSupportFragmentManager(),MyDialog.CLASS_ADD_DIALOG);
      dialog.setListener((className,subjectName)->addClass(className,subjectName));
    }

    private void addClass(String className,String subjectName) {
        long cid= dbHelper.addClass(className,subjectName);
        Classitem classitem = new Classitem(cid, className, subjectName);
        classitems.add(classitem);
        classAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0:
                showUpdateDialog(item.getGroupId());
                break;
            case 1:
                deleteClass(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(int position) {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.CLASS_UPDATE_DIALOG);
        dialog.setListener((className,subjectName)->updateClass(position,className,subjectName));
    }

    private void updateClass(int position, String className, String subjectName) {
        dbHelper.updateClass(classitems.get(position).getCid(),className,subjectName);
        classitems.get(position).setClassName(className);
        classitems.get(position).setSubjectName(subjectName);
        classAdapter.notifyItemChanged(position);
    }

    private void deleteClass(int position) {
        dbHelper.deleteClass(classitems.get(position).getCid());
        classitems.remove(position);
        classAdapter.notifyItemRemoved(position);
    }
}