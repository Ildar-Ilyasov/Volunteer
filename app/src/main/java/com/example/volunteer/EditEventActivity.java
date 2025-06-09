package com.example.volunteer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.widget.ImageButton;

public class EditEventActivity extends AppCompatActivity {

    private EditText etEventTitle, etEventDescription, etEventDate, etEventLocation, etEventOrganizer;
    private Button btnSaveEvent;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        eventId = getIntent().getStringExtra("eventId");
        String eventTitle = getIntent().getStringExtra("eventTitle");
        String eventDescription = getIntent().getStringExtra("eventDescription");
        String eventDate = getIntent().getStringExtra("eventDate");
        String eventLocation = getIntent().getStringExtra("eventLocation");
        String eventOrganizer = getIntent().getStringExtra("eventOrganizer");

        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventDate = findViewById(R.id.etEventDate);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventOrganizer = findViewById(R.id.etEventOrganizer);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);

        etEventTitle.setText(eventTitle);
        etEventDescription.setText(eventDescription);
        etEventDate.setText(eventDate);
        etEventLocation.setText(eventLocation);
        etEventOrganizer.setText(eventOrganizer);

        btnSaveEvent.setOnClickListener(v -> saveEventChanges());
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());
    }


    private void saveEventChanges() {
        String newTitle = etEventTitle.getText().toString();
        String newDescription = etEventDescription.getText().toString();
        String newDate = etEventDate.getText().toString();
        String newLocation = etEventLocation.getText().toString();
        String newOrganizer = etEventOrganizer.getText().toString();

        if (newTitle.isEmpty() || newDescription.isEmpty() || newDate.isEmpty() || newLocation.isEmpty() || newOrganizer.isEmpty()) {
            Toast.makeText(EditEventActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);
        eventRef.child("title").setValue(newTitle);
        eventRef.child("description").setValue(newDescription);
        eventRef.child("date").setValue(newDate);
        eventRef.child("location").setValue(newLocation);
        eventRef.child("organizer").setValue(newOrganizer)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditEventActivity.this, "Изменения сохранены", Toast.LENGTH_SHORT).show();
                    finish(); // Закрыть активность после успешного сохранения
                })
                .addOnFailureListener(e -> Toast.makeText(EditEventActivity.this, "Ошибка сохранения", Toast.LENGTH_SHORT).show());
    }
}
