package xyz.cotoha.program.todolist;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class EditTaskDialogFragment extends DialogFragment {
    // 例: 編集するタスクのIDをフラグメントに渡すためのキー
    private static final String ARG_TASK_ID = "task_id";

    public static EditTaskDialogFragment newInstance(int taskId) {
        EditTaskDialogFragment fragment = new EditTaskDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TASK_ID, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("タスクを編集");

        // タスクの詳細を編集するためのEditTextを設定
        final EditText editText = new EditText(getActivity());
        builder.setView(editText);

        // ダイアログのボタンを設定
        builder.setPositiveButton("保存", (dialog, id) -> {
            // ここでユーザーが入力したテキストを取得し、データベースに保存する処理を行う
            String taskDetail = editText.getText().toString();
            // TODO: データベースに保存する処理を追加
        });
        builder.setNegativeButton("キャンセル", (dialog, id) -> dialog.cancel());

        // AlertDialogを作成して返す
        return builder.create();
    }
}

