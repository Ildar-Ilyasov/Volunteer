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

        // Загружаем экран по умолчанию (MenuFragment)
        loadFragment(new MenuFragment());

        btnMenu.setOnClickListener(v -> loadFragment(new MenuFragment()));
        btnFeed.setOnClickListener(v -> loadFragment(new FeedFragment()));
        btnSOS.setOnClickListener(v -> loadFragment(new SosFragment()));
        btnMap.setOnClickListener(v -> loadFragment(new MapFragment()));
        btnProfile.setOnClickListener(v -> {
            // Получаем userId из Intent
            String userId = getIntent().getStringExtra("userId");
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

    // Остальные методы жизненного цикла активности можно оставить без изменений
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}