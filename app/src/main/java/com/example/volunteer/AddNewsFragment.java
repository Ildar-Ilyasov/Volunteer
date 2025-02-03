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

public class AddNewsFragment extends Fragment {

    private EditText etTitle, etContent;
    private Button btnPublish;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_news, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etContent = view.findViewById(R.id.etContent);
        btnPublish = view.findViewById(R.id.btnPublish);

        databaseReference = FirebaseDatabase.getInstance().getReference("news");

        btnPublish.setOnClickListener(v -> publishNews());

        return view;
    }

    private void publishNews() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String newsId = databaseReference.push().getKey();
        String imageUrl = "";
        String publication_date = "";
        News news = new News(newsId, title, content, imageUrl, publication_date);
        databaseReference.child(newsId).setValue(news)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Новость опубликована", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack(); // Возвращаемся на предыдущий экран
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
