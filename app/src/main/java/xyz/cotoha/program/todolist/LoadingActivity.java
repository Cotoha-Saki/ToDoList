package xyz.cotoha.program.todolist;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        // 指定した時間（ここでは3秒）スリープさせる
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 3秒後にメインアクティビティに遷移する
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // ローディング画面を終了する
    }
}
