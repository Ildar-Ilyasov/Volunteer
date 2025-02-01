package com.example.volunteer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NewsDetailFragment extends Fragment {
    private TextView tvTitle, tvContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);

        tvTitle = view.findViewById(R.id.tvDetailTitle);
        tvContent = view.findViewById(R.id.tvDetailContent);

        Bundle bundle = getArguments();
        if (bundle != null) {
            tvTitle.setText(bundle.getString("title"));
            tvContent.setText(bundle.getString("content"));
        }

        return view;
    }
}
