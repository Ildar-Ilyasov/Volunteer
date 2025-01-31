package com.example.volunteer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvLogin, tvDob, tvRegistrationDate, tvEmail;
    private RecyclerView recyclerViewUserEvents;
    private EventAdapter userEventAdapter;
    private List<Event> userEventList;
    private DatabaseReference userRef, userEventsRef;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Инициализация UI элементов
        tvName = view.findViewById(R.id.tv_name);
        tvLogin = view.findViewById(R.id.tv_login);
        tvDob = view.findViewById(R.id.tv_dob);
        tvRegistrationDate = view.findViewById(R.id.tv_registration_date);
        tvEmail = view.findViewById(R.id.tv_email);
        recyclerViewUserEvents = view.findViewById(R.id.recyclerViewUserEvents);

        // Устанавливаем значения по умолчанию
        tvName.setText("Загружается...");
        tvLogin.setText("Загружается...");
        tvDob.setText("Загружается...");
        tvRegistrationDate.setText("Загружается...");
        tvEmail.setText("Загружается...");

        // Инициализация RecyclerView
        recyclerViewUserEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        userEventList = new ArrayList<>();

        // Получаем userId из аргументов фрагмента
        userId = getArguments() != null ? getArguments().getString("userId") : null;

        // Передаем userId в адаптер
        userEventAdapter = new EventAdapter(userEventList, getContext(), userId);
        recyclerViewUserEvents.setAdapter(userEventAdapter);

        if (userId != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userEventsRef = FirebaseDatabase.getInstance().getReference("user_events").child(userId);
            loadUserData();
            loadUserEvents();
        }

        return view;
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Отображаем данные пользователя
                        tvName.setText(user.getFirstName() + " " + user.getLastName());
                        tvLogin.setText("Логин: " + user.getLogin());
                        tvDob.setText("Дата рождения: " + user.getDob());
                        tvRegistrationDate.setText("Дата регистрации: " + user.getRegistrationDate());
                        tvEmail.setText("Email: " + user.getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибки
            }
        });
    }

    private void loadUserEvents() {
        userEventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userEventList.clear();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    String eventId = eventSnapshot.getKey();
                    if (eventId != null) {
                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);
                        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Event event = snapshot.getValue(Event.class);
                                if (event != null) {
                                    event.setId(eventId); // Устанавливаем ID мероприятия
                                    userEventList.add(event);
                                    userEventAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Обработка ошибки
                            }
                        });
                    }
                }
                // Показываем RecyclerView после загрузки данных
                recyclerViewUserEvents.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибки
            }
        });
    }
}