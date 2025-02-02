package com.example.volunteer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SosFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sos, container, false);

        Button policeButton = view.findViewById(R.id.policeButton);
        Button medicsButton = view.findViewById(R.id.medicsButton);
        Button fireButton = view.findViewById(R.id.fireButton);
        Button gasButton = view.findViewById(R.id.gasButton);
        Button rescueButton = view.findViewById(R.id.rescueButton);
        Button infoButton = view.findViewById(R.id.infoButton);

        policeButton.setOnClickListener(v -> makeEmergencyCall("102"));
        medicsButton.setOnClickListener(v -> makeEmergencyCall("103"));
        fireButton.setOnClickListener(v -> makeEmergencyCall("101"));
        gasButton.setOnClickListener(v -> makeEmergencyCall("104"));
        rescueButton.setOnClickListener(v -> makeEmergencyCall("112"));
        infoButton.setOnClickListener(v -> makeEmergencyCall("109"));

        return view;
    }

    private void makeEmergencyCall(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }
}
