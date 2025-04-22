package com.example.volunteer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.widget.ImageButton;

public class AddNewsFragment extends Fragment {

    private EditText etTitle, etContent, etImageUrl, etPublicationDate;
    private Button btnPublish;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_news, container, false);
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        etTitle = view.findViewById(R.id.etTitle);
        etContent = view.findViewById(R.id.etContent);
        etImageUrl = view.findViewById(R.id.etImageUrl);
        etPublicationDate = view.findViewById(R.id.etPublicationDate);
        btnPublish = view.findViewById(R.id.btnPublish);

        databaseReference = FirebaseDatabase.getInstance().getReference("news");

        btnPublish.setOnClickListener(v -> publishNews());

        return view;
    }


    private void publishNews() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String publicationDate = etPublicationDate.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty() || imageUrl.isEmpty() || publicationDate.isEmpty()) {
            Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String newsId = databaseReference.push().getKey();
        News news = new News(newsId, title, content, imageUrl, publicationDate);

        databaseReference.child(newsId).setValue(news)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Новость опубликована", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
