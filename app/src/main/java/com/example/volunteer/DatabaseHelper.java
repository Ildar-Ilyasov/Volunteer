package com.example.volunteer;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper {

    private final DatabaseReference mDatabase;

    public DatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Регистрация пользователя
    public void registerUser(String firstName, String lastName, String dob, String email, String login, String password) {
        // Получаем текущую дату регистрации
        String registrationDate = getCurrentDate();

        // Создаем объект пользователя с датой регистрации
        User user = new User(firstName, lastName, dob, email, login, password, registrationDate);

        // Добавляем нового пользователя в узел "users"
        mDatabase.child("users").child(login).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("DatabaseHelper", "Пользователь успешно зарегистрирован");
                    Log.d("DatabaseHelper", "Данные пользователя: " + user);
                })
                .addOnFailureListener(e -> {
                    Log.e("DatabaseHelper", "Ошибка при регистрации пользователя: " + e.getMessage());
                    e.printStackTrace();
                });
    }



    // Проверка логина и пароля
    public void checkUser(String login, String password, final OnLoginCheckListener listener) {
        mDatabase.child("users").child(login).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                User user = task.getResult().getValue(User.class);
                if (user != null && user.getPassword().equals(password)) {  // Проверка простого пароля
                    listener.onLoginSuccess(login);  // Передаем login как userId
                } else {
                    listener.onLoginFailure("Неверный логин или пароль");
                }
            } else {
                listener.onLoginFailure("Ошибка при проверке пользователя");
            }
        });
    }

    // Интерфейс для обработки результатов проверки логина
    public interface OnLoginCheckListener {
        void onLoginSuccess(String userId);  // Добавляем параметр userId
        void onLoginFailure(String errorMessage);
    }
}
