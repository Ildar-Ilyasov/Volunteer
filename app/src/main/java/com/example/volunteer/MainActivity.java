package com.example.volunteer;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnMenu = findViewById(R.id.btnMenu);
        Button btnFeed = findViewById(R.id.btnFeed);
        Button btnSOS = findViewById(R.id.btnSOS);
        Button btnMap = findViewById(R.id.btnMap);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnAddEvent = findViewById(R.id.btnAddEvent);  // Новая кнопка

        // Получаем userId из Intent
        String userId = getIntent().getStringExtra("userId");

        // Загружаем экран по умолчанию (MenuFragment)
        loadFragment(new MenuFragment(userId));

        btnMenu.setOnClickListener(v -> loadFragment(new MenuFragment(userId)));
        btnFeed.setOnClickListener(v -> loadFragment(new FeedFragment()));
        btnSOS.setOnClickListener(v -> loadFragment(new SosFragment()));
        btnMap.setOnClickListener(v -> loadFragment(new MapFragment()));
        btnProfile.setOnClickListener(v -> {
            // Получаем userId из Intent
            if (userId != null) {
                // Создаем фрагмент и передаем userId в Bundle
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                profileFragment.setArguments(bundle);

                loadFragment(profileFragment);  // Загружаем фрагмент
            }
        });

        // Обработка нажатия на кнопку "Создать"
        btnAddEvent.setOnClickListener(v -> {
            // Переход на фрагмент AddEventFragment
            AddEventFragment addEventFragment = new AddEventFragment();
            loadFragment(addEventFragment);
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .addToBackStack(null)  // Добавляем в стек возврата
                .commit();
    }
}
