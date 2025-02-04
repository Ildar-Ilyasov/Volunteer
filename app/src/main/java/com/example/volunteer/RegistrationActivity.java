package com.example.volunteer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etDob, etEmail, etLogin, etPassword;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etDob = findViewById(R.id.etDob);
        etEmail = findViewById(R.id.etEmail);
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        dbHelper = new DatabaseHelper();

        etDob.setOnClickListener(v -> showDatePickerDialog());

        btnRegister.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String dobString = etDob.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String login = etLogin.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (firstName.isEmpty()) {
                etFirstName.setError("Это поле не может быть пустым");
                return;
            }
            if (lastName.isEmpty()) {
                etLastName.setError("Это поле не может быть пустым");
                return;
            }
            if (dobString.isEmpty()) {
                etDob.setError("Выберите дату");
                return;
            }
            if (email.isEmpty()) {
                etEmail.setError("Это поле не может быть пустым");
                return;
            }
            if (login.isEmpty()) {
                etLogin.setError("Это поле не может быть пустым");
                return;
            }
            if (password.isEmpty()) {
                etPassword.setError("Это поле не может быть пустым");
                return;
            }

            Date dob;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                dob = sdf.parse(dobString);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(RegistrationActivity.this, "Неверный формат даты", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dobFormatted = sdf.format(dob);

            String hashedPassword = hashPassword(password);

            dbHelper.registerUser(firstName, lastName, dobFormatted, email, login, hashedPassword);
            //Toast.makeText(RegistrationActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    etDob.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

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