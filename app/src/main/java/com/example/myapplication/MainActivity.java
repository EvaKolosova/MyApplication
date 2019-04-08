package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    ImageButton SettingsButton;
    ImageButton RefreshButton;
    TextView tvOut;
    AlertDialog.Builder ad;
    Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // найдем View-элементы
        tvOut = (TextView) findViewById(R.id.textView);
        SettingsButton = (ImageButton) findViewById(R.id.imageButton2);
        RefreshButton = (ImageButton) findViewById(R.id.imageButton);

        context = MainActivity.this;
        String title = "Settings";
        String message = "Please, choose:";
        String button1String = "Celsius degrees(C)";
        String button2String = "Fahrenheit degrees(F)";
        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //передача параметров - C

                Toast.makeText(context, "You have chosen Celsius degrees(C)",
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //передача параметров - F

                Toast.makeText(context, "You have chosen Fahrenheit degrees(F)",
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "You haven't chosen anything...",
                        Toast.LENGTH_LONG).show();
            }
        });

        // создаем обработчик нажатия
        View.OnClickListener oclBtnSettings = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Сделать изменение градусов на фаренгейты
                ad.show();

//                tvOut.setVisibility(View.VISIBLE);
//                tvOut.setText("Нажата кнопка :)");
            }
        };


        View.OnClickListener oclBtnRefresh = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Сделать обновление данных и их выгрузку
                tvOut.setText("Нажата кнопка Refresh");
            }
        };

        // присвоим обработчик кнопке
        SettingsButton.setOnClickListener(oclBtnSettings);
        RefreshButton.setOnClickListener(oclBtnRefresh);
    }
}