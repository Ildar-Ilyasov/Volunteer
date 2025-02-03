package com.example.volunteer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class NewsDetailFragment extends Fragment {
    private TextView tvTitle, tvContent, tvPublicationDate;
    private ImageView newsImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);

        tvTitle = view.findViewById(R.id.tvDetailTitle);
        tvContent = view.findViewById(R.id.tvDetailContent);
        tvPublicationDate = view.findViewById(R.id.tvPublicationDate);
        newsImage = view.findViewById(R.id.newsImage);

        Bundle bundle = getArguments();
        if (bundle != null) {
            tvTitle.setText(bundle.getString("title"));
            tvContent.setText(bundle.getString("content"));
            String publicationDate = bundle.getString("publication_date");
            if (publicationDate != null && !publicationDate.isEmpty()) {
                tvPublicationDate.setText(publicationDate);
            }
            String imageUrl = bundle.getString("image_url");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(newsImage);
            } else {
                newsImage.setImageResource(R.drawable.placeholder_image);  // Замените на свой ресурс изображения по умолчанию
            }
        }

        return view;
    }
}

