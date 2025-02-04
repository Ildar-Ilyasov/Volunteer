package com.example.volunteer;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.Button;

public class EventDetailFragment extends Fragment {

    private TextView tvTitle, tvDate, tvDescription, tvLocation, tvOrganizer;
    private Button btnParticipate;
    private DatabaseReference eventRef, userEventsRef;
    private String eventId, userId;

    public static EventDetailFragment newInstance(String eventId, String userId) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        // Инициализация UI элементов
        tvTitle = view.findViewById(R.id.tvTitle);
        tvDate = view.findViewById(R.id.tvDate);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvOrganizer = view.findViewById(R.id.tvOrganizer);
        btnParticipate = view.findViewById(R.id.btnParticipate);
        Button btnOpenChat = view.findViewById(R.id.btnOpenChat);

        // Получаем eventId и userId из аргументов
        eventId = getArguments() != null ? getArguments().getString("eventId") : null;
        userId = getArguments() != null ? getArguments().getString("userId") : null;

        if (eventId != null && userId != null) {
            // Инициализация Firebase
            eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);
            userEventsRef = FirebaseDatabase.getInstance().getReference("user_events");

            // Загрузка данных о мероприятии
            loadEventDetails();

            // Проверка, участвует ли пользователь в мероприятии
            checkIfUserParticipates();
        } else {
            Toast.makeText(getContext(), "Не удалось найти данные о пользователе или мероприятии.", Toast.LENGTH_SHORT).show();
        }

        // Обработчик нажатия на кнопку "Участвовать"
        btnParticipate.setOnClickListener(v -> joinEvent());
        btnOpenChat.setOnClickListener(v -> openChatFragment());

        return view;
    }

    private void loadEventDetails() {
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                        // Отображаем все данные о мероприятии
                        tvTitle.setText(event.getTitle());
                        tvDate.setText("Дата: " + event.getDate());
                        tvDescription.setText("Описание: " + event.getDescription());
                        tvLocation.setText("Место проведения: " + event.getLocation());
                        tvOrganizer.setText("Организатор: " + event.getOrganizer());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибки
                Log.e("EventDetailFragment", "Ошибка загрузки данных о мероприятии: " + error.getMessage());
            }
        });
    }

    private void checkIfUserParticipates() {
        if (userId != null && eventId != null) {
            userEventsRef.child(userId).child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Если пользователь участвует
                        btnParticipate.setText("Вы участвуете");
                        btnParticipate.setEnabled(false);
                        btnParticipate.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY)); // Серый цвет
                    } else {
                        // Если не участвует
                        btnParticipate.setText("Принять участие");
                        btnParticipate.setEnabled(true);
                        btnParticipate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.goal))); // Оранжевый цвет
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("EventDetailFragment", "Ошибка при проверке участия: " + error.getMessage());
                }
            });
        }
    }

    private void joinEvent() {
        if (userId != null && eventId != null) {
            userEventsRef.child(userId).child(eventId).setValue(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Вы успешно зарегистрированы!", Toast.LENGTH_SHORT).show();
                            btnParticipate.setText("Вы участвуете");
                            btnParticipate.setEnabled(false);
                            btnParticipate.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY)); // Серый цвет
                        } else {
                            Toast.makeText(getContext(), "Ошибка при регистрации.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void openChatFragment() {
        ChatFragment chatFragment = new ChatFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.contentFrame, chatFragment)
                .addToBackStack(null)
                .commit();
    }

}