package com.example.volunteer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvLogin, tvDob, tvRegistrationDate, tvEmail;
    private RecyclerView recyclerViewUserEvents;
    private EventAdapter userEventAdapter;
    private List<Event> userEventList;
    private DatabaseReference userRef, userEventsRef;
    private String userId;
    private ImageView profileImage;
    private static final int PICK_IMAGE_REQUEST = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.tv_name);
        tvLogin = view.findViewById(R.id.tv_login);
        tvDob = view.findViewById(R.id.tv_dob);
        tvRegistrationDate = view.findViewById(R.id.tv_registration_date);
        tvEmail = view.findViewById(R.id.tv_email);
        recyclerViewUserEvents = view.findViewById(R.id.recyclerViewUserEvents);
        ImageButton btnEditProfile = view.findViewById(R.id.btnEditProfile);
        profileImage = view.findViewById(R.id.profile_image);

        tvName.setText("Загружается...");
        tvLogin.setText("Загружается...");
        tvDob.setText("Загружается...");
        tvRegistrationDate.setText("Загружается...");
        tvEmail.setText("Загружается...");

        recyclerViewUserEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        userEventList = new ArrayList<>();
        userEventAdapter = new EventAdapter(userEventList, getContext(), userId, false);
        recyclerViewUserEvents.setAdapter(userEventAdapter);

        userId = getArguments() != null ? getArguments().getString("userId") : null;

        if (userId != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userEventsRef = FirebaseDatabase.getInstance().getReference("user_events").child(userId);

            userEventAdapter = new EventAdapter(userEventList, getContext(), userId, false);
            recyclerViewUserEvents.setAdapter(userEventAdapter);

            loadUserData();
            loadUserEvents();
        }

        btnEditProfile.setOnClickListener(v -> {
            if (userId != null) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("userId", userId);
                startActivityForResult(intent, 1);
            }
        });

        profileImage.setOnClickListener(v -> openImageChooser());

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Установка текстовых данных
                        tvName.setText(user.getFirstName() + " " + user.getLastName());
                        tvName.setTextColor(getResources().getColor(R.color.orange));
                        tvLogin.setText("Логин: " + user.getLogin());
                        tvDob.setText("Дата рождения: " + user.getDob());
                        tvRegistrationDate.setText("Дата регистрации: " + user.getRegistrationDate());
                        tvEmail.setText("Email: " + user.getEmail());

                        if (user.getProfileImageBase64() != null && !user.getProfileImageBase64().isEmpty()) {
                            byte[] decodedBytes = Base64.decode(user.getProfileImageBase64(), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            profileImage.setImageBitmap(bitmap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
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
                                    event.setId(eventId);
                                    userEventList.add(event);
                                    userEventAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Ошибка загрузки мероприятий", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                recyclerViewUserEvents.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Ошибка загрузки мероприятий", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String firstName = data.getStringExtra("firstName");
            String lastName = data.getStringExtra("lastName");
            String dob = data.getStringExtra("dob");
            String email = data.getStringExtra("email");

            tvName.setText(firstName + " " + lastName);
            tvDob.setText("Дата рождения: " + dob);
            tvEmail.setText("Email: " + email);
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profileImage.setImageBitmap(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Сжатие до 50%
                String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                if (userId != null) {
                    userRef.child("profileImageBase64").setValue(base64Image)
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Аватар обновлен", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}