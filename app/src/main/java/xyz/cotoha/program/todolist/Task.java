package xyz.cotoha.program.todolist;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "task_name")
    public String taskName;

    // コンストラクタ、ゲッター、セッターなどは必要に応じて追加
}



