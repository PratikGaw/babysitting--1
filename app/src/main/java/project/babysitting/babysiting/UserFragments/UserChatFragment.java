package project.babysitting.babysiting.UserFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import project.babysitting.babysiting.R;
import project.babysitting.babysiting.databinding.FragmentUserChatBinding;

public class UserChatFragment extends Fragment {
    private FragmentUserChatBinding binding;
    public UserChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserChatBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }
}