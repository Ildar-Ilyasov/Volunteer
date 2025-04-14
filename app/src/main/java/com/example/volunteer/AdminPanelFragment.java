package com.example.volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminPanelFragment extends Fragment {

    private Button btnManageEvents, btnManageNews;
    private LinearLayout itemListContainer;
    private Button btnAdd;
    private String currentCategory = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_panel, container, false);

        btnManageEvents = view.findViewById(R.id.btnManageEvents);
        btnManageNews = view.findViewById(R.id.btnManageNews);
        itemListContainer = view.findViewById(R.id.itemListContainer);
        btnAdd = view.findViewById(R.id.btnAddItem);

        btnManageEvents.setOnClickListener(v -> showManage("events"));
        btnManageNews.setOnClickListener(v -> showManage("news"));

        btnAdd.setOnClickListener(v -> {
            Fragment fragmentToOpen = null;

            if (currentCategory.equals("events")) {
                fragmentToOpen = new AddEventFragment();
            } else if (currentCategory.equals("news")) {
                fragmentToOpen = new AddNewsFragment();
            }

            if (fragmentToOpen != null) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentFrame, fragmentToOpen)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Категория не выбрана", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showManage(String category) {
        currentCategory = category; // запоминаем выбранную категорию
        btnAdd.setVisibility(View.VISIBLE);
        itemListContainer.removeAllViews();

        if (category.equals("events")) {
            btnAdd.setText("Добавить мероприятие");
            loadEventsFromFirebase();
        } else if (category.equals("news")) {
            btnAdd.setText("Добавить новость");
            loadNewsFromFirebase();
        }

        itemListContainer.setVisibility(View.VISIBLE);
    }



    private void loadEventsFromFirebase() {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("events");

        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        addItemToList(event.getTitle(), event, null, null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Ошибка загрузки мероприятий", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNewsFromFirebase() {
        DatabaseReference newsRef = FirebaseDatabase.getInstance().getReference("news");

        newsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                    String newsId = newsSnapshot.getKey();
                    String newsTitle = newsSnapshot.child("title").getValue(String.class);
                    String newsContent = newsSnapshot.child("content").getValue(String.class);
                    String newsImageUrl = newsSnapshot.child("ImageUrl").getValue(String.class);
                    String newsDate = newsSnapshot.child("publication_date").getValue(String.class);

                    if (newsId != null && newsTitle != null) {
                        addItemToList(newsTitle, null, newsId, new String[]{newsContent, newsImageUrl, newsDate});
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Ошибка загрузки новостей", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItemToList(String itemTitle, Event event, String newsId, String[] newsData) {
        if (getContext() == null) return;

        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(8, 8, 8, 8);
        itemLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView itemText = new TextView(getContext());
        itemText.setText(itemTitle);
        itemText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        ImageView menuIcon = new ImageView(getContext());
        menuIcon.setImageResource(R.drawable.ic_more_vert);
        menuIcon.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuIcon.setPadding(16, 0, 0, 0);

        menuIcon.setVisibility((event != null || newsId != null) ? View.VISIBLE : View.GONE);

        itemLayout.addView(itemText);
        itemLayout.addView(menuIcon);
        itemListContainer.addView(itemLayout);

        menuIcon.setOnClickListener(v -> {
            if (event != null) {
                showEditOrDeleteMenu(event, v);
            } else if (newsId != null && newsData != null) {
                showNewsEditOrDeleteMenu(newsId, itemTitle, newsData[0], newsData[1], newsData[2], v);
            }
        });
    }

    private void showEditOrDeleteMenu(Event event, View anchorView) {
        PopupMenu popupMenu = new PopupMenu(getContext(), anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_event, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                openEditEventScreen(event);
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                deleteEvent(event);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void openEditEventScreen(Event event) {
        Intent intent = new Intent(getContext(), EditEventActivity.class);
        intent.putExtra("eventId", event.getId());
        intent.putExtra("eventTitle", event.getTitle());
        intent.putExtra("eventDescription", event.getDescription());
        intent.putExtra("eventDate", event.getDate());
        intent.putExtra("eventLocation", event.getLocation());
        intent.putExtra("eventOrganizer", event.getOrganizer());
        startActivity(intent);
    }

    private void deleteEvent(Event event) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(event.getId());
        eventRef.removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Мероприятие удалено", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка удаления мероприятия", Toast.LENGTH_SHORT).show());
    }

    private void showNewsEditOrDeleteMenu(String newsId, String newsTitle, String newsContent, String newsImageUrl, String newsDate, View anchorView) {
        PopupMenu popupMenu = new PopupMenu(getContext(), anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_event, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                openEditNewsScreen(newsId, newsTitle, newsContent, newsImageUrl, newsDate);
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                deleteNews(newsId);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void openEditNewsScreen(String newsId, String newsTitle, String newsContent, String newsImageUrl, String newsDate) {
        Intent intent = new Intent(getContext(), EditNewsActivity.class);
        intent.putExtra("newsId", newsId);
        intent.putExtra("newsTitle", newsTitle);
        intent.putExtra("newsContent", newsContent);
        intent.putExtra("newsImageUrl", newsImageUrl);
        intent.putExtra("newsDate", newsDate);
        startActivity(intent);
    }

    private void deleteNews(String newsId) {
        DatabaseReference newsRef = FirebaseDatabase.getInstance().getReference("news").child(newsId);
        newsRef.removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Новость удалена", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка удаления новости", Toast.LENGTH_SHORT).show());
    }
}
