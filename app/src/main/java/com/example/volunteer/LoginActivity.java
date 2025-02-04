package com.example.volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private EditText etLogin, etPassword;
    private Button btnLogin, btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        dbHelper = new DatabaseHelper();

        btnLogin.setOnClickListener(v -> {
            String login = etLogin.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                Toast.makeText(LoginActivity.this, "Ошибка при хешировании пароля", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.checkUser(login, hashedPassword, new DatabaseHelper.OnLoginCheckListener() {
                @Override
                public void onLoginSuccess(String userId) {
                    //Toast.makeText(LoginActivity.this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
                    Log.d("LoginActivity", "Пользователь успешно вошел: " + login);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onLoginFailure(String errorMessage) {
                    Log.e("LoginActivity", "Ошибка входа: " + errorMessage);

                    if (errorMessage.equals("Неверный логин или пароль")) {
                        Toast.makeText(LoginActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                    } else if (errorMessage.contains("нет подключения") || errorMessage.contains("DatabaseError")) {
                        Toast.makeText(LoginActivity.this, "Ошибка соединения с базой данных. Попробуйте позже.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Ошибка: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    // Метод для хеширования пароля (SHA-512)
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
