package com.example.volunteer;

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
                        // Если пользователь уже участвует, обновляем кнопку
                        btnParticipate.setText("Вы участвуете");
                        btnParticipate.setEnabled(false);
                    } else {
                        // Если пользователь не участвует, оставляем кнопку активной
                        btnParticipate.setText("Подать заявку");
                        btnParticipate.setEnabled(true);
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
            // Создаем ссылку на путь, где сохраняются события пользователя
            userEventsRef.child(userId).child(eventId).setValue(true)  // Мы используем true как значение, чтобы просто отметить участие
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Уведомление о том, что участие успешно добавлено
                            Toast.makeText(getContext(), "Вы успешно зарегистрированы на мероприятие!", Toast.LENGTH_SHORT).show();

                            // Меняем текст кнопки на "Вы участвуете"
                            btnParticipate.setText("Вы участвуете");

                            // Делаем кнопку неактивной, чтобы пользователь не мог нажать её снова
                            btnParticipate.setEnabled(false);
                        } else {
                            // Обработка ошибок при добавлении
                            Toast.makeText(getContext(), "Ошибка при регистрации.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Не удалось найти данные о пользователе или мероприятии.", Toast.LENGTH_SHORT).show();
        }
    }
}