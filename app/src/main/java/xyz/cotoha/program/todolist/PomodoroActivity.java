package xyz.cotoha.program.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PomodoroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        Button backButton = findViewById(R.id.btnBackPomodoro);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MenuActivity に遷移する
                Intent intent = new Intent(PomodoroActivity.this, MenuActivity.class);
                startActivity(intent);
                finish(); // 現在のアクティビティ（設定画面）を終了する
            }
        });
    }
}
