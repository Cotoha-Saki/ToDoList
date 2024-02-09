package xyz.cotoha.program.todolist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private PopupWindow popupWindow; // ポップアップウィンドウのインスタンスを保持するための変数

    private TabLayout tabLayout;
    private TabsAdapter tabsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MenuActivity に遷移する
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                finish(); // 現在のアクティビティ（設定画面）を終了する
            }
        });

        Button newButton = findViewById(R.id.btnAdd);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MemoEditActivity に遷移する
                Intent intent = new Intent(MainActivity.this, MemoEditActivity.class);
                startActivity(intent);
            }
        });

        // ポップアップウィンドウを表示するボタンの設定（仮にメニューボタンとします）
        Button menuButton = findViewById(R.id.menu_button); // ボタンのIDに合わせて修正してください
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(); // ポップアップウィンドウを表示するメソッドを呼び出す
            }
        });

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        Button addTabButton = findViewById(R.id.addTabButton);

        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(tabsAdapter);

        // デフォルトタブを追加
        Fragment newTabFragment = new NewTabFragment(); // DefaultFragmentはデフォルトタブの実装クラス
        String newTabTitle = "新規タブ"; // 実際にはユーザー入力などから取得する

// TabsAdapterのaddFragmentメソッドを呼び出し、新規タブを追加
        tabsAdapter.addFragment(new NewTabFragment(), newTabTitle);

        tabLayout.addTab(tabLayout.newTab().setText("デフォルトタブ"));

        // 新規タブ追加ボタンのリスナー設定
        addTabButton.setOnClickListener(v -> {
            // 新規タブ追加処理
            // ここで新規タブ追加のダイアログを表示するコードを追加します
        });

        // TabLayoutとViewPagerの連携
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // ここでタブのタイトルを設定
            if (position == 0) {
                tab.setText("デフォルトタブ");
            } else {
                // ユーザーによって追加されたタブのタイトル設定
                // 実際にはデータベースまたは他の永続化されたストレージからタイトルを読み込む
            }
        }).attach();

        // 新規タブ追加ボタンのリスナー設定
        addTabButton.setOnClickListener(v -> {
            // 新規タブの名前を入力するダイアログを表示
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("新規タブの名前");

            // EditTextを生成してダイアログに設置
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // 「作成」ボタンの設定
            builder.setPositiveButton("作成", (dialog, which) -> {
                String tabName = input.getText().toString();
                if (tabName.isEmpty()) {
                    tabName = "新規タブ"; // 名前が空の場合はデフォルトの名前を使用
                }
                // 新しいタブとフラグメントを追加
                Fragment newFragment = new NewTabFragment(); // 新規タブのフラグメント
                tabsAdapter.addFragment(new NewTabFragment(), "タブのタイトル");
                tabLayout.addTab(tabLayout.newTab().setText(tabName), true);
                // 新規タブを選択状態にする
                viewPager.setCurrentItem(tabsAdapter.getItemCount() - 1);
            });

            // 「キャンセル」ボタンの設定
            builder.setNegativeButton("キャンセル", (dialog, which) -> dialog.cancel());

            // ダイアログを表示
            builder.show();
        });

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
                    .setContentText(message) // 通知の内容
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // 優先度を高に設定
                    .setAutoCancel(true); // タップするとキャンセル（削除）される

            // 通知を表示
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            NotificationManagerCompat.from(this).notify(new Random().nextInt(), builder.build());
        } else {
            // 通知の権限がない場合、ユーザーに権限を要請するための設定画面を開く
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
        }
    }

    private void showPopup() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        // 余白を計算
        int marginHorizontal = getResources().getDimensionPixelSize(R.dimen.popup_horizontal_margin);
        int popupWidth = width - 2 * marginHorizontal; // 両側の余白を引く

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_menu_layout, null);
        // Initialize the popup window
        popupWindow = new PopupWindow(popupView, popupWidth, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 700);


        // Set the button click listeners
        Button button1 = popupView.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAnotherActivity();
                popupWindow.dismiss();
            }
        });

        Button button2 = popupView.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button button3 = popupView.findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button button4 = popupView.findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button button5 = popupView.findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button button6 = popupView.findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button button7 = popupView.findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button button8 = popupView.findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button button9 = popupView.findViewById(R.id.button9);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        // Show the popup window at the center of the parent view

    }

    // このメソッドはボタンがクリックされたときに呼び出されます。
    private void openAnotherActivity() {
        // 新しいアクティビティへのIntentを作成します。
        Intent intent = new Intent(MainActivity.this, TrashActivity.class);
        // アクティビティを開始します。
        startActivity(intent);
    }
}