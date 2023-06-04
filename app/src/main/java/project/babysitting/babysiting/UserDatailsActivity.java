package project.babysitting.babysiting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import project.babysitting.babysiting.databinding.ActivityUserDatailsBinding;

public class UserDatailsActivity extends AppCompatActivity {
    private ActivityUserDatailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDatailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}