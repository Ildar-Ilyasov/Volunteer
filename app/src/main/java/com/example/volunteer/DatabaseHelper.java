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

    public void registerUser(String firstName, String lastName, String dob, String email, String login, String password) {

        String registrationDate = getCurrentDate();

        User user = new User(firstName, lastName, dob, email, login, password, registrationDate);

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



    public void checkUser(String login, String password, final OnLoginCheckListener listener) {
        mDatabase.child("users").child(login).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                User user = task.getResult().getValue(User.class);
                if (user != null && user.getPassword().equals(password)) {
                    listener.onLoginSuccess(login);
                } else {
                    listener.onLoginFailure("Неверный логин или пароль");
                }
            } else {
                listener.onLoginFailure("Ошибка при проверке пользователя");
            }
        });
    }

    public interface OnLoginCheckListener {
        void onLoginSuccess(String userId);
        void onLoginFailure(String errorMessage);
    }
}
