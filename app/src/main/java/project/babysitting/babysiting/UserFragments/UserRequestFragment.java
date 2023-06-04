package project.babysitting.babysiting.UserFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import project.babysitting.babysiting.Adapters.UserRequestAdapter;
import project.babysitting.babysiting.Models.Interests;
import project.babysitting.babysiting.R;
import project.babysitting.babysiting.databinding.FragmentUserRequestBinding;

public class UserRequestFragment extends Fragment {
    private FragmentUserRequestBinding binding;
    private ArrayList<Interests> reqList;
    private UserRequestAdapter adapter;
    private DatabaseReference reqRef;
    private FirebaseAuth auth;
    public UserRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserRequestBinding.inflate(inflater, container, false);
        reqList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        binding.rvRequest.setHasFixedSize(true);
        adapter = new UserRequestAdapter(getActivity(),reqList);
        binding.rvRequest.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        reqRef = FirebaseDatabase.getInstance().getReference(auth.getCurrentUser().getUid()).child("MyRequest");
        reqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reqList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Interests interests = ds.getValue(Interests.class);
                    reqList.add(interests);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }
}