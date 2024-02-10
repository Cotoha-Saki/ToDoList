package xyz.cotoha.program.todolist;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    private List<Task> tasks;
    private Context context;

    public TaskAdapter(@NonNull Context context, @NonNull List<Task> objects) {
        super(context, 0, objects);
        this.context = context;
        this.tasks = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        }

        Task task = getItem(position);
        TextView textViewName = convertView.findViewById(R.id.textView_task_name);
        CheckBox checkBoxCompleted = convertView.findViewById(R.id.checkBox_task_completed);

        textViewName.setText(task.taskName);
        checkBoxCompleted.setChecked(task.isCompleted);

        checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // チェックボックスの状態が変更された時の処理
            task.isCompleted = isChecked;
            // TODO: データベースのタスクを更新する
        });

        return convertView;
    }
}
