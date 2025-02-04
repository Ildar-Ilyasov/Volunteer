package com.example.volunteer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import androidx.annotation.NonNull;


public class ChatFragment extends Fragment {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private DatabaseReference messagesRef;
    private String eventId, userId;
    private final HashMap<String, String> userNamesCache = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Получаем eventId и userId из аргументов
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            userId = getArguments().getString("userId");
        }

        // Инициализация RecyclerView и других UI элементов
        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);

        messagesRef = FirebaseDatabase.getInstance().getReference("chats").child(eventId); // Ссылка на чат мероприятия

        // Загрузка сообщений
        loadMessages();

        // Обработчик отправки сообщения
        Button sendButton = view.findViewById(R.id.sendButton);
        EditText messageInput = view.findViewById(R.id.messageInput);
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);  // Отправить сообщение
            }
        });

        return view;
    }

    private void loadMessages() {
        messagesRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                List<Message> tempMessageList = new ArrayList<>();
                List<String> userIdsToFetch = new ArrayList<>();

                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Long timestamp = messageSnapshot.child("timestamp").getValue(Long.class);
                    String userId = messageSnapshot.child("userId").getValue(String.class);
                    String text = messageSnapshot.child("text").getValue(String.class);

                    if (timestamp != null && userId != null && text != null) {
                        tempMessageList.add(new Message(userId, text, timestamp));
                        if (!userNamesCache.containsKey(userId)) {
                            userIdsToFetch.add(userId);
                        }
                    }
                }

                if (userIdsToFetch.isEmpty()) {
                    updateMessageList(tempMessageList);
                } else {
                    fetchUserNames(userIdsToFetch, tempMessageList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void fetchUserNames(List<String> userIds, List<Message> tempMessageList) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (String userId : userIds) {
                    DataSnapshot userSnapshot = snapshot.child(userId);
                    String firstName = userSnapshot.child("firstName").getValue(String.class);
                    String lastName = userSnapshot.child("lastName").getValue(String.class);
                    String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");

                    userNamesCache.put(userId, fullName.trim());
                }
                updateMessageList(tempMessageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void updateMessageList(List<Message> tempMessageList) {
        messageList.clear();
        for (Message msg : tempMessageList) {
            String fullName = userNamesCache.getOrDefault(msg.getUserId(), "Неизвестный");
            messageList.add(new Message(fullName, msg.getText(), msg.getTimestamp()));
        }
        messageAdapter.notifyDataSetChanged();
    }




    private void sendMessage(String messageText) {
        Long timestamp = System.currentTimeMillis(); // Добавляем время в миллисекундах

        Message message = new Message(userId, messageText, timestamp);
        messagesRef.push().setValue(message)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        EditText messageInput = getView().findViewById(R.id.messageInput);
                        messageInput.setText("");
                    } else {
                        Toast.makeText(getContext(), "Ошибка при отправке сообщения", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}


