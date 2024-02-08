package xyz.cotoha.program.todolist;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class SettingsActivity extends Activity {

    private static final String PREFS_NAME = "SettingsPref";
    private static final String NOTIFICATIONS_KEY = "notifications";
    private static final String DEVELOPER_MODE_KEY = "developer_mode";
    private static final String BETA_MODE_KEY = "beta_mode";
    private Button resetNotificationsButton;
    private Switch developerModeSwitch;
    private Switch betaModeSwitch;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initialize SharedPreferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Setup UI elements
        developerModeSwitch = (Switch) findViewById(R.id.switch_developer_mode);
        betaModeSwitch = (Switch) findViewById(R.id.switch_beta_mode);
        resetNotificationsButton = (Button) findViewById(R.id.button_reset_notifications);

        // Load saved settings and apply them to the switches
        developerModeSwitch.setChecked(settings.getBoolean(DEVELOPER_MODE_KEY, false));
        betaModeSwitch.setChecked(settings.getBoolean(BETA_MODE_KEY, false));
        // Note: Beta mode switch visibility is dependent on developer mode status
        betaModeSwitch.setVisibility(developerModeSwitch.isChecked() ? View.VISIBLE : View.GONE);
        developerModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // パスワードダイアログを表示
                    showDeveloperModePasswordDialog();
                } else {
                    // 開発者モードを無効にする
                    setDeveloperMode(false);
                }
            }
        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MenuActivity に遷移する
                Intent intent = new Intent(SettingsActivity.this, MenuActivity.class);
                startActivity(intent);
                finish(); // 現在のアクティビティ（設定画面）を終了する
            }
        });

        // 通知設定リセットボタンのリスナー
        resetNotificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通知設定画面を開くためのインテントを作成
                Intent intent = new Intent();
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

                // Android 5.0以上の場合の設定
                intent.putExtra("app_package", getPackageName());
                intent.putExtra("app_uid", getApplicationInfo().uid);

                // Android 4.1 - 4.4の場合の設定
                intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

                startActivity(intent);
            }
        });
    }

    private void showDeveloperModePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("有効にするにはパスワードを入力してください");

        // パスワード入力フィールドを持つLinearLayoutをセットアップ
        final EditText passwordInput = new EditText(this);
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordInput);

        // ダイアログのボタンをセットアップ
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("CthDev".equals(passwordInput.getText().toString())) {
                    // パスワードが正しい場合、開発者モードを有効にする
                    setDeveloperMode(true);
                } else {
                    // パスワードが間違っている場合、ポップアップ通知を表示
                    showPopupNotification("パスワードが違います。やり直してください");
                    // 開発者モードスイッチをオフに戻す
                    developerModeSwitch.setChecked(false);
                }

            }
        });
        builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                // 開発者モードスイッチをオフに戻す
                developerModeSwitch.setChecked(false);
            }
        });

        builder.show();
    }
    private void setDeveloperMode(boolean isEnabled) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(DEVELOPER_MODE_KEY, isEnabled);
        editor.apply();

        // 開発者モードスイッチのUIを更新
        developerModeSwitch.setChecked(isEnabled);

        // ベータ版スイッチの表示を切り替え
        betaModeSwitch.setVisibility(isEnabled ? View.VISIBLE : View.GONE);

        // ユーザーに開発者モードの状態を通知
        showPopupNotification(isEnabled ? "開発者モードがオンになりました" : "開発者モードがオフになりました");
    }



    private void showPopupNotification(String message) {
        String channelId = "default_channel_id";
        String channelDescription = "Default Channel";

        // 通知チャネルをシステムに登録（Android 8.0以上必要）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelDescription, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // 通知の権限があるかをチェック
        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            // 通知を構築
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_notification) // アイコンを設定
                    .setContentTitle("ToDoList") // 通知のタイトル
                    .setContentText(message) // 通知の内容
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // 優先度を高に設定
                    .setAutoCancel(true); // タップするとキャンセル（削除）される

            // 通知を表示
            NotificationManagerCompat.from(this).notify(new Random().nextInt(), builder.build());
        } else {
            // 通知の権限がない場合、ユーザーに権限を要請するための設定画面を開く
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
        }
    }


}

