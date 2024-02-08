package xyz.cotoha.program.todolist;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MenuActivity extends Activity {

    private static final int PERMISSION_REQUEST_CODE = 1; // 任意のリクエストコード
    private static final String[] PERMISSIONS = {
            android.Manifest.permission.POST_NOTIFICATIONS // Android 13 (APIレベル 33)以降で必要
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu); // XMLレイアウトを設定

        // タスク画面への遷移ボタン
        Button btnTasks = findViewById(R.id.btnTasks);
        btnTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivityへのIntentを作成し、アクティビティを開始する
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 設定画面への遷移ボタン
        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SettingsActivityへのIntentを作成し、アクティビティを開始する
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // 権限が既に許可されているか確認する
        if (ContextCompat.checkSelfPermission(this, PERMISSIONS[0])
                != PackageManager.PERMISSION_GRANTED) {
            // 権限が許可されていない場合は、ユーザーに権限を要求する
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ユーザーが権限を許可した場合の処理をここに書く
            } else {
                // ユーザーが権限を拒否した場合はアラートダイアログを表示する
                showAlertForPermissionDenied();
            }
        }
    }

    private void showAlertForPermissionDenied() {
        new AlertDialog.Builder(this)
                .setTitle("通知権限が許可されていません\n設定で通知をオンにしてください")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}

