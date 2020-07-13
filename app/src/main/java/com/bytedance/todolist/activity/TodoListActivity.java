package com.bytedance.todolist.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import com.bytedance.todolist.database.TodoListDao;
import com.bytedance.todolist.database.TodoListDatabase;
import com.bytedance.todolist.database.TodoListEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import com.bytedance.todolist.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;

public class TodoListActivity extends AppCompatActivity {

    private TodoListAdapter mAdapter;
    private FloatingActionButton mFab;
    private static final int REQUEST_CODE_1 = 1357;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list_activity_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TodoListAdapter();
        recyclerView.setAdapter(mAdapter);

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
                Intent intent = new Intent(TodoListActivity.this, TodoListNewActivity.class);
                startActivityForResult(intent,REQUEST_CODE_1);
            }
        });

        mFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        TodoListDao dao = TodoListDatabase.inst(TodoListActivity.this).todoListDao();
                        dao.deleteAll();
                        for (int i = 0; i < 20; ++i) {
                            dao.addTodo(new TodoListEntity("This is " + i + " item", new Date(System.currentTimeMillis()),0L));
                        }
                        final List<TodoListEntity> entityList = dao.loadAll();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.setData(entityList);
                            }
                        });
                        Snackbar.make(mFab, R.string.hint_insert_complete, Snackbar.LENGTH_SHORT).show();
                    }
                }.start();
                return true;
            }
        });
        loadFromDatabase();
    }

    private void loadFromDatabase() {
        new Thread() {
            @Override
            public void run() {
                TodoListDao dao = TodoListDatabase.inst(TodoListActivity.this).todoListDao();
                final List<TodoListEntity> entityList = dao.loadAll();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setData(entityList);
                    }
                });
            }
        }.start();
    }

    public void deleteNote(final TodoListEntity entity)
    {
        new Thread() {
            @Override
            public void run() {
                TodoListDao dao = TodoListDatabase.inst(TodoListActivity.this).todoListDao();
                dao.deleteTodo(entity);
                final List<TodoListEntity> entityList = dao.loadAll();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setData(entityList);
                    }
                });
            }
        }.start();
    }

    public void updateNote(final TodoListEntity entity)
    {
        new Thread() {
            @Override
            public void run() {
                TodoListDao dao = TodoListDatabase.inst(TodoListActivity.this).todoListDao();
                dao.updateTodo(entity);
                final List<TodoListEntity> entityList = dao.loadAll();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setData(entityList);
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_CODE_1) {
            if(resultCode == RESULT_OK && data != null) {
                final String result = data.getStringExtra(TodoListNewActivity.KEY);
                final Date date = new Date(System.currentTimeMillis());
                Toast.makeText(this, result + " - " + date, Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TodoListDao dao = TodoListDatabase.inst(TodoListActivity.this).todoListDao();
                        dao.addTodo(new TodoListEntity(result,date,0L));
                        final List<TodoListEntity> entityList = dao.loadAll();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.setData(entityList);
                            }
                        });
                    }
                }).start();
            }
            else {
                Toast.makeText(this, "没有输入", Toast.LENGTH_LONG).show();
            }
        }
    }

}
