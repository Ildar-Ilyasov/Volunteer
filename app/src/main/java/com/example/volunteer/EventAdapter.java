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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private Context context;
    private String userId;
    private boolean isAdmin;
    private DatabaseReference userEventsRef;

    public EventAdapter(List<Event> eventList, Context context, String userId, boolean isAdmin) {
        this.eventList = eventList;
        this.context = context;
        this.userId = userId;
        this.isAdmin = isAdmin;

        if (userId != null) {
            userEventsRef = FirebaseDatabase.getInstance().getReference("user_events").child(userId);
        }
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

        // Проверка участия и установка соответствующей рамки
        if (userId != null) {
            userEventsRef.child(event.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        holder.itemView.setBackgroundResource(R.drawable.border_green);
                    } else {
                        holder.itemView.setBackgroundResource(R.drawable.border_goal);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    holder.itemView.setBackgroundResource(R.drawable.border_goal);
                }
            });
        } else {
            holder.itemView.setBackgroundResource(R.drawable.border_goal);
        }

        // Меню администратора
        if (isAdmin) {
            holder.itemMenu.setVisibility(View.VISIBLE);
            holder.itemMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, holder.itemMenu);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_event, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_edit) {
                        openEditEventScreen(event);
                        return true;
                    } else if (item.getItemId() == R.id.menu_delete) {
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

        // Обработчик клика на мероприятие
        holder.itemView.setOnClickListener(v -> {
            EventDetailFragment detailFragment = EventDetailFragment.newInstance(event.getId(), userId);
            FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentFrame, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

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
            itemMenu = itemView.findViewById(R.id.itemMenu);
        }
    }
}
