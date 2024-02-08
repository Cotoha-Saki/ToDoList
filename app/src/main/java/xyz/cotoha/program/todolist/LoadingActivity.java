package xyz.cotoha.program.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {
    private static final long LOADING_DELAY_MS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        // 指定した時間後にメインアクティビティに遷移する
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, MenuActivity.class);
                startActivity(intent);
                finish(); // ローディング画面を終了する
            }
        }, LOADING_DELAY_MS);
    }

}
