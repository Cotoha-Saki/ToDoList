package xyz.cotoha.program.todolist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.graphics.Color;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.google.android.material.tabs.TabLayout;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private PopupWindow popupWindow; // ポップアップウィンドウのインスタンスを保持するための変数
    private TabLayout tabLayout;
    private DatabaseHelper databaseHelper;
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

        // DatabaseHelperのインスタンス化
        databaseHelper = new DatabaseHelper(this);

        // TabLayoutのインスタンスを取得
        tabLayout = findViewById(R.id.tabLayout);

        // データベースからタブ情報を読み込む
        loadTabsFromDatabase();

        // DatabaseHelperのインスタンス化
        databaseHelper = new DatabaseHelper(this);
        //ここからタブ機能
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setBackgroundColor(Color.parseColor("#98DEFF"));
        // 初期タブの設定
        tabLayout.addTab(tabLayout.newTab().setText("全てのタスク"));
        // 新規タブ追加ボタンのリスナーを設定（後で実装）

        // タブ選択のリスナー
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // タブが選択された時の処理
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // タブが選択解除された時の処理
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // タブが再選択された時の処理
            }
        });

        findViewById(R.id.addTabButton).setOnClickListener(view -> {
            // EditTextを持つダイアログを表示して新規タブの名前を入力
            final EditText input = new EditText(MainActivity.this);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("新規タブの追加")
                    .setMessage("タブの名前を入力してください")
                    .setView(input)
                    .setPositiveButton("作成", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String tabName = input.getText().toString();
                            if (tabName.isEmpty()) {
                                tabName = "新規タブ";
                            }
                            tabLayout.addTab(tabLayout.newTab().setText(tabName));
                            databaseHelper.addTab(tabName); // データベースにタブを追加
                            // ここでデータベースにタブを追加するロジックを実装

                            // 新規タブにリスナーを設定
                            setupTabListener();
                        }
                    })
                    .setNegativeButton("キャンセル", null)
                    .show();

        });
        setupTabListener();
    }

    private void loadTabsFromDatabase() {
        List<String> tabNames = databaseHelper.getAllTabs();
        for (String name : tabNames) {
            tabLayout.addTab(tabLayout.newTab().setText(name));
        }

        // タブリスナーを設定
        setupTabListener();
    }

    private void setupTabListener() {
        // すべてのタブにダブルタップリスナーを設定する処理
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null && !tab.getText().toString().equals("全てのタスク")) {
                View tabView = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
                int finalI = i;
                tabView.setOnTouchListener(new View.OnTouchListener() {
                    private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            showTabPopupMenu(finalI, tabView);
                            return super.onDoubleTap(e);
                        }
                    });

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetector.onTouchEvent(event);
                        return false;
                    }
                });
            }
        }
    }


    private void showTabPopupMenu(int tabIndex, View anchor) {
        PopupMenu popup = new PopupMenu(MainActivity.this, anchor);
        popup.getMenuInflater().inflate(R.menu.tab_options_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
                if (item.getItemId() == R.id.rename_tab) {
                    // タブの名前を変更する処理
                    if (tab != null) {
                        showRenameDialog(tab);
                    }
                    return true;
                } else if (item.getItemId() == R.id.delete_tab) {
                    // タブを削除する処理
                    showDeleteTabConfirmationDialog(tab);
                    if (tab != null) {

                        // ここでデータベースからタブを削除するロジックを実装
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        popup.show();
    }
    private int getTabId(TabLayout.Tab tab) {
        Object tag = tab.getTag();
        if (tag instanceof Integer) {
            return (int) tag;
        } else {
            throw new IllegalStateException("Tab does not have a proper ID tag.");
        }
    }

    private void showRenameDialog(TabLayout.Tab tab) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("タブの名前を変更");

        // EditTextを作成してダイアログにセット
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // 「変更」ボタンの設定
        builder.setPositiveButton("変更", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                if (!newName.isEmpty()) {
                    tab.setText(newName);
                    int tabId = getTabId(tab); // 修正されたメソッドの呼び出し
                    databaseHelper.updateTabName(tabId, newName); // データベースのタブ名を更新
                }
            }
        });

        // 「キャンセル」ボタンの設定
        builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showDeleteTabConfirmationDialog(TabLayout.Tab tab) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("タブの削除");
        builder.setMessage("このタブを削除してもよろしいですか？");

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (tab != null) {
                int tabId = getTabId(tab);
                // UIスレッドで実行するための確認
                runOnUiThread(() -> {
                    tabLayout.removeTab(tab);
                    databaseHelper.deleteTab(tabId); // データベースからタブを削除
                });
            }
        });

        builder.setNegativeButton("キャンセル", (dialog, which) -> dialog.dismiss());

        builder.show();
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

        Button button3 = popupView.findViewById(R.id.button4);
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