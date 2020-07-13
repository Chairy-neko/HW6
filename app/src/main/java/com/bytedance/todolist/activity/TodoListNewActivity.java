package com.bytedance.todolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.todolist.R;

public class TodoListNewActivity extends AppCompatActivity {
    public static final String KEY = "CONTENT";
    private EditText mEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_new_layout);

        mEditText = findViewById(R.id.newNote_text);

        // set button callback
        final Button button = findViewById(R.id.btn_confirm);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String text = mEditText.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(KEY,text);
                if (text.isEmpty()) {
                    setResult(RESULT_CANCELED, intent);
                }
                else {
                    // get other infomation
                    setResult(RESULT_OK,intent);
                }
                finish();
            }
        });
    }
}

