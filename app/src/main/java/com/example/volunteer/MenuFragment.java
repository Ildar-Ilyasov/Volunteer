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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
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

        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        eventList = new ArrayList<>();
        filteredEventList = new ArrayList<>();
        eventAdapter = new EventAdapter(filteredEventList, getContext(), userId, false);  // Пользователь НЕ админ
        recyclerViewEvents.setAdapter(eventAdapter);

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

                Collections.sort(eventList, new Comparator<Event>() {
                    @Override
                    public int compare(Event e1, Event e2) {
                        Date date1 = getStartDateFromEvent(e1);
                        Date date2 = getStartDateFromEvent(e2);
                        if (date1 == null || date2 == null) {
                            return 0;  // Если не получилось спарсить дату
                        }
                        return date1.compareTo(date2);
                    }
                });

                filteredEventList.clear();
                filteredEventList.addAll(eventList);
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
    private Date getStartDateFromEvent(Event event) {
        if (event.getDate() == null || event.getDate().isEmpty()) {
            return null;
        }
        try {
            String[] dates = event.getDate().split(" - ");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.parse(dates[0]);  // Берем первую дату из диапазона
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
