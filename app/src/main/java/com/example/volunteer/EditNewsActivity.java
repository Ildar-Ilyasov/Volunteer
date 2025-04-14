package com.example.volunteer;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditNewsActivity extends AppCompatActivity {

    private EditText editTitle, editContent, editImageUrl, editPublicationDate;
    private Button btnSave;

    private String newsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_news);

        // Инициализация полей
        editTitle = findViewById(R.id.editNewsTitle);
        editContent = findViewById(R.id.editNewsContent);
        editImageUrl = findViewById(R.id.editNewsImageUrl);
        editPublicationDate = findViewById(R.id.editNewsDate);
        btnSave = findViewById(R.id.btnSaveNews);

        // Получение данных из Intent
        newsId = getIntent().getStringExtra("newsId");
        String title = getIntent().getStringExtra("newsTitle");
        String content = getIntent().getStringExtra("newsContent");
        String imageUrl = getIntent().getStringExtra("newsImageUrl");
        String publicationDate = getIntent().getStringExtra("newsDate");

        // Установка данных в поля
        editTitle.setText(title);
        editContent.setText(content);
        editImageUrl.setText(imageUrl);
        editPublicationDate.setText(publicationDate);

        // Сохранение обновлений
        btnSave.setOnClickListener(v -> {
            String updatedTitle = editTitle.getText().toString().trim();
            String updatedContent = editContent.getText().toString().trim();
            String updatedImageUrl = editImageUrl.getText().toString().trim();
            String updatedDate = editPublicationDate.getText().toString().trim();

            if (TextUtils.isEmpty(updatedTitle) || TextUtils.isEmpty(updatedContent)
                    || TextUtils.isEmpty(updatedImageUrl) || TextUtils.isEmpty(updatedDate)) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference newsRef = FirebaseDatabase.getInstance()
                    .getReference("news").child(newsId);

            newsRef.child("title").setValue(updatedTitle);
            newsRef.child("content").setValue(updatedContent);
            newsRef.child("ImageUrl").setValue(updatedImageUrl);
            newsRef.child("publication_date").setValue(updatedDate);

            Toast.makeText(this, "Новость обновлена", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
