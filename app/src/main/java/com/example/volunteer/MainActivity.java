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
        Button btnAddEvent = findViewById(R.id.btnAddEvent);
        Button btnNews = findViewById(R.id.btnNews);

        String userId = getIntent().getStringExtra("userId");
        loadFragment(new MenuFragment(userId));

        btnMenu.setOnClickListener(v -> loadFragment(new MenuFragment(userId)));
        btnFeed.setOnClickListener(v -> loadFragment(new NewsFragment()));
        btnSOS.setOnClickListener(v -> loadFragment(new SosFragment()));
        btnMap.setOnClickListener(v -> loadFragment(new MapFragment()));
        btnProfile.setOnClickListener(v -> {
            if (userId != null) {
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                profileFragment.setArguments(bundle);
                loadFragment(profileFragment);
            }
        });
        btnAddEvent.setOnClickListener(v -> loadFragment(new AddEventFragment()));
        btnNews.setOnClickListener(v -> loadFragment(new AddNewsFragment()));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .addToBackStack(null)
                .commit();
    }
}
