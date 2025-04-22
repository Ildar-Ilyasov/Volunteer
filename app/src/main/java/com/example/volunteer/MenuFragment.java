package com.example.volunteer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.appcompat.widget.SearchView;

public class MenuFragment extends Fragment {

    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private List<Event> filteredEventList;
    private DatabaseReference eventsRef;
    private String userId;

    public MenuFragment(String userId) {
        this.userId = userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Инициализация RecyclerView
        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        // Инициализация списка мероприятий и адаптера
        eventList = new ArrayList<>();
        filteredEventList = new ArrayList<>();
        eventAdapter = new EventAdapter(filteredEventList, getContext(), userId, false);  // Пользователь НЕ админ
        recyclerViewEvents.setAdapter(eventAdapter);

        // Инициализация SearchView
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterEvents(newText);
                return true;
            }
        });

        // Загрузка мероприятий из Firebase
        eventsRef = FirebaseDatabase.getInstance().getReference("events");
        loadEvents();

        return view;
    }

    private void loadEvents() {
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        event.setId(eventSnapshot.getKey());
                        eventList.add(event);
                    }
                }
                filteredEventList.clear();
                filteredEventList.addAll(eventList);  // По умолчанию показываем все мероприятия
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибки
            }
        });
    }

    private void filterEvents(String query) {
        filteredEventList.clear();
        for (Event event : eventList) {
            if (event.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredEventList.add(event);
            }
        }
        eventAdapter.notifyDataSetChanged();
    }
}
