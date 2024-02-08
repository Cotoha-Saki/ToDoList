package xyz.cotoha.program.todolist;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private PopupWindow popupWindow; // ポップアップウィンドウのインスタンスを保持するための変数
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

        // ポップアップウィンドウを表示するボタンの設定（仮にメニューボタンとします）
        Button menuButton = findViewById(R.id.menu_button); // ボタンのIDに合わせて修正してください
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(); // ポップアップウィンドウを表示するメソッドを呼び出す
            }
        });
    }

    private void showPopup() {
        Toast.makeText(MainActivity.this, "showPopup called", Toast.LENGTH_SHORT).show();

        // レイアウトインフレーターを取得し、ポップアップのためのビューをインフレートする
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_menu_layout, null); // ポップアップに使用するレイアウトのID

        // ポップアップウィンドウのインスタンスを作成
        popupWindow = new PopupWindow();

        // ポップアップウィンドウがフォーカス可能であるように設定し、外側をタップしたときに閉じるようにする
        popupWindow.setFocusable(true);

        // ポップアップウィンドウを画面中央に表示する
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        Button button1 = popupView.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ハンドルボタン 1 クリック
                handleButton1Click();
                popupWindow.dismiss(); // ポップアップウィンドウを閉じる
            }
        });

        Button button2 = popupView.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButton2Click();
                popupWindow.dismiss(); // Dismiss the popup window
            }
        });

        // Create and show the PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true); // Set the PopupWindow to be focusable
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }
    private void handleButton1Click() {
        // ボタン 1のクリックに応じて何かを行う
        Toast.makeText(MainActivity.this, "Button 1 clicked", Toast.LENGTH_SHORT).show();
    }

    private void handleButton2Click() {
        // ボタン 2のクリックに応じて何かを行う
        Toast.makeText(MainActivity.this, "Button 2 clicked", Toast.LENGTH_SHORT).show();
    }
}