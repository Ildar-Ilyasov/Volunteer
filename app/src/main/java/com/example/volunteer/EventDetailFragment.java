package com.example.volunteer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventDetailFragment extends Fragment {

    private TextView tvTitle, tvDate, tvDescription, tvLocation, tvOrganizer;
    private DatabaseReference eventRef;
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

        // Получаем eventId и userId из аргументов
        eventId = getArguments() != null ? getArguments().getString("eventId") : null;
        userId = getArguments() != null ? getArguments().getString("userId") : null;

        if (eventId != null) {
            // Инициализация Firebase
            eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);

            // Загрузка данных о мероприятии
            loadEventDetails();
        }

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
            }
        });
    }
}