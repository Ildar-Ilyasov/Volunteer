package com.example.volunteer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventFragment extends Fragment {

    private EditText etEventTitle, etEventDescription, etEventStartDate, etEventEndDate, etEventLocation, etEventOrganizer;
    private Button btnAddEvent;
    private DatabaseReference eventsRef;
    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        // Инициализация Firebase
        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        // Инициализация UI элементов
        etEventTitle = view.findViewById(R.id.etEventTitle);
        etEventDescription = view.findViewById(R.id.etEventDescription);
        etEventStartDate = view.findViewById(R.id.etEventStartDate);
        etEventEndDate = view.findViewById(R.id.etEventEndDate);
        etEventLocation = view.findViewById(R.id.etEventLocation);
        etEventOrganizer = view.findViewById(R.id.etEventOrganizer);
        btnAddEvent = view.findViewById(R.id.btnAddEvent);

        // Обработка нажатия на поле выбора даты начала
        etEventStartDate.setOnClickListener(v -> showDatePickerDialog(startDateCalendar, etEventStartDate));

        // Обработка нажатия на поле выбора даты окончания
        etEventEndDate.setOnClickListener(v -> showDatePickerDialog(endDateCalendar, etEventEndDate));

        // Обработка нажатия на кнопку "Добавить мероприятие"
        btnAddEvent.setOnClickListener(v -> addEventToDatabase());

        return view;
    }

    private void showDatePickerDialog(final Calendar calendar, final EditText editText) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Форматируем дату и устанавливаем в EditText
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                editText.setText(sdf.format(calendar.getTime()));
            }
        };

        new DatePickerDialog(
                getContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void addEventToDatabase() {
        // Получаем данные из полей ввода
        String eventId = eventsRef.push().getKey();
        String title = etEventTitle.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();
        String startDate = etEventStartDate.getText().toString().trim();
        String endDate = etEventEndDate.getText().toString().trim();
        String location = etEventLocation.getText().toString().trim();
        String organizer = etEventOrganizer.getText().toString().trim();

        // Проверяем, что все поля заполнены
        if (title.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || location.isEmpty() || organizer.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Объединяем даты в одну строку
        String dateRange = startDate + " - " + endDate;

        // Создаем объект мероприятия
        Event event = new Event(eventId, title, description, dateRange, location, organizer);

        // Устанавливаем ID мероприятия
        if (eventId != null) {
            event.setId(eventId);

            // Добавляем мероприятие в базу данных
            eventsRef.child(eventId).setValue(event)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Успешно добавлено
                            Toast.makeText(getContext(), "Мероприятие добавлено!", Toast.LENGTH_SHORT).show();
                            // Возвращаемся на предыдущий фрагмент
                            getParentFragmentManager().popBackStack();
                        } else {
                            // Ошибка
                            Toast.makeText(getContext(), "Ошибка при добавлении мероприятия.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}