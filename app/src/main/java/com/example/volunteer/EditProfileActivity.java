package com.example.volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etDob, etEmail;
    private Button btnSave;
    private DatabaseReference userRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etDob = findViewById(R.id.etDob);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);

        ImageButton iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> finish());

        userId = getIntent().getStringExtra("userId");

        if (userId == null) {
            Toast.makeText(this, "Ошибка: не удалось загрузить профиль", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        loadUserData();

        btnSave.setOnClickListener(v -> saveProfileData());
        etDob.setOnClickListener(v -> showDatePicker());
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        etFirstName.setText(user.getFirstName());
                        etLastName.setText(user.getLastName());
                        etDob.setText(user.getDob());
                        etEmail.setText(user.getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileData() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(dob) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }

        // Обновляем данные в Firebase
        userRef.child("firstName").setValue(firstName);
        userRef.child("lastName").setValue(lastName);
        userRef.child("dob").setValue(dob);
        userRef.child("email").setValue(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditProfileActivity.this, "Данные сохранены", Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("firstName", firstName);
                resultIntent.putExtra("lastName", lastName);
                resultIntent.putExtra("dob", dob);
                resultIntent.putExtra("email", email);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(EditProfileActivity.this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        String currentDate = etDob.getText().toString().trim();
        if (!currentDate.isEmpty()) {
            String[] dateParts = currentDate.split("/");
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1;
            int year = Integer.parseInt(dateParts[2]);
            calendar.set(year, month, day);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {

                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    etDob.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}
