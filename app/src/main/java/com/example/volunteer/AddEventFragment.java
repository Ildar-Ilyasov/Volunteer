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
import android.widget.ImageButton;

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
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        etEventTitle = view.findViewById(R.id.etEventTitle);
        etEventDescription = view.findViewById(R.id.etEventDescription);
        etEventStartDate = view.findViewById(R.id.etEventStartDate);
        etEventEndDate = view.findViewById(R.id.etEventEndDate);
        etEventLocation = view.findViewById(R.id.etEventLocation);
        etEventOrganizer = view.findViewById(R.id.etEventOrganizer);
        btnAddEvent = view.findViewById(R.id.btnAddEvent);

        etEventStartDate.setOnClickListener(v -> showDatePickerDialog(startDateCalendar, etEventStartDate));

        etEventEndDate.setOnClickListener(v -> showDatePickerDialog(endDateCalendar, etEventEndDate));

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
        String eventId = eventsRef.push().getKey();
        String title = etEventTitle.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();
        String startDate = etEventStartDate.getText().toString().trim();
        String endDate = etEventEndDate.getText().toString().trim();
        String location = etEventLocation.getText().toString().trim();
        String organizer = etEventOrganizer.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || location.isEmpty() || organizer.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();

            startCalendar.setTime(sdf.parse(startDate));
            endCalendar.setTime(sdf.parse(endDate));

            if (startCalendar.after(endCalendar)) {
                Toast.makeText(getContext(), "Дата начала не может быть позже даты окончания.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка при обработке дат.", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateRange = startDate + " - " + endDate;

        Event event = new Event(title, description, dateRange, location, organizer);

        if (eventId != null) {
            event.setId(eventId);
            eventsRef.child(eventId).setValue(event)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Мероприятие добавлено!", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getContext(), "Ошибка при добавлении мероприятия.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}