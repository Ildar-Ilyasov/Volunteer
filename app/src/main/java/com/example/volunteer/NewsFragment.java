package com.example.volunteer;

import android.os.Bundle;
import android.util.Log;
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

public class NewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<News> newsList;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(newsList, news -> openNewsDetail(news));
        recyclerView.setAdapter(newsAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("news");

        loadNews();

        return view;
    }

    private void loadNews() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    News news = dataSnapshot.getValue(News.class);
                    if (news != null) {
                        // Логирование данных
                        Log.d("NewsFragment", "Loaded news: " + news.getTitle() + ", publicationDate: " + news.getPublication_Date());
                        if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
                            newsList.add(news);
                        }
                    }
                }
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Логирование ошибок
                Log.e("NewsFragment", "Error loading news: " + error.getMessage());
            }
        });
    }




    private void openNewsDetail(News news) {
        NewsDetailFragment newsDetailFragment = new NewsDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", news.getTitle());
        bundle.putString("content", news.getContent());
        bundle.putString("publication_date", news.getPublication_Date());
        bundle.putString("image_url", news.getImageUrl());
        newsDetailFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentFrame, newsDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
