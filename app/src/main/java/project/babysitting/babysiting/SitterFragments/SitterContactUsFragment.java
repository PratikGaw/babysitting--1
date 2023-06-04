package project.babysitting.babysiting.SitterFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import project.babysitting.babysiting.R;
import project.babysitting.babysiting.configs.Information;
import project.babysitting.babysiting.databinding.FragmentSitterContactUsBinding;

public class SitterContactUsFragment extends Fragment {
    private FragmentSitterContactUsBinding binding;
    public SitterContactUsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSitterContactUsBinding.inflate(inflater, container, false);
        binding.txtContactUs.setText(Information.contactUs);
        return  binding.getRoot();
    }
}