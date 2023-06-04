package project.babysitting.babysiting.SitterFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import project.babysitting.babysiting.R;
import project.babysitting.babysiting.configs.Information;
import project.babysitting.babysiting.databinding.FragmentSitterAppointmentBinding;

public class SitterAboutUsFragment extends Fragment {
    private FragmentSitterAppointmentBinding binding;
    public SitterAboutUsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSitterAppointmentBinding.inflate(inflater, container, false);
        binding.txtAboutUs.setText(Information.aboutUs);
        return binding.getRoot();
    }
}