package project.babysitting.babysiting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import project.babysitting.babysiting.databinding.ActivityBookingDatailBinding;

public class BookingDatailActivity extends AppCompatActivity {
    private ActivityBookingDatailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingDatailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}