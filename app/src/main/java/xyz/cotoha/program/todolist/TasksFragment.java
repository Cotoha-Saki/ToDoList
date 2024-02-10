package xyz.cotoha.program.todolist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.List;

public class TasksFragment extends Fragment {

    private ListView listView;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        listView = view.findViewById(R.id.list_view_tasks);

        db = AppDatabase.getDatabase(getActivity());

        new LoadTasksAsync(db).execute();

        return view;
    }

    private class LoadTasksAsync extends AsyncTask<Void, Void, List<Task>> {
        private final AppDatabase db;

        LoadTasksAsync(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {
            return db.taskDao().getAllTasks();
        }

        @Override
        protected void onPostExecute(List<Task> tasks) {
            super.onPostExecute(tasks);
            // ListViewにタスクを表示する処理をここに書きます
            ArrayAdapter<Task> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, tasks);
            listView.setAdapter(adapter);
        }
    }

}

