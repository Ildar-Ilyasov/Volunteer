package com.example.volunteer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;
    private String userId;
    private boolean isAdmin; // флаг администратора

    public EventAdapter(List<Event> eventList, Context context, String userId, boolean isAdmin) {
        this.eventList = eventList;
        this.context = context;
        this.userId = userId;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.tvTitle.setText(event.getTitle());
        holder.tvDate.setText(event.getDate());
        holder.tvLocation.setText(event.getLocation());

        // Показать или скрыть меню администратора
        if (isAdmin) {
            holder.itemMenu.setVisibility(View.VISIBLE);
            holder.itemMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, holder.itemMenu);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_event, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_edit) {
                        // Открытие экрана редактирования события
                        openEditEventScreen(event);
                        return true;
                    } else if (item.getItemId() == R.id.menu_delete) {
                        // Удаление события
                        deleteEvent(event, position);
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });
        } else {
            holder.itemMenu.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            // Переход на фрагмент с деталями мероприятия
            EventDetailFragment detailFragment = EventDetailFragment.newInstance(event.getId(), userId);
            FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentFrame, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    // Метод для открытия экрана редактирования события
    private void openEditEventScreen(Event event) {
        Intent intent = new Intent(context, EditEventActivity.class);
        intent.putExtra("eventId", event.getId());
        intent.putExtra("eventTitle", event.getTitle());
        intent.putExtra("eventDescription", event.getDescription());
        intent.putExtra("eventDate", event.getDate());
        intent.putExtra("eventLocation", event.getLocation());
        intent.putExtra("eventOrganizer", event.getOrganizer());
        context.startActivity(intent);
    }

    // Метод для удаления события
    private void deleteEvent(Event event, int position) {
        FirebaseDatabase.getInstance().getReference("events")
                .child(event.getId())
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    eventList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Мероприятие удалено", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvLocation;
        ImageView itemMenu;

        public EventViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            itemMenu = itemView.findViewById(R.id.itemMenu); // добавляем
        }
    }
}
