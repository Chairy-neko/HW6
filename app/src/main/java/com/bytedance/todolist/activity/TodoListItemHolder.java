package com.bytedance.todolist.activity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.todolist.R;
import com.bytedance.todolist.database.TodoListDao;
import com.bytedance.todolist.database.TodoListDatabase;
import com.bytedance.todolist.database.TodoListEntity;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wangrui.sh
 * @since Jul 11, 2020
 */
public class TodoListItemHolder extends RecyclerView.ViewHolder{
    private TextView mContent;
    private TextView mTimestamp;
    private CheckBox mCheckBox;
    private ImageButton mDeleteButton;


    public TodoListItemHolder(@NonNull View itemView) {
        super(itemView);
        mContent = itemView.findViewById(R.id.tv_content);
        mTimestamp = itemView.findViewById(R.id.tv_timestamp);
        mCheckBox = itemView.findViewById(R.id.item_checkbox);
        mDeleteButton = itemView.findViewById(R.id.item_delete_btn);
    }

    public void bind(final TodoListEntity entity) {
        mContent.setText(entity.getContent());
        mTimestamp.setText(formatDate(entity.getTime()));

        // set checked
        mCheckBox.setOnCheckedChangeListener(null);
        mCheckBox.setChecked(entity.getDone().equals(1L));
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                Long l = 0L;
                if(isChecked) { l = 1L; }
                entity.setDone(l);
//                mCheckBox.setChecked(isChecked);
                Activity activity = getActivity(buttonView);
                updateNoteInMain((TodoListActivity)activity, entity);
            }
        });

        // set text style
        int textColor = 0;
        int flags = 0;
        if(mCheckBox.isChecked())
        {
            Log.i("CHECK: ","true");
            textColor = Color.GRAY;
            flags = mContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG;
        }
        else
        {
            flags = mContent.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG;
            textColor = Color.BLACK;
        }
        mContent.setTextColor(textColor);
        mContent.setPaintFlags(flags);

        //set delete button
        mDeleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Activity activity = getActivity(v);
                deleteNoteInMain((TodoListActivity)activity, entity);
            }
        });
    }

    private String formatDate(Date date) {
        DateFormat format = SimpleDateFormat.getDateInstance();
        return format.format(date);
    }

    private Activity getActivity(View view) {
        Activity ret = null;

        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                ret = (Activity)context;
                break;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return ret;
    }

    private void deleteNoteInMain(TodoListActivity activity, TodoListEntity entity)
    {
        activity.deleteNote(entity);
    }

    private void updateNoteInMain(TodoListActivity activity, TodoListEntity note)
    {
        activity.updateNote(note);
    }
}
